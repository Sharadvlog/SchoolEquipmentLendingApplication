package org.school.equipment.lending.service;

import jakarta.transaction.Transactional;
import org.school.equipment.lending.constants.BorrowStatus;
import org.school.equipment.lending.entity.BorrowRequest;
import org.school.equipment.lending.entity.Equipment;
import org.school.equipment.lending.entity.User;
import org.school.equipment.lending.model.BorrowRequestDto;
import org.school.equipment.lending.repo.BorrowRequestRepository;
import org.school.equipment.lending.repo.EquipmentRepository;
import org.school.equipment.lending.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class BorrowService {

    private final BorrowRequestRepository borrowRequestRepository;
    private final EquipmentRepository equipmentRepository;
    private final UserRepository userRepository;

    private static final List<BorrowStatus> ACTIVE_STATUSES =
            List.of(BorrowStatus.APPROVED, BorrowStatus.ISSUED);

    @Autowired
    public BorrowService(BorrowRequestRepository borrowRequestRepository,
                         EquipmentRepository equipmentRepository,
                         UserRepository userRepository) {
        this.borrowRequestRepository = borrowRequestRepository;
        this.equipmentRepository = equipmentRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public BorrowRequest placeRequest(Long userId, Long equipmentId, int quantity,
                                      LocalDateTime from, LocalDateTime to) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Equipment equipment = equipmentRepository.findById(equipmentId)
                .orElseThrow(() -> new RuntimeException("Equipment not found"));

        if (quantity <= 0) throw new IllegalArgumentException("Quantity must be > 0");
        if (!from.isBefore(to)) throw new IllegalArgumentException("Invalid date range");

        BorrowRequest request = new BorrowRequest();
        request.setRequester(user);
        request.setEquipment(equipment);
        request.setQuantity(quantity);
        request.setFromDate(from);
        request.setToDate(to);
        request.setStatus(BorrowStatus.PENDING);
        return borrowRequestRepository.save(request);
    }

    @Transactional
    public BorrowRequest approveRequest(Long requestId) {
        BorrowRequest request = borrowRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found"));

        if (request.getStatus() != BorrowStatus.PENDING) {
            throw new IllegalStateException("Only PENDING requests can be approved");
        }

        Equipment equipment = equipmentRepository.findByIdForUpdate(request.getEquipment().getId())
                .orElseThrow(() -> new RuntimeException("Equipment not found"));

        Long reserved = borrowRequestRepository.getCountOfOvelappingRq(
                equipment.getId(),
                request.getFromDate(),
                request.getToDate(),
                ACTIVE_STATUSES
        );

        if (reserved + request.getQuantity() > equipment.getTotalQuantity()) {
            throw new IllegalStateException("Insufficient quantity available ");
        }

        request.setStatus(BorrowStatus.APPROVED);
        return borrowRequestRepository.save(request);
    }

    @Transactional
    public BorrowRequest issue(Long requestId) {
        BorrowRequest request = borrowRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found"));

        if (request.getStatus() != BorrowStatus.APPROVED) {
            throw new IllegalStateException("Only APPROVED requests can be issued");
        }

        request.setStatus(BorrowStatus.ISSUED);
        return borrowRequestRepository.save(request);
    }

    @Transactional
    public BorrowRequest markReturned(Long requestId) {
        BorrowRequest request = borrowRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found"));

        if (request.getStatus() != BorrowStatus.ISSUED) {
            throw new IllegalStateException("Only ISSUED requests can be returned");
        }

        request.setStatus(BorrowStatus.RETURNED);
        return borrowRequestRepository.save(request);
    }

    public List<BorrowRequestDto> getAllRequestsByUser(Long userId) {
        List<BorrowRequest> list = borrowRequestRepository.findAll().stream()
                .filter(r -> r.getRequester().getId().equals(userId))
                .toList();
        return list.stream().map(r -> {
            BorrowRequestDto borrowRequestDto = new BorrowRequestDto();
            borrowRequestDto.setUserId(r.getRequester().getId());
            borrowRequestDto.setEquipmentId(r.getEquipment().getId());
            borrowRequestDto.setQuantity(r.getQuantity());
            borrowRequestDto.setFromDate(r.getFromDate());
            borrowRequestDto.setToDate(r.getToDate());
            return borrowRequestDto;
        }).toList();

    }
}

package org.school.equipment.lending.controller;

import org.school.equipment.lending.constants.BorrowStatus;
import org.school.equipment.lending.entity.Equipment;
import org.school.equipment.lending.model.EquipmentAvailabilityDto;
import org.school.equipment.lending.repo.BorrowRequestRepository;
import org.school.equipment.lending.repo.EquipmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final EquipmentRepository equipmentRepository;
    private final BorrowRequestRepository borrowRequestRepository;

    @Autowired
    public DashboardController(EquipmentRepository equipmentRepository,
                               BorrowRequestRepository borrowRequestRepository) {
        this.equipmentRepository = equipmentRepository;
        this.borrowRequestRepository = borrowRequestRepository;
    }

    @GetMapping("/equipment")
    public ResponseEntity<List<EquipmentAvailabilityDto>> getAllWithAvailability() {
        List<Equipment> all = equipmentRepository.findAll();
        List<EquipmentAvailabilityDto> dtoList = all.stream().map(e -> {
            long reserved = borrowRequestRepository.getCountOfOvelappingRq(
                    e.getId(),
                    LocalDateTime.now(),
                    LocalDateTime.now().plusDays(1),
                    List.of(BorrowStatus.APPROVED, BorrowStatus.ISSUED)
            );
            return new EquipmentAvailabilityDto(e.getId(), e.getName(), e.getCategory(),
                    e.getConditionInfo(), (int) (e.getTotalQuantity() - reserved));
        }).toList();
        return ResponseEntity.ok(dtoList);
    }

    @GetMapping("/search")
    public ResponseEntity<List<EquipmentAvailabilityDto>> getAvailableEquipmentByCategory(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Boolean available) {

        List<Equipment> all = equipmentRepository.findAll();
        Stream<Equipment> stream = all.stream();

        if (category != null && !category.isBlank()) {
            stream = stream.filter(e -> e.getCategory().equalsIgnoreCase(category));
        }

        List<EquipmentAvailabilityDto> dtoList = stream.map(e -> {
            long reserved = borrowRequestRepository.getCountOfOvelappingRq(
                    e.getId(),
                    LocalDateTime.now(),
                    LocalDateTime.now().plusDays(1),
                    List.of(BorrowStatus.APPROVED, BorrowStatus.ISSUED)
            );
            int availableQty = (int) (e.getTotalQuantity() - reserved);
            return new EquipmentAvailabilityDto(e.getId(), e.getName(), e.getCategory(),
                    e.getConditionInfo(), availableQty);
        }).toList();

        if (available != null && available) {
            dtoList = dtoList.stream().filter(d -> d.getAvailableQuantity() > 0).toList();
        }

        return ResponseEntity.ok(dtoList);
    }
}


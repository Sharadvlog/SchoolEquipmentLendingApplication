package org.school.equipment.lending.controller;

import org.school.equipment.lending.constants.BorrowStatus;
import org.school.equipment.lending.entity.BorrowRequest;
import org.school.equipment.lending.model.BorrowRequestDto;
import org.school.equipment.lending.repo.BorrowRequestRepository;
import org.school.equipment.lending.service.BorrowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/borrow")
public class BorrowController {

    private final BorrowService borrowService;
    private final BorrowRequestRepository borrowRequestRepository;

    @Autowired
    public BorrowController(BorrowService borrowService,
                            BorrowRequestRepository borrowRequestRepository) {
        this.borrowService = borrowService;
        this.borrowRequestRepository = borrowRequestRepository;
    }

    @PostMapping
    public ResponseEntity<BorrowRequestDto> placeBorrowRequest(@RequestBody BorrowRequestDto dto) {
        BorrowRequest request = borrowService.placeRequest(dto.getUserId(), dto.getEquipmentId(), dto.getQuantity(), dto.getFromDate(), dto.getToDate());
        BorrowRequestDto borrowRequestDto = new BorrowRequestDto();
        borrowRequestDto.setUserId(request.getRequester().getId());
        borrowRequestDto.setEquipmentId(request.getEquipment().getId());
        borrowRequestDto.setQuantity(request.getQuantity());
        borrowRequestDto.setFromDate(request.getFromDate());
        borrowRequestDto.setToDate(request.getToDate());
        return ResponseEntity.status(HttpStatus.CREATED).body(borrowRequestDto);
    }

    @GetMapping("/myrequest")
    public ResponseEntity<List<BorrowRequestDto>> myRequests(@RequestParam Long userId) {
        List<BorrowRequestDto> requestDtos = borrowService.getAllRequestsByUser(userId);
        return ResponseEntity.ok(requestDtos);
    }

    @GetMapping("/pending")
    public ResponseEntity<List<BorrowRequest>> listPending() {
        List<BorrowRequest> borrowRequestList = borrowRequestRepository.findAll().stream()
                .filter(r -> r.getStatus() == BorrowStatus.PENDING)
                .toList();
        return ResponseEntity.ok(borrowRequestList);
    }

    @PostMapping("/{id}/approve")
    public ResponseEntity<BorrowRequest> approve(@PathVariable("id") Long borrowRequestId) {
        BorrowRequest updated = borrowService.approveRequest(borrowRequestId);
        return ResponseEntity.ok(updated);
    }

    @PostMapping("/{id}/reject")
    public ResponseEntity<BorrowRequest> reject(@PathVariable Long id) {
        BorrowRequest request = borrowRequestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Request not found"));
        request.setStatus(BorrowStatus.REJECTED);
        BorrowRequest req = borrowRequestRepository.save(request);
        return ResponseEntity.ok(req);
    }

    @PostMapping("/{id}/issue")
    public ResponseEntity<BorrowRequest> issue(@PathVariable Long id) {
        BorrowRequest borrowRequest = borrowService.issue(id);
        return ResponseEntity.ok(borrowRequest);
    }

    @PostMapping("/{id}/return")
    public ResponseEntity<BorrowRequest> markReturned(@PathVariable Long id) {
        BorrowRequest borrowRequest = borrowService.markReturned(id);
        return ResponseEntity.ok(borrowRequest);
    }
}


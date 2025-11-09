package org.school.equipment.lending.model;


import java.time.LocalDateTime;

public class BorrowRequestDto {

    private Long userId;
    private Long equipmentId;
    private int quantity;
    private LocalDateTime fromDate;
    private LocalDateTime toDate;

    public BorrowRequestDto() {}

    public BorrowRequestDto(Long userId, Long equipmentId, int quantity, LocalDateTime fromDate, LocalDateTime toDate) {
        this.userId = userId;
        this.equipmentId = equipmentId;
        this.quantity = quantity;
        this.fromDate = fromDate;
        this.toDate = toDate;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getEquipmentId() {
        return equipmentId;
    }

    public void setEquipmentId(Long equipmentId) {
        this.equipmentId = equipmentId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public LocalDateTime getFromDate() {
        return fromDate;
    }

    public void setFromDate(LocalDateTime fromDate) {
        this.fromDate = fromDate;
    }

    public LocalDateTime getToDate() {
        return toDate;
    }

    public void setToDate(LocalDateTime toDate) {
        this.toDate = toDate;
    }
}


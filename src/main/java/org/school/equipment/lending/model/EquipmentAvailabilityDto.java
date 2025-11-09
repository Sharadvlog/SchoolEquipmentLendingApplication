package org.school.equipment.lending.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
public class EquipmentAvailabilityDto {
    private Long id;
    private String name;
    private String category;
    private String conditionInfo;
    private Integer availableQuantity;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getConditionInfo() {
        return conditionInfo;
    }

    public void setConditionInfo(String conditionInfo) {
        this.conditionInfo = conditionInfo;
    }

    public Integer getAvailableQuantity() {
        return availableQuantity;
    }

    public void setAvailableQuantity(Integer availableQuantity) {
        this.availableQuantity = availableQuantity;
    }
}

package org.school.equipment.lending.controller;

import org.school.equipment.lending.entity.Equipment;
import org.school.equipment.lending.repo.EquipmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Stream;

@RestController
@RequestMapping("/api/equipment")
public class EquipmentController {

    private final EquipmentRepository equipmentRepository;

    @Autowired
    public EquipmentController(EquipmentRepository equipmentRepository) {
        this.equipmentRepository = equipmentRepository;
    }

    @GetMapping("/")
    public ResponseEntity<List<Equipment>> getAllEquipment(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Boolean available) {

        List<Equipment> all = equipmentRepository.findAll();

        Stream<Equipment> stream = all.stream();
        if (category != null && !category.isBlank()) {
            stream = stream.filter(e -> e.getCategory().equalsIgnoreCase(category));
        }

        if (available != null && available) {
            stream = stream.filter(e -> e.getTotalQuantity() > 0);
        }

        return ResponseEntity.ok(stream.toList());
    }


    @PostMapping("/")
    public ResponseEntity<Equipment> addEquipment(@RequestBody Equipment equipment) {
        Equipment saved = equipmentRepository.save(equipment);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }


    @PutMapping("/{id}")
    public ResponseEntity<Equipment> updateEquipment(@PathVariable("id") Long equipmentID, @RequestBody Equipment updated) {
        return equipmentRepository.findById(equipmentID)
                .map(existing -> {
                    existing.setName(updated.getName());
                    existing.setCategory(updated.getCategory());
                    existing.setConditionInfo(updated.getConditionInfo());
                    existing.setTotalQuantity(updated.getTotalQuantity());
                    return ResponseEntity.ok(equipmentRepository.save(existing));
                })
                .orElse(ResponseEntity.notFound().build());
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEquipment(@PathVariable("id") Long EquipmentId) {
        if (equipmentRepository.existsById(EquipmentId)) {
            equipmentRepository.deleteById(EquipmentId);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}

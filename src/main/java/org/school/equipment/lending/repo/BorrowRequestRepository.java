package org.school.equipment.lending.repo;

import org.school.equipment.lending.constants.BorrowStatus;
import org.school.equipment.lending.entity.BorrowRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface BorrowRequestRepository extends JpaRepository<BorrowRequest, Long> {

    @Query("SELECT COALESCE(SUM(br.quantity),0) FROM BorrowRequest br " +
            "WHERE br.equipment.id = :equipmentId " +
            "AND br.status IN :activeStatus " +
            "AND NOT (br.toDate <= :fromDate OR br.fromDate >= :toDate)")
    Long getCountOfOvelappingRq(@Param("equipmentId") Long equipmentId,
                                @Param("fromDate") LocalDateTime fromDate,
                                @Param("toDate") LocalDateTime toDate,
                                @Param("activeStatus") List<BorrowStatus> activeStatus);
}

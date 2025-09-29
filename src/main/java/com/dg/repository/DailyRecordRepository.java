package com.dg.repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.dg.model.DailyRecord;

@Repository
public interface DailyRecordRepository extends JpaRepository<DailyRecord, Long> {
	
	Page<DailyRecord> findPageByUserIdOrderByDateDesc(Pageable pageable, Long userId);
	
	List<DailyRecord> findByUserIdOrderByDateDesc(Long userId);
	
    List<DailyRecord> findTop5ByUserIdOrderByDateDesc(Long userId);
    
    Optional<DailyRecord> findByIdAndUserId(Long id, Long userId);
    
    List<DailyRecord> findAllByUserIdAndDateBetween(Long userId, LocalDate startDate, LocalDate endDate);
    
    @Query("SELECT dr FROM DailyRecord dr WHERE dr.user.id = :userId AND dr.date BETWEEN :startDate AND :endDate ORDER BY dr.date DESC")
    List<DailyRecord> findByUserIdAndDateBetween(@Param("userId") Long userId, 
                                                @Param("startDate") LocalDate startDate, 
                                                @Param("endDate") LocalDate endDate);
    
    @Query("SELECT SUM(dr.grossEarnings) FROM DailyRecord dr WHERE dr.user.id = :userId AND MONTH(dr.date) = :month AND YEAR(dr.date) = :year")
    BigDecimal sumEarningsByUserAndMonth(@Param("userId") Long userId, 
                                       @Param("month") int month, 
                                       @Param("year") int year);
    
    // Método removido pois os custos agora são calculados a partir das despesas relacionadas
    
    @Query("SELECT SUM(dr.hoursWorked) FROM DailyRecord dr WHERE dr.user.id = :userId AND MONTH(dr.date) = :month AND YEAR(dr.date) = :year")
    Double sumHoursByUserAndMonth(@Param("userId") Long userId, 
                                @Param("month") int month, 
                                @Param("year") int year);
    
    void deleteByIdAndUserId(Long id, Long userId);
}

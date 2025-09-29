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
import com.dg.model.ExpensesRecords;

@Repository
public interface ExpensesRecordRepository extends JpaRepository<ExpensesRecords, Long> {
	
	Page<ExpensesRecords> findPageByUserIdOrderByDateDesc(Pageable pageable, Long userId);
	
	List<ExpensesRecords> findByUserIdOrderByDateDesc(Long userId);
	
    List<ExpensesRecords> findTop5ByUserIdOrderByDateDesc(Long userId);
    
    Optional<ExpensesRecords> findByIdAndUserId(Long id, Long userId);
    
    List<ExpensesRecords> findAllByUserIdAndDateBetween(Long userId, LocalDate startDate, LocalDate endDate);
    
    List<ExpensesRecords> findByDailyRecordId(Long dailyRecordId);
    
    
    @Query("SELECT SUM(er.valueExpense) FROM ExpensesRecords er WHERE er.user.id = :userId AND MONTH(er.date) = :month AND YEAR(er.date) = :year")
    BigDecimal sumCostsByUserAndMonth(@Param("userId") Long userId, 
                                    @Param("month") int month, 
                                    @Param("year") int year);
    
    void deleteByIdAndUserId(Long id, Long userId);
}

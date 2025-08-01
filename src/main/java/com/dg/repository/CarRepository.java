package com.dg.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.dg.model.Car;

@Repository
public interface CarRepository extends JpaRepository<Car, Long> {
    List<Car> findByUserIdOrderByCreatedDateDesc(Long userId);
    
    Optional<Car> findByIdAndUserId(Long id, Long userId);
    
    @Query("SELECT COUNT(c) FROM Car c WHERE c.user.id = :userId")
    Long countByUserId(@Param("userId") Long userId);
    
    @Query(value = "SELECT qtd_cars(:userId)", nativeQuery = true)
    Long countByCarsId(@Param("userId") Long userId);
    
    void deleteByIdAndUserId(Long id, Long userId);
}

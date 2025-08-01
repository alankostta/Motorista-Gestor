package com.dg.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import com.dg.dto.DailyRecordDTO;
import com.dg.model.Car;
import com.dg.model.DailyRecord;
import com.dg.model.User;
import com.dg.repository.CarRepository;
import com.dg.repository.DailyRecordRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

@Service
@Transactional
public class DailyRecordService {
    
    @Autowired
    private DailyRecordRepository dailyRecordRepository;
    
    @Autowired
    private CarRepository carRepository;
    
    public Page<DailyRecord> findAllByUserPage(Pageable pageable,Long userId) {
        return dailyRecordRepository.findPageByUserIdOrderByDateDesc(pageable, userId);
    }
    public List<DailyRecord> findAllByUser(Long userId) {
        return dailyRecordRepository.findByUserIdOrderByDateDesc(userId);
    }

    public List<DailyRecord> findAllByUserTop5(Long userId) {
        return dailyRecordRepository.findTop5ByUserIdOrderByDateDesc(userId);
    }
    
    public DailyRecord save(DailyRecordDTO recordDto, User user) {
        Car car = carRepository.findByIdAndUserId(recordDto.getCarId(), user.getId())
            .orElseThrow(() -> new EntityNotFoundException("Carro não encontrado com id: " + recordDto.getCarId()));
        
        DailyRecord record = new DailyRecord();
        if (recordDto.getId() != null) {
            record = dailyRecordRepository.findByIdAndUserId(recordDto.getId(), user.getId())
                .orElseThrow(() -> new EntityNotFoundException("Registro não encontrado"));
        }
        
        // Mapeamento do DTO para a Entidade
        record.setDate(recordDto.getDate());
        record.setStartTime(recordDto.getStartTime());
        record.setEndTime(recordDto.getEndTime());
        record.setHoursWorked(recordDto.getHoursWorked());
        record.setKmDriven(recordDto.getKmDriven());
        record.setGrossEarnings(recordDto.getGrossEarnings());
        record.setFuelCost(recordDto.getFuelCost() != null ? recordDto.getFuelCost() : BigDecimal.ZERO);
        record.setFoodCost(recordDto.getFoodCost() != null ? recordDto.getFoodCost() : BigDecimal.ZERO);
        record.setMaintenanceCost(recordDto.getMaintenanceCost() != null ? recordDto.getMaintenanceCost() : BigDecimal.ZERO);
        record.setOtherCosts(recordDto.getOtherCosts() != null ? recordDto.getOtherCosts() : BigDecimal.ZERO);
        record.setOtherCostsDescription(recordDto.getOtherCostsDescription());
        record.setAppPlatform(recordDto.getAppPlatform());
        record.setNotes(recordDto.getNotes());
        record.setCar(car);
        record.setUser(user);

        return dailyRecordRepository.save(record);
    }
    
    public DailyRecord findByIdAndUser(Long id, Long userId) {
        return dailyRecordRepository.findByIdAndUserId(id, userId)
            .orElseThrow(() -> new EntityNotFoundException("Registro não encontrado"));
    }
    
    public void deleteByIdAndUser(Long id, Long userId) {
        dailyRecordRepository.deleteByIdAndUserId(id, userId);
    }
    
    public Map<String, Object> generateAnalytics(Long userId) {
        List<DailyRecord> records = findAllByUser(userId);
        
        BigDecimal totalEarnings = records.stream()
            .map(DailyRecord::getGrossEarnings)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
            
        Double totalHours = records.stream()
            .mapToDouble(DailyRecord::getHoursWorked)
            .sum();
            
        Double totalKm = records.stream()
            .mapToDouble(DailyRecord::getKmDriven)
            .sum();
            
        BigDecimal totalCosts = records.stream()
            .map(DailyRecord::getTotalCosts)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        Long totalCars = carRepository.countByCarsId(userId);
        
        Map<String, Object> analytics = new HashMap<>();
        analytics.put("totalEarnings", totalEarnings);
        analytics.put("totalHours", totalHours);
        analytics.put("totalKm", totalKm);
        analytics.put("totalCars", totalCars);
        analytics.put("totalCosts", totalCosts);
        analytics.put("netProfit", totalEarnings.subtract(totalCosts));
        analytics.put("earningsPerHour", totalHours > 0 ? totalEarnings.divide(BigDecimal.valueOf(totalHours), 2, RoundingMode.HALF_UP) : BigDecimal.ZERO);
        analytics.put("earningsPerKm", totalKm > 0 ? totalEarnings.divide(BigDecimal.valueOf(totalKm), 2, RoundingMode.HALF_UP) : BigDecimal.ZERO);
        analytics.put("costPerKm", totalKm > 0 ? totalCosts.divide(BigDecimal.valueOf(totalKm), 2, RoundingMode.HALF_UP) : BigDecimal.ZERO);
        analytics.put("totalRecords", records.size());
        
        return analytics;
    }
    
    public List<DailyRecord> findByUserAndDateRange(Long userId, LocalDate startDate, LocalDate endDate) {
        return dailyRecordRepository.findByUserIdAndDateBetween(userId, startDate, endDate);
    }
    
    public List<Map<String, Object>> findAllByUserIdAndRecordDate(Long id, LocalDate inicioMes, LocalDate fimMes) {
        
        List<DailyRecord> records = dailyRecordRepository.findAllByUserIdAndDateBetween(id, inicioMes, fimMes);

        return records.stream().map(record -> {
            Map<String, Object> map = new HashMap<>();
            map.put("date", record.getDate()); // certifique-se do nome correto do campo
            map.put("gross_earnings", record.getGrossEarnings());
            map.put("fuel_cost", record.getFuelCost());
            map.put("food_cost", record.getFoodCost());
            map.put("maintenance_cost", record.getMaintenanceCost());
            map.put("other_costs", record.getOtherCosts());
            return map;
        }).collect(Collectors.toList());
    }  
    public Map<String, Object> analytics(Long userId) {
        YearMonth now = YearMonth.now();
        LocalDate inicioMes = now.atDay(1);
        LocalDate fimMes = now.atEndOfMonth();

        List<DailyRecord> registros = dailyRecordRepository.findAllByUserIdAndDateBetween(userId, inicioMes, fimMes);

        BigDecimal totalHoras = BigDecimal.ZERO;
        BigDecimal totalKm = BigDecimal.ZERO;
        BigDecimal totalGanhos = BigDecimal.ZERO;
        BigDecimal totalCustos = BigDecimal.ZERO;

        BigDecimal custoCombustivel = BigDecimal.ZERO;
        BigDecimal custoRefeicao = BigDecimal.ZERO;
        BigDecimal custoManutencao = BigDecimal.ZERO;
        BigDecimal custoOutros = BigDecimal.ZERO;

        Map<String, BigDecimal> ganhosPorPlataforma = new HashMap<>();

        for (DailyRecord record : registros) {
            totalHoras = totalHoras.add(BigDecimal.valueOf(record.getHoursWorked()));
            totalKm = totalKm.add(BigDecimal.valueOf(record.getKmDriven()));
            totalGanhos = totalGanhos.add(record.getGrossEarnings());

            BigDecimal custosDoDia = record.getFuelCost()
                    .add(record.getFoodCost())
                    .add(record.getMaintenanceCost())
                    .add(record.getOtherCosts());

            totalCustos = totalCustos.add(custosDoDia);

            // Custos separados
            custoCombustivel = custoCombustivel.add(record.getFuelCost());
            custoRefeicao = custoRefeicao.add(record.getFoodCost());
            custoManutencao = custoManutencao.add(record.getMaintenanceCost());
            custoOutros = custoOutros.add(record.getOtherCosts());

            // Ganhos por plataforma
            String plataforma = record.getAppPlatform().getDisplayName(); // 'UBER', '99', etc
            ganhosPorPlataforma.merge(plataforma, record.getGrossEarnings(), BigDecimal::add);
        }

        int diasComRegistro = registros.size();

        Map<String, Object> result = new HashMap<>();
        result.put("ganhoHora", totalHoras.compareTo(BigDecimal.ZERO) > 0 ? totalGanhos.divide(totalHoras, 2, RoundingMode.HALF_UP) : BigDecimal.ZERO);
        result.put("ganhoKm", totalKm.compareTo(BigDecimal.ZERO) > 0 ? totalGanhos.divide(totalKm, 2, RoundingMode.HALF_UP) : BigDecimal.ZERO);
        result.put("custoKm", totalKm.compareTo(BigDecimal.ZERO) > 0 ? totalCustos.divide(totalKm, 2, RoundingMode.HALF_UP) : BigDecimal.ZERO);
        result.put("custoDia", diasComRegistro > 0 ? totalCustos.divide(BigDecimal.valueOf(diasComRegistro), 2, RoundingMode.HALF_UP) : BigDecimal.ZERO);

        result.put("custos", List.of(
            custoCombustivel.setScale(2, RoundingMode.HALF_UP),
            custoRefeicao.setScale(2, RoundingMode.HALF_UP),
            custoManutencao.setScale(2, RoundingMode.HALF_UP),
            custoOutros.setScale(2, RoundingMode.HALF_UP)
        ));

        result.put("ganhosPlataforma", ganhosPorPlataforma);

        return result;
    }
}





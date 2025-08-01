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
import com.dg.dto.ExpensesRecordDTO;
import com.dg.model.Car;
import com.dg.model.ExpensesRecords;
import com.dg.model.User;
import com.dg.repository.CarRepository;
import com.dg.repository.ExpensesRecordRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

@Service
@Transactional
public class ExpenseService {
    
    @Autowired
    private ExpensesRecordRepository expenseRecordRepository;
    
    @Autowired
    private CarRepository carRepository;
    
    public Page<ExpensesRecords> findAllByUserPage(Pageable pageable,Long userId) {
        return expenseRecordRepository.findPageByUserIdOrderByDateDesc(pageable, userId);
    }
    public List<ExpensesRecords> findAllByUser(Long userId) {
        return expenseRecordRepository.findByUserIdOrderByDateDesc(userId);
    }

    public List<ExpensesRecords> findAllByUserTop5(Long userId) {
        return expenseRecordRepository.findTop5ByUserIdOrderByDateDesc(userId);
    }
    
    public ExpensesRecords save(ExpensesRecordDTO expensedDto, User user) {
        Car car = carRepository.findByIdAndUserId(expensedDto.getCarId(), user.getId())
            .orElseThrow(() -> new EntityNotFoundException("Carro não encontrado com id: " + expensedDto.getCarId()));
        
        ExpensesRecords expense = new ExpensesRecords();
        if (expensedDto.getId() != null) {
        	expense = expenseRecordRepository.findByIdAndUserId(expensedDto.getId(), user.getId())
                .orElseThrow(() -> new EntityNotFoundException("Registro não encontrado"));
        }
        
        // Mapeamento do DTO para a Entidade
        expense.setDate(expensedDto.getDate());
        expense.setFuelCost(expensedDto.getFuelCost() != null ? expensedDto.getFuelCost() : BigDecimal.ZERO);
        expense.setFoodCost(expensedDto.getFoodCost() != null ? expensedDto.getFoodCost() : BigDecimal.ZERO);
        expense.setMaintenanceCost(expensedDto.getMaintenanceCost() != null ? expensedDto.getMaintenanceCost() : BigDecimal.ZERO);
        expense.setOtherCosts(expensedDto.getOtherCosts() != null ? expensedDto.getOtherCosts() : BigDecimal.ZERO);
        expense.setOtherCostsDescription(expensedDto.getOtherCostsDescription());
        expense.setCar(car);
        expense.setUser(user);

        return expenseRecordRepository.save(expense);
    }
    
    public ExpensesRecords findByIdAndUser(Long id, Long userId) {
        return expenseRecordRepository.findByIdAndUserId(id, userId)
            .orElseThrow(() -> new EntityNotFoundException("Registro não encontrado"));
    }
    
    public void deleteByIdAndUser(Long id, Long userId) {
    	expenseRecordRepository.deleteByIdAndUserId(id, userId);
    }
    
    public Map<String, Object> generateAnalytics(Long userId) {
        List<ExpensesRecords> expenses = findAllByUser(userId);
               
        BigDecimal totalCosts = expenses.stream()
            .map(ExpensesRecords::getTotalCosts)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        
        Map<String, Object> analytics = new HashMap<>();
        
        analytics.put("totalCosts", totalCosts);
      
        analytics.put("totalExpense", expenses.size());
        
        return analytics;
    }
    
    public List<Map<String, Object>> findAllByUserIdAndRecordDate(Long id, LocalDate inicioMes, LocalDate fimMes) {
        
        List<ExpensesRecords> expenses = expenseRecordRepository.findAllByUserIdAndDateBetween(id, inicioMes, fimMes);

        return expenses.stream().map(expense -> {
            Map<String, Object> map = new HashMap<>();
            map.put("date", expense.getDate()); // certifique-se do nome correto do campo
            map.put("fuel_cost", expense.getFuelCost());
            map.put("food_cost", expense.getFoodCost());
            map.put("maintenance_cost", expense.getMaintenanceCost());
            map.put("other_costs", expense.getOtherCosts());
            return map;
        }).collect(Collectors.toList());
    }  
    public Map<String, Object> analytics(Long userId) {
        YearMonth now = YearMonth.now();
        LocalDate inicioMes = now.atDay(1);
        LocalDate fimMes = now.atEndOfMonth();

        List<ExpensesRecords> registros = expenseRecordRepository.findAllByUserIdAndDateBetween(userId, inicioMes, fimMes);

        BigDecimal totalCustos = BigDecimal.ZERO;
        BigDecimal custoCombustivel = BigDecimal.ZERO;
        BigDecimal custoRefeicao = BigDecimal.ZERO;
        BigDecimal custoManutencao = BigDecimal.ZERO;
        BigDecimal custoOutros = BigDecimal.ZERO;

        for (ExpensesRecords expense : registros) {
          

            BigDecimal custosDoDia = expense.getFuelCost()
                    .add(expense.getFoodCost())
                    .add(expense.getMaintenanceCost())
                    .add(expense.getOtherCosts());

            totalCustos = totalCustos.add(custosDoDia);
            // Custos separados
            custoCombustivel = custoCombustivel.add(expense.getFuelCost());
            custoRefeicao = custoRefeicao.add(expense.getFoodCost());
            custoManutencao = custoManutencao.add(expense.getMaintenanceCost());
            custoOutros = custoOutros.add(expense.getOtherCosts());

        }

        int diasComRegistro = registros.size();

        Map<String, Object> result = new HashMap<>();
        result.put("custoDia", diasComRegistro > 0 ? totalCustos.divide(BigDecimal.valueOf(diasComRegistro), 2, RoundingMode.HALF_UP) : BigDecimal.ZERO);

        result.put("custos", List.of(
            custoCombustivel.setScale(2, RoundingMode.HALF_UP),
            custoRefeicao.setScale(2, RoundingMode.HALF_UP),
            custoManutencao.setScale(2, RoundingMode.HALF_UP),
            custoOutros.setScale(2, RoundingMode.HALF_UP)
        ));

        return result;
    }
}





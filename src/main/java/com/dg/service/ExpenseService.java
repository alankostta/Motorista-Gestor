package com.dg.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
import com.dg.model.ExpensesRecords;
import com.dg.model.User;
import com.dg.repository.ExpensesRecordRepository;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

@Service
@Transactional
public class ExpenseService {
    
    @Autowired
    private ExpensesRecordRepository expenseRecordRepository;
    
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
        
        ExpensesRecords expense = new ExpensesRecords();
        if (expensedDto.getId() != null) {
        	expense = expenseRecordRepository.findByIdAndUserId(expensedDto.getId(), user.getId())
                .orElseThrow(() -> new EntityNotFoundException("Registro não encontrado"));
        }
        
        // Mapeamento do DTO para a Entidade
        expense.setDate(expensedDto.getDate());
        expense.setValueExpense(expensedDto.getValueExpense() != null ? expensedDto.getValueExpense() : BigDecimal.ZERO);
        expense.setDescriptionExpense(expensedDto.getDescriptionExpense());
        expense.setExpenseCategory(expensedDto.getExpenseCategory());
        expense.setUpdatedDate(LocalDateTime.now());
        
        // Apenas define a data de criação se for um novo registro
        if (expensedDto.getId() == null) {
            expense.setCreatedDate(LocalDateTime.now());
        }
        
        // Associar ao registro diário se o ID estiver presente
        if (expensedDto.getDailyRecordId() != null) {
            // A associação com DailyRecord será feita pelo controlador
            // usando o método setDailyRecord
        }
        
        expense.setUser(user);

        return expenseRecordRepository.save(expense);
    }
    
    /**
     * Salva uma entidade ExpensesRecords existente
     * 
     * @param expense A entidade a ser salva
     * @return A entidade salva
     */
    public ExpensesRecords save(ExpensesRecords expense) {
        return expenseRecordRepository.save(expense);
    }
    
    public ExpensesRecords findByIdAndUser(Long id, Long userId) {
        return expenseRecordRepository.findByIdAndUserId(id, userId)
            .orElseThrow(() -> new EntityNotFoundException("Registro não encontrado"));
    }
    
    public void deleteByIdAndUser(Long id, Long userId) {
    	expenseRecordRepository.deleteByIdAndUserId(id, userId);
    }
    
    public Map<String, Object> despeasTotais(Long userId) {
        List<ExpensesRecords> expenses = findAllByUser(userId);
               
        BigDecimal totalCosts = expenses.stream()
            .map(ExpensesRecords::getValueExpense)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        
        Map<String, Object> despesasTotais = new HashMap<>();
        
        despesasTotais.put("totalCosts", totalCosts);
        despesasTotais.put("totalExpense", expenses.size());
        
        return despesasTotais;
    }
    
    public List<Map<String, Object>> findAllByUserIdAndRecordDate(Long id, LocalDate inicioMes, LocalDate fimMes) {
        
        List<ExpensesRecords> expenses = expenseRecordRepository.findAllByUserIdAndDateBetween(id, inicioMes, fimMes);

        return expenses.stream().map(expense -> {
            Map<String, Object> map = new HashMap<>();
            map.put("date", expense.getDate()); // certifique-se do nome correto do campo
            map.put("valueExpense", expense.getValueExpense());
           
            return map;
        }).collect(Collectors.toList());
    }  
    public Map<String, Object> analytics(Long userId) {
        YearMonth now = YearMonth.now();
        LocalDate inicioMes = now.atDay(1);
        LocalDate fimMes = now.atEndOfMonth();

        List<ExpensesRecords> registros = expenseRecordRepository.findAllByUserIdAndDateBetween(userId, inicioMes, fimMes);

        BigDecimal totalCustos = BigDecimal.ZERO;

        for (ExpensesRecords expense : registros) {
          
            BigDecimal custosDoDia = expense.getValueExpense();
            totalCustos = totalCustos.add(custosDoDia);
         
        }

        int diasComRegistro = registros.size();

        Map<String, Object> result = new HashMap<>();
        result.put("custoDia", diasComRegistro > 0 ? totalCustos.divide(BigDecimal.valueOf(diasComRegistro), 2, RoundingMode.HALF_UP) : BigDecimal.ZERO);

        return result;
    }
    
    /**
     * Calcula a soma das despesas de um usuário em um mês específico
     * 
     * @param userId ID do usuário
     * @param month Mês (1-12)
     * @param year Ano
     * @return Total das despesas no mês
     */
    public BigDecimal sumCostsByUserAndMonth(Long userId, int month, int year) {
        BigDecimal total = expenseRecordRepository.sumCostsByUserAndMonth(userId, month, year);
        return total != null ? total : BigDecimal.ZERO;
    }
}

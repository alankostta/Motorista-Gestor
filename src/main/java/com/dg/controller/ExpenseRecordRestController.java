package com.dg.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dg.dto.ExpensesRecordDTO;
import com.dg.model.ExpensesRecords;
import com.dg.model.User;
import com.dg.service.ExpenseService;
import com.dg.service.UserService;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;

/**
 * Controlador REST para operações CRUD de despesas
 */
@RestController
@RequestMapping("/api/expenses")
public class ExpenseRecordRestController {

    @Autowired
    private ExpenseService expenseService;
    
    @Autowired
    private UserService userService;
    
    /**
     * Lista todas as despesas do usuário autenticado
     * 
     * @param userDetails Detalhes do usuário autenticado
     * @return Lista de despesas
     */
    @GetMapping
    public ResponseEntity<List<ExpensesRecordDTO>> getAllExpenses(@AuthenticationPrincipal UserDetails userDetails) {
        User currentUser = userService.findByEmail(userDetails.getUsername());
        List<ExpensesRecords> expenses = expenseService.findAllByUser(currentUser.getId());
        
        List<ExpensesRecordDTO> expenseDTOs = expenses.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(expenseDTOs);
    }
    
    /**
     * Busca uma despesa específica pelo ID
     * 
     * @param id ID da despesa
     * @param userDetails Detalhes do usuário autenticado
     * @return Despesa encontrada
     */
    @GetMapping("/{id}")
    public ResponseEntity<ExpensesRecordDTO> getExpenseById(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        try {
            User currentUser = userService.findByEmail(userDetails.getUsername());
            ExpensesRecords expense = expenseService.findByIdAndUser(id, currentUser.getId());
            return ResponseEntity.ok(convertToDTO(expense));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * Cria uma nova despesa
     * 
     * @param expenseDTO DTO com os dados da despesa
     * @param userDetails Detalhes do usuário autenticado
     * @return Despesa criada
     */
    @PostMapping
    public ResponseEntity<ExpensesRecordDTO> createExpense(
            @Valid @RequestBody ExpensesRecordDTO expenseDTO,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        User currentUser = userService.findByEmail(userDetails.getUsername());
        ExpensesRecords savedExpense = expenseService.save(expenseDTO, currentUser);
        
        return new ResponseEntity<>(convertToDTO(savedExpense), HttpStatus.CREATED);
    }
    
    /**
     * Atualiza uma despesa existente
     * 
     * @param id ID da despesa
     * @param expenseDTO DTO com os dados atualizados
     * @param userDetails Detalhes do usuário autenticado
     * @return Despesa atualizada
     */
    @PutMapping("/{id}")
    public ResponseEntity<ExpensesRecordDTO> updateExpense(
            @PathVariable Long id,
            @Valid @RequestBody ExpensesRecordDTO expenseDTO,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        try {
            User currentUser = userService.findByEmail(userDetails.getUsername());
            
            // Verifica se a despesa existe e pertence ao usuário
            expenseService.findByIdAndUser(id, currentUser.getId());
            
            // Define o ID no DTO para atualização
            expenseDTO.setId(id);
            
            ExpensesRecords updatedExpense = expenseService.save(expenseDTO, currentUser);
            return ResponseEntity.ok(convertToDTO(updatedExpense));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * Remove uma despesa
     * 
     * @param id ID da despesa
     * @param userDetails Detalhes do usuário autenticado
     * @return Resposta sem conteúdo
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteExpense(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        try {
            User currentUser = userService.findByEmail(userDetails.getUsername());
            
            // Verifica se a despesa existe e pertence ao usuário
            expenseService.findByIdAndUser(id, currentUser.getId());
            
            expenseService.deleteByIdAndUser(id, currentUser.getId());
            return ResponseEntity.noContent().build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * Converte uma entidade ExpensesRecords para DTO
     * 
     * @param expense Entidade a ser convertida
     * @return DTO correspondente
     */
    private ExpensesRecordDTO convertToDTO(ExpensesRecords expense) {
        ExpensesRecordDTO dto = new ExpensesRecordDTO();
        dto.setId(expense.getId());
        dto.setDate(expense.getDate());
        dto.setValueExpense(expense.getValueExpense());
        dto.setDescriptionExpense(expense.getDescriptionExpense());
        dto.setExpenseCategory(expense.getExpenseCategory());
        return dto;
    }
}
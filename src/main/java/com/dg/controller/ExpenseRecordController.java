package com.dg.controller;

import java.math.BigDecimal;
import java.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.dg.dto.ExpensesRecordDTO;
import com.dg.model.DailyRecord;
import com.dg.model.ExpenseCategory;
import com.dg.model.ExpensesRecords;
import com.dg.model.User;
import com.dg.service.DailyRecordService;
import com.dg.service.ExpenseService;
import com.dg.service.UserService;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/expenses")
public class ExpenseRecordController {
    
    @Autowired
    private ExpenseService expenseService;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private DailyRecordService dailyRecordService;

    @GetMapping
    public String listExpense(Model model, @AuthenticationPrincipal UserDetails userDetails,  
    		@RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "5") int size,
            @RequestParam(value = "sort", defaultValue = "id") String sort,
            @RequestParam(value = "direction", defaultValue = "asc") String direction) {
    	
    	 Sort sortOrder = direction.equals("asc") ? Sort.by(sort).ascending() : Sort.by(sort).descending();
    	 Pageable pageable = PageRequest.of(page < 0 ? 0 : page, size, sortOrder);
    	
        User currentUser = userService.findByEmail(userDetails.getUsername());
        
        Page<ExpensesRecords> expenses = expenseService.findAllByUserPage(pageable, currentUser.getId());
        
        if (page >= expenses.getTotalPages() && expenses.getTotalPages() > 0) {
            pageable = PageRequest.of(expenses.getTotalPages() - 1, size, sortOrder);
            expenses = expenseService.findAllByUserPage(pageable, currentUser.getId());
        }
        
        model.addAttribute("expenses", expenses)
        	 .addAttribute("activePage", "expenses")
        	 .addAttribute("controleGerais", expenses.getContent())
        	 .addAttribute("currentPage", expenses.getNumber())
        	 .addAttribute("totalPages", expenses.getTotalPages())
        	 .addAttribute("totalElements", expenses.getTotalElements())
        	 .addAttribute("sort", sort)
        	 .addAttribute("direction", direction);
        
        return "expense";
    }
    
    @GetMapping("/expenses-new")
    public String newExpenseForm(Model model, 
                               @AuthenticationPrincipal UserDetails userDetails,
                               @RequestParam(value = "dailyRecordId", required = false) Long dailyRecordId) {

        ExpensesRecordDTO expenseDto = new ExpensesRecordDTO();
        
        // Se um ID de registro diário foi fornecido, associe-o ao DTO
        if (dailyRecordId != null) {
            expenseDto.setDailyRecordId(dailyRecordId);
            // Definir a data como a data do registro diário, se disponível
            if (dailyRecordService != null) {
                try {
                    User currentUser = userService.findByEmail(userDetails.getUsername());
                    DailyRecord dailyRecord = dailyRecordService.findByIdAndUser(dailyRecordId, currentUser.getId());
                    expenseDto.setDate(dailyRecord.getDate());
                } catch (Exception e) {
                    // Se não conseguir obter o registro diário, use a data atual
                    expenseDto.setDate(LocalDate.now());
                }
            } else {
                expenseDto.setDate(LocalDate.now());
            }
        } else {
            expenseDto.setDate(LocalDate.now());
        }

        model.addAttribute("expenseDto", expenseDto)
        	 .addAttribute("categorias", ExpenseCategory.values())
        	 .addAttribute("activePage", "expenses");
        
        return "expense-form";
    }
    
    @GetMapping("/edit/{id}")
    public String editExpenseForm(@PathVariable Long id, Model model, @AuthenticationPrincipal UserDetails userDetails) {
        User currentUser = userService.findByEmail(userDetails.getUsername());
        ExpensesRecords expense = expenseService.findByIdAndUser(id, currentUser.getId());
        
        ExpensesRecordDTO expenseDto = new ExpensesRecordDTO();
        expenseDto.setId(expense.getId());
        expenseDto.setDate(expense.getDate());
        expenseDto.setValueExpense(expense.getValueExpense());
        expenseDto.setExpenseCategory(expense.getExpenseCategory());
        expenseDto.setDescriptionExpense(expense.getDescriptionExpense());
        
        // Se a despesa estiver associada a um registro diário, inclua o ID no DTO
        if (expense.getDailyRecord() != null) {
            expenseDto.setDailyRecordId(expense.getDailyRecord().getId());
        }
        
        model.addAttribute("expenseDto", expenseDto)
        	 .addAttribute("categorias", ExpenseCategory.values())
        	 .addAttribute("activePage", "expense");
        
        return "expense-form";
    }
    
    @PostMapping
    public String saveExpense(@Valid @ModelAttribute("expenseDto") ExpensesRecordDTO expenseDto, 
                           BindingResult result, 
                           Model model,
                           @AuthenticationPrincipal UserDetails userDetails,
                           RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model
            	 .addAttribute("categorias", ExpenseCategory.values())
            	 .addAttribute("activePage", "expense");
            
            return "expense-form";
        }
        
        User currentUser = userService.findByEmail(userDetails.getUsername());
        ExpensesRecords savedExpense = expenseService.save(expenseDto, currentUser);
        
        // Se houver um registro diário associado, vincule a despesa a ele
        if (expenseDto.getDailyRecordId() != null) {
            try {
                DailyRecord dailyRecord = dailyRecordService.findByIdAndUser(expenseDto.getDailyRecordId(), currentUser.getId());
                savedExpense.setDailyRecord(dailyRecord);
                // Salvar novamente para persistir a associação
                expenseService.save(savedExpense);
                
                // Redirecionar para a página de edição do registro diário
                redirectAttributes.addFlashAttribute("successMessage", "Despesa adicionada com sucesso!");
                return "redirect:/records/edit/" + expenseDto.getDailyRecordId();
            } catch (Exception e) {
                // Se não conseguir encontrar o registro diário, continue normalmente
            }
        }
        
        redirectAttributes.addFlashAttribute("successMessage", "Registro salvo com sucesso!");
        
        return "redirect:/expenses";
    }
    
    @PostMapping("/delete/{id}")
    public String deleteExpese(@PathVariable Long id, 
                             @AuthenticationPrincipal UserDetails userDetails,
                             RedirectAttributes redirectAttributes) {
        User currentUser = userService.findByEmail(userDetails.getUsername());
        
        // Verificar se a despesa está associada a um registro diário antes de excluí-la
        ExpensesRecords expense = expenseService.findByIdAndUser(id, currentUser.getId());
        Long dailyRecordId = null;
        if (expense.getDailyRecord() != null) {
            dailyRecordId = expense.getDailyRecord().getId();
        }
        
        // Excluir a despesa
        expenseService.deleteByIdAndUser(id, currentUser.getId());
        
        redirectAttributes.addFlashAttribute("successMessage", "Registro excluído com sucesso!");
        
        // Se a despesa estava associada a um registro diário, redirecionar para a página de edição do registro
        if (dailyRecordId != null) {
            return "redirect:/records/edit/" + dailyRecordId;
        }
        
        return "redirect:/expenses";
    }
    
    @GetMapping("/despesas-totais")
    public ResponseEntity<BigDecimal> calcularTotalDespesasDoMes(@AuthenticationPrincipal UserDetails userDetails) {
        LocalDate hoje = LocalDate.now();
        int mes = hoje.getMonthValue();
        int ano = hoje.getYear();
        
        User currentUser = userService.findByEmail(userDetails.getUsername());
        BigDecimal total = expenseService.sumCostsByUserAndMonth(currentUser.getId(), mes, ano);

        // Evita retornar null
        if (total == null) {
            total = BigDecimal.ZERO;
        }

        return ResponseEntity.ok(total);
    }
}
package com.dg.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
import com.dg.model.Car;
import com.dg.model.ExpenseCategory;
import com.dg.model.ExpensesRecords;
import com.dg.model.User;
import com.dg.service.CarService;
import com.dg.service.ExpenseService;
import com.dg.service.UserService;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/expenses")
public class ExpenseRecordController {
    
    @Autowired
    private ExpenseService expenseService;
    
    @Autowired
    private CarService carService;
    
    @Autowired
    private UserService userService;

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
    public String newExpenseForm(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        User currentUser = userService.findByEmail(userDetails.getUsername());
        List<Car> cars = carService.findAllByUser(currentUser.getId());
        
        model.addAttribute("expenseDto", new ExpensesRecordDTO())
        	 .addAttribute("cars", cars)
        	 .addAttribute("categorias", ExpenseCategory.values())
        	 .addAttribute("activePage", "expenses");
        
        return "expense-form";
    }
    
    @GetMapping("/edit/{id}")
    public String editExpenseForm(@PathVariable Long id, Model model, @AuthenticationPrincipal UserDetails userDetails) {
        User currentUser = userService.findByEmail(userDetails.getUsername());
        ExpensesRecords expense = expenseService.findByIdAndUser(id, currentUser.getId());
        List<Car> cars = carService.findAllByUser(currentUser.getId());
        
        ExpensesRecordDTO expenseDto = new ExpensesRecordDTO();
        expenseDto.setId(expense.getId());
        expenseDto.setDate(expense.getDate());
        expenseDto.setFuelCost(expense.getFuelCost());
        expenseDto.setFoodCost(expense.getFoodCost());
        expenseDto.setMaintenanceCost(expense.getMaintenanceCost());
        expenseDto.setOtherCosts(expense.getOtherCosts());
        expenseDto.setOtherCostsDescription(expense.getOtherCostsDescription());
        expenseDto.setCarId(expense.getCar().getId());
        
        model.addAttribute("expenseDto", expenseDto)
        	 .addAttribute("cars", cars)
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
            User currentUser = userService.findByEmail(userDetails.getUsername());
            List<Car> cars = carService.findAllByUser(currentUser.getId());
            model.addAttribute("cars", cars)
            	 .addAttribute("categorias", ExpenseCategory.values())
            	 .addAttribute("activePage", "expense");
            
            return "expense-form";
        }
        
        User currentUser = userService.findByEmail(userDetails.getUsername());
        expenseService.save(expenseDto, currentUser);
        
        redirectAttributes.addFlashAttribute("successMessage", "Registro salvo com sucesso!");
        
        return "redirect:/expenses";
    }
    
    @PostMapping("/delete/{id}")
    public String deleteExpese(@PathVariable Long id, 
                             @AuthenticationPrincipal UserDetails userDetails,
                             RedirectAttributes redirectAttributes) {
        User currentUser = userService.findByEmail(userDetails.getUsername());
        expenseService.deleteByIdAndUser(id, currentUser.getId());
        
        redirectAttributes.addFlashAttribute("successMessage", "Registro excluído com sucesso!");
        
        return "redirect:/expenses";
    }
}

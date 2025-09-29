package com.dg.controller;

import java.util.List;
import java.util.Map;

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

import com.dg.dto.DailyRecordDTO;
import com.dg.model.AppPlatform;
import com.dg.model.Car;
import com.dg.model.DailyRecord;
import com.dg.model.ExpensesRecords;
import com.dg.model.User;
import com.dg.service.CarService;
import com.dg.service.DailyRecordService;
import com.dg.service.UserService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/records")
public class DailyRecordController {
    
    @Autowired
    private DailyRecordService dailyRecordService;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private CarService carService;

    @GetMapping
    public String listRecords(Model model, @AuthenticationPrincipal UserDetails userDetails,  
    		@RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "5") int size,
            @RequestParam(value = "sort", defaultValue = "id") String sort,
            @RequestParam(value = "direction", defaultValue = "asc") String direction) {
    	
    	 Sort sortOrder = direction.equals("asc") ? Sort.by(sort).ascending() : Sort.by(sort).descending();
    	 Pageable pageable = PageRequest.of(page < 0 ? 0 : page, size, sortOrder);
    	
        User currentUser = userService.findByEmail(userDetails.getUsername());
        
        Page<DailyRecord> records = dailyRecordService.findAllByUserPage(pageable, currentUser.getId());
        
        if (page >= records.getTotalPages() && records.getTotalPages() > 0) {
            pageable = PageRequest.of(records.getTotalPages() - 1, size, sortOrder);
            records = dailyRecordService.findAllByUserPage(pageable, currentUser.getId());
        }
        
        // Dados para os cards de estatísticas
        Map<String, Object> analytics = dailyRecordService.generateAnalytics(currentUser.getId());
        long totalCars = carService.countByUser(currentUser.getId());
        
        model.addAttribute("records", records)
        	 .addAttribute("activePage", "records")
        	 .addAttribute("controleGerais", records.getContent())
        	 .addAttribute("currentPage", records.getNumber())
        	 .addAttribute("totalPages", records.getTotalPages())
        	 .addAttribute("totalElements", records.getTotalElements())
        	 .addAttribute("sort", sort)
        	 .addAttribute("direction", direction)
        	 .addAttribute("analytics", analytics)
        	 .addAttribute("totalCars", totalCars);
        
        return "records";
    }
    
    @GetMapping("/new")
    public String newRecordForm(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        User currentUser = userService.findByEmail(userDetails.getUsername());
        List<Car> cars = carService.findAllByUser(currentUser.getId());
        
        model.addAttribute("recordDto", new DailyRecordDTO())
        	 .addAttribute("cars", cars)
        	 .addAttribute("platforms", AppPlatform.values())
        	 .addAttribute("activePage", "records");
        
        return "record-form";
    }
    
    @GetMapping("/edit/{id}")
    public String editRecordForm(@PathVariable Long id, Model model, @AuthenticationPrincipal UserDetails userDetails) {
        User currentUser = userService.findByEmail(userDetails.getUsername());
        DailyRecord record = dailyRecordService.findByIdAndUser(id, currentUser.getId());
        List<Car> cars = carService.findAllByUser(currentUser.getId());
        
        DailyRecordDTO recordDto = new DailyRecordDTO();
        recordDto.setId(record.getId());
        recordDto.setDate(record.getDate());
        recordDto.setStartTime(record.getStartTime());
        recordDto.setEndTime(record.getEndTime());
        recordDto.setHoursWorked(record.getHoursWorked());
        recordDto.setKmDriven(record.getKmDriven());
        recordDto.setGrossEarnings(record.getGrossEarnings());
        recordDto.setAppPlatform(record.getAppPlatform());
        recordDto.setNotes(record.getNotes());
        recordDto.setCarId(record.getCar().getId());
        
        // Obter as despesas associadas ao registro diário
        List<ExpensesRecords> expenses = dailyRecordService.getExpensesByDailyRecord(id);
        model.addAttribute("expenses", expenses);
        
        model.addAttribute("recordDto", recordDto)
        	 .addAttribute("cars", cars)
        	 .addAttribute("platforms", AppPlatform.values())
        	 .addAttribute("activePage", "records");
        
        return "record-form";
    }
    
    @PostMapping
    public String saveRecord(@Valid @ModelAttribute("recordDto") DailyRecordDTO recordDto, 
                           BindingResult result, 
                           Model model,
                           @AuthenticationPrincipal UserDetails userDetails,
                           RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            User currentUser = userService.findByEmail(userDetails.getUsername());
            List<Car> cars = carService.findAllByUser(currentUser.getId());
            model.addAttribute("cars", cars)
            	 .addAttribute("platforms", AppPlatform.values())
            	 .addAttribute("activePage", "records");
            
            return "record-form";
        }
        
        User currentUser = userService.findByEmail(userDetails.getUsername());
        dailyRecordService.save(recordDto, currentUser);
        
        redirectAttributes.addFlashAttribute("successMessage", "Registro salvo com sucesso!");
        
        return "redirect:/records";
    }
    
    @PostMapping("/delete/{id}")
    public String deleteRecord(@PathVariable Long id, 
                             @AuthenticationPrincipal UserDetails userDetails,
                             RedirectAttributes redirectAttributes) {
        User currentUser = userService.findByEmail(userDetails.getUsername());
        dailyRecordService.deleteByIdAndUser(id, currentUser.getId());
        
        redirectAttributes.addFlashAttribute("successMessage", "Registro excluído com sucesso!");
        
        return "redirect:/records";
    }
}

package com.dg.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.dg.model.DailyRecord;
import com.dg.model.User;
import com.dg.service.CarService;
import com.dg.service.DailyRecordService;
import com.dg.service.UserService;

@Controller
public class DashboardController {
    
    @Autowired
    private DailyRecordService dailyRecordService;
    
    @Autowired
    private CarService carService;
    
    @Autowired
    private UserService userService;

    @GetMapping("/dashboard")
    public String dashboard(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        // Busca o usuário atual
        User currentUser = userService.findByEmail(userDetails.getUsername());

        // Dados para o dashboard
        Map<String, Object> analytics = dailyRecordService.generateAnalytics(currentUser.getId());
        List<DailyRecord> recentRecords = dailyRecordService.findAllByUserTop5(currentUser.getId());
        long totalCars = carService.countByUser(currentUser.getId());

        // Adiciona atributos ao modelo de forma fluida
        model.addAttribute("currentUser", currentUser)
             .addAttribute("analytics", analytics)
             .addAttribute("recentRecords", recentRecords)
             .addAttribute("totalCars", totalCars)
             .addAttribute("activePage", "dashboard");

        return "dashboard";
    }
    
    @GetMapping("/grafico")
    @ResponseBody
    public List<Map<String, Object>> getMonthlyRecords(@AuthenticationPrincipal UserDetails userDetails) {
        User currentUser = userService.findByEmail(userDetails.getUsername());
        LocalDate inicioMes = LocalDate.now().withDayOfMonth(1);
        LocalDate fimMes = LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth());

        List<Map<String, Object>> records = dailyRecordService.findAllByUserIdAndRecordDate(currentUser.getId(), inicioMes, fimMes);
        return records;
    }

    @GetMapping("/analytics")  
    public String getAnalytics(@AuthenticationPrincipal UserDetails userDetails, Model model) {
    	
        User currentUser = userService.findByEmail(userDetails.getUsername());
        
        model.addAttribute("currentUser", currentUser)
        .addAttribute("activePage", "analytics");
        
        return "analytics";
    } 
    
    @GetMapping("/analytics-data")
    public ResponseEntity<Map<String, Object>> getAnalyticsData(@AuthenticationPrincipal UserDetails userDetails) {
        User currentUser = userService.findByEmail(userDetails.getUsername());
        Map<String, Object> analytics = dailyRecordService.analytics(currentUser.getId());
        return ResponseEntity.ok(analytics);
    }
}

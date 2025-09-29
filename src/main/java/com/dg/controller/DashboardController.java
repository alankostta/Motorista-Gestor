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
    
    @GetMapping("/km-by-month")
    public ResponseEntity<Map<String, Object>> getKmByMonth(@AuthenticationPrincipal UserDetails userDetails) {
        User currentUser = userService.findByEmail(userDetails.getUsername());
        Map<String, Object> kmData = dailyRecordService.getKmByMonth(currentUser.getId());
        return ResponseEntity.ok(kmData);
    }
    
    @GetMapping("/km-chart-data")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getKmChartData(@AuthenticationPrincipal UserDetails userDetails) {
        System.out.println("DEBUG: Endpoint /km-chart-data chamado");
        System.out.println("DEBUG: UserDetails: " + (userDetails != null ? userDetails.getUsername() : "null"));
        
        User currentUser = userService.findByEmail(userDetails.getUsername());
        System.out.println("DEBUG: Usuário encontrado: " + (currentUser != null ? currentUser.getId() : "null"));
        
        Map<String, Object> kmChartData = dailyRecordService.getKmChartData(currentUser.getId());
        System.out.println("DEBUG: Dados retornados: " + kmChartData);
        
        return ResponseEntity.ok(kmChartData);
    }
}

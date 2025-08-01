package com.dg.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.dg.dto.CarDTO;
import com.dg.model.Car;
import com.dg.model.FuelType;
import com.dg.model.User;
import com.dg.service.CarService;
import com.dg.service.UserService;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/cars")
public class CarController {
    
    @Autowired
    private CarService carService;
    
    @Autowired
    private UserService userService;

    @GetMapping
    public String listCars(Model model, @AuthenticationPrincipal UserDetails userDetails, RedirectAttributes redirectAttributes) {
        User currentUser = userService.findByEmail(userDetails.getUsername());
        List<Car> cars = carService.findAllByUser(currentUser.getId());
        CarDTO carDto = new CarDTO();
        
        if (cars.isEmpty()) {
            redirectAttributes.addFlashAttribute("infoMessage", "Você ainda não possui carros cadastrados. Cadastre um carro para começar.");
            return "redirect:/cars/new";
        }
        model.addAttribute("currentUser", currentUser)
         	 .addAttribute("carDto", carDto)
         	 .addAttribute("fuelTypes", FuelType.values())
        	 .addAttribute("cars", cars)
             .addAttribute("activePage", "cars");
        
        return "car-form";
    }
    
    @GetMapping("/new")
    public String newCarForm(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        User currentUser = userService.findByEmail(userDetails.getUsername());
        model.addAttribute("currentUser", currentUser)
             .addAttribute("carDto", new CarDTO())
             .addAttribute("fuelTypes", FuelType.values())
             .addAttribute("activePage", "cars");
        return "car-form";
    }
    
    @GetMapping("/edit/{id}")
    public String editCarForm(@PathVariable Long id, Model model, @AuthenticationPrincipal UserDetails userDetails) {
        User currentUser = userService.findByEmail(userDetails.getUsername());
        Car car = carService.findByIdAndUser(id, currentUser.getId());
        
        CarDTO carDto = new CarDTO();
        carDto.setId(car.getId());
        carDto.setBrand(car.getBrand());
        carDto.setModel(car.getModel());
        carDto.setYear(car.getYear());
        carDto.setLicensePlate(car.getLicensePlate());
        carDto.setFuelType(car.getFuelType());
        carDto.setAvgConsumption(car.getAvgConsumption());
        
        model.addAttribute("currentUser", currentUser)
        	 .addAttribute("carDto", carDto)
        	 .addAttribute("fuelTypes", FuelType.values())
        	 .addAttribute("activePage", "cars");
        
        return "car-form";
    }
    
    @PostMapping
    public String saveCar(@Valid @ModelAttribute("carDto") CarDTO carDto, 
                         BindingResult result, 
                         Model model,
                         @AuthenticationPrincipal UserDetails userDetails,
                         RedirectAttributes redirectAttributes) {
    	User currentUser = userService.findByEmail(userDetails.getUsername());
    	
        if (result.hasErrors()) {
        	model.addAttribute("currentUser", currentUser)
             	 .addAttribute("carDto", carDto)
             	 .addAttribute("fuelTypes", FuelType.values())
             	 .addAttribute("activePage", "cars");
        	
            return "car-form";
        }
        carService.save(carDto, currentUser);
        
        redirectAttributes.addFlashAttribute("successMessage", "Carro salvo com sucesso!");
        return "redirect:/cars";
    }
    
    @PostMapping("/delete/{id}")
    public String deleteCar(@PathVariable Long id, 
                           @AuthenticationPrincipal UserDetails userDetails,
                           RedirectAttributes redirectAttributes) {
        User currentUser = userService.findByEmail(userDetails.getUsername());
        carService.deleteByIdAndUser(id, currentUser.getId());
        
        redirectAttributes.addFlashAttribute("successMessage", "Carro excluído com sucesso!");
        return "redirect:/cars";
    }
}
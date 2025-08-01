package com.dg.dto;

import com.dg.model.FuelType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class CarDTO {
    
    private Long id;
    
    @NotBlank(message = "A marca é obrigatória")
    private String brand;
    
    @NotBlank(message = "O modelo é obrigatório")
    private String model;
    
    @NotNull(message = "O ano é obrigatório")
    @Min(value = 1950, message = "O ano deve ser válido")
    private Integer year;
    
    @NotBlank(message = "A placa é obrigatória")
    private String licensePlate;
    
    @NotNull(message = "O tipo de combustível é obrigatório")
    private FuelType fuelType;
    
    @Positive(message = "O consumo médio deve ser um número positivo")
    private Double avgConsumption;

    // Constructors
    public CarDTO() {}

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand.toUpperCase();
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model.toUpperCase();
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public String getLicensePlate() {
        return licensePlate;
    }

    public void setLicensePlate(String licensePlate) {
        this.licensePlate = licensePlate.toUpperCase();
    }

    public FuelType getFuelType() {
        return fuelType;
    }

    public void setFuelType(FuelType fuelType) {
        this.fuelType = fuelType;
    }

    public Double getAvgConsumption() {
        return avgConsumption;
    }

    public void setAvgConsumption(Double avgConsumption) {
        this.avgConsumption = avgConsumption;
    }
}

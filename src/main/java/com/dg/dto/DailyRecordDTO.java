package com.dg.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import com.dg.model.AppPlatform;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import org.springframework.format.annotation.DateTimeFormat;

public class DailyRecordDTO {
    
    private Long id;
    
    @NotNull(message = "A data é obrigatória")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;
    
    private LocalTime startTime;
    
    private LocalTime endTime;
    
    @NotNull(message = "As horas trabalhadas são obrigatórias")
    @Positive(message = "As horas devem ser um número positivo")
    private Double hoursWorked;
    
    @NotNull(message = "Os quilômetros rodados são obrigatórios")
    @Positive(message = "Os quilômetros devem ser um número positivo")
    private Double kmDriven;
    
    @NotNull(message = "Os ganhos brutos são obrigatórios")
    @PositiveOrZero(message = "Os ganhos devem ser um número positivo ou zero")
    private BigDecimal grossEarnings;
    
    @NotNull(message = "A plataforma é obrigatória")
    private AppPlatform appPlatform;
    
    private String notes;
    
    @NotNull(message = "O carro é obrigatório")
    private Long carId;

    // Constructors
    public DailyRecordDTO() {}

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public Double getHoursWorked() {
        return hoursWorked;
    }

    public void setHoursWorked(Double hoursWorked) {
        this.hoursWorked = hoursWorked;
    }

    public Double getKmDriven() {
        return kmDriven;
    }

    public void setKmDriven(Double kmDriven) {
        this.kmDriven = kmDriven;
    }

    public BigDecimal getGrossEarnings() {
        return grossEarnings;
    }

    public void setGrossEarnings(BigDecimal grossEarnings) {
        this.grossEarnings = grossEarnings;
    }

    public AppPlatform getAppPlatform() {
        return appPlatform;
    }

    public void setAppPlatform(AppPlatform appPlatform) {
        this.appPlatform = appPlatform;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Long getCarId() {
        return carId;
    }

    public void setCarId(Long carId) {
        this.carId = carId;
    }
}

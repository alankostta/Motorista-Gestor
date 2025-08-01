package com.dg.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import com.dg.model.ExpenseCategory;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public class ExpensesRecordDTO {
    
    private Long id;
    
    @NotNull(message = "A data é obrigatória")
    private LocalDate date;
       
    @PositiveOrZero(message = "O custo de combustível deve ser positivo ou zero")
    private BigDecimal fuelCost;
    
    @PositiveOrZero(message = "O custo de alimentação deve ser positivo ou zero")
    private BigDecimal foodCost;
    
    @PositiveOrZero(message = "O custo de manutenção deve ser positivo ou zero")
    private BigDecimal maintenanceCost;
    
    @PositiveOrZero(message = "Os outros custos devem ser positivos ou zero")
    private BigDecimal otherCosts;
    
    private String otherCostsDescription;
       
    private String notes;
    
    @NotNull(message = "O carro é obrigatório")
    private Long carId;
    
    @NotNull(message = "A categoria é obrigatória")
    private ExpenseCategory expenseCategory;

    // Constructors
    public ExpensesRecordDTO() {}

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

    public BigDecimal getFuelCost() {
        return fuelCost;
    }

    public void setFuelCost(BigDecimal fuelCost) {
        this.fuelCost = fuelCost;
    }

    public BigDecimal getFoodCost() {
        return foodCost;
    }

    public void setFoodCost(BigDecimal foodCost) {
        this.foodCost = foodCost;
    }

    public BigDecimal getMaintenanceCost() {
        return maintenanceCost;
    }

    public void setMaintenanceCost(BigDecimal maintenanceCost) {
        this.maintenanceCost = maintenanceCost;
    }

    public BigDecimal getOtherCosts() {
        return otherCosts;
    }

    public void setOtherCosts(BigDecimal otherCosts) {
        this.otherCosts = otherCosts;
    }

    public String getOtherCostsDescription() {
        return otherCostsDescription;
    }

    public void setOtherCostsDescription(String otherCostsDescription) {
        this.otherCostsDescription = otherCostsDescription;
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

	public ExpenseCategory getExpenseCategory() {
		return expenseCategory;
	}

	public void setExpenseCategory(ExpenseCategory expenseCategory) {
		this.expenseCategory = expenseCategory;
	}
}

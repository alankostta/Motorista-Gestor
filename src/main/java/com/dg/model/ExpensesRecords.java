package com.dg.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "expenses_records")
public class ExpensesRecords {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "fuel_cost", precision = 10, scale = 2)
	private BigDecimal fuelCost = BigDecimal.ZERO;

	@Column(name = "food_cost", precision = 10, scale = 2)
	private BigDecimal foodCost = BigDecimal.ZERO;

	@Column(name = "maintenance_cost", precision = 10, scale = 2)
	private BigDecimal maintenanceCost = BigDecimal.ZERO;

	@Column(name = "other_costs", precision = 10, scale = 2)
	private BigDecimal otherCosts = BigDecimal.ZERO;

	@Column(name = "other_costs_description")
	private String otherCostsDescription;

	@Column(nullable = false)
	private LocalDate date;

	@CreationTimestamp
	@Column(name = "created_date", updatable = false)
	private LocalDateTime createdDate;

	@UpdateTimestamp
	@Column(name = "updated_date")
	private LocalDateTime updatedDate;
	
	@Enumerated(EnumType.STRING)
    @Column(name = "expense_category")
    private ExpenseCategory expenseCategory;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "car_id")
	private Car car;

	public ExpensesRecords() {
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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

	public LocalDateTime getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(LocalDateTime createdDate) {
		this.createdDate = createdDate;
	}

	public LocalDateTime getUpdatedDate() {
		return updatedDate;
	}

	public void setUpdatedDate(LocalDateTime updatedDate) {
		this.updatedDate = updatedDate;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Car getCar() {
		return car;
	}

	public void setCar(Car car) {
		this.car = car;
	}

	public LocalDate getDate() {
		return date;
	}

	public void setDate(LocalDate date) {
		this.date = date;
	}
	  // Método utilitário para calcular total de custos
    public BigDecimal getTotalCosts() {
        return fuelCost.add(foodCost).add(maintenanceCost).add(otherCosts);
    }

	public ExpenseCategory getExpenseCategory() {
		return expenseCategory;
	}

	public void setExpenseCategory(ExpenseCategory expenseCategory) {
		this.expenseCategory = expenseCategory;
	}
    
}

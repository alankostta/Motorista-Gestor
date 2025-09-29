package com.dg.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

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
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "daily_records")
public class DailyRecord {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private LocalDate date;
    
    @Column(name = "start_time")
    private LocalTime startTime;
    
    @Column(name = "end_time")
    private LocalTime endTime;
    
    @Column(name = "hours_worked", nullable = false)
    private Double hoursWorked;
    
    @Column(name = "km_driven", nullable = false)
    private Double kmDriven;
    
    @Column(name = "gross_earnings", nullable = false, precision = 10, scale = 2)
    private BigDecimal grossEarnings;
    
    @OneToMany(mappedBy = "dailyRecord", fetch = FetchType.LAZY)
    private List<ExpensesRecords> expenses = new ArrayList<>();
    
    @Enumerated(EnumType.STRING)
    @Column(name = "app_platform")
    private AppPlatform appPlatform;
    
    @Column(columnDefinition = "TEXT")
    private String notes;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "car_id")
    private Car car;
    
    @CreationTimestamp
    @Column(name = "created_date", updatable = false)
    private LocalDateTime createdDate;
    
    @UpdateTimestamp
    @Column(name = "updated_date")
    private LocalDateTime updatedDate;

    // Constructors
    public DailyRecord() {}

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
    
    public List<ExpensesRecords> getExpenses() {
        return expenses;
    }

    public void setExpenses(List<ExpensesRecords> expenses) {
        this.expenses = expenses;
    }
    
    public void addExpense(ExpensesRecords expense) {
        this.expenses.add(expense);
        expense.setDailyRecord(this);
    }
    
    public void removeExpense(ExpensesRecords expense) {
        this.expenses.remove(expense);
        expense.setDailyRecord(null);
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

    // Método utilitário para calcular total de custos
    public BigDecimal getTotalCosts() {
        return expenses.stream()
                .map(ExpensesRecords::getValueExpense)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    // Método utilitário para calcular lucro líquido
    public BigDecimal getNetProfit() {
        return grossEarnings.subtract(getTotalCosts());
    }
}

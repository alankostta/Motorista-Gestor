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
       
    @PositiveOrZero(message = "O valor da despesa deve ser positivo ou zero")
    private BigDecimal valueExpense;

    @NotNull(message = "Informe a despesa!")
    private String descriptionExpense;
       
    @NotNull(message = "A categoria é obrigatória")
    private ExpenseCategory expenseCategory;
    
    private Long dailyRecordId;

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

	public ExpenseCategory getExpenseCategory() {
		return expenseCategory;
	}

	public void setExpenseCategory(ExpenseCategory expenseCategory) {
		this.expenseCategory = expenseCategory;
	}

	public BigDecimal getValueExpense() {
		return valueExpense;
	}

	public void setValueExpense(BigDecimal valueExpense) {
		this.valueExpense = valueExpense;
	}

	public String getDescriptionExpense() {
		return descriptionExpense;
	}

	public void setDescriptionExpense(String descriptionExpense) {
		this.descriptionExpense = descriptionExpense;
	}
	
	public Long getDailyRecordId() {
		return dailyRecordId;
	}

	public void setDailyRecordId(Long dailyRecordId) {
		this.dailyRecordId = dailyRecordId;
	}
}

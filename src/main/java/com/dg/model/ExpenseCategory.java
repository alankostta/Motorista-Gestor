package com.dg.model;

public enum ExpenseCategory {
	
	COMBUSTIVEL("Combustível"),
	MANUTENCAO("Manutenção"),
	ALIMENTACAO("Alimentação"),
	PEDAGIO("Pedágio"),
	ESTACIONAMENTO("Estacionamento"),
	MULTA("Multa"),
	DOCUMENTACAO("Documentação"),
	SEGURO("Seguro"),
	OUTROS("Outros");
	
	private String displayName;
	
	private ExpenseCategory(String displayName) {
		this.displayName = displayName;
	}
	
	 public String getDisplayName() {
	        return displayName;
	    }
}

package com.dg.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import com.dg.dto.DailyRecordDTO;
import com.dg.model.Car;
import com.dg.model.DailyRecord;
import com.dg.model.ExpenseCategory;
import com.dg.model.ExpensesRecords;
import com.dg.model.User;
import com.dg.repository.CarRepository;
import com.dg.repository.DailyRecordRepository;
import com.dg.repository.ExpensesRecordRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

@Service
@Transactional
public class DailyRecordService {
    
    @Autowired
    private DailyRecordRepository dailyRecordRepository;
    
    @Autowired
    private CarRepository carRepository;
    
    @Autowired
    private ExpensesRecordRepository expensesRecordRepository;
    
    @Autowired
    private ExpenseService expenseService;
    
    public Page<DailyRecord> findAllByUserPage(Pageable pageable,Long userId) {
        return dailyRecordRepository.findPageByUserIdOrderByDateDesc(pageable, userId);
    }
    public List<DailyRecord> findAllByUser(Long userId) {
        return dailyRecordRepository.findByUserIdOrderByDateDesc(userId);
    }

    public List<DailyRecord> findAllByUserTop5(Long userId) {
        return dailyRecordRepository.findTop5ByUserIdOrderByDateDesc(userId);
    }
    
    public DailyRecord save(DailyRecordDTO recordDto, User user) {
        Car car = carRepository.findByIdAndUserId(recordDto.getCarId(), user.getId())
            .orElseThrow(() -> new EntityNotFoundException("Carro não encontrado com id: " + recordDto.getCarId()));
        
        DailyRecord record = new DailyRecord();
        if (recordDto.getId() != null) {
            record = dailyRecordRepository.findByIdAndUserId(recordDto.getId(), user.getId())
                .orElseThrow(() -> new EntityNotFoundException("Registro não encontrado"));
        }
        
        // Mapeamento do DTO para a Entidade
        record.setDate(recordDto.getDate());
        record.setStartTime(recordDto.getStartTime());
        record.setEndTime(recordDto.getEndTime());
        record.setHoursWorked(recordDto.getHoursWorked());
        record.setKmDriven(recordDto.getKmDriven());
        record.setGrossEarnings(recordDto.getGrossEarnings());
        record.setAppPlatform(recordDto.getAppPlatform());
        record.setNotes(recordDto.getNotes());
        record.setCar(car);
        record.setUser(user);

        return dailyRecordRepository.save(record);
    }
    
    public DailyRecord findByIdAndUser(Long id, Long userId) {
        return dailyRecordRepository.findByIdAndUserId(id, userId)
            .orElseThrow(() -> new EntityNotFoundException("Registro não encontrado"));
    }
    
    public void deleteByIdAndUser(Long id, Long userId) {
   
        // Obter todas as despesas associadas
        List<ExpensesRecords> expenses = expensesRecordRepository.findByDailyRecordId(id);
        
        // Remover a associação das despesas com o registro diário
        for (ExpensesRecords expense : expenses) {
            expense.setDailyRecord(null);
            expenseService.save(expense);
        }
        
        // Excluir o registro diário
        dailyRecordRepository.deleteByIdAndUserId(id, userId);
    }
    
    /**
     * Obtém todas as despesas associadas a um registro diário
     * 
     * @param dailyRecordId ID do registro diário
     * @return Lista de despesas associadas ao registro diário
     */
    public List<ExpensesRecords> getExpensesByDailyRecord(Long dailyRecordId) {
        return expensesRecordRepository.findByDailyRecordId(dailyRecordId);
    }
    
    public Map<String, Object> generateAnalytics(Long userId) {
        List<DailyRecord> records = findAllByUser(userId);
        
        BigDecimal totalEarnings = records.stream()
            .map(DailyRecord::getGrossEarnings)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
            
        Double totalHours = records.stream()
            .mapToDouble(DailyRecord::getHoursWorked)
            .sum();
            
        Double totalKm = records.stream()
            .mapToDouble(DailyRecord::getKmDriven)
            .sum();
            
        BigDecimal totalCosts = records.stream()
            .map(DailyRecord::getTotalCosts)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        Long totalCars = carRepository.countByUserId(userId);
        
        // Calcular média diária de ganhos
        BigDecimal dailyAverage = BigDecimal.ZERO;
        if (!records.isEmpty()) {
            dailyAverage = totalEarnings.divide(BigDecimal.valueOf(records.size()), 2, RoundingMode.HALF_UP);
        }
        
        Map<String, Object> analytics = new HashMap<>();
        analytics.put("totalEarnings", totalEarnings);
        analytics.put("totalHours", totalHours);
        analytics.put("totalKm", totalKm);
        analytics.put("totalCars", totalCars);
        analytics.put("totalCosts", totalCosts);
        analytics.put("netProfit", totalEarnings.subtract(totalCosts));
        analytics.put("earningsPerHour", totalHours > 0 ? totalEarnings.divide(BigDecimal.valueOf(totalHours), 2, RoundingMode.HALF_UP) : BigDecimal.ZERO);
        analytics.put("earningsPerKm", totalKm > 0 ? totalEarnings.divide(BigDecimal.valueOf(totalKm), 2, RoundingMode.HALF_UP) : BigDecimal.ZERO);
        analytics.put("costPerKm", totalKm > 0 ? totalCosts.divide(BigDecimal.valueOf(totalKm), 2, RoundingMode.HALF_UP) : BigDecimal.ZERO);
        analytics.put("totalRecords", records.size());
        analytics.put("dailyAverage", dailyAverage);
        
        return analytics;
    }
    
    public List<DailyRecord> findByUserAndDateRange(Long userId, LocalDate startDate, LocalDate endDate) {
        return dailyRecordRepository.findByUserIdAndDateBetween(userId, startDate, endDate);
    }
    
    public List<Map<String, Object>> findAllByUserIdAndRecordDate(Long id, LocalDate inicioMes, LocalDate fimMes) {
        
        List<DailyRecord> records = dailyRecordRepository.findByUserIdAndDateBetween(id, inicioMes, fimMes);

        return records.stream().map(record -> {
            Map<String, Object> map = new HashMap<>();
            map.put("date", record.getDate());
            map.put("gross_earnings", record.getGrossEarnings());
            map.put("total_costs", record.getTotalCosts());
            
            // Calcular custos detalhados por categoria
            List<ExpensesRecords> despesas = expensesRecordRepository.findByDailyRecordId(record.getId());
            
            BigDecimal fuelCost = BigDecimal.ZERO;
            BigDecimal foodCost = BigDecimal.ZERO;
            BigDecimal maintenanceCost = BigDecimal.ZERO;
            BigDecimal otherCosts = BigDecimal.ZERO;
            
            for (ExpensesRecords despesa : despesas) {
                if (despesa.getValueExpense() != null) {
                    switch (despesa.getExpenseCategory()) {
                        case COMBUSTIVEL:
                            fuelCost = fuelCost.add(despesa.getValueExpense());
                            break;
                        case ALIMENTACAO:
                            foodCost = foodCost.add(despesa.getValueExpense());
                            break;
                        case MANUTENCAO:
                            maintenanceCost = maintenanceCost.add(despesa.getValueExpense());
                            break;
                        default:
                            otherCosts = otherCosts.add(despesa.getValueExpense());
                            break;
                    }
                }
            }
            
            map.put("fuel_cost", fuelCost);
            map.put("food_cost", foodCost);
            map.put("maintenance_cost", maintenanceCost);
            map.put("other_costs", otherCosts);
            
            return map;
        }).collect(Collectors.toList());
    }  
    public Map<String, Object> analytics(Long userId) {
        YearMonth now = YearMonth.now();
        LocalDate inicioMes = now.atDay(1);
        LocalDate fimMes = now.atEndOfMonth();

        List<DailyRecord> registros = dailyRecordRepository.findByUserIdAndDateBetween(userId, inicioMes, fimMes);

        BigDecimal totalHoras = BigDecimal.ZERO;
        BigDecimal totalKm = BigDecimal.ZERO;
        BigDecimal totalGanhos = BigDecimal.ZERO;
        BigDecimal totalCustos = BigDecimal.ZERO;

        BigDecimal custoCombustivel = BigDecimal.ZERO;
        BigDecimal custoRefeicao = BigDecimal.ZERO;
        BigDecimal custoManutencao = BigDecimal.ZERO;
        BigDecimal custoOutros = BigDecimal.ZERO;

        Map<String, BigDecimal> ganhosPorPlataforma = new HashMap<>();
        
        // Mapa para agrupar custos por semana e categoria
        Map<String, Map<String, BigDecimal>> custosPorSemana = new HashMap<>();

        for (DailyRecord record : registros) {
            totalHoras = totalHoras.add(BigDecimal.valueOf(record.getHoursWorked()));
            totalKm = totalKm.add(BigDecimal.valueOf(record.getKmDriven()));
            totalGanhos = totalGanhos.add(record.getGrossEarnings());

            // Calcular semana do ano para agrupamento
            LocalDate dataRecord = record.getDate();
            String semanaAno = "Semana " + getWeekOfMonth(dataRecord);

            // Obter todas as despesas do registro diário
            List<ExpensesRecords> despesasDoDia = expensesRecordRepository.findByDailyRecordId(record.getId());
            
            // Calcular custos do dia
            BigDecimal custosDoDia = despesasDoDia.stream()
                .map(ExpensesRecords::getValueExpense)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
            totalCustos = totalCustos.add(custosDoDia);

            // Inicializar mapa da semana se não existir
            custosPorSemana.putIfAbsent(semanaAno, new HashMap<>());
            Map<String, BigDecimal> custosSemanais = custosPorSemana.get(semanaAno);
            
            // Inicializar categorias se não existirem
            custosSemanais.putIfAbsent("Combustível", BigDecimal.ZERO);
            custosSemanais.putIfAbsent("Alimentação", BigDecimal.ZERO);
            custosSemanais.putIfAbsent("Manutenção", BigDecimal.ZERO);
            custosSemanais.putIfAbsent("Outros", BigDecimal.ZERO);

            // Custos separados por categoria
            for (ExpensesRecords despesa : despesasDoDia) {
                ExpenseCategory categoria = despesa.getExpenseCategory();
                BigDecimal valor = despesa.getValueExpense();
                
                if (valor != null) {
                    if (categoria == ExpenseCategory.COMBUSTIVEL) {
                        custoCombustivel = custoCombustivel.add(valor);
                        custosSemanais.put("Combustível", custosSemanais.get("Combustível").add(valor));
                    } else if (categoria == ExpenseCategory.ALIMENTACAO) {
                        custoRefeicao = custoRefeicao.add(valor);
                        custosSemanais.put("Alimentação", custosSemanais.get("Alimentação").add(valor));
                    } else if (categoria == ExpenseCategory.MANUTENCAO) {
                        custoManutencao = custoManutencao.add(valor);
                        custosSemanais.put("Manutenção", custosSemanais.get("Manutenção").add(valor));
                    } else {
                        custoOutros = custoOutros.add(valor);
                        custosSemanais.put("Outros", custosSemanais.get("Outros").add(valor));
                    }
                }
            }

            // Ganhos por plataforma
            if (record.getAppPlatform() != null) {
                String plataforma = record.getAppPlatform().getDisplayName(); // 'UBER', '99', etc
                ganhosPorPlataforma.merge(plataforma, record.getGrossEarnings(), BigDecimal::add);
            }
        }

        int diasComRegistro = registros.size();

        Map<String, Object> result = new HashMap<>();
        result.put("ganhoHora", totalHoras.compareTo(BigDecimal.ZERO) > 0 ? totalGanhos.divide(totalHoras, 2, RoundingMode.HALF_UP) : BigDecimal.ZERO);
        result.put("ganhoKm", totalKm.compareTo(BigDecimal.ZERO) > 0 ? totalGanhos.divide(totalKm, 2, RoundingMode.HALF_UP) : BigDecimal.ZERO);
        result.put("custoKm", totalKm.compareTo(BigDecimal.ZERO) > 0 ? totalCustos.divide(totalKm, 2, RoundingMode.HALF_UP) : BigDecimal.ZERO);
        result.put("custoDia", diasComRegistro > 0 ? totalCustos.divide(BigDecimal.valueOf(diasComRegistro), 2, RoundingMode.HALF_UP) : BigDecimal.ZERO);

        result.put("custos", List.of(
            custoCombustivel.setScale(2, RoundingMode.HALF_UP),
            custoRefeicao.setScale(2, RoundingMode.HALF_UP),
            custoManutencao.setScale(2, RoundingMode.HALF_UP),
            custoOutros.setScale(2, RoundingMode.HALF_UP)
        ));

        result.put("ganhosPlataforma", ganhosPorPlataforma);
        result.put("custosPorSemana", custosPorSemana);
        
        // Adicionar campos necessários para o gráfico Ganhos vs Despesas
        result.put("totalEarnings", totalGanhos.setScale(2, RoundingMode.HALF_UP));
        result.put("totalCosts", totalCustos.setScale(2, RoundingMode.HALF_UP));

        return result;
    }
    
    // Método para calcular Km rodados por mês
    public Map<String, Object> getKmByMonth(Long userId) {
        try {
            System.out.println("DEBUG: getKmByMonth chamado para userId: " + userId);
            
            YearMonth now = YearMonth.now();
            LocalDate inicioMes = now.atDay(1);
            LocalDate fimMes = now.atEndOfMonth();

            System.out.println("DEBUG: Buscando registros entre " + inicioMes + " e " + fimMes);

            List<DailyRecord> registros = dailyRecordRepository.findByUserIdAndDateBetween(userId, inicioMes, fimMes);
            System.out.println("DEBUG: Encontrados " + registros.size() + " registros");

            Double totalKmMes = registros.stream()
                .mapToDouble(DailyRecord::getKmDriven)
                .sum();

            System.out.println("DEBUG: Total Km no mês: " + totalKmMes);

            // Calcular Km por dia do mês
            Map<String, Double> kmPorDia = new HashMap<>();
            for (DailyRecord record : registros) {
                String dia = String.valueOf(record.getDate().getDayOfMonth());
                kmPorDia.merge(dia, record.getKmDriven(), Double::sum);
            }

            // Calcular média diária
            Double mediaDiaria = registros.isEmpty() ? 0.0 : totalKmMes / registros.size();

            Map<String, Object> result = new HashMap<>();
            result.put("kmMes", String.format("%.0f KM", totalKmMes));
            result.put("kmDia", String.format("%.0f KM", mediaDiaria));
            result.put("totalKm", String.format("%.0f KM", totalKmMes));
            result.put("diasComRegistro", String.valueOf(registros.size()));
            result.put("kmPorDiaDetalhado", kmPorDia);
            result.put("mesAno", now.getMonth().getDisplayName(java.time.format.TextStyle.FULL, new java.util.Locale("pt", "BR")) + " " + now.getYear());

            System.out.println("DEBUG: Resultado: " + result);
            return result;
        } catch (Exception e) {
            System.err.println("ERRO em getKmByMonth: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    // Método para obter dados de quilometragem para gráfico por dia
    public Map<String, Object> getKmChartData(Long userId) {
        try {
            System.out.println("DEBUG: getKmChartData chamado para userId: " + userId);
            
            YearMonth now = YearMonth.now();
            LocalDate inicioMes = now.atDay(1);
            LocalDate fimMes = now.atEndOfMonth();

            System.out.println("DEBUG: Buscando registros entre " + inicioMes + " e " + fimMes);

            List<DailyRecord> registros = dailyRecordRepository.findByUserIdAndDateBetween(userId, inicioMes, fimMes);
            System.out.println("DEBUG: Encontrados " + registros.size() + " registros para gráfico");

            // Agrupar por dia
            Map<LocalDate, Double> kmPorDia = new HashMap<>();
            
            // Inicializar todos os dias do mês com 0
            LocalDate dataAtual = inicioMes;
            while (!dataAtual.isAfter(fimMes)) {
                kmPorDia.put(dataAtual, 0.0);
                dataAtual = dataAtual.plusDays(1);
            }

            // Somar quilometragem por dia
            for (DailyRecord record : registros) {
                kmPorDia.put(record.getDate(), record.getKmDriven());
            }

            // Preparar dados para o gráfico
            List<String> labels = new ArrayList<>();
            List<Double> data = new ArrayList<>();
            
            // Ordenar por data e criar labels e dados
            dataAtual = inicioMes;
            while (!dataAtual.isAfter(fimMes)) {
                labels.add(String.format("%02d", dataAtual.getDayOfMonth()));
                data.add(kmPorDia.get(dataAtual));
                dataAtual = dataAtual.plusDays(1);
            }

            Map<String, Object> result = new HashMap<>();
            result.put("labels", labels);
            result.put("data", data);
            result.put("title", "Quilometragem Diária - " + 
                now.getMonth().getDisplayName(java.time.format.TextStyle.FULL, new java.util.Locale("pt", "BR")) + 
                " " + now.getYear());

            System.out.println("DEBUG: Dados do gráfico de KM: " + result);
            return result;
        } catch (Exception e) {
            System.err.println("ERRO em getKmChartData: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    // Método auxiliar para calcular a semana do mês
    private int getWeekOfMonth(LocalDate date) {
        LocalDate firstDayOfMonth = date.withDayOfMonth(1);
        int dayOfMonth = date.getDayOfMonth();
        int firstDayWeekValue = firstDayOfMonth.getDayOfWeek().getValue();
        
        // Ajustar para que domingo seja 0
        int adjustedFirstDay = firstDayWeekValue == 7 ? 0 : firstDayWeekValue;
        
        return ((dayOfMonth + adjustedFirstDay - 1) / 7) + 1;
    }
}

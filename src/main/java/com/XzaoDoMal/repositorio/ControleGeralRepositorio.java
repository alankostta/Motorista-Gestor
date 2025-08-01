package com.XzaoDoMal.repositorio;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.XzaoDoMal.modelo.ControleGeral;

@Repository
public interface ControleGeralRepositorio extends JpaRepository<ControleGeral, Long > {
	
	Page<ControleGeral> findAll(Pageable pageable);
	
	List<ControleGeral> findAll();
	
	@Query(value="SELECT calcular_horas_dia_id(:id, :data)", nativeQuery = true)
	Double calcularHorasDiaPorId(@Param("id") Integer id, @Param("data") Date data);
	
	@Query(value="SELECT calcular_horas_dia_geral(:data)", nativeQuery = true)
	Double calcularHorasDiaGeral(@Param("data") Date data);
	
	@Query(value="SELECT calcular_horas_semana_id(:id,:data)", nativeQuery = true)
	Double calcularHorasSemnaPorId(@Param("id") Integer id, @Param("data") Date data);
	
	@Query(value="SELECT calcular_horas_semana_geral(:data)", nativeQuery = true)
	Double calcularHorasSemnaGeral(@Param("data") Date data);
	
	@Query(value="SELECT calcular_horas_mes_id(:id,:data)", nativeQuery = true)
	Double calcularHorasMesPorId(@Param("id") Integer id, @Param("data") Date data);
	
	@Query(value="SELECT calcular_horas_mes_geral(:data)", nativeQuery = true)
	Double calcularHorasMesGeral(@Param("data") Date data);
	
	@Query(value="SELECT calcular_horas_ano_id(:id,:data)", nativeQuery = true)
	Double calcularHorasAnoPorId(@Param("id") Integer id, @Param("data") Date data);
	
	@Query(value="SELECT calcular_horas_ano_geral(:data)", nativeQuery = true)
	Double calcularHorasAnoGeral(@Param("data") Date data);
	
	@Query(value="SELECT calcular_horas_geral_total(:data)", nativeQuery = true)
	Double calcularHorasGrealTotal(@Param("data") Date data);

}

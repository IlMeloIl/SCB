package com.example.demo.repository;

import com.example.demo.model.Totem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TotemRepository extends JpaRepository<Totem, Long> {
	List<Totem> findByLocalizacao(String localizacao);

	List<Totem> findByCapacidadeMaxima(Integer capacidadeMaxima);
}
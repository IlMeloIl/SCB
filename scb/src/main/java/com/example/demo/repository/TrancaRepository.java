package com.example.demo.repository;

import com.example.demo.model.Tranca;
import com.example.demo.model.StatusTranca;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TrancaRepository extends JpaRepository<Tranca, Long> {
	Optional<Tranca> findByNumero(String numero);

	List<Tranca> findByStatus(StatusTranca status);

	List<Tranca> findByTotemId(Long totemId);

}
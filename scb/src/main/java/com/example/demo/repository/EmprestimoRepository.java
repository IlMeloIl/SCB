package com.example.demo.repository;

import com.example.demo.model.Emprestimo;
import com.example.demo.model.StatusEmprestimo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmprestimoRepository extends JpaRepository<Emprestimo, Long> {
	Optional<Emprestimo> findByCiclistaIdAndStatus(Long ciclistaId, StatusEmprestimo status);

	List<Emprestimo> findByCiclistaId(Long ciclistaId);
}
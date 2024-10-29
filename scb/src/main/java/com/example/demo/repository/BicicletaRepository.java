package com.example.demo.repository;

import com.example.demo.model.Bicicleta;
import com.example.demo.model.StatusBicicleta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BicicletaRepository extends JpaRepository<Bicicleta, Long> {
	Optional<Bicicleta> findByNumero(String numero);

	List<Bicicleta> findByStatus(StatusBicicleta status);

	List<Bicicleta> findByMarca(String marca);
}
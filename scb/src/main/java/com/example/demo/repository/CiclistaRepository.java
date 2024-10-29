package com.example.demo.repository;

import com.example.demo.model.Ciclista;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CiclistaRepository extends JpaRepository<Ciclista, Long> {
	Optional<Ciclista> findByEmail(String email);

	@Query("SELECT c FROM Ciclista c WHERE TYPE(c) = Brasileiro AND c.cpf = :cpf")
	Optional<Ciclista> findByCpf(@Param("cpf") String cpf);

	@Query("SELECT c FROM Ciclista c WHERE TYPE(c) = Estrangeiro AND c.passaporte = :passaporte")
	Optional<Ciclista> findByPassaporte(@Param("passaporte") String passaporte);
}
package com.example.demo.repository;

import com.example.demo.model.CartaoCredito;
import com.example.demo.model.Ciclista;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartaoCreditoRepository extends JpaRepository<CartaoCredito, Long> {
	List<CartaoCredito> findByCiclista(Ciclista ciclista);

	Optional<CartaoCredito> findByCiclistaAndPrincipal(Ciclista ciclista, boolean principal);
}
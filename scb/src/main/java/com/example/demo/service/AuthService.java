package com.example.demo.service;

import org.springframework.stereotype.Service;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Service
public class AuthService {

	public String criptografarSenha(String senha) {
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			byte[] hash = digest.digest(senha.getBytes(StandardCharsets.UTF_8));
			return Base64.getEncoder().encodeToString(hash);
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException("Erro ao criptografar senha", e);
		}
	}

	public boolean verificarSenha(String senhaDigitada, String senhaArmazenada) {
		String senhaDigitadaHash = criptografarSenha(senhaDigitada);
		return senhaDigitadaHash.equals(senhaArmazenada);
	}
}
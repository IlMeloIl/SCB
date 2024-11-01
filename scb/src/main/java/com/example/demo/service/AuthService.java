/**
 * Serviço responsável pela autenticação e segurança do sistema
 * Fornece métodos para criptografia e validação de senhas
 * 
 * @Service Marca como um componente de serviço do Spring
 */
package com.example.demo.service;

import org.springframework.stereotype.Service;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Service
public class AuthService {

	/**
     * Criptografa uma senha usando SHA-256
     * Este método é usado no cadastro e atualização de senhas
     * 
     * @param senha senha em texto plano para ser criptografada
     * @return String senha criptografada em formato Base64
     * @throws RuntimeException se o algoritmo de hash não estiver disponível
     */
	public String criptografarSenha(String senha) {
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			byte[] hash = digest.digest(senha.getBytes(StandardCharsets.UTF_8));
			return Base64.getEncoder().encodeToString(hash);
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException("Erro ao criptografar senha", e);
		}
	}

	/**
     * Verifica se uma senha fornecida corresponde à senha armazenada
     * Usado no processo de autenticação
     * 
     * @param senhaDigitada senha fornecida pelo usuário
     * @param senhaArmazenada hash da senha armazenada no banco
     * @return boolean true se as senhas correspondem, false caso contrário
     */
	public boolean verificarSenha(String senhaDigitada, String senhaArmazenada) {
		String senhaDigitadaHash = criptografarSenha(senhaDigitada);
		return senhaDigitadaHash.equals(senhaArmazenada);
	}
}
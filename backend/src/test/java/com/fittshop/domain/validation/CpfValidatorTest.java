package com.fittshop.domain.validation;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class CpfValidatorTest {

    @ParameterizedTest
    @ValueSource(strings = {
            "52998224725",
            "11144477735",
            "529.982.247-25",
            "111.444.777-35"
    })
    @DisplayName("Deve validar com sucesso CPFs matematicamente válidos")
    void shouldValidateValidCpfs(String cpf) {
        assertTrue(CpfValidator.isValid(cpf), "CPF deveria ser considerado válido: " + cpf);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "00000000000",
            "11111111111",
            "22222222222",
            "33333333333",
            "99999999999"
    })
    @DisplayName("Deve rejeitar sequências de dígitos repetidos")
    void shouldRejectRepeatedDigitSequences(String cpf) {
        assertFalse(CpfValidator.isValid(cpf), "Dígitos repetidos devem ser inválidos: " + cpf);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "12345678900", // Dígitos verificadores incorretos
            "52998224726", // Último dígito alterado
            "123",
            "",
            "abcdefghijk"
    })
    @DisplayName("Deve rejeitar CPFs com dígitos verificadores inválidos ou formato incorreto")
    void shouldRejectInvalidCpfs(String cpf) {
        assertFalse(CpfValidator.isValid(cpf), "CPF inválido deve ser rejeitado: " + cpf);
    }

    @Test
    @DisplayName("Deve rejeitar CPF nulo")
    void shouldRejectNullCpf() {
        assertFalse(CpfValidator.isValid(null));
    }

    @Test
    @DisplayName("Deve formatar CPF com máscara oficial")
    void shouldFormatCpf() {
        assertEquals("529.982.247-25", CpfValidator.format("52998224725"));
    }
}

package com.fittshop.domain.algorithms;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Validador e Gerador de Chave de Acesso SEFAZ (Modulo 11 Ponderado)")
class SefazAccessKeyGeneratorTest {

    private final SefazAccessKeyGenerator generator = new SefazAccessKeyGenerator(new Random(42));

    @Test
    @DisplayName("Deve gerar chave de 44 digitos valida para CNPJ oficial da FittShop")
    void shouldGenerateValid44DigitKey() {
        String cnpj = "48.234.901/0001-88";
        long invoiceNumber = 1045L;
        LocalDateTime date = LocalDateTime.of(2026, 10, 24, 10, 0);

        String key = generator.generateKey(cnpj, invoiceNumber, date);

        assertNotNull(key);
        assertEquals(44, key.length());
        assertTrue(key.startsWith("35261048234901000188550010000010451"));
        assertTrue(SefazAccessKeyGenerator.isValidKey(key));
    }

    @Test
    @DisplayName("Deve validar corretamente calculo de DV modulo 11 ponderado oficial")
    void shouldCalculateCorrectDv() {
        // Base de 43 digitos
        String base43 = "3526104823490100018855001000001045112345678";
        int dv = SefazAccessKeyGenerator.calculateModulo11Weighted(base43);

        assertTrue(dv >= 0 && dv <= 9);
        String fullKey = base43 + dv;
        assertTrue(SefazAccessKeyGenerator.isValidKey(fullKey));

        // Alterando um unico digito deve invalidar a chave
        String corruptedKey = base43 + ((dv + 1) % 10);
        assertFalse(SefazAccessKeyGenerator.isValidKey(corruptedKey));
    }

    @Test
    @DisplayName("Deve formatar chave de 44 digitos em 11 blocos de 4 digitos")
    void shouldFormatKeyInBlocksOfFour() {
        String key = "35261048234901000188550010000010451123456789";
        String formatted = SefazAccessKeyGenerator.formatKey(key);

        assertEquals("3526 1048 2349 0100 0188 5500 1000 0010 4511 2345 6789", formatted);
    }

    @Test
    @DisplayName("Deve rejeitar CNPJ invalido ou nulo")
    void shouldRejectInvalidCnpj() {
        assertThrows(IllegalArgumentException.class, () -> generator.generateKey(null, 1L, LocalDateTime.now()));
        assertThrows(IllegalArgumentException.class, () -> generator.generateKey("123", 1L, LocalDateTime.now()));
    }
}

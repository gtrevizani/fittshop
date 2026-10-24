package com.fittshop.domain.algorithms;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.Random;

/**
 * Gerador e Validador da Chave de Acesso da NF-e (Modelo 55) segundo manual de integracao da SEFAZ.
 * Estrutura da Chave de 44 digitos:
 * - cUF (2 digitos): Codigo da UF do emitente (ex: 35 para SP)
 * - AAMM (4 digitos): Ano e mes de emissao
 * - CNPJ (14 digitos): CNPJ do emitente
 * - mod (2 digitos): Modelo da NF-e (55)
 * - serie (3 digitos): Serie da NF-e (001)
 * - nNF (9 digitos): Numero sequencial da NF-e
 * - tpEmis (1 digito): Tipo de emissao (1 - Normal)
 * - cNF (8 digitos): Codigo numerico de seguranca
 * - cDV (1 digito): Digito verificador calculado por Modulo 11 Ponderado (Pesos 2 a 9)
 */
public class SefazAccessKeyGenerator {

    private static final String DEFAULT_UF = "35"; // Sao Paulo
    private static final String DEFAULT_MODEL = "55"; // NF-e
    private static final String DEFAULT_SERIE = "001";
    private static final String EMISSION_NORMAL = "1";

    private final Random random;

    public SefazAccessKeyGenerator() {
        this.random = new Random();
    }

    public SefazAccessKeyGenerator(Random random) {
        this.random = Objects.requireNonNull(random, "Random nao pode ser nulo");
    }

    /**
     * Gera uma chave de acesso SEFAZ completa de 44 digitos matematicamente valida.
     */
    public String generateKey(String rawCnpj, long invoiceNumber, LocalDateTime emissionDate) {
        if (rawCnpj == null) {
            throw new IllegalArgumentException("CNPJ nao pode ser nulo");
        }
        String cleanCnpj = rawCnpj.replaceAll("\\D", "");
        if (cleanCnpj.length() != 14) {
            throw new IllegalArgumentException("CNPJ deve conter 14 digitos numericos: " + rawCnpj);
        }

        LocalDateTime date = (emissionDate != null) ? emissionDate : LocalDateTime.now();
        String aamm = date.format(DateTimeFormatter.ofPattern("yyMM"));

        String nNfFormatted = String.format("%09d", Math.abs(invoiceNumber) % 1_000_000_000L);
        int randomCode = Math.abs(random.nextInt(90_000_000)) + 10_000_000;
        String cNfFormatted = String.format("%08d", randomCode);

        String base43 = DEFAULT_UF + aamm + cleanCnpj + DEFAULT_MODEL + DEFAULT_SERIE + nNfFormatted + EMISSION_NORMAL + cNfFormatted;
        if (base43.length() != 43) {
            throw new IllegalStateException("Base da chave deve ter exatamente 43 digitos. Obtido: " + base43.length());
        }

        int dv = calculateModulo11Weighted(base43);
        return base43 + dv;
    }

    /**
     * Calcula o digito verificador modulo 11 ponderado (pesos de 2 a 9 da direita para a esquerda).
     */
    public static int calculateModulo11Weighted(String base43) {
        if (base43 == null || base43.length() != 43 || !base43.matches("\\d{43}")) {
            throw new IllegalArgumentException("A base para calculo do DV deve conter exatamente 43 digitos numericos");
        }

        int soma = 0;
        int peso = 2;

        for (int i = base43.length() - 1; i >= 0; i--) {
            int digito = Character.getNumericValue(base43.charAt(i));
            soma += digito * peso;
            peso++;
            if (peso > 9) {
                peso = 2;
            }
        }

        int resto = soma % 11;
        int dv = 11 - resto;

        if (dv == 11 || dv == 10) {
            return 0;
        }
        return dv;
    }

    /**
     * Valida se uma chave de 44 digitos tem o digito verificador matematicamente correto.
     */
    public static boolean isValidKey(String key44) {
        if (key44 == null) return false;
        String clean = key44.replaceAll("\\s+", "");
        if (clean.length() != 44 || !clean.matches("\\d{44}")) {
            return false;
        }

        String base43 = clean.substring(0, 43);
        int expectedDv = Character.getNumericValue(clean.charAt(43));
        return calculateModulo11Weighted(base43) == expectedDv;
    }

    /**
     * Formata a chave em 11 blocos de 4 digitos para exibicao no DANFE.
     */
    public static String formatKey(String key44) {
        if (key44 == null) return "";
        String clean = key44.replaceAll("\\s+", "");
        if (clean.length() != 44) return key44;

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < clean.length(); i++) {
            if (i > 0 && i % 4 == 0) {
                sb.append(" ");
            }
            sb.append(clean.charAt(i));
        }
        return sb.toString();
    }
}

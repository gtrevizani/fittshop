package com.fittshop.domain.validation;

/**
 * Validador oficial de CPF conforme algoritmo de módulo 11 da Receita Federal do Brasil.
 */
public final class CpfValidator {

    private CpfValidator() {}

    /**
     * Valida um número de CPF contra regras de dígitos verificadores e dígitos repetidos.
     *
     * @param cpf CPF com ou sem máscara
     * @return true se o CPF for matematicamente válido
     */
    public static boolean isValid(String cpf) {
        if (cpf == null) {
            return false;
        }

        // Remove pontuação e caracteres não numéricos
        String clean = cpf.replaceAll("\\D", "");

        // Deve possuir exatamente 11 dígitos
        if (clean.length() != 11) {
            return false;
        }

        // Rejeita sequências de dígitos idênticos (00000000000, 11111111111, etc.)
        if (clean.chars().distinct().count() == 1) {
            return false;
        }

        // 1º Dígito Verificador (pesos de 10 a 2)
        int sum1 = 0;
        for (int i = 0; i < 9; i++) {
            sum1 += (clean.charAt(i) - '0') * (10 - i);
        }
        int remainder1 = 11 - (sum1 % 11);
        int digit1 = (remainder1 >= 10) ? 0 : remainder1;

        if (digit1 != (clean.charAt(9) - '0')) {
            return false;
        }

        // 2º Dígito Verificador (pesos de 11 a 2)
        int sum2 = 0;
        for (int i = 0; i < 10; i++) {
            sum2 += (clean.charAt(i) - '0') * (11 - i);
        }
        int remainder2 = 11 - (sum2 % 11);
        int digit2 = (remainder2 >= 10) ? 0 : remainder2;

        return digit2 == (clean.charAt(10) - '0');
    }

    /**
     * Formata um CPF no padrão 000.000.000-00.
     */
    public static String format(String cpf) {
        if (cpf == null) return null;
        String clean = cpf.replaceAll("\\D", "");
        if (clean.length() != 11) return cpf;
        return clean.substring(0, 3) + "." +
               clean.substring(3, 6) + "." +
               clean.substring(6, 9) + "-" +
               clean.substring(9, 11);
    }
}

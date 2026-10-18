package com.isac.marketplace.util;

/**
 * Validacao de CNPJ (tamanho e digitos verificadores).
 */
public final class CnpjValidator {

    private static final int[] PESOS_DV1 = {5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};
    private static final int[] PESOS_DV2 = {6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};

    private CnpjValidator() {
    }

    public static boolean isValido(String cnpj) {
        if (cnpj == null) {
            return false;
        }
        String digitos = cnpj.replaceAll("[^0-9]", "");

        if (digitos.length() != 14) {
            return false;
        }
        // 00000000000000, 11111111111111, etc. passam na conta mas nao sao validos
        if (digitos.chars().distinct().count() == 1) {
            return false;
        }

        int dv1 = calcularDigito(digitos, PESOS_DV1);
        int dv2 = calcularDigito(digitos, PESOS_DV2);

        return dv1 == Character.getNumericValue(digitos.charAt(12))
                && dv2 == Character.getNumericValue(digitos.charAt(13));
    }

    private static int calcularDigito(String digitos, int[] pesos) {
        int soma = 0;
        for (int i = 0; i < pesos.length; i++) {
            soma += Character.getNumericValue(digitos.charAt(i)) * pesos[i];
        }
        int resto = soma % 11;
        return resto < 2 ? 0 : 11 - resto;
    }
}

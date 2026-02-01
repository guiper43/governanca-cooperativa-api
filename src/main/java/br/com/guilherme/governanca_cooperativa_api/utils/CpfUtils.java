package br.com.guilherme.governanca_cooperativa_api.utils;

public class CpfUtils {
    public static String mascararCpf(String cpf) {
        return cpf.substring(0, 3) + "*****" + cpf.substring(9);
    }
}

package com.fittshop.service.customer;

import java.util.List;

public record CpfCadastralStatus(
        String cpf,
        boolean valid,
        boolean existsInFederalRegistry,
        Integer fiscalRegion,
        List<String> states,
        String rejectionReason
) {
    public static CpfCadastralStatus approved(String cpf, Integer fiscalRegion, List<String> states) {
        return new CpfCadastralStatus(cpf, true, true, fiscalRegion, states, null);
    }

    public static CpfCadastralStatus rejected(String cpf, String reason) {
        return new CpfCadastralStatus(cpf, false, false, null, List.of(), reason);
    }

    public static CpfCadastralStatus contingencyApproved(String cpf) {
        return new CpfCadastralStatus(cpf, true, true, null, List.of(), "Aprovado em contingência técnica (validação sintática módulo 11)");
    }
}

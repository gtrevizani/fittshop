package com.fittshop.service.customer;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CpfCadastralVerificationServiceTest {

    private HttpClient mockHttpClient;
    private HttpResponse<String> mockHttpResponse;
    private CpfCadastralVerificationService service;

    @BeforeEach
    @SuppressWarnings("unchecked")
    void setUp() {
        mockHttpClient = mock(HttpClient.class);
        mockHttpResponse = (HttpResponse<String>) mock(HttpResponse.class);
        service = new CpfCadastralVerificationService(mockHttpClient, new ObjectMapper());
    }

    @Test
    @DisplayName("Deve rejeitar imediatamente CPF com dígitos verificadores matematicamente inválidos sem chamar a rede")
    void shouldRejectSyntacticallyInvalidCpfWithoutNetwork() {
        // CPF com checksum incorreto
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                service.verify("123.456.789-00")
        );

        assertTrue(ex.getMessage().contains("módulo 11"), "Deve apontar falha no algoritmo de módulo 11");
        verifyNoInteractions(mockHttpClient);
    }

    @Test
    @DisplayName("Deve aprovar CPF autêntico confirmado na base cadastral federal da Receita Federal")
    void shouldApproveAuthenticCpfFromFederalBase() throws Exception {
        String jsonResponse = "{\"cpf\":\"529.982.247-25\",\"isValid\":true,\"rf\":7,\"ufs\":[\"ES\",\"RJ\"]}";
        when(mockHttpResponse.statusCode()).thenReturn(200);
        when(mockHttpResponse.body()).thenReturn(jsonResponse);
        doReturn(mockHttpResponse).when(mockHttpClient).send(any(HttpRequest.class), any());

        CpfCadastralStatus status = service.verify("529.982.247-25");

        assertNotNull(status);
        assertTrue(status.valid());
        assertTrue(status.existsInFederalRegistry());
        assertEquals(7, status.fiscalRegion());
        assertTrue(status.states().contains("ES"));
        assertTrue(status.states().contains("RJ"));

        // Verifica cache hit: segunda chamada não deve invocar HttpClient novamente
        CpfCadastralStatus cachedStatus = service.verify("529.982.247-25");
        assertSame(status, cachedStatus);
        verify(mockHttpClient, times(1)).send(any(HttpRequest.class), any());
    }

    @Test
    @DisplayName("Deve rejeitar CPF que passa no módulo 11 mas consta como fake/inválido na base federal")
    void shouldRejectFakeCpfThatFailsFederalRegistration() throws Exception {
        String jsonResponse = "{\"isValid\":false}";
        when(mockHttpResponse.statusCode()).thenReturn(200);
        when(mockHttpResponse.body()).thenReturn(jsonResponse);
        doReturn(mockHttpResponse).when(mockHttpClient).send(any(HttpRequest.class), any());

        // CPF sintaticamente válido gerado
        String fakeCpf = "52998224725";

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                service.verify(fakeCpf)
        );

        assertTrue(ex.getMessage().contains("fake ou inexistente") || ex.getMessage().contains("Receita Federal"),
                "Mensagem deve esclarecer rejeição na base cadastral federal: " + ex.getMessage());
    }

    @Test
    @DisplayName("Deve rejeitar CPF quando a API governamental retornar 404 (documento não localizado no Brasil)")
    void shouldRejectCpfWhenApiReturns404() throws Exception {
        when(mockHttpResponse.statusCode()).thenReturn(404);
        when(mockHttpResponse.body()).thenReturn("{\"message\":\"CPF not found\"}");
        doReturn(mockHttpResponse).when(mockHttpClient).send(any(HttpRequest.class), any());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                service.verify("529.982.247-25")
        );

        assertTrue(ex.getMessage().contains("inexistente") || ex.getMessage().contains("não cadastrado"));
    }

    @Test
    @DisplayName("Deve acionar contingência técnica quando a API da Receita estiver offline")
    void shouldHandleContingencyWhenApiIsOffline() throws Exception {
        when(mockHttpClient.send(any(HttpRequest.class), any()))
                .thenThrow(new java.net.ConnectException("Receita Federal API Timeout"));

        CpfCadastralStatus status = service.verify("529.982.247-25");

        assertNotNull(status);
        assertTrue(status.valid());
        assertTrue(status.rejectionReason().contains("contingência"));
    }
}

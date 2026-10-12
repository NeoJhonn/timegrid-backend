package br.com.jhonnyazevedo.timegrid_backend.exception;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class GlobalExceptionHandlerTest {

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new TestController())
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void handleBusinessException_shouldReturnBadRequest() throws Exception {
        mockMvc.perform(get("/test/business-error"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Regra de negócio violada"))
                .andExpect(jsonPath("$.message").value("Regra de negocio invalida"))
                .andExpect(jsonPath("$.path").value("/test/business-error"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void handleValidationException_shouldReturnFieldErrors() throws Exception {
        String requestBody = """
                {
                  "name": ""
                }
                """;

        mockMvc.perform(post("/test/validation-error")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Dados inválidos"))
                .andExpect(jsonPath("$.message").value("Um ou mais campos estão inválidos"))
                .andExpect(jsonPath("$.path").value("/test/validation-error"))
                .andExpect(jsonPath("$.fields.name").exists())
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void handleTypeMismatchException_shouldReturnBadRequest() throws Exception {
        mockMvc.perform(get("/test/type-mismatch")
                        .param("number", "abc"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Parâmetro inválido"))
                .andExpect(jsonPath("$.message").value("O parâmetro 'number' possui um valor inválido"))
                .andExpect(jsonPath("$.path").value("/test/type-mismatch"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void handleHttpMessageNotReadableException_shouldReturnBadRequest() throws Exception {
        String malformedJson = """
                {
                  "name": "Teste"
                """;

        mockMvc.perform(post("/test/validation-error")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(malformedJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Requisição inválida"))
                .andExpect(jsonPath("$.message").value("O corpo da requisição está inválido ou mal formatado"))
                .andExpect(jsonPath("$.path").value("/test/validation-error"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void handleNoResourceFoundException_shouldReturnNotFound() throws Exception {
        mockMvc.perform(get("/test/resource-not-found"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Recurso não encontrado"))
                .andExpect(jsonPath("$.message").value("Não existe endpoint para este caminho"))
                .andExpect(jsonPath("$.path").value("/test/resource-not-found"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void handleGenericException_shouldReturnInternalServerError() throws Exception {
        mockMvc.perform(get("/test/generic-error"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.error").value("Erro interno do servidor"))
                .andExpect(jsonPath("$.message").value("Ocorreu um erro inesperado"))
                .andExpect(jsonPath("$.path").value("/test/generic-error"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @RestController
    private static class TestController {

        @GetMapping("/test/business-error")
        void businessError() {
            throw new BusinessException("Regra de negocio invalida");
        }

        @PostMapping("/test/validation-error")
        void validationError(@RequestBody @Valid TestRequest request) {
        }

        @GetMapping("/test/type-mismatch")
        void typeMismatch(@RequestParam Integer number) {
        }

        @GetMapping("/test/resource-not-found")
        void resourceNotFound() throws NoResourceFoundException {
            throw new NoResourceFoundException(
                    HttpMethod.GET,
                    "/test/resource-not-found",
                    "Nenhum recurso encontrado"
            );
        }

        @GetMapping("/test/generic-error")
        void genericError() {
            throw new RuntimeException("Erro inesperado");
        }
    }

    private record TestRequest(
            @NotBlank String name
    ) {
    }
}

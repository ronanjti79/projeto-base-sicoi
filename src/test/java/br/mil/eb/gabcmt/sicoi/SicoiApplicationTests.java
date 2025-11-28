package br.mil.eb.gabcmt.sicoi;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * Testes de integração da aplicação SiCoI 2.0
 * <p>
 * Esta classe verifica se o contexto do Spring Boot carrega corretamente
 * com todas as configurações, beans e componentes.
 */
@SpringBootTest
@ActiveProfiles("test")
public class SicoiApplicationTests {
    /**
     * Teste básico que verifica se o contexto da aplicação carrega sem erros.
     * <p>
     * Este teste garante que:
     * - Todas as configurações do Spring estão corretas
     * - Todos os beans podem ser criados
     * - Não há erros de injeção de dependência
     * - A aplicação está configurada adequadamente
     */
    @Test
    void contextLoads() {
        // Se o contexto carregar sem lançar exceção, o teste passa
        // Este é um teste de "smoke test" básico mas essencial
    }
}

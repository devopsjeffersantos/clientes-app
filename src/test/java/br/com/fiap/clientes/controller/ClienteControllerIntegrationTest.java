package br.com.fiap.clientes.controller;

import br.com.fiap.clientes.api.model.ClienteDto;
import br.com.fiap.clientes.dados.ClienteDados;
import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(scripts = "/data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
public class ClienteControllerIntegrationTest extends ClienteDados {

    @LocalServerPort
    private int port;

    @BeforeEach
    public void setUp() {
        RestAssured.port = port;
    }

    @Nested
    class BuscarClientePorNome {

        @Test
        void deveRetornarTodosOsClientesQuandoNomeNaoFornecido() {
            given()
                    .when()
                    .get("/api/clientes")
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("size()", greaterThan(0));
        }

        @Test
        void deveRetornarClienteQuandoNomeFornecido() {
            var nome = "Ricardo Silva";

            given()
                    .queryParam("nome", nome)
                    .when()
                    .get("/api/clientes")
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("[0].nome", equalTo(nome));
        }
    }

    @Nested
    class GetClienteById {

        @Test
        void deveRetornarClientePorId() {
            var clienteId = 1L;

            given()
                    .when()
                    .get("/api/clientes/{id}", clienteId)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("nome", equalTo("Ricardo Silva"));

        }

        @Test
        void deveRetornarNotFoundQuandoClienteNaoExistir() {
            var clienteId = 999L;

            given()
                    .when()
                    .get("/api/clientes/{id}", clienteId)
                    .then()
                    .statusCode(HttpStatus.NOT_FOUND.value())
                    .body("message", containsString("Cliente não foi encontrado"));
        }
    }

    @Nested
    class AddCliente {

        @Test
        void deveAdicionarClienteComSucesso() {
            var clienteDto = criarClienteDto1();

            given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body(clienteDto)
                    .when()
                    .post("/api/clientes")
                    .then()
                    .statusCode(HttpStatus.CREATED.value());
        }

        @Test
        void deveRetornarBadRequestQuandoDadosInvalidos() {
            var clienteDto = ClienteDto.builder()
                    .id(null)
                    .nome("")
                    .build();

            given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body(clienteDto)
                    .when()
                    .post("/api/clientes")
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value());
        }
    }

    @Nested
    class UpdateCliente {

        @Test
        void deveAtualizarClienteComSucesso() {
            var clienteDto = criarClienteDto1();
            clienteDto.setNome("Cliente Atualizado");

            given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body(clienteDto)
                    .when()
                    .put("/api/clientes/{id}", clienteDto.getId())
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("nome", equalTo("Cliente Atualizado"));
        }

        @Test
        void deveRetornarNotFoundQuandoClienteNaoExistir() {
            Long clienteId = 999L;
            ClienteDto clienteDto = criarClienteDto1();

            given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body(clienteDto)
                    .when()
                    .put("/api/clientes/{id}", clienteId)
                    .then()
                    .statusCode(HttpStatus.NOT_FOUND.value())
                    .body("message", containsString("Cliente não foi encontrado"));
        }
    }

    @Nested
    class DeleteCliente {

        @Test
        void deveDeletarClienteComSucesso() {
            Long clienteId = 1L;

            given()
                    .when()
                    .delete("/api/clientes/{id}", clienteId)
                    .then()
                    .statusCode(HttpStatus.NO_CONTENT.value());
        }

        @Test
        void deveRetornarNotFoundQuandoClienteNaoExistir() {
            Long clienteId = 999L;

            given()
                    .when()
                    .delete("/api/clientes/{id}", clienteId)
                    .then()
                    .statusCode(HttpStatus.NOT_FOUND.value())
                    .body("message", containsString("Cliente não foi encontrado"));
        }
    }
}

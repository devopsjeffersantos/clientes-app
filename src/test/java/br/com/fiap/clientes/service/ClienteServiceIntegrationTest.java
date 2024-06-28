package br.com.fiap.clientes.service;

import br.com.fiap.clientes.api.model.ClienteDto;
import br.com.fiap.clientes.dados.ClienteDados;
import br.com.fiap.clientes.domain.exception.ClienteNaoEncontradoException;
import br.com.fiap.clientes.domain.repository.ClienteRepository;
import br.com.fiap.clientes.domain.service.ClienteService;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Sql(scripts = "/data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
public class ClienteServiceIntegrationTest extends ClienteDados {

    @Autowired
    private ClienteService clienteService;

    @Autowired
    private ClienteRepository clienteRepository;

    @Nested
    class buscarClientePorNome {

        @Test
        void deveBuscarClientePorNomeUnico_ComSucesso() {
            var nome = "Ricardo Silva";

            var listaClientes = clienteService.buscarClientePorNome(nome);

            assertThat(listaClientes)
                    .isNotNull()
                    .hasSize(1);
            assertThat(listaClientes.get(0))
                    .isNotNull()
                    .isInstanceOf(ClienteDto.class);
            assertThat(listaClientes.get(0).getEmail())
                    .isEqualTo("ricardolara.ti@gmail.com");
        }

        @Test
        void deveBuscarClientePorNomeParcial_ComSucesso() {
            var nome = "Silva";

            var listaClientes = clienteService.buscarClientePorNome(nome);

            assertThat(listaClientes)
                    .isNotNull()
                    .hasSize(2);
            assertThat(listaClientes.get(0))
                    .isNotNull()
                    .isInstanceOf(ClienteDto.class);
            assertThat(listaClientes.get(0).getEmail())
                    .isEqualTo("ricardolara.ti@gmail.com");
        }

        @Test
        void deveBuscarClientePorNomeQueNaoExiste_ComSucesso() {
            var nome = "Rafael";

            var listaClientes = clienteService.buscarClientePorNome(nome);

            assertThat(listaClientes)
                    .isEmpty();
        }
    }

    @Nested
    class adicionarCliente {

        @Test
        void deveAdicionarCliente_ComSucesso() {
            var clienteDto = criarClienteDto1();
            clienteDto.setId(null);

            clienteService.add(clienteDto);
            var listaClientes = clienteService.buscarClientePorNome(clienteDto.getNome());

            assertThat(listaClientes)
                    .isNotNull()
                    .hasSize(1);
            assertThat(listaClientes.get(0))
                    .isNotNull()
                    .isInstanceOf(ClienteDto.class);
            assertThat(listaClientes.get(0).getEmail())
                    .isEqualTo(clienteDto.getEmail());
        }
    }

    @Nested
    class atualizarCliente {

        @Test
        void deveAtualizarCliente_ComSucesso() {
            var clienteDto = clienteService.getClienteById(1L);
            clienteDto.setNome(clienteDto.getNome() + " ATUALIZADO");

            var clienteDtoAtualizado = clienteService.update(clienteDto, clienteDto.getId());

            assertThat(clienteDtoAtualizado)
                    .isNotNull()
                    .isInstanceOf(ClienteDto.class);
            assertThat(clienteDtoAtualizado.getEmail())
                    .isEqualTo(clienteDto.getEmail());
            assertThat(clienteDtoAtualizado.getNome())
                    .isEqualTo(clienteDto.getNome());
        }

        @Test
        void deveAtualizarClienteQueNaoExiste_DeveLancarThrowException() {
            var clienteDto = new ClienteDto();
            clienteDto.setId(999L);

            assertThatThrownBy(() -> clienteService.update(clienteDto, clienteDto.getId()))
                    .isInstanceOf(ClienteNaoEncontradoException.class)
                    .hasMessage("Cliente não foi encontrado");
        }
    }

    @Nested
    class deletarCliente {

        @Test
        void deveDeletarCliente_ComSucesso() {
            var cliente = criarCliente1();
            cliente.setId(null);

            cliente = clienteRepository.save(cliente);
            clienteService.delete(cliente.getId());
            var listaClientes = clienteService.findAll();

            assertThat(listaClientes)
                    .isNotNull()
                    .hasSize(2);
            assertThat(listaClientes.get(0))
                    .isNotNull()
                    .isInstanceOf(ClienteDto.class);
        }

        @Test
        void deveLancarExceptionAoTentarDeletarUsuarioQueNaoExiste() {
            var id = 999L;

            assertThatThrownBy(() -> clienteService.delete(id))
                    .isInstanceOf(ClienteNaoEncontradoException.class)
                    .hasMessage("Cliente não foi encontrado");
        }
    }

    @Nested
    class buscarClientePorId {

        @Test
        void deveBuscarClientePorId_ComSucesso() {
            var id = 1L;

            var cliente = clienteService.getClienteById(id);

            assertThat(cliente)
                    .isNotNull()
                    .isInstanceOf(ClienteDto.class);
            assertThat(cliente.getEmail())
                    .isEqualTo("ricardolara.ti@gmail.com");
            assertThat(cliente.getNome())
                    .isEqualTo("Ricardo Silva");
        }

        @Test
        void deveLancarExceptionAoTentarEncontrarUsuarioQueNaoExiste() {
            var id = 999L;

            assertThatThrownBy(() -> clienteService.delete(id))
                    .isInstanceOf(ClienteNaoEncontradoException.class)
                    .hasMessage("Cliente não foi encontrado");
        }
    }

    @Nested
    class buscarClientes {

        @Test
        void deveBuscarTodosClientes_ComSucesso() {
            var listaClientes = clienteService.findAll();

            assertThat(listaClientes)
                    .isNotNull()
                    .hasSize(2);
            assertThat(listaClientes.get(0))
                    .isNotNull()
                    .isInstanceOf(ClienteDto.class);
        }
    }
}

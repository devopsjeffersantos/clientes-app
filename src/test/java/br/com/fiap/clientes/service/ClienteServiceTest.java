package br.com.fiap.clientes.service;

import br.com.fiap.clientes.api.model.ClienteDto;
import br.com.fiap.clientes.config.MessageConfig;
import br.com.fiap.clientes.dados.ClienteDados;
import br.com.fiap.clientes.domain.exception.ClienteNaoEncontradoException;
import br.com.fiap.clientes.domain.model.Cliente;
import br.com.fiap.clientes.domain.model.MensagemEmail;
import br.com.fiap.clientes.domain.repository.ClienteRepository;
import br.com.fiap.clientes.domain.service.ClienteService;
import br.com.fiap.clientes.domain.service.SQSService;
import org.junit.jupiter.api.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class ClienteServiceTest extends ClienteDados {

    private AutoCloseable closeable;

    @Mock
    private ClienteRepository clienteRepository;

    @InjectMocks
    private ClienteService clienteService;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private MessageConfig messageConfig;

    @Mock
    private SQSService sqsService;

    @BeforeEach
    void setup() {
        closeable = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void closeService() throws Exception {
        closeable.close();
    }

    @Nested
    class buscarClientePorNome {

        @Test
        void deveBuscarClientePorNome_ComSucesso() {
            var nome = "Bruno";
            var cliente1 = criarCliente1();
            var cliente2 = criarCliente2();
            var clienteDto1 = criarClienteDto1();
            var clienteDto2 = criarClienteDto2();
            var clienteList = Arrays.asList(cliente1, cliente2);

            when(clienteRepository.findByNomeIgnoreCaseContaining(nome)).thenReturn(clienteList);
            when(modelMapper.map(cliente1, ClienteDto.class)).thenReturn(clienteDto1);
            when(modelMapper.map(cliente2, ClienteDto.class)).thenReturn(clienteDto2);

            var result = clienteService.buscarClientePorNome(nome);

            assertEquals(2, result.size());
            assertEquals(clienteDto1, result.get(0));
            assertEquals(clienteDto2, result.get(1));

            verify(clienteRepository, times(1)).findByNomeIgnoreCaseContaining(nome);
            verify(modelMapper, times(1)).map(cliente1, ClienteDto.class);
            verify(modelMapper, times(1)).map(cliente2, ClienteDto.class);
        }

        @Test
        void deveBuscarClientePorNome_SemResultados() {
            var nome = "";
            List<Cliente> clienteList = List.of();

            when(clienteRepository.findByNomeIgnoreCaseContaining(nome)).thenReturn(clienteList);

            var result = clienteService.buscarClientePorNome(nome);

            assertEquals(0, result.size());

            verify(clienteRepository, times(1)).findByNomeIgnoreCaseContaining(nome);
            verifyNoInteractions(modelMapper);
        }
    }

    @Nested
    class adicionarCliente {

        @Test
        void deveAdicionarCliente_ComSucesso() {
            var cliente = criarCliente1();
            var clienteDto = criarClienteDto1();

            when(modelMapper.map(clienteDto, Cliente.class)).thenReturn(cliente);
            doNothing().when(sqsService).enviarMensagem(any(MensagemEmail.class));

            clienteService.add(clienteDto);

            verify(clienteRepository, times(1)).save(cliente);
            verify(modelMapper, times(1)).map(clienteDto, Cliente.class);
        }
    }

    @Nested
    class atualizarCliente {

        @Test
        void deveAtualizarERetornarCliente_ComSucesso() {
            Long id = 1L;
            ClienteDto clienteDto = criarClienteDto1();
            Cliente cliente = criarCliente1();

            when(clienteRepository.findById(id)).thenReturn(Optional.of(cliente));
            when(modelMapper.map(clienteDto, Cliente.class)).thenReturn(cliente);
            when(clienteRepository.save(any(Cliente.class))).thenReturn(cliente);
            when(modelMapper.map(cliente, ClienteDto.class)).thenReturn(clienteDto);

            ClienteDto updatedClienteDto = clienteService.update(clienteDto, id);

            assertEquals(clienteDto, updatedClienteDto);
            verify(clienteRepository, times(1)).findById(id);
            verify(modelMapper, times(1)).map(clienteDto, cliente);
            verify(clienteRepository, times(1)).save(cliente);
        }

        @Test
        void atualizarClienteQueNaoExiste_DeveLancarThrowException() {
            Long id = 1L;
            ClienteDto clienteDto = new ClienteDto();

            when(clienteRepository.findById(id)).thenReturn(Optional.empty());
            when(messageConfig.getClienteNaoEncontrado()).thenReturn("Cliente não encontrado");

            assertThrows(ClienteNaoEncontradoException.class, () -> clienteService.update(clienteDto, id));
            verify(clienteRepository, times(1)).findById(id);
            verifyNoMoreInteractions(modelMapper, clienteRepository);
        }
    }

    @Nested
    class deletarCliente {
        @Test
        void deveDeletarCliente_ComSucesso() {
            Long id = 1L;
            when(clienteRepository.findById(id)).thenReturn(Optional.of(new Cliente()));

            clienteService.delete(id);

            verify(clienteRepository, times(1)).findById(id);
            verify(clienteRepository, times(1)).deleteById(id);
        }

        @Test
        void deletarClienteQueNaoExiste_DeveLancarThrowException() {
            Long id = 1L;
            when(clienteRepository.findById(id)).thenReturn(Optional.empty());
            when(messageConfig.getClienteNaoEncontrado()).thenReturn("Cliente não encontrado");

            assertThrows(ClienteNaoEncontradoException.class, () -> clienteService.delete(id));
            verify(clienteRepository, times(1)).findById(id);
            verifyNoMoreInteractions(clienteRepository);
        }
    }

    @Nested
    class buscarClientePorId {
        @Test
        void deveBuscarClientePorId_ComSucesso() {
            Long id = 1L;
            Cliente cliente = criarCliente1();
            ClienteDto clienteDto = criarClienteDto1();

            when(clienteRepository.findById(id)).thenReturn(Optional.of(cliente));
            when(modelMapper.map(cliente, ClienteDto.class)).thenReturn(clienteDto);

            ClienteDto resultDto = clienteService.getClienteById(id);

            assertEquals(clienteDto, resultDto);
            verify(clienteRepository, times(1)).findById(id);
            verify(modelMapper, times(1)).map(cliente, ClienteDto.class);
        }

        @Test
        void aoBuscarClienteQueNaoExiste_DeveLancarThrowException() {
            Long id = 1L;

            when(clienteRepository.findById(id)).thenReturn(Optional.empty());
            when(messageConfig.getClienteNaoEncontrado()).thenReturn("Cliente não encontrado");

            assertThrows(ClienteNaoEncontradoException.class, () -> clienteService.getClienteById(id));
            verify(clienteRepository, times(1)).findById(id);
            verifyNoMoreInteractions(modelMapper);
        }
    }

    @Nested
    class buscarTodosClientes {
        @Test
        void deveBuscarTodosClientes_ComSucesso() {
            Cliente cliente1 = criarCliente1();
            Cliente cliente2 = criarCliente2();
            List<Cliente> clientes = Arrays.asList(cliente1, cliente2);

            ClienteDto clienteDto1 = criarClienteDto1();
            ClienteDto clienteDto2 = criarClienteDto2();
            List<ClienteDto> expectedDtos = Arrays.asList(clienteDto1, clienteDto2);

            when(clienteRepository.findAll()).thenReturn(clientes);
            when(modelMapper.map(cliente1, ClienteDto.class)).thenReturn(clienteDto1);
            when(modelMapper.map(cliente2, ClienteDto.class)).thenReturn(clienteDto2);

            List<ClienteDto> resultDtos = clienteService.findAll();

            // Assert
            assertEquals(expectedDtos.size(), resultDtos.size());
            for (int i = 0; i < expectedDtos.size(); i++) {
                assertEquals(expectedDtos.get(i), resultDtos.get(i));
            }
            verify(clienteRepository, times(1)).findAll();
            verify(modelMapper, times(1)).map(cliente1, ClienteDto.class);
            verify(modelMapper, times(1)).map(cliente2, ClienteDto.class);
        }
    }
}

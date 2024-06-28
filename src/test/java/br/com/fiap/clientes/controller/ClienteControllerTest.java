package br.com.fiap.clientes.controller;

import br.com.fiap.clientes.api.controller.ClienteController;
import br.com.fiap.clientes.api.model.ClienteDto;
import br.com.fiap.clientes.dados.ClienteDados;
import br.com.fiap.clientes.domain.service.ClienteService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ClienteController.class)
@AutoConfigureMockMvc
class ClienteControllerTest extends ClienteDados {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ClienteService clienteService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void deveBuscarClientePorNomeComSucesso() throws Exception {
        String nome = "Bruno";
        List<ClienteDto> clientes = Collections.singletonList(criarClienteDto1());

        when(clienteService.buscarClientePorNome(nome)).thenReturn(clientes);

        mockMvc.perform(get("/api/clientes").param("nome", nome))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(clientes.size()));

        verify(clienteService, times(1)).buscarClientePorNome(nome);
    }

    @Test
    void deveBuscarTodosClienteSeNomeNullComSucesso() throws Exception {
        List<ClienteDto> clientes = Arrays.asList(criarClienteDto1(), criarClienteDto2());

        when(clienteService.findAll()).thenReturn(clientes);

        mockMvc.perform(get("/api/clientes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(clientes.size()));

        verify(clienteService, times(1)).findAll();
    }

    @Test
    void deveBuscarClientePorIdComSucesso() throws Exception {
        var id = 1L;
        var clienteDto = criarClienteDto1();

        when(clienteService.getClienteById(id)).thenReturn(clienteDto);

        mockMvc.perform(get("/api/clientes/{id}", id))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(clienteDto)));

        verify(clienteService, times(1)).getClienteById(id);
    }

    @Test
    void deveAdicionarClienteComSucesso() throws Exception {
        var clienteDto = criarClienteDto1();

        doNothing().when(clienteService).add(clienteDto);

        mockMvc.perform(post("/api/clientes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(clienteDto)))
                .andExpect(status().isCreated());

        verify(clienteService, times(1)).add(clienteDto);
    }

    @Test
    void deveAtualizarClienteComSucesso() throws Exception {
        var clienteDto = criarClienteDto1();

        when(clienteService.update(clienteDto, clienteDto.getId())).thenReturn(clienteDto);

        mockMvc.perform(put("/api/clientes/{id}", clienteDto.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(clienteDto)))
                .andExpect(status().isOk());

        verify(clienteService, times(1)).update(clienteDto, clienteDto.getId());
    }

    @Test
    void deveDeletarClienteComSucesso() throws Exception {
        var id = 1L;

        doNothing().when(clienteService).delete(id);

        mockMvc.perform(delete("/api/clientes/{id}", id))
                .andExpect(status().isNoContent());

        verify(clienteService, times(1)).delete(id);
    }

}

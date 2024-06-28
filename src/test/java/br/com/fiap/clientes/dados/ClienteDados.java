package br.com.fiap.clientes.dados;

import br.com.fiap.clientes.api.model.ClienteDto;
import br.com.fiap.clientes.domain.model.Cliente;

import java.time.LocalDate;

public class ClienteDados {

    public Cliente criarCliente1(){
        return Cliente.builder()
                .id(1L)
                .nome("Bruno Silveira")
                .email("bruno.silveira@gmail.com")
                .telefone("(11) 89345-5687")
                .dataNascimento(LocalDate.of(1990, 12, 1))
                .cpf("156.683.240-36")
                .build();
    }

    public Cliente criarCliente2(){
        return Cliente.builder()
                .id(1L)
                .nome("Bruno Otávio")
                .build();
    }

    public ClienteDto criarClienteDto1(){
        return ClienteDto.builder()
                .id(1L)
                .nome("Bruno Silveira")
                .email("bruno.silveira@gmail.com")
                .telefone("(11) 89345-5687")
                .dataNascimento(LocalDate.of(1990, 12, 1))
                .cpf("156.683.240-36")
                .build();
    }

    public ClienteDto criarClienteDto2(){
        return ClienteDto.builder()
                .id(1L)
                .nome("Bruno Otávio")
                .build();
    }
}

package br.com.fiap.clientes.api.model;

import br.com.fiap.clientes.domain.model.Endereco;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ClienteDto {

    private Long id;

    @NotBlank(message = "Nome precisa ser enviado")
    private String nome;

    @NotBlank(message = "Email precisa ser enviado")
    private String email;

    private String telefone;

    private Endereco endereco;

    @JsonFormat(pattern="dd/MM/yyyy")
    private LocalDate dataNascimento;

    @NotBlank(message = "CPF precisa ser enviado")
    private String cpf;
}

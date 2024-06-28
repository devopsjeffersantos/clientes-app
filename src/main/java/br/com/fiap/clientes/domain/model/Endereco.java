package br.com.fiap.clientes.domain.model;

import br.com.fiap.clientes.domain.enums.Estado;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
public class Endereco {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonIgnore
    private Long id;

    @Column(nullable = false, length = 200)
    @NotNull(message = "Logradouro não foi preenchido")
    private String logradouro;

    @Column(nullable = false, length = 100)
    @NotNull(message = "Bairro não foi preenchido")
    private String bairro;

    @Column(nullable = false, length = 19)
    @NotNull(message = "Estado não foi preenchido")
    @Enumerated(EnumType.STRING)
    private Estado estado;

    @Column(nullable = false, length = 100)
    @NotNull(message = "Estado não foi preenchido")
    private String cidade;

    @Column(nullable = false, length = 15)
    @NotNull(message = "Cep não foi preenchido")
    private String cep;

    public String getEstado(){
        return this.estado.getSigla();
    }

}

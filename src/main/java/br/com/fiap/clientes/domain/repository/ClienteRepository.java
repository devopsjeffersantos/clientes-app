package br.com.fiap.clientes.domain.repository;

import br.com.fiap.clientes.domain.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    List<Cliente> findByNomeIgnoreCaseContaining(String nome);
}

package br.com.fiap.clientes.domain.exception;

public class ErroConverterObjetoParaJsonException extends RuntimeException{

    public ErroConverterObjetoParaJsonException(String mensagem) {
        super(mensagem);
    }
}

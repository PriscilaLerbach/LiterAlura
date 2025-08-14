package br.com.alura.literalura.LiterAlura.servico;

import br.com.alura.literalura.LiterAlura.modelo.Autor;
import br.com.alura.literalura.LiterAlura.repository.RepositorioAutor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Servico para operacoes relacionadas a autores
 */
@Service
public class ServicoAutor {

    private final RepositorioAutor repositorioAutor;

    public ServicoAutor(RepositorioAutor repositorioAutor) {
        this.repositorioAutor = repositorioAutor;
    }

    /**
     * Obtem todos os autores do banco de dados
     *
     * @return Lista de todos os autores
     */
    public List<Autor> obterTodosAutores() {
        return repositorioAutor.findAll();
    }

    /**
     * Busca autores que estavam vivos em um ano especifico
     *
     * @param ano O ano para verificar
     * @return Lista de autores que estavam vivos no ano especificado
     */
    public List<Autor> obterAutoresVivosNoAno(Integer ano) {
        if (ano == null) {
            throw new IllegalArgumentException("Ano nao pode ser nulo");
        }

        return repositorioAutor.buscarAutoresVivosNoAno(ano);
    }
}
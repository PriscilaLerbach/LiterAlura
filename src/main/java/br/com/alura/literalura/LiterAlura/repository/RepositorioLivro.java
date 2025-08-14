package br.com.alura.literalura.LiterAlura.repository;

import br.com.alura.literalura.LiterAlura.modelo.Livro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RepositorioLivro  extends JpaRepository<Livro, Long> {
    /**
     * Busca um livro pelo titulo exato
     * @param titulo O titulo do livro a ser buscado
     * @return Um Optional contendo o livro se encontrado, ou vazio se nao encontrado
     */
    Optional<Livro> findByTitulo(String titulo);

    /**
     * Busca livros pelo titulo
     * @param titulo O titulo dos livros a serem buscados
     * @return Uma lista de livros com o titulo especificado
     */
    List<Livro> findAllByTitulo(String titulo);

    /**
     * Busca livros pelo idioma
     * @param idioma O idioma dos livros a serem buscados
     * @return Uma lista de livros no idioma especificado
     */
    List<Livro> findByIdioma(String idioma);

    /**
     * Conta livros por idioma
     * @param idioma O idioma dos livros a serem contados
     * @return O numero de livros no idioma especificado
     */
    long countByIdioma(String idioma);
}

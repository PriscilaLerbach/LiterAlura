package br.com.alura.literalura.LiterAlura.repository;

import java.util.List;

import br.com.alura.literalura.LiterAlura.modelo.Autor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


/**
 * Repositorio para operacoes de banco de dados relacionadas a entidade Autor
 */
@Repository
public interface RepositorioAutor extends JpaRepository<Autor, Long> {

    /**
     * Busca autores que estavam vivos em um ano especifico.
     * Um autor e considerado vivo em um ano se:
     * 1. Ele nasceu antes ou no ano especificado, E
     * 2. Ou ele ainda nao morreu (anoFalecimento e nulo) OU ele morreu depois do ano especificado
     *
     * @param ano O ano para verificar se os autores estavam vivos
     * @return Lista de autores vivos no ano especificado
     */
    @Query("SELECT a FROM Autor a WHERE a.anoNascimento <= :ano AND (a.anoFalecimento IS NULL OR a.anoFalecimento >= :ano)")
    List<Autor> buscarAutoresVivosNoAno(@Param("ano") Integer ano);

    /**
     * Metodo alternativo usando nome de consulta derivado
     *
     * @param anoNascimento O ano de nascimento maximo
     * @param anoFalecimento O ano de falecimento minimo
     * @return Lista de autores que nasceram antes ou no ano especificado e morreram depois ou no ano especificado
     */
    List<Autor> findByAnoNascimentoLessThanEqualAndAnoFalecimentoGreaterThanEqual(Integer anoNascimento, Integer anoFalecimento);

    /**
     * Metodo alternativo para autores que ainda nao morreram
     *
     * @param anoNascimento O ano de nascimento maximo
     * @return Lista de autores que nasceram antes ou no ano especificado e ainda nao morreram
     */
    List<Autor> findByAnoNascimentoLessThanEqualAndAnoFalecimentoIsNull(Integer anoNascimento);
}

package br.com.alura.literalura.LiterAlura.servico;

import br.com.alura.literalura.LiterAlura.modelo.Livro;
import br.com.alura.literalura.LiterAlura.repository.RepositorioLivro;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ServicoLivro {

    private final RepositorioLivro repositorioLivro;
    private final ServicoApiGutendex servicoApiGutendex;

    public ServicoLivro(RepositorioLivro repositorioLivro, ServicoApiGutendex servicoApiGutendex) {
        this.repositorioLivro = repositorioLivro;
        this.servicoApiGutendex = servicoApiGutendex;
    }

    /**
     * Busca um livro por titulo na API e salva no banco de dados
     *
     * @param titulo O titulo a ser buscado
     * @return Optional contendo o livro se encontrado, ou vazio se nao encontrado
     */
    public Optional<Livro> buscarESalvarLivroPorTitulo(String titulo) {
        // Primeiro verifica se o livro ja esta no banco de dados
        List<Livro> livrosExistentes = repositorioLivro.findAllByTitulo(titulo);

        // Se encontrou livros com esse título, retorna o primeiro
        if (!livrosExistentes.isEmpty()) {
            return Optional.of(livrosExistentes.get(0));
        }

        // Se nao encontrado no banco de dados, busca na API
        Optional<Livro> livroDaApi = servicoApiGutendex.buscarLivroPorTitulo(titulo);

        // Se encontrado na API, verifica se já existe um livro similar antes de salvar
        if (livroDaApi.isPresent()) {
            Livro livroParaSalvar = livroDaApi.get();

            // Verifica se já existe um livro com o mesmo título, autor e idioma
            List<Livro> livrosSimilares = repositorioLivro.findAll().stream()
                    .filter(l -> l.getTitulo().equals(livroParaSalvar.getTitulo()))
                    .filter(l -> {
                        // Compara autores se ambos não forem nulos
                        if (l.getAutor() != null && livroParaSalvar.getAutor() != null) {
                            return l.getAutor().getNome().equals(livroParaSalvar.getAutor().getNome());
                        }
                        // Se um é nulo e o outro não, são diferentes
                        return l.getAutor() == livroParaSalvar.getAutor();
                    })
                    .filter(l -> l.getIdioma().equals(livroParaSalvar.getIdioma()))
                    .collect(Collectors.toList());

            // Se já existe um livro similar, retorna o existente
            if (!livrosSimilares.isEmpty()) {
                return Optional.of(livrosSimilares.get(0));
            }

            try {
                // Se não existe, tenta salvar
                Livro livroSalvo = repositorioLivro.save(livroParaSalvar);
                return Optional.of(livroSalvo);
            } catch (Exception e) {
                // Em caso de erro (como violação de constraint), loga o erro e retorna o livro não salvo
                System.err.println("Erro ao salvar livro: " + e.getMessage());
                return Optional.of(livroParaSalvar); // Retorna o livro mesmo sem salvar
            }
        }

        return Optional.empty();
    }

    /**
     * Busca todos os livros por título
     *
     * @param titulo O título a ser buscado
     * @return Lista de livros com o título especificado
     */
    public List<Livro> buscarLivrosPorTitulo(String titulo) {
        return repositorioLivro.findAllByTitulo(titulo);
    }

    /**
     * Obtem todos os livros do banco de dados
     *
     * @return Lista de todos os livros
     */
    public List<Livro> obterTodosLivros() {
        return repositorioLivro.findAll();
    }

    /**
     * Obtem livros por idioma
     *
     * @param idioma O idioma para filtrar
     * @return Lista de livros no idioma especificado
     */
    public List<Livro> obterLivrosPorIdioma(String idioma) {
        return repositorioLivro.findByIdioma(idioma);
    }

    /**
     * Conta livros por idioma
     *
     * @param idioma O idioma para contar
     * @return O numero de livros no idioma especificado
     */
    public long contarLivrosPorIdioma(String idioma) {
        return repositorioLivro.countByIdioma(idioma);
    }

    /**
     * Obtem estatisticas sobre livros por idioma
     *
     * @return Mapa de idioma para contagem
     */
    public Map<String, Long> obterEstatisticasLivrosPorIdioma() {
        List<Livro> todosLivros = repositorioLivro.findAll();

        return todosLivros.stream()
                .collect(Collectors.groupingBy(Livro::getIdioma, Collectors.counting()));
    }
}

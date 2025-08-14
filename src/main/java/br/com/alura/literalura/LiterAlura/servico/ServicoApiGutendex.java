package br.com.alura.literalura.LiterAlura.servico;

import br.com.alura.literalura.LiterAlura.modelo.Autor;
import br.com.alura.literalura.LiterAlura.modelo.Livro;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Optional;

@Service
public class ServicoApiGutendex {
    private static final String URL_BASE_API = "https://gutendex.com/books";
    private final HttpClient clienteHttp;
    private final ObjectMapper mapeadorObjeto;

    public ServicoApiGutendex(ObjectMapper mapeadorObjeto) {
        this.clienteHttp = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .followRedirects(HttpClient.Redirect.NORMAL)
                .build();
        this.mapeadorObjeto = mapeadorObjeto;
    }

    /**
     * Busca livros por titulo
     * @param titulo O titulo a ser buscado
     * @return Optional contendo o primeiro livro encontrado, ou vazio se nenhum for encontrado
     */
    public Optional<Livro> buscarLivroPorTitulo(String titulo) {
        try {
            String tituloCodeificado = URLEncoder.encode(titulo, StandardCharsets.UTF_8);
            String url = URL_BASE_API + "?search=" + tituloCodeificado;

            HttpRequest requisicao = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();

            HttpResponse<String> resposta = clienteHttp.send(requisicao, HttpResponse.BodyHandlers.ofString());

            if (resposta.statusCode() == 200) {
                return analisarLivroDeResposta(resposta.body());
            } else {
                System.err.println("Erro: " + resposta.statusCode() + " - " + resposta.body());
                return Optional.empty();
            }
        } catch (IOException | InterruptedException e) {
            System.err.println("Erro ao buscar livro: " + e.getMessage());
            return Optional.empty();
        }
    }

    /**
     * Analisa o primeiro livro da resposta da API
     * @param corpoResposta O corpo da resposta JSON
     * @return Optional contendo o primeiro livro, ou vazio se nenhum for encontrado
     */
    private Optional<Livro> analisarLivroDeResposta(String corpoResposta) {
        try {
            JsonNode noRaiz = mapeadorObjeto.readTree(corpoResposta);
            JsonNode noResultados = noRaiz.get("results");

            if (noResultados != null && noResultados.isArray() && noResultados.size() > 0) {
                JsonNode noLivro = noResultados.get(0);

                String titulo = noLivro.get("title").asText();

                // Obtem o primeiro idioma (conforme requisitos)
                String idioma = "";
                JsonNode noIdiomas = noLivro.get("languages");
                if (noIdiomas != null && noIdiomas.isArray() && noIdiomas.size() > 0) {
                    idioma = noIdiomas.get(0).asText();
                }

                int downloads = noLivro.get("download_count").asInt();

                // Obtem o primeiro autor (conforme requisitos)
                Autor autor = null;
                JsonNode noAutores = noLivro.get("authors");
                if (noAutores != null && noAutores.isArray() && noAutores.size() > 0) {
                    JsonNode noAutor = noAutores.get(0);
                    String nome = noAutor.get("name").asText();

                    Integer anoNascimento = null;
                    if (!noAutor.get("birth_year").isNull()) {
                        anoNascimento = noAutor.get("birth_year").asInt();
                    }

                    Integer anoFalecimento = null;
                    if (!noAutor.get("death_year").isNull()) {
                        anoFalecimento = noAutor.get("death_year").asInt();
                    }

                    autor = new Autor(nome, anoNascimento, anoFalecimento);
                }

                Livro livro = new Livro(titulo, autor, idioma, downloads);
                return Optional.of(livro);
            }

            return Optional.empty();
        } catch (Exception e) {
            System.err.println("Erro ao analisar livro da resposta: " + e.getMessage());
            return Optional.empty();
        }
    }
}

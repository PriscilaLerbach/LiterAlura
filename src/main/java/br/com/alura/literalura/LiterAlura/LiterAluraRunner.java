package br.com.alura.literalura.LiterAlura;

import br.com.alura.literalura.LiterAlura.modelo.Autor;
import br.com.alura.literalura.LiterAlura.modelo.Livro;
import br.com.alura.literalura.LiterAlura.servico.ServicoAutor;
import br.com.alura.literalura.LiterAlura.servico.ServicoLivro;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Scanner;

@Component
public class LiterAluraRunner implements CommandLineRunner {

    private final ServicoLivro servicoLivro;
    private final ServicoAutor servicoAutor;
    private final Scanner scanner;

    public LiterAluraRunner(ServicoLivro servicoLivro, ServicoAutor servicoAutor) {
        this.servicoLivro = servicoLivro;
        this.servicoAutor = servicoAutor;
        this.scanner = new Scanner(System.in);
    }

    @Override
    public void run(String... args) throws Exception {
        exibirBanner();

        boolean executando = true;
        while (executando) {
            exibirMenu();
            System.out.print("literalura:> ");
            String comando = scanner.nextLine().trim();

            try {
                executando = processarComando(comando);
            } catch (Exception e) {
                System.out.println("Erro ao processar comando: " + e.getMessage());
            }
        }

        scanner.close();
        System.out.println("Aplicação encerrada. Até logo!");
    }

    /**
     * Exibe o banner da aplicação
     */
    private void exibirBanner() {
        System.out.println("\n" +
                "  _     _ _              _                     \n" +
                " | |   () | _ _ _  / \\   _ _   _ _ _ __ _ \n" +
                " | |   | | _/ _ \\ '/ / _ \\ | | | | | '_/ _` |\n" +
                " | |_| | ||  _/ |   / __ \\| || | | | | (| |\n" +
                " |__||\\_\\_||  //   \\\\_,|||  \\_,|\n" +
                "                                                \n" +
                "=== Catálogo de Livros com Menu Interativo ===\n");
    }

    /**
     * Exibe o menu de opções
     */
    private void exibirMenu() {
        System.out.println("\nComandos disponíveis:");
        System.out.println("  1. Buscar livro por título");
        System.out.println("  2. Listar todos os livros");
        System.out.println("  3. Listar todos os autores");
        System.out.println("  4. Listar autores vivos em determinado ano");
        System.out.println("  5. Mostrar estatísticas de livros por idioma");
        System.out.println("  6. Executar modo sem interação (headless)");
        System.out.println("  0. Sair");
    }

    /**
     * Processa o comando digitado pelo usuário
     *
     * @param comando Comando digitado
     * @return true se deve continuar executando, false para encerrar
     */
    private boolean processarComando(String comando) {
        if (comando.isEmpty()) {
            return true;
        }

        try {
            int opcao = Integer.parseInt(comando);

            switch (opcao) {
                case 0:
                    return false;
                case 1:
                    buscarLivroPorTitulo();
                    break;
                case 2:
                    listarTodosLivros();
                    break;
                case 3:
                    listarTodosAutores();
                    break;
                case 4:
                    listarAutoresVivosNoAno();
                    break;
                case 5:
                    mostrarEstatisticasLivrosPorIdioma();
                    break;
                case 6:
                    executarModoSemInteracao();
                    break;
                default:
                    System.out.println("Opção inválida. Por favor, escolha uma opção válida.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Por favor, digite o número da opção desejada.");
        }

        return true;
    }

    /**
     * Busca um livro por título
     */
    private void buscarLivroPorTitulo() {
        System.out.print("Digite o título do livro: ");
        String titulo = scanner.nextLine().trim();

        if (titulo.isEmpty()) {
            System.out.println("Título não pode ser vazio.");
            return;
        }

        System.out.println("Buscando livro '" + titulo + "'...");

        // Primeiro verificamos se existem múltiplos livros com esse título
        List<Livro> livrosEncontrados = servicoLivro.buscarLivrosPorTitulo(titulo);

        if (livrosEncontrados.size() > 1) {
            // Se encontrou múltiplos livros, mostra todos
            StringBuilder sb = new StringBuilder();
            sb.append("Encontrados ").append(livrosEncontrados.size()).append(" livros com esse título:\n");

            for (Livro livro : livrosEncontrados) {
                formatarLivro(livro, sb);
            }

            System.out.println(sb.toString());
        } else {
            // Se não encontrou ou encontrou apenas um, usa o método original
            Optional<Livro> livro = servicoLivro.buscarESalvarLivroPorTitulo(titulo);

            if (livro.isPresent()) {
                StringBuilder sb = new StringBuilder();
                sb.append("Livro encontrado e salvo no banco de dados:\n");
                formatarLivro(livro.get(), sb);
                System.out.println(sb.toString());
            } else {
                System.out.println("Livro não encontrado.");
            }
        }
    }

    /**
     * Lista todos os livros
     */
    private void listarTodosLivros() {
        List<Livro> livros = servicoLivro.obterTodosLivros();

        if (livros.isEmpty()) {
            System.out.println("Não há livros no banco de dados.");
        } else {
            StringBuilder sb = new StringBuilder();
            sb.append("=== Lista de Livros ===\n");

            for (Livro livro : livros) {
                formatarLivro(livro, sb);
            }

            sb.append("Total de livros: ").append(livros.size());
            System.out.println(sb.toString());
        }
    }

    /**
     * Lista todos os autores
     */
    private void listarTodosAutores() {
        List<Autor> autores = servicoAutor.obterTodosAutores();

        if (autores.isEmpty()) {
            System.out.println("Não há autores no banco de dados.");
        } else {
            StringBuilder sb = new StringBuilder();
            sb.append("=== Lista de Autores ===\n");

            for (Autor autor : autores) {
                formatarAutor(autor, sb);
            }

            sb.append("Total de autores: ").append(autores.size());
            System.out.println(sb.toString());
        }
    }

    /**
     * Lista autores vivos em determinado ano
     */
    private void listarAutoresVivosNoAno() {
        System.out.print("Digite o ano para verificar: ");
        String anoStr = scanner.nextLine().trim();

        if (anoStr.isEmpty()) {
            System.out.println("Ano não pode ser vazio.");
            return;
        }

        try {
            int ano = Integer.parseInt(anoStr);
            List<Autor> autores = servicoAutor.obterAutoresVivosNoAno(ano);

            if (autores.isEmpty()) {
                System.out.println("Não foram encontrados autores vivos no ano " + ano + ".");
            } else {
                StringBuilder sb = new StringBuilder();
                sb.append("=== Autores vivos no ano ").append(ano).append(" ===\n");

                for (Autor autor : autores) {
                    formatarAutor(autor, sb);
                }

                sb.append("Total de autores vivos no ano ").append(ano).append(": ").append(autores.size());
                System.out.println(sb.toString());
            }
        } catch (NumberFormatException e) {
            System.out.println("Por favor, digite um ano válido.");
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Mostra estatísticas de livros por idioma
     */
    private void mostrarEstatisticasLivrosPorIdioma() {
        Map<String, Long> estatisticas = servicoLivro.obterEstatisticasLivrosPorIdioma();

        if (estatisticas.isEmpty()) {
            System.out.println("Não há livros no banco de dados para gerar estatísticas.");
        } else {
            StringBuilder sb = new StringBuilder();
            sb.append("=== Estatísticas de Livros por Idioma ===\n");

            estatisticas.forEach((idioma, quantidade) -> {
                sb.append("Idioma: ").append(idioma).append(" - Quantidade: ").append(quantidade).append("\n");
            });

            System.out.println(sb.toString());
        }
    }

    /**
     * Executa a aplicação em modo sem interação (headless)
     */
    private void executarModoSemInteracao() {
        System.out.println("=== LiterAlura em Modo Sem Interação ===\n");

        // Exibe informações sobre a aplicação
        System.out.println("=== Informações da Aplicação ===");
        System.out.println("LiterAlura é uma aplicação de catálogo de livros que utiliza a API Gutendex.\n");

        // Busca por 100 livros famosos
        System.out.println("=== Buscando 100 Livros Famosos ===");
        String[] titulosLivrosFamosos = {
                "Don Quixote", "Pride and Prejudice", "The Little Prince", "Dom Casmurro",
                "Crime and Punishment", "One Hundred Years of Solitude", "1984", "The Great Gatsby",
                "Ulysses", "The Divine Comedy", "Moby Dick", "War and Peace",
                "Les Misérables", "In Search of Lost Time", "The Metamorphosis", "Hamlet",
                "The Trial", "The Picture of Dorian Gray", "The Lord of the Rings", "Madame Bovary",
                "Anna Karenina", "The Catcher in the Rye", "The Hobbit", "The Magic Mountain",
                "Romeo and Juliet", "The Iliad", "The Odyssey", "Faust",
                "The Red and the Black", "The Stranger", "Lolita", "The Sound and the Fury",
                "Gulliver's Travels", "Robinson Crusoe", "Frankenstein", "Dracula",
                "Wuthering Heights", "Jane Eyre", "Oliver Twist", "David Copperfield",
                "The Republic", "The Prince", "Thus Spoke Zarathustra", "The World as Will and Representation",
                "Das Kapital", "On the Origin of Species", "The Elements", "The Interpretation of Dreams",
                "The Art of War", "The Book of Five Rings", "Tao Te Ching", "The Tibetan Book of the Dead",
                "The Bible", "The Quran", "The Talmud", "The Analects of Confucius",
                "The Book of the Dead", "The Bhagavad Gita", "The Mahabharata", "The Ramayana",
                "Alice's Adventures in Wonderland", "The Adventures of Tom Sawyer", "Adventures of Huckleberry Finn", "Peter Pan",
                "The Wind in the Willows", "Treasure Island", "The Chronicles of Narnia", "Harry Potter",
                "Dune", "Foundation", "Neuromancer", "2001: A Space Odyssey",
                "Fahrenheit 451", "The Hitchhiker's Guide to the Galaxy", "I, Robot", "Blade Runner",
                "The Count of Monte Cristo", "The Three Musketeers", "Twenty Thousand Leagues Under the Sea", "Around the World in Eighty Days",
                "The Sea-Wolf", "Moby Dick", "Treasure Island", "Robinson Crusoe",
                "The Old Man and the Sea", "For Whom the Bell Tolls", "A Farewell to Arms", "The Sun Also Rises",
                "A Clockwork Orange", "Brave New World", "The Time Machine", "The War of the Worlds",
                "The Invisible Man", "The Island of Doctor Moreau", "Frankenstein", "Dracula",
                "The Picture of Dorian Gray", "Dr. Jekyll and Mr. Hyde", "The Strange Case of Dr Jekyll and Mr Hyde", "The Phantom of the Opera"
        };

        int totalEncontrados = 0;
        System.out.println("Iniciando busca por livros famosos...");

        for (String titulo : titulosLivrosFamosos) {
            System.out.println("Buscando: " + titulo);
            Optional<Livro> livroEncontrado = servicoLivro.buscarESalvarLivroPorTitulo(titulo);
            if (livroEncontrado.isPresent()) {
                totalEncontrados++;
                System.out.println("Encontrado e salvo: " + livroEncontrado.get().getTitulo());
            } else {
                System.out.println("Não encontrado: " + titulo);
            }
        }

        System.out.println("Busca concluída. Total de livros encontrados e salvos: " + totalEncontrados + "\n");

        // Executa os outros comandos
        listarTodosLivros();
        System.out.println();
        listarTodosAutores();
        System.out.println();
        mostrarEstatisticasLivrosPorIdioma();
        System.out.println();

        System.out.println("=== Fim do Modo Sem Interação ===");
    }

    /**
     * Formata um livro para exibição
     *
     * @param livro Livro a ser formatado
     * @param sb    StringBuilder para acumular a formatação
     */
    private void formatarLivro(Livro livro, StringBuilder sb) {
        sb.append("---------------------------\n");
        sb.append("Título: ").append(livro.getTitulo()).append("\n");
        sb.append("Idioma: ").append(livro.getIdioma()).append("\n");
        sb.append("Downloads: ").append(livro.getDownloads()).append("\n");

        if (livro.getAutor() != null) {
            sb.append("Autor: ").append(livro.getAutor().getNome()).append("\n");

            Integer anoNascimento = livro.getAutor().getAnoNascimento();
            Integer anoFalecimento = livro.getAutor().getAnoFalecimento();

            sb.append("Período: ");
            if (anoNascimento != null) {
                sb.append(anoNascimento);
            } else {
                sb.append("?");
            }

            sb.append(" - ");

            if (anoFalecimento != null) {
                sb.append(anoFalecimento);
            } else {
                sb.append("presente");
            }
            sb.append("\n");
        } else {
            sb.append("Autor: Desconhecido\n");
        }
    }

    /**
     * Formata um autor para exibição
     *
     * @param autor Autor a ser formatado
     * @param sb    StringBuilder para acumular a formatação
     */
    private void formatarAutor(Autor autor, StringBuilder sb) {
        sb.append("---------------------------\n");
        sb.append("Nome: ").append(autor.getNome()).append("\n");

        Integer anoNascimento = autor.getAnoNascimento();
        Integer anoFalecimento = autor.getAnoFalecimento();

        sb.append("Período: ");
        if (anoNascimento != null) {
            sb.append(anoNascimento);
        } else {
            sb.append("?");
        }

        sb.append(" - ");

        if (anoFalecimento != null) {
            sb.append(anoFalecimento);
        } else {
            sb.append("presente");
        }
        sb.append("\n");
    }
}

package presenter.controllers;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.DAO.QuestionDAO;
import model.DAO.SubjectDAO;
import model.entities.Question;
import model.entities.Subject;
import model.services.QuestionService;
import model.services.SubjectService;

import java.util.List;
import java.util.stream.Collectors;

public class QuestionController {

    @FXML private Button btnNovaQuestao;
    @FXML private MenuButton menuDisciplina;
    @FXML private TextField txtBuscaAssunto;
    @FXML private MenuButton menuNivel;
    @FXML private VBox containerQuestoes;
    @FXML private VBox lblEstadoVazio;

    private QuestionService questionService;
    private SubjectService subjectService;

    // Filtros Ativos na memória
    private Subject disciplinaSelecionada = null;
    private String nivelSelecionado = "TODOS";

    @FXML
    public void initialize() {
        try {
            this.questionService = new QuestionService(new QuestionDAO());
            this.subjectService = new SubjectService(new SubjectDAO());

            // Carrega dinamicamente as opções limpando o lixo pré-definido do FXML
            carregarFiltrosDisciplina();
            configurarGatilhosFiltros();

            // Lista inicial do banco
            carregarEFiltrarQuestoes();

        } catch (Exception e) {
            System.err.println("Erro ao inicializar tela de questões: " + e.getMessage());
            e.printStackTrace();
        }

        if (btnNovaQuestao != null) {
            btnNovaQuestao.setOnAction(event -> handleNovaQuestao());
        }
    }

    private void carregarFiltrosDisciplina() {
        if (menuDisciplina == null) return;
        menuDisciplina.getItems().clear();

        // Item padrão inicial
        MenuItem itemTodos = new MenuItem("Todas as Disciplinas");
        itemTodos.setOnAction(e -> {
            disciplinaSelecionada = null;
            menuDisciplina.setText("Todas as Disciplinas"); // Atualiza o texto do botão
            carregarEFiltrarQuestoes();
        });
        menuDisciplina.getItems().add(itemTodos);

        try {
            if (subjectService != null) {
                List<Subject> disciplinas = subjectService.findAll();
                for (Subject subj : disciplinas) {
                    MenuItem item = new MenuItem(subj.getName());
                    item.setOnAction(e -> {
                        disciplinaSelecionada = subj;
                        menuDisciplina.setText(subj.getName()); // Altera o texto do botão para a disciplina escolhida!
                        carregarEFiltrarQuestoes();
                    });
                    menuDisciplina.getItems().add(item);
                }
            }
        } catch (Exception e) {
            System.err.println("Aviso: Não foi possível carregar disciplinas no filtro: " + e.getMessage());
        }
    }

    private void configurarGatilhosFiltros() {
        if (txtBuscaAssunto != null) {
            txtBuscaAssunto.textProperty().addListener((observable, oldValue, newValue) -> {
                carregarEFiltrarQuestoes();
            });
        }

        if (menuNivel != null) {
            menuNivel.getItems().clear();

            // Opção Padrão
            MenuItem itemTodos = new MenuItem("Todos os Níveis");
            itemTodos.setOnAction(e -> {
                nivelSelecionado = "TODOS";
                menuNivel.setText("Todos os Níveis");
                carregarEFiltrarQuestoes();
            });
            menuNivel.getItems().add(itemTodos);

            // 🎯 Correção aqui: mapeia o texto visual com o valor real do ENUM
            MenuItem itemFacil = new MenuItem("Fácil");
            itemFacil.setOnAction(e -> {
                nivelSelecionado = "EASY"; // Alinha com o Enum do banco
                menuNivel.setText("Fácil");
                carregarEFiltrarQuestoes();
            });

            MenuItem itemMedio = new MenuItem("Médio");
            itemMedio.setOnAction(e -> {
                nivelSelecionado = "MEDIUM"; // Alinha com o Enum do banco
                menuNivel.setText("Médio");
                carregarEFiltrarQuestoes();
            });

            MenuItem itemDificil = new MenuItem("Difícil");
            itemDificil.setOnAction(e -> {
                nivelSelecionado = "HARD"; // Alinha com o Enum do banco
                menuNivel.setText("Difícil");
                carregarEFiltrarQuestoes();
            });

            menuNivel.getItems().addAll(itemFacil, itemMedio, itemDificil);
        }
    }

    private void carregarEFiltrarQuestoes() {
        if (questionService == null || containerQuestoes == null) return;

        try {
            List<Question> listaFiltrada = questionService.findAll();

            // Filtro 1: Disciplina
            if (disciplinaSelecionada != null) {
                listaFiltrada = listaFiltrada.stream()
                        .filter(q -> q.getSubject() != null && q.getSubject().getIdSubject() == disciplinaSelecionada.getIdSubject())
                        .collect(Collectors.toList());
            }

            // Filtro 2: Assunto (Com trava anti-null)
            String buscaAssunto = (txtBuscaAssunto != null) ? txtBuscaAssunto.getText() : "";
            if (buscaAssunto != null && !buscaAssunto.trim().isEmpty()) {
                final String termo = buscaAssunto.toLowerCase().trim();
                listaFiltrada = listaFiltrada.stream()
                        .filter(q -> q.getTopic() != null && q.getTopic().toLowerCase().contains(termo))
                        .collect(Collectors.toList());
            }

            // Filtro 3: Nível de Dificuldade
            if (!"TODOS".equals(nivelSelecionado)) {
                listaFiltrada = listaFiltrada.stream()
                        .filter(q -> q.getDifficulty() != null && q.getDifficulty().name().equalsIgnoreCase(nivelSelecionado))
                        .collect(Collectors.toList());
            }

            renderizarCardsQuestoes(listaFiltrada);

        } catch (Exception e) {
            System.err.println("Erro ao filtrar questões: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void renderizarCardsQuestoes(List<Question> questoes) {
        containerQuestoes.getChildren().clear();

        if (questoes == null || questoes.isEmpty()) {
            if (lblEstadoVazio != null) {
                lblEstadoVazio.setVisible(true);
                lblEstadoVazio.setManaged(true);
            }
            return;
        }

        if (lblEstadoVazio != null) {
            lblEstadoVazio.setVisible(false);
            lblEstadoVazio.setManaged(false);
        }

        for (Question q : questoes) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/card-question.fxml"));
                VBox card = loader.load();

                CardQuestionController cardController = loader.getController();
                cardController.setQuestionData(q, this::handleEditarQuestao, this::handleDeletarQuestao);

                containerQuestoes.getChildren().add(card);
            } catch (Exception e) {
                System.err.println("Erro ao carregar card da questão ID " + q.getQuestionId() + ": " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private void handleNovaQuestao() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/add-question.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Nova Questão");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

            carregarEFiltrarQuestoes();

        } catch (Exception e) {
            System.err.println("Erro ao abrir modal de cadastro: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void handleEditarQuestao(Question q) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/edit-question.fxml"));
            Parent root = loader.load();

            EditQuestionController controller = loader.getController();
            controller.inicializarDados(q);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Editar Questão");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

            carregarEFiltrarQuestoes();

        } catch (Exception e) {
            System.err.println("Erro ao abrir modal de edição: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void handleDeletarQuestao(Question q) {
        try {
            if (questionService != null) {
                questionService.delete(q.getQuestionId());
                carregarEFiltrarQuestoes();
                System.out.println("Questão deletada.");
            }
        } catch (Exception e) {
            System.err.println("Erro ao deletar questão: " + e.getMessage());
        }
    }
}
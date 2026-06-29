package presenter.controllers;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.DAO.QuestionDAO;
import model.DAO.SubjectDAO;
import model.entities.MultipleChoiceQuestion;
import model.entities.DiscursiveQuestion;
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
            // 1. Inicialização dos Serviços locais
            QuestionDAO questionDAO = new QuestionDAO();
            this.questionService = new QuestionService(questionDAO);
            this.subjectService = new SubjectService(new SubjectDAO());

            // 2. Carrega as disciplinas cadastradas dentro do MenuButton de filtros
            carregarFiltrosDisciplina();

            // 3. Configura os gatilhos dos filtros textuais e de nível
            configurarGatilhosFiltros();

            // 4. Carrega a listagem inicial vinda direto do banco de dados
            carregarEFiltrarQuestoes();

        } catch (Exception e) {
            System.err.println("Erro ao inicializar tela de questões: " + e.getMessage());
        }

        // 5. Ação do botão para abrir o Modal de Cadastro
        btnNovaQuestao.setOnAction(event -> handleNovaQuestao());
    }

    private void carregarFiltrosDisciplina() {
        if (subjectService == null) return;

        menuDisciplina.getItems().clear();

        // Item padrão: Todas as disciplinas
        MenuItem itemTodos = new MenuItem("Todas");
        itemTodos.setOnAction(e -> {
            disciplinaSelecionada = null;
            menuDisciplina.setText("Todas");
            carregarEFiltrarQuestoes();
        });
        menuDisciplina.getItems().add(itemTodos);

        // Busca disciplinas reais do banco de dados
        try {
            List<Subject> disciplinas = subjectService.findAll();
            for (Subject subj : disciplinas) {
                MenuItem item = new MenuItem(subj.getName());
                item.setOnAction(e -> {
                    disciplinaSelecionada = subj;
                    menuDisciplina.setText(subj.getName());
                    carregarEFiltrarQuestoes();
                });
                menuDisciplina.getItems().add(item);
            }
        } catch (Exception e) {
            System.err.println("Aviso: Não foi possível carregar disciplinas do banco ainda.");
        }
    }

    private void configurarGatilhosFiltros() {
        // Escuta digitação no campo de assunto em tempo real
        txtBuscaAssunto.textProperty().addListener((observable, oldValue, newValue) -> {
            carregarEFiltrarQuestoes();
        });

        // Limpa e recria os itens do menu de nível para garantir o vínculo por código
        menuNivel.getItems().clear();

        String[] niveis = {"Todos", "Fácil", "Médio", "Difícil"};
        for (String nivel : niveis) {
            MenuItem item = new MenuItem(nivel);
            item.setOnAction(e -> {
                nivelSelecionado = nivel.toUpperCase();
                menuNivel.setText(nivel);
                carregarEFiltrarQuestoes();
            });
            menuNivel.getItems().add(item);
        }
    }

    private void carregarEFiltrarQuestoes() {
        if (questionService == null) return;

        // Puxa a lista bruta do banco
        List<Question> listaFiltrada = questionService.findAll();

        // Filtro 1: Disciplina
        if (disciplinaSelecionada != null) {
            listaFiltrada = listaFiltrada.stream()
                    .filter(q -> q.getSubject() != null && q.getSubject().getIdSubject() == disciplinaSelecionada.getIdSubject())
                    .collect(Collectors.toList());
        }

        // Filtro 2: Assunto (Tópico)
        String buscaAssunto = txtBuscaAssunto.getText();
        if (buscaAssunto != null && !buscaAssunto.trim().isEmpty()) {
            listaFiltrada = listaFiltrada.stream()
                    .filter(q -> q.getTopic() != null && q.getTopic().toLowerCase().contains(buscaAssunto.toLowerCase()))
                    .collect(Collectors.toList());
        }

        // Filtro 3: Nível de Dificuldade
        if (!nivelSelecionado.equals("TODOS") && !nivelSelecionado.contains("TODOS")) {
            listaFiltrada = listaFiltrada.stream()
                    .filter(q -> q.getDifficulty() != null && q.getDifficulty().name().equalsIgnoreCase(nivelSelecionado))
                    .collect(Collectors.toList());
        }

        // Atualiza a renderização na tela
        renderizarCardsQuestoes(listaFiltrada);
    }

    private void renderizarCardsQuestoes(List<Question> questoes) {
        containerQuestoes.getChildren().clear();

        if (questoes.isEmpty()) {
            lblEstadoVazio.setVisible(true);
            lblEstadoVazio.setManaged(true);
            return;
        }

        lblEstadoVazio.setVisible(false);
        lblEstadoVazio.setManaged(false);

        // Gera dinamicamente o design do card idêntico ao protótipo
        for (Question q : questoes) {
            VBox card = new VBox(12);
            card.setStyle("-fx-background-color: #ffffff; -fx-background-radius: 12; -fx-border-color: #e2e8f0; -fx-border-radius: 12; -fx-padding: 16;");

            // --- Linha 1: Badges e Botões de Ação ---
            HBox linhaTop = new HBox(8);
            linhaTop.setAlignment(Pos.CENTER_LEFT);

            Label badgeCodigo = criarBadge("Q-" + q.getQuestionId(), "-fx-background-color: #f1f5f9; -fx-text-fill: #334155;");

            String corNivel = q.getDifficulty().name().equals("EASY") ? "-fx-background-color: #f0fdf4; -fx-text-fill: #16a34a;" :
                    q.getDifficulty().name().equals("MEDIUM") ? "-fx-background-color: #fefce8; -fx-text-fill: #ca8a04;" :
                    "-fx-background-color: #fef2f2; -fx-text-fill: #dc2626;";
            Label badgeNivel = criarBadge(q.getDifficulty().name(), corNivel);

            String tipoTexto = (q instanceof MultipleChoiceQuestion) ? "Múltipla Escolha" : "Discursiva";
            Label badgeTipo = criarBadge(tipoTexto, "-fx-background-color: #f8fafc; -fx-text-fill: #475569; -fx-border-color: #cbd5e1; -fx-border-radius: 6;");

            linhaTop.getChildren().addAll(badgeCodigo, badgeNivel, badgeTipo);

            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);
            linhaTop.getChildren().add(spacer);

            Button btnEdit = criarBotaoIcone("/icons/edit.png");
            Button btnDelete = criarBotaoIcone("/icons/trash.png");

            btnEdit.setOnAction(e -> handleEditarQuestao(q));
            btnDelete.setOnAction(e -> handleDeletarQuestao(q));

            linhaTop.getChildren().addAll(btnEdit, btnDelete);

            // --- Linha 2: Subtítulo (Disciplina • Tópico) ---
            String nomeDisciplina = q.getSubject() != null ? q.getSubject().getName() : "Sem Disciplina";
            Label lblSubtitulo = new Label(nomeDisciplina + " • " + q.getTopic());
            lblSubtitulo.setStyle("-fx-font-size: 13px; -fx-text-fill: #64748b;");

            // --- Linha 3: Enunciado ---
            Label lblEnunciado = new Label(q.getStatement());
            lblEnunciado.setWrapText(true);
            lblEnunciado.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: #1e293b;");

            card.getChildren().addAll(linhaTop, lblSubtitulo, lblEnunciado);

            // --- Linha 4: Alternativas ---
            if (q instanceof MultipleChoiceQuestion) {
                MultipleChoiceQuestion mcq = (MultipleChoiceQuestion) q;
                VBox boxAlternativas = new VBox(6);
                boxAlternativas.setPadding(new Insets(0, 0, 0, 10));

                boxAlternativas.getChildren().addAll(
                        criarLabelOpcao("A) " + mcq.getAlternativeA()),
                        criarLabelOpcao("B) " + mcq.getAlternativeB()),
                        criarLabelOpcao("C) " + mcq.getAlternativeC()),
                        criarLabelOpcao("D) " + mcq.getAlternativeD())
                );

                Label lblGabarito = new Label("Gabarito: " + q.getAnswerKey());
                lblGabarito.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #16a34a; -fx-padding: 4 0 0 0;");

                card.getChildren().addAll(boxAlternativas, lblGabarito);
            } else if (q instanceof DiscursiveQuestion) {
                DiscursiveQuestion dq = (DiscursiveQuestion) q;
                Label lblLinhas = new Label("📋 Resposta esperada em: " + dq.getExpectedLines() + " linhas.");
                lblLinhas.setStyle("-fx-font-size: 13px; -fx-text-fill: #2563eb; -fx-font-style: italic;");
                card.getChildren().add(lblLinhas);
            }

            containerQuestoes.getChildren().add(card);
        }
    }

    private Label criarBadge(String texto, String estiloCss) {
        Label b = new Label(texto);
        b.setStyle(estiloCss + " -fx-font-size: 11px; -fx-font-weight: bold; -fx-background-radius: 6; -fx-padding: 3 8;");
        return b;
    }

    private Label criarLabelOpcao(String texto) {
        Label l = new Label(texto);
        l.setWrapText(true);
        l.setStyle("-fx-font-size: 13px; -fx-text-fill: #475569;");
        return l;
    }

    private Button criarBotaoIcone(String pathIcone) {
        Button btn = new Button();
        btn.setStyle("-fx-background-color: transparent; -fx-cursor: hand; -fx-padding: 4;");
        try {
            ImageView iv = new ImageView(new Image(getClass().getResourceAsStream(pathIcone)));
            iv.setFitHeight(16);
            iv.setFitWidth(16);
            btn.setGraphic(iv);
        } catch (Exception e) {
            btn.setText("⚙");
        }
        return btn;
    }

    private void handleNovaQuestao() {
        try {
            // Carrega o arquivo FXML do modal
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/view/add-question.fxml"));
            javafx.scene.Parent root = loader.load();

            // Cria uma nova janela (Stage) para o modal
            javafx.stage.Stage stage = new javafx.stage.Stage();
            stage.setScene(new javafx.scene.Scene(root));
            stage.setTitle("Nova Questão");

            // Torna o modal "Modal", ou seja, bloqueia a tela de trás até fechar esse
            stage.initModality(javafx.stage.Modality.APPLICATION_MODAL);

            // Exibe a janela e espera ela ser fechada
            stage.showAndWait();

            // Assim que fechar o cadastro, recarrega a lista para mostrar a nova questão se foi salva
            carregarEFiltrarQuestoes();

        } catch (Exception e) {
            System.err.println("Erro ao abrir a tela de adicionar questão: " + e.getMessage());
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
            System.err.println("Erro ao abrir a tela de edição: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void handleDeletarQuestao(Question q) {
        try {
            if (questionService != null) {
                questionService.delete(q.getQuestionId());
                carregarEFiltrarQuestoes();
                System.out.println("Questão excluída com sucesso.");
            }
        } catch (Exception e) {
            System.err.println("Erro ao deletar questão: " + e.getMessage());
        }
    }
}
package presenter.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import model.DAO.ExamDAO;
import model.DAO.QuestionDAO;
import model.DAO.SubjectDAO;
import model.services.ExamService;
import model.services.QuestionService;
import model.services.SubjectService;
import presenter.utils.SceneManager;

public class HomeController {

    @FXML
    private VBox cardProvas;

    @FXML
    private VBox cardQuestoes;

    @FXML
    private VBox cardDisciplinas;

    @FXML
    private Label lblTotalProvas;

    @FXML
    private Label lblTotalQuestoes;

    @FXML
    private Label lblTotalDisciplinas;

    @FXML
    private Button btnNovaProva;

    @FXML
    private Button btnNovaQuestao;

    private ExamService examService;
    private QuestionService questionService;
    private SubjectService subjectService;

    // Construtor vazio obrigatório para o JavaFX
    public HomeController() {
    }

    @FXML
    public void initialize() {
        // 1. Inicializa os DAOs e Services corretamente na raiz
        try {
            QuestionDAO questionDAO = new QuestionDAO();
            this.questionService = new QuestionService(questionDAO);

            // Aqui estava o segredo: O ExamService precisa do ExamDAO e do QuestionDAO!
            this.examService = new ExamService(new ExamDAO(), questionDAO);

            this.subjectService = new SubjectService(new SubjectDAO());
        } catch (Exception e) {
            System.err.println("Aviso: Banco offline ou erro de injeção. " + e.getMessage());
        }

        // 2. Configura a ação de clique para os botões de ação rápida
        btnNovaProva.setOnAction(event -> handleNewExam());
        btnNovaQuestao.setOnAction(event -> handleNewQuestion());

        // 3. Configura a ação de clique para os cards do dashboard
        cardProvas.setOnMouseClicked(event -> SceneManager.getInstance().navigateTo("/view/exam.fxml", "Gerenciamento de Provas"));
        cardQuestoes.setOnMouseClicked(event -> SceneManager.getInstance().navigateTo("/view/question.fxml", "Gerenciamento de Questões"));
        cardDisciplinas.setOnMouseClicked(event -> SceneManager.getInstance().navigateTo("/view/subject.fxml", "Gerenciamento de Disciplinas"));

        // 4. Carrega as estatísticas reais vindas do banco de dados nos cards
        loadDashboardStatistics();
    }

    private void handleNewExam() {
        SceneManager.getInstance().navigateTo("/view/new-exam.fxml", "Gerar Nova Prova");
    }

    private void handleNewQuestion() {
        SceneManager.getInstance().navigateTo("/view/add-question.fxml", "Cadastrar Nova Questão");
    }

    private void loadDashboardStatistics() {
        try {
            int totalExams = 0;
            int totalQuestions = 0;
            int totalSubjects = 0;

            // Busca os dados reais se os serviços estiverem ativos
            if (examService != null) totalExams = examService.findAll().size();
            if (questionService != null) totalQuestions = questionService.findAll().size();
            if (subjectService != null) totalSubjects = subjectService.findAll().size();

            // Atualiza as labels na tela
            lblTotalProvas.setText(String.valueOf(totalExams));
            lblTotalQuestoes.setText(String.valueOf(totalQuestions));
            lblTotalDisciplinas.setText(String.valueOf(totalSubjects));

        } catch (Exception e) {
            lblTotalProvas.setText("0");
            lblTotalQuestoes.setText("0");
            lblTotalDisciplinas.setText("0");
            System.err.println("Erro ao carregar as estatísticas: " + e.getMessage());
        }
    }
}
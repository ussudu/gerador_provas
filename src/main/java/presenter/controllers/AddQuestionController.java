package presenter.controllers;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import model.DAO.QuestionDAO;
import model.entities.Difficulty;
import model.entities.DiscursiveQuestion;
import model.entities.MultipleChoiceQuestion;
import model.entities.Question;
import model.entities.Subject;
import model.services.QuestionService;

public class AddQuestionController {

    @FXML private Label lblTituloModal;
    @FXML private Button btnFechar;
    @FXML private Button btnCancelar;
    @FXML private Button btnSalvar;

    @FXML private TextField txtCodigo; // Caso usem o código visual como o assunto/tag
    @FXML private ComboBox<String> cbTipo;
    @FXML private ComboBox<Subject> cbDisciplina;
    @FXML private TextField txtAssunto;
    @FXML private ComboBox<String> cbNivel;
    @FXML private TextArea txtEnunciado;

    // Campos de Múltipla Escolha
    @FXML private TextField txtOpcaoA;
    @FXML private TextField txtOpcaoB;
    @FXML private TextField txtOpcaoC;
    @FXML private TextField txtOpcaoD;
    @FXML private ComboBox<String> cbGabarito;

    private QuestionService questionService;

    @FXML
    public void initialize() {
        this.questionService = new QuestionService(new QuestionDAO());

        // Inicializa Combos com os Enums e Tipos padrão
        cbTipo.setItems(FXCollections.observableArrayList("Múltipla Escolha", "Discursiva"));
        cbTipo.setValue("Múltipla Escolha");

        cbNivel.setItems(FXCollections.observableArrayList("Fácil", "Médio", "Difícil"));
        cbGabarito.setItems(FXCollections.observableArrayList("A", "B", "C", "D"));

        // Gatilhos de fechar/cancelar
        btnFechar.setOnAction(e -> fecharJanela());
        btnCancelar.setOnAction(e -> fecharJanela());
        btnSalvar.setOnAction(e -> salvarQuestao());

        // Listener para esconder/exibir campos de opções caso mude o tipo
        cbTipo.valueProperty().addListener((obs, velho, novo) -> ajustarCamposPorTipo(novo));
    }

    private void ajustarCamposPorTipo(String tipo) {
        boolean ehMultiplaEscolha = "Múltipla Escolha".equals(tipo);
        txtOpcaoA.setDisable(!ehMultiplaEscolha);
        txtOpcaoB.setDisable(!ehMultiplaEscolha);
        txtOpcaoC.setDisable(!ehMultiplaEscolha);
        txtOpcaoD.setDisable(!ehMultiplaEscolha);
        cbGabarito.setDisable(!ehMultiplaEscolha);
    }

    private void salvarQuestao() {
        try {
            Question novaQuestao;
            String tipo = cbTipo.getValue();

            if ("Múltipla Escolha".equals(tipo)) {
                MultipleChoiceQuestion mcq = new MultipleChoiceQuestion();
                mcq.setAlternativeA(txtOpcaoA.getText());
                mcq.setAlternativeB(txtOpcaoB.getText());
                mcq.setAlternativeC(txtOpcaoC.getText());
                mcq.setAlternativeD(txtOpcaoD.getText());
                novaQuestao = mcq;
            } else {
                DiscursiveQuestion dq = new DiscursiveQuestion();
                dq.setExpectedLines(10); // Valor padrão inicial, ou capturado de campo se houver
                novaQuestao = dq;
            }

            // Configura os atributos base
            novaQuestao.setStatement(txtEnunciado.getText());
            novaQuestao.setTopic(txtAssunto.getText());
            novaQuestao.setAnswerKey(ehMultiplaEscolha(tipo) ? cbGabarito.getValue() : "Resposta Discursiva");

            // Mapeia Dificuldade do ComboBox para o Enum
            String nivel = cbNivel.getValue();
            if ("Fácil".equals(nivel)) novaQuestao.setDifficulty(Difficulty.EASY);
            else if ("Difícil".equals(nivel)) novaQuestao.setDifficulty(Difficulty.HARD);
            else novaQuestao.setDifficulty(Difficulty.MEDIUM);

            // Temporário: Instancia uma disciplina mock até linkarmos o SubjectService
            Subject mockSubj = new Subject();
            mockSubj.setIdSubject(1); // ID temporário para testes
            novaQuestao.setSubject(mockSubj);

            // Salva no banco através do Service
            questionService.insert(novaQuestao);

            System.out.println("Questão salva com sucesso!");
            fecharJanela();

        } catch (Exception e) {
            mostrarAlertaErro("Erro ao Salvar", e.getMessage());
        }
    }

    private boolean ehMultiplaEscolha(String tipo) {
        return "Múltipla Escolha".equals(tipo);
    }

    private void fecharJanela() {
        Stage stage = (Stage) btnFechar.getScene().getWindow();
        stage.close();
    }

    private void mostrarAlertaErro(String titulo, String mensagem) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
}
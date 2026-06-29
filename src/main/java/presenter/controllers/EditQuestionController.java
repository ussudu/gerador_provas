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

public class EditQuestionController {

    @FXML private Label lblTituloModal;
    @FXML private Button btnFechar;
    @FXML private Button btnCancelar;
    @FXML private Button btnSalvar;

    @FXML private TextField txtCodigo;
    @FXML private ComboBox<String> cbTipo;
    @FXML private ComboBox<Subject> cbDisciplina;
    @FXML private TextField txtAssunto;
    @FXML private ComboBox<String> cbNivel;
    @FXML private TextArea txtEnunciado;

    @FXML private TextField txtOpcaoA;
    @FXML private TextField txtOpcaoB;
    @FXML private TextField txtOpcaoC;
    @FXML private TextField txtOpcaoD;
    @FXML private ComboBox<String> cbGabarito;

    private QuestionService questionService;
    private Question questionEmEdicao;

    @FXML
    public void initialize() {
        this.questionService = new QuestionService(new QuestionDAO());

        cbTipo.setItems(FXCollections.observableArrayList("Múltipla Escolha", "Discursiva"));
        cbNivel.setItems(FXCollections.observableArrayList("Fácil", "Médio", "Difícil"));
        cbGabarito.setItems(FXCollections.observableArrayList("A", "B", "C", "D"));

        btnFechar.setOnAction(e -> fecharJanela());
        btnCancelar.setOnAction(e -> fecharJanela());
        btnSalvar.setOnAction(e -> salvarAlteracoes());
    }

    /**
     * Pega a questão selecionada na listagem e injeta os dados nos inputs da tela de edição.
     */
    public void inicializarDados(Question question) {
        this.questionEmEdicao = question;

        txtEnunciado.setText(question.getStatement());
        txtAssunto.setText(question.getTopic());
        txtCodigo.setText("Q-" + question.getQuestionId());

        // Carrega Dificuldade
        if (question.getDifficulty() == Difficulty.EASY) cbNivel.setValue("Fácil");
        else if (question.getDifficulty() == Difficulty.HARD) cbNivel.setValue("Difícil");
        else cbNivel.setValue("Médio");

        // Carrega dados específicos por tipo de objeto
        if (question instanceof MultipleChoiceQuestion) {
            cbTipo.setValue("Múltipla Escolha");
            MultipleChoiceQuestion mcq = (MultipleChoiceQuestion) question;
            txtOpcaoA.setText(mcq.getAlternativeA());
            txtOpcaoB.setText(mcq.getAlternativeB());
            txtOpcaoC.setText(mcq.getAlternativeC());
            txtOpcaoD.setText(mcq.getAlternativeD());
            cbGabarito.setValue(question.getAnswerKey());
        } else {
            cbTipo.setValue("Discursiva");
            desativarCamposMultiplaEscolha();
        }

        cbTipo.setDisable(true); // Bloqueia a alteração do tipo da questão na edição para evitar quebra no banco
    }

    private void desativarCamposMultiplaEscolha() {
        txtOpcaoA.setDisable(true);
        txtOpcaoB.setDisable(true);
        txtOpcaoC.setDisable(true);
        txtOpcaoD.setDisable(true);
        cbGabarito.setDisable(true);
    }

    private void salvarAlteracoes() {
        try {
            questionEmEdicao.setStatement(txtEnunciado.getText());
            questionEmEdicao.setTopic(txtAssunto.getText());

            String nivel = cbNivel.getValue();
            if ("Fácil".equals(nivel)) questionEmEdicao.setDifficulty(Difficulty.EASY);
            else if ("Difícil".equals(nivel)) questionEmEdicao.setDifficulty(Difficulty.HARD);
            else questionEmEdicao.setDifficulty(Difficulty.MEDIUM);

            if (questionEmEdicao instanceof MultipleChoiceQuestion) {
                MultipleChoiceQuestion mcq = (MultipleChoiceQuestion) questionEmEdicao;
                mcq.setAlternativeA(txtOpcaoA.getText());
                mcq.setAlternativeB(txtOpcaoB.getText());
                mcq.setAlternativeC(txtOpcaoC.getText());
                mcq.setAlternativeD(txtOpcaoD.getText());
                mcq.setAnswerKey(cbGabarito.getValue());
            }

            // Executa a atualização no banco
            questionService.update(questionEmEdicao);
            System.out.println("Questão atualizada com sucesso!");
            fecharJanela();

        } catch (Exception e) {
            mostrarAlertaErro("Erro ao Atualizar", e.getMessage());
        }
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
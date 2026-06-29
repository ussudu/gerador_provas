package presenter.controllers;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.DAO.QuestionDAO;
import model.DAO.SubjectDAO;
import model.entities.Difficulty;
import model.entities.DiscursiveQuestion;
import model.entities.MultipleChoiceQuestion;
import model.entities.Question;
import model.entities.Subject;
import model.services.QuestionService;
import model.services.SubjectService;

import java.util.List;

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

    // Elementos vinculados ao Bloco de Múltipla Escolha
    @FXML private VBox blocoMultiplaEscolha;
    @FXML private TextField txtOpcaoA;
    @FXML private TextField txtOpcaoB;
    @FXML private TextField txtOpcaoC;
    @FXML private TextField txtOpcaoD;
    @FXML private ComboBox<String> cbGabarito;

    // Elementos vinculados ao Bloco de Discursiva
    @FXML private VBox blocoDiscursiva;
    @FXML private TextField txtLinhasEsperadas;
    @FXML private TextArea txtRespostaDiscursiva;

    private QuestionService questionService;
    private SubjectService subjectService;
    private Question questionEmEdicao;

    @FXML
    public void initialize() {
        this.questionService = new QuestionService(new QuestionDAO());
        this.subjectService = new SubjectService(new SubjectDAO());

        // Preenche as predefinições estáticas das ComboBoxes
        cbTipo.setItems(FXCollections.observableArrayList("Múltipla Escolha", "Discursiva"));
        cbNivel.setItems(FXCollections.observableArrayList("Fácil", "Médio", "Difícil"));
        cbGabarito.setItems(FXCollections.observableArrayList("A", "B", "C", "D"));

        // Carrega dinamicamente as disciplinas do banco
        try {
            List<Subject> disciplinas = subjectService.findAll();
            cbDisciplina.setItems(FXCollections.observableArrayList(disciplinas));
        } catch (Exception e) {
            System.err.println("Erro ao carregar disciplinas no modal de edição: " + e.getMessage());
        }

        // Formatação visual para o ComboBox exibir o nome da disciplina corretamente
        cbDisciplina.setCellFactory(p -> new ListCell<>() {
            @Override
            protected void updateItem(Subject item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getName());
            }
        });
        cbDisciplina.setButtonCell(cbDisciplina.getCellFactory().call(null));

        // 🎯 Escuta ativa para alternar visibilidade se mudarem o tipo de questão
        cbTipo.getSelectionModel().selectedItemProperty().addListener((obs, antigo, novo) -> {
            atualizarVisibilidadeBlocos(novo);
        });

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

        // Seleciona a disciplina atual da questão no ComboBox
        if (question.getSubject() != null) {
            for (Subject s : cbDisciplina.getItems()) {
                if (s.getIdSubject() == question.getSubject().getIdSubject()) {
                    cbDisciplina.setValue(s);
                    break;
                }
            }
        }

        // Traduz e carrega a Dificuldade
        if (question.getDifficulty() == Difficulty.EASY) cbNivel.setValue("Fácil");
        else if (question.getDifficulty() == Difficulty.HARD) cbNivel.setValue("Difícil");
        else cbNivel.setValue("Médio");

        // Carrega dados específicos baseando-se no tipo da questão e ativa o bloco correspondente
        if (question instanceof MultipleChoiceQuestion) {
            cbTipo.setValue("Múltipla Escolha");
            atualizarVisibilidadeBlocos("Múltipla Escolha");

            MultipleChoiceQuestion mcq = (MultipleChoiceQuestion) question;
            txtOpcaoA.setText(mcq.getAlternativeA());
            txtOpcaoB.setText(mcq.getAlternativeB());
            txtOpcaoC.setText(mcq.getAlternativeC());
            txtOpcaoD.setText(mcq.getAlternativeD());
            cbGabarito.setValue(question.getAnswerKey());
        } else if (question instanceof DiscursiveQuestion) {
            cbTipo.setValue("Discursiva");
            atualizarVisibilidadeBlocos("Discursiva");

            DiscursiveQuestion dq = (DiscursiveQuestion) question;
            txtLinhasEsperadas.setText(String.valueOf(dq.getExpectedLines()));
            txtRespostaDiscursiva.setText(dq.getAnswerKey());
        }

        cbTipo.setDisable(true); // Bloqueia o ComboBox de tipo para não quebrar regras relacionais do banco
    }

    /**
     * 🛡️ Método central que garante que o bloco não utilizado suma e fique desativado.
     */
    private void atualizarVisibilidadeBlocos(String tipo) {
        if (tipo == null) return;

        boolean ehDiscursiva = tipo.equalsIgnoreCase("Discursiva");

        // Gerenciamento dinâmico do bloco de Múltipla Escolha
        blocoMultiplaEscolha.setVisible(!ehDiscursiva);
        blocoMultiplaEscolha.setManaged(!ehDiscursiva);

        // Gerenciamento dinâmico do bloco de Discursiva
        blocoDiscursiva.setVisible(ehDiscursiva);
        blocoDiscursiva.setManaged(ehDiscursiva);
    }

    private void salvarAlteracoes() {
        try {
            questionEmEdicao.setStatement(txtEnunciado.getText());
            questionEmEdicao.setTopic(txtAssunto.getText());
            questionEmEdicao.setSubject(cbDisciplina.getValue());

            // Define o enum de Dificuldade
            String nivel = cbNivel.getValue();
            if ("Fácil".equals(nivel)) questionEmEdicao.setDifficulty(Difficulty.EASY);
            else if ("Difícil".equals(nivel)) questionEmEdicao.setDifficulty(Difficulty.HARD);
            else questionEmEdicao.setDifficulty(Difficulty.MEDIUM);

            // Mapeia o salvamento específico dependendo da instância correta do objeto
            if (questionEmEdicao instanceof MultipleChoiceQuestion) {
                MultipleChoiceQuestion mcq = (MultipleChoiceQuestion) questionEmEdicao;
                mcq.setAlternativeA(txtOpcaoA.getText());
                mcq.setAlternativeB(txtOpcaoB.getText());
                mcq.setAlternativeC(txtOpcaoC.getText());
                mcq.setAlternativeD(txtOpcaoD.getText());
                mcq.setAnswerKey(cbGabarito.getValue());
            } else if (questionEmEdicao instanceof DiscursiveQuestion) {
                DiscursiveQuestion dq = (DiscursiveQuestion) questionEmEdicao;

                int linhas = 10;
                try {
                    linhas = Integer.parseInt(txtLinhasEsperadas.getText().trim());
                } catch (NumberFormatException e) {
                    System.out.println("Formato de linhas inválido, assumindo 10.");
                }
                dq.setExpectedLines(linhas);
                dq.setAnswerKey(txtRespostaDiscursiva.getText());
            }

            // Executa a atualização no banco de dados
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
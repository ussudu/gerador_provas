package presenter.controllers;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.DAO.QuestionDAO;
import model.DAO.SubjectDAO;
import model.entities.*;
import model.services.QuestionService;
import model.services.SubjectService;

import java.util.List;

public class AddQuestionController {

    @FXML private Button btnFechar;
    @FXML private Button btnCancelar;
    @FXML private Button btnSalvar;
    @FXML private TextField txtCodigo;
    @FXML private ComboBox<String> cbTipo;
    @FXML private ComboBox<Subject> cbDisciplina;
    @FXML private TextField txtAssunto;
    @FXML private ComboBox<String> cbNivel;
    @FXML private TextArea txtEnunciado;

    // Elementos da Múltipla Escolha
    @FXML private VBox blocoMultiplaEscolha;
    @FXML private TextField txtOpcaoA;
    @FXML private TextField txtOpcaoB;
    @FXML private TextField txtOpcaoC;
    @FXML private TextField txtOpcaoD;
    @FXML private ComboBox<String> cbGabarito;

    // Elementos da Discursiva
    @FXML private VBox blocoDiscursiva;
    @FXML private TextField txtLinhasEsperadas;
    @FXML private TextArea txtRespostaDiscursiva;

    private QuestionService questionService;
    private SubjectService subjectService;

    @FXML
    public void initialize() {
        this.questionService = new QuestionService(new QuestionDAO());
        this.subjectService = new SubjectService(new SubjectDAO());

        // Configuração inicial dos combos
        cbTipo.setItems(FXCollections.observableArrayList("Múltipla Escolha", "Discursiva"));
        cbNivel.setItems(FXCollections.observableArrayList("Fácil", "Médio", "Difícil"));
        cbGabarito.setItems(FXCollections.observableArrayList("A", "B", "C", "D"));

        // Seleção padrão inicial
        cbTipo.setValue("Múltipla Escolha");
        blocoDiscursiva.setVisible(false);
        blocoDiscursiva.setManaged(false);

        // Preenche as disciplinas cadastradas no banco de dados
        try {
            List<Subject> disciplinas = subjectService.findAll();
            cbDisciplina.setItems(FXCollections.observableArrayList(disciplinas));
        } catch (Exception e) {
            System.err.println("Erro ao carregar disciplinas: " + e.getMessage());
        }

        cbDisciplina.setCellFactory(p -> new ListCell<>() {
            @Override
            protected void updateItem(Subject item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getName());
            }
        });
        cbDisciplina.setButtonCell(cbDisciplina.getCellFactory().call(null));

        // 🎯 O SEGREDO DO FLUXO: Alterna visibilidade total dos blocos na interface
        cbTipo.getSelectionModel().selectedItemProperty().addListener((obs, antigo, novo) -> {
            boolean ehDiscursiva = "Discursiva".equals(novo);

            // Gerenciamento do Bloco Múltipla Escolha
            blocoMultiplaEscolha.setVisible(!ehDiscursiva);
            blocoMultiplaEscolha.setManaged(!ehDiscursiva);

            // Gerenciamento do Bloco Discursiva
            blocoDiscursiva.setVisible(ehDiscursiva);
            blocoDiscursiva.setManaged(ehDiscursiva);
        });

        btnFechar.setOnAction(e -> fecharJanela());
        btnCancelar.setOnAction(e -> fecharJanela());
        btnSalvar.setOnAction(e -> salvarQuestao());
    }

    private void salvarQuestao() {
        try {
            String tipoSelecionado = cbTipo.getValue();
            String flagTipo = "Discursiva".equals(tipoSelecionado) ? "DISCURSIVE" : "MULTIPLE_CHOICE";

            Question q = model.DAO.QuestionFactory.createQuestion(flagTipo);
            q.setStatement(txtEnunciado.getText());
            q.setTopic(txtAssunto.getText());
            q.setSubject(cbDisciplina.getValue());

            // Configuração da Dificuldade
            String nivel = cbNivel.getValue();
            if ("Fácil".equals(nivel)) q.setDifficulty(Difficulty.EASY);
            else if ("Difícil".equals(nivel)) q.setDifficulty(Difficulty.HARD);
            else q.setDifficulty(Difficulty.MEDIUM);

            // Salvamento específico de acordo com a classe concreta instanciada
            if (q instanceof MultipleChoiceQuestion) {
                MultipleChoiceQuestion mcq = (MultipleChoiceQuestion) q;
                mcq.setAlternativeA(txtOpcaoA.getText());
                mcq.setAlternativeB(txtOpcaoB.getText());
                mcq.setAlternativeC(txtOpcaoC.getText());
                mcq.setAlternativeD(txtOpcaoD.getText());
                q.setAnswerKey(cbGabarito.getValue() != null ? cbGabarito.getValue() : "");
            } else if (q instanceof DiscursiveQuestion) {
                DiscursiveQuestion dq = (DiscursiveQuestion) q;

                int linhas = 10; // valor padrão
                if (txtLinhasEsperadas.getText() != null && !txtLinhasEsperadas.getText().trim().isEmpty()) {
                    try {
                        linhas = Integer.parseInt(txtLinhasEsperadas.getText().trim());
                    } catch (NumberFormatException e) {
                        System.out.println("Aviso: Número de linhas inválido, usando padrão de 10.");
                    }
                }
                dq.setExpectedLines(linhas);
                // Para a questão discursiva, a "Expectativa de Resposta" vira o texto da chave (answer_key) no banco
                q.setAnswerKey(txtRespostaDiscursiva.getText());
            }

            // Mapeamento automático do professor associado (Correção de ID da sua amiga)
            try (java.sql.Connection conn = model.DAO.ConnectionFactory.getConnection();
                 java.sql.PreparedStatement stmt = conn.prepareStatement("SELECT teacher_id FROM teacher ORDER BY teacher_id DESC LIMIT 1");
                 java.sql.ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Teacher t = new Teacher();
                    User u = new User();
                    u.setIdUser(rs.getInt("teacher_id"));
                    t.setUser(u);
                    q.setTeacher(t);
                }
            }

            questionService.insert(q);
            fecharJanela();

        } catch (Exception e) {
            System.err.println("Erro ao salvar questão: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void fecharJanela() {
        Stage stage = (Stage) btnFechar.getScene().getWindow();
        stage.close();
    }
}
package presenter.controllers;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import model.DAO.SubjectDAO;
import model.entities.Subject;
import model.services.SubjectService;

import java.util.ArrayList;
import java.util.List;

public class AddSubjectController {

    @FXML private Button btnFechar;
    @FXML private Button btnCancelar;
    @FXML private Button btnSalvar;
    @FXML private Button btnAddAssunto;
    @FXML private TextField txtNome;
    @FXML private TextField txtCodigo;
    @FXML private TextField txtAssunto;
    @FXML private FlowPane containerTags;

    private SubjectService subjectService;
    private List<String> listaDeAssuntos = new ArrayList<>();

    @FXML
    public void initialize() {
        this.subjectService = new SubjectService(new SubjectDAO());

        btnFechar.setOnAction(e -> fecharJanela());
        btnCancelar.setOnAction(e -> fecharJanela());
        btnSalvar.setOnAction(e -> salvarDisciplina());

        // Adiciona tag ao clicar no botão + ou ao apertar Enter no campo de assunto
        btnAddAssunto.setOnAction(e -> adicionarTagAssunto());
        txtAssunto.setOnAction(e -> adicionarTagAssunto());
    }

    private void adicionarTagAssunto() {
        String texto = txtAssunto.getText();
        if (texto != null && !texto.trim().isEmpty() && !listaDeAssuntos.contains(texto.trim())) {
            String assuntoLimpo = texto.trim();
            listaDeAssuntos.add(assuntoLimpo);

            // Cria o contêiner visual da tag (pílula azul)
            HBox tagBox = new HBox(6);
            tagBox.setAlignment(Pos.CENTER);
            tagBox.setStyle("-fx-background-color: #eff6ff; -fx-border-color: #bfdbfe; -fx-border-radius: 6; -fx-background-radius: 6; -fx-padding: 4 8;");

            Label lblTexto = new Label(assuntoLimpo);
            lblTexto.setStyle("-fx-text-fill: #1e40af; -fx-font-size: 13px;");

            Button btnRemove = new Button("×");
            btnRemove.setStyle("-fx-background-color: transparent; -fx-text-fill: #1e40af; -fx-font-weight: bold; -fx-cursor: hand; -fx-padding: 0;");
            btnRemove.setOnAction(e -> {
                listaDeAssuntos.remove(assuntoLimpo);
                containerTags.getChildren().remove(tagBox);
            });

            tagBox.getChildren().addAll(lblTexto, btnRemove);
            containerTags.getChildren().add(tagBox);

            txtAssunto.clear();
        }
    }

    private void salvarDisciplina() {
        try {
            Subject s = new Subject();
            s.setName(txtNome.getText());
            s.setCode(txtCodigo.getText());

            // Junta a lista de assuntos em uma única String separada por vírgula
            String stringTopics = String.join(", ", listaDeAssuntos);
            s.setTopics(stringTopics);

            int idDoProfessorLogado = 1; // Fallback padrão de segurança

            try (java.sql.Connection conn = model.DAO.ConnectionFactory.getConnection();
                 java.sql.PreparedStatement stmt = conn.prepareStatement("SELECT teacher_id FROM teacher ORDER BY teacher_id DESC LIMIT 1");
                 java.sql.ResultSet rs = stmt.executeQuery()) {

                if (rs.next()) {
                    // Recupera automaticamente o ID gerado pelo auto-incremento do último cadastro
                    idDoProfessorLogado = rs.getInt("teacher_id");
                }
            } catch (java.sql.SQLException e) {
                System.err.println("Erro ao recuperar ID automático do professor: " + e.getMessage());
            }

            // Monta os objetos vinculados com o ID correto encontrado
            model.entities.Teacher t = new model.entities.Teacher();
            model.entities.User u = new model.entities.User();
            u.setIdUser(idDoProfessorLogado);
            t.setUser(u);
            s.setTeacher(t);

            // Executa a validação e insere no banco
            subjectService.insert(s);
            fecharJanela();

        } catch (Exception e) {
            System.err.println("Erro ao salvar disciplina: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void fecharJanela() {
        Stage stage = (Stage) btnFechar.getScene().getWindow();
        stage.close();
    }
}
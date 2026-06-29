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

public class EditSubjectController {

    @FXML private Button btnFechar;
    @FXML private Button btnCancelar;
    @FXML private Button btnSalvar;
    @FXML private Button btnAddAssunto;
    @FXML private TextField txtNome;
    @FXML private TextField txtCodigo;
    @FXML private TextField txtAssunto;
    @FXML private FlowPane containerTags;

    private SubjectService subjectService;
    private Subject disciplinaEmEdicao;
    private List<String> listaDeAssuntos = new ArrayList<>();

    @FXML
    public void initialize() {
        this.subjectService = new SubjectService(new SubjectDAO());

        btnFechar.setOnAction(e -> fecharJanela());
        btnCancelar.setOnAction(e -> fecharJanela());
        btnSalvar.setOnAction(e -> salvarAlteracoes());

        btnAddAssunto.setOnAction(e -> adicionarTagAssunto());
        txtAssunto.setOnAction(e -> adicionarTagAssunto());
    }

    public void inicializarDados(Subject s) {
        this.disciplinaEmEdicao = s;
        txtNome.setText(s.getName());
        txtCodigo.setText(s.getCode());

        // Se a disciplina veio do banco com o ID do professor zerado ou nulo
        if (s.getTeacher() == null ||
                s.getTeacher().getUser() == null ||
                s.getTeacher().getUser().getIdUser() <= 0) {

            try (java.sql.Connection conn = model.DAO.ConnectionFactory.getConnection();
                 java.sql.PreparedStatement stmt = conn.prepareStatement("SELECT teacher_id FROM teacher ORDER BY teacher_id DESC LIMIT 1");
                 java.sql.ResultSet rs = stmt.executeQuery()) {

                int idDoProfessorLogado = 1; // Fallback caso o banco esteja totalmente vazio

                if (rs.next()) {
                    // Pega automaticamente o ID do último professor cadastrado via AUTO_INCREMENT do sistema
                    idDoProfessorLogado = rs.getInt("teacher_id");
                }

                model.entities.Teacher professor = new model.entities.Teacher();
                model.entities.User usuario = new model.entities.User();
                usuario.setIdUser(idDoProfessorLogado);
                professor.setUser(usuario);
                s.setTeacher(professor);

            } catch (java.sql.SQLException e) {
                System.err.println("Não foi possível recuperar o professor automático do banco: " + e.getMessage());
            }
        }

        containerTags.getChildren().clear();
        listaDeAssuntos.clear();

        if (s.getTopics() != null && !s.getTopics().trim().isEmpty()) {
            String[] topicsArray = s.getTopics().split(",");
            for (String topic : topicsArray) {
                remontarTagExistente(topic.trim());
            }
        }
    }

    private void remontarTagExistente(String assunto) {
        if (assunto.isEmpty() || listaDeAssuntos.contains(assunto)) return;
        listaDeAssuntos.add(assunto);

        HBox tagBox = new HBox(6);
        tagBox.setAlignment(Pos.CENTER);
        tagBox.setStyle("-fx-background-color: #eff6ff; -fx-border-color: #bfdbfe; -fx-border-radius: 6; -fx-background-radius: 6; -fx-padding: 4 8;");

        Label lblTexto = new Label(assunto);
        lblTexto.setStyle("-fx-text-fill: #1e40af; -fx-font-size: 13px;");

        Button btnRemove = new Button("×");
        btnRemove.setStyle("-fx-background-color: transparent; -fx-text-fill: #1e40af; -fx-font-weight: bold; -fx-cursor: hand; -fx-padding: 0;");

        // Sincroniza a remoção do clique tanto na tela quanto no array da memória
        btnRemove.setOnAction(e -> {
            listaDeAssuntos.remove(assunto);
            containerTags.getChildren().remove(tagBox);
        });

        tagBox.getChildren().addAll(lblTexto, btnRemove);
        containerTags.getChildren().add(tagBox);
    }

    private void adicionarTagAssunto() {
        String texto = txtAssunto.getText();
        if (texto != null && !texto.trim().isEmpty()) {
            remontarTagExistente(texto.trim());
            txtAssunto.clear();
        }
    }

    private void salvarAlteracoes() {
        try {
            disciplinaEmEdicao.setName(txtNome.getText());
            disciplinaEmEdicao.setCode(txtCodigo.getText());

            String stringTopics = String.join(", ", listaDeAssuntos);
            disciplinaEmEdicao.setTopics(stringTopics);

            //  Mantém o professor original que veio do banco (com seu respectivo user e id_user)
            // subjectService vai validar perfeitamente usando: disciplinaEmEdicao.getTeacher().getUser().getIdUser()

            subjectService.update(disciplinaEmEdicao);
            System.out.println("Alterações da disciplina salvas com sucesso!");
            fecharJanela();

        } catch (Exception e) {
            System.err.println("Erro ao atualizar disciplina: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void fecharJanela() {
        // Usa o próprio botão fechar para achar a Scene/Stage ativa e encerrar o modal
        Stage stage = (Stage) btnFechar.getScene().getWindow();
        stage.close();
    }
}
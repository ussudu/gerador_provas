package presenter.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.DAO.SubjectDAO;
import model.entities.Subject;
import model.services.SubjectService;

import java.util.List;

public class SubjectController {

    @FXML private Button btnNovaDisciplina;
    @FXML private FlowPane containerDisciplinas;
    @FXML private VBox lblEstadoVazio;

    private SubjectService subjectService;

    @FXML
    public void initialize() {
        this.subjectService = new SubjectService(new SubjectDAO());
        btnNovaDisciplina.setOnAction(e -> handleNovaDisciplina());
        carregarDisciplinas();
    }

    private void carregarDisciplinas() {
        containerDisciplinas.getChildren().clear();
        List<Subject> disciplinas = subjectService.findAll();

        System.out.println("Quantidade de disciplinas encontradas no banco: " + disciplinas.size());

        if (disciplinas.isEmpty()) {
            lblEstadoVazio.setVisible(true);
            lblEstadoVazio.setManaged(true);
            return;
        }

        lblEstadoVazio.setVisible(false);
        lblEstadoVazio.setManaged(false);

        for (Subject s : disciplinas) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/card-subject.fxml"));
                VBox card = loader.load();

                // Recupera o controlador do card instanciado pelo FXML
                CardSubjectController cardController = loader.getController();

                // Repassa o objeto e define as ações usando referências de método (::)
                cardController.setSubjectData(s, this::handleEditarDisciplina, this::handleDeletarDisciplina);

                containerDisciplinas.getChildren().add(card);

            } catch (Exception e) {
                System.err.println("Erro ao renderizar o card da disciplina: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private void handleNovaDisciplina() {
        abrirModal("/view/add-subject.fxml", "Nova Disciplina", null);
    }

    private void handleEditarDisciplina(Subject s) {
        abrirModal("/view/edit-subject.fxml", "Editar Disciplina", s);
    }

    private void abrirModal(String fxmlPath, String titulo, Subject subject) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();

            if (subject != null && fxmlPath.contains("edit")) {
                EditSubjectController controller = loader.getController();
                controller.inicializarDados(subject);
            }

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle(titulo);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

            carregarDisciplinas();
        } catch (Exception e) {
            System.err.println("Erro ao abrir janela modal: " + fxmlPath);
            e.printStackTrace();
        }
    }

    private void handleDeletarDisciplina(Subject s) {
        try {
            subjectService.delete(s.getIdSubject());
            carregarDisciplinas();
        } catch (Exception e) {
            System.err.println("Erro ao deletar disciplina: " + e.getMessage());
        }
    }
}
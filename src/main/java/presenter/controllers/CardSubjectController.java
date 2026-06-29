package presenter.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import model.entities.Subject;
import java.util.function.Consumer;

public class CardSubjectController {

    @FXML private Label lblNome;
    @FXML private Label lblCodigo;
    @FXML private Label lblQtdAssuntos;
    @FXML private FlowPane flowPaneTags;
    @FXML private Button btnEditar;
    @FXML private Button btnExcluir;

    private Subject subject;
    private Consumer<Subject> onEditAction;
    private Consumer<Subject> onDeleteAction;

    @FXML
    public void initialize() {
        // Vincula as ações de clique chamando os callbacks passados pelo pai
        btnEditar.setOnAction(e -> {
            if (onEditAction != null) onEditAction.accept(subject);
        });

        btnExcluir.setOnAction(e -> {
            if (onDeleteAction != null) onDeleteAction.accept(subject);
        });
    }

    public void setSubjectData(Subject s, Consumer<Subject> onEdit, Consumer<Subject> onDelete) {
        this.subject = s;
        this.onEditAction = onEdit;
        this.onDeleteAction = onDelete;

        // Injeta os dados textuais nos componentes locais
        if (lblNome != null) lblNome.setText(s.getName());
        if (lblCodigo != null) lblCodigo.setText("Código: " + s.getCode());

        // Processa as tags dinamicamente
        if (flowPaneTags != null) {
            flowPaneTags.getChildren().clear();

            if (s.getTopics() != null && !s.getTopics().trim().isEmpty()) {
                String[] topics = s.getTopics().split(",");

                if (lblQtdAssuntos != null) {
                    lblQtdAssuntos.setText("Assuntos (" + topics.length + ")");
                }

                for (String topic : topics) {
                    if (!topic.trim().isEmpty()) {
                        Label tag = new Label(topic.trim());
                        tag.getStyleClass().add("tag-assunto");
                        flowPaneTags.getChildren().add(tag);
                    }
                }
            } else {
                if (lblQtdAssuntos != null) {
                    lblQtdAssuntos.setText("Assuntos (0)");
                }
            }
        }
    }
}
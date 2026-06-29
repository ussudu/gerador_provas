package presenter.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import model.entities.MultipleChoiceQuestion;
import model.entities.DiscursiveQuestion;
import model.entities.Question;
import java.util.function.Consumer;

public class CardQuestionController {

    @FXML private Label lblIdQuestao;
    @FXML private Label lblNivelQuestao;
    @FXML private Label lblTipoQuestao;
    @FXML private Label lblCaminho;
    @FXML private Label lblEnunciado;
    @FXML private Label lblGabarito;

    @FXML private VBox boxAlternativas;
    @FXML private Label lblOpcaoA;
    @FXML private Label lblOpcaoB;
    @FXML private Label lblOpcaoC;
    @FXML private Label lblOpcaoD;

    @FXML private Button btnEditarQuestao;
    @FXML private Button btnExcluirQuestao;

    public void setQuestionData(Question q, Consumer<Question> onEdit, Consumer<Question> onDelete) {
        // 1. Identificador da Questão
        if (lblIdQuestao != null) lblIdQuestao.setText("Q-" + q.getQuestionId());

        // 2. Mapeamento do Nível para Português e Aplicação das Cores do CSS
        if (lblNivelQuestao != null && q.getDifficulty() != null) {
            String diffStr = q.getDifficulty().name();

            // Limpa as classes antigas para evitar sobreposição de cores
            lblNivelQuestao.getStyleClass().removeAll("badge-facil", "badge-medio", "badge-dificil", "badge-padrao");

            if ("EASY".equalsIgnoreCase(diffStr)) {
                lblNivelQuestao.setText("FÁCIL");
                lblNivelQuestao.getStyleClass().add("badge-facil");
            } else if ("HARD".equalsIgnoreCase(diffStr)) {
                lblNivelQuestao.setText("DIFÍCIL");
                lblNivelQuestao.getStyleClass().add("badge-dificil");
            } else {
                lblNivelQuestao.setText("MÉDIO");
                lblNivelQuestao.getStyleClass().add("badge-medio");
            }
        }

        // 3. Enunciado da Questão
        if (lblEnunciado != null) lblEnunciado.setText(q.getStatement());

        // 4. Correção do Caminho (Evitando Disciplina com valor null)
        if (lblCaminho != null) {
            String nomeDisciplina = "Geral";
            if (q.getSubject() != null) {
                // Se o nome estiver nulo no objeto, tenta exibir uma string padrão ou o ID,
                // mas caso o seu Subject venha preenchido, ele usará o getName() perfeitamente.
                nomeDisciplina = q.getSubject().getName() != null ? q.getSubject().getName() : "Disciplina";
            }
            String nomeTopico = (q.getTopic() != null && !q.getTopic().trim().isEmpty()) ? q.getTopic() : "Geral";
            lblCaminho.setText(nomeDisciplina + " > " + nomeTopico);
        }

        // 5. Configuração específica de Múltipla Escolha vs Discursiva
        if (q instanceof MultipleChoiceQuestion) {
            if (lblTipoQuestao != null) lblTipoQuestao.setText("Múltipla Escolha");
            if (lblGabarito != null) lblGabarito.setText("Gabarito: " + q.getAnswerKey());
            if (boxAlternativas != null) {
                boxAlternativas.setVisible(true);
                boxAlternativas.setManaged(true);
            }

            MultipleChoiceQuestion mcq = (MultipleChoiceQuestion) q;
            if (lblOpcaoA != null) lblOpcaoA.setText("A) " + mcq.getAlternativeA());
            if (lblOpcaoB != null) lblOpcaoB.setText("B) " + mcq.getAlternativeB());
            if (lblOpcaoC != null) lblOpcaoC.setText("C) " + mcq.getAlternativeC());
            if (lblOpcaoD != null) lblOpcaoD.setText("D) " + mcq.getAlternativeD());

        } else if (q instanceof DiscursiveQuestion) {
            if (lblTipoQuestao != null) lblTipoQuestao.setText("Discursiva");
            if (lblGabarito != null) lblGabarito.setText("Expectativa de Resposta: " + q.getAnswerKey());

            // Oculta o bloco de alternativas estáticas caso a questão seja discursiva
            if (boxAlternativas != null) {
                boxAlternativas.setVisible(false);
                boxAlternativas.setManaged(false);
            }
        }

        // 6. Cliques de Ação
        if (btnEditarQuestao != null) btnEditarQuestao.setOnAction(e -> onEdit.accept(q));
        if (btnExcluirQuestao != null) btnExcluirQuestao.setOnAction(e -> onDelete.accept(q));
    }
}
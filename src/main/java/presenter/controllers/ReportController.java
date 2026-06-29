package presenter.controllers;

import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import model.DAO.ExamDAO;
import model.DAO.QuestionDAO;
import model.DAO.SubjectDAO;
import model.entities.Exam;
import model.entities.Question;
import model.entities.Subject;
import model.services.ExamService;
import model.services.QuestionService;
import model.services.SubjectService;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class ReportController {

    @FXML private Label lblTotalProvas;
    @FXML private Label lblTotalQuestoes;
    @FXML private Label lblTotalDisciplinas;

    @FXML private Label lblFacilQtd;
    @FXML private Label lblFacilPorcento;
    @FXML private Label lblMedioQtd;
    @FXML private Label lblMedioPorcento;
    @FXML private Label lblDificilQtd;
    @FXML private Label lblDificilPorcento;

    @FXML private GridPane gridDisciplinas;
    @FXML private GridPane gridProvas;

    private ExamService examService;
    private QuestionService questionService;
    private SubjectService subjectService;

    private int linhaAtualDisciplina = 1;
    private int linhaAtualProva = 1;

    @FXML
    public void initialize() {
        try {
            QuestionDAO questionDAO = new QuestionDAO();
            this.questionService = new QuestionService(questionDAO);
            this.examService = new ExamService(new ExamDAO(), questionDAO);
            this.subjectService = new SubjectService(new SubjectDAO());

            gerarRelatoriosEEstatistias();

        } catch (Exception e) {
            System.err.println("Erro ao inicializar serviços no ReportController: " + e.getMessage());
        }
    }

    private void gerarRelatoriosEEstatistias() {
        try {
            List<Exam> todasProvas = examService.findAll();
            List<Question> todasQuestoes = questionService.findAll();
            List<Subject> todasDisciplinas = subjectService.findAll();

            // --- 1. CARDS SUPERIORES ---
            lblTotalProvas.setText(String.valueOf(todasProvas.size()));
            lblTotalQuestoes.setText(String.valueOf(todasQuestoes.size()));
            lblTotalDisciplinas.setText(String.valueOf(todasDisciplinas.size()));

            // --- 2. NÍVEIS DE DIFICULDADE ---
            int qtdFacil = 0, qtdMedio = 0, qtdDificil = 0;
            for (Question q : todasQuestoes) {
                String nivel = String.valueOf(q.getDifficulty()).toUpperCase();
                if (nivel.contains("FACIL") || nivel.contains("EASY") || nivel.contains("FÁCIL")) {
                    qtdFacil++;
                } else if (nivel.contains("MEDIO") || nivel.contains("MEDIUM") || nivel.contains("MÉDIO")) {
                    qtdMedio++;
                } else if (nivel.contains("DIFICIL") || nivel.contains("HARD") || nivel.contains("DIFÍCIL")) {
                    qtdDificil++;
                }
            }

            int totalQ = todasQuestoes.size();
            int porcFacil = totalQ > 0 ? (qtdFacil * 100) / totalQ : 0;
            int porcMedio = totalQ > 0 ? (qtdMedio * 100) / totalQ : 0;
            int porcDificil = totalQ > 0 ? (qtdDificil * 100) / totalQ : 0;

            lblFacilQtd.setText(String.valueOf(qtdFacil));
            lblFacilPorcento.setText(porcFacil + "% do total");
            lblMedioQtd.setText(String.valueOf(qtdMedio));
            lblMedioPorcento.setText(porcMedio + "% do total");
            lblDificilQtd.setText(String.valueOf(qtdDificil));
            lblDificilPorcento.setText(porcDificil + "% do total");

            // --- 3. ESTATÍSTICAS POR DISCIPLINA ---
            for (Subject subj : todasDisciplinas) {
                int qTot = 0, pTot = 0, f = 0, m = 0, d = 0;

                for (Question q : todasQuestoes) {
                    // Utiliza o getIdSubject() real da classe Subject
                    if (q.getSubject() != null && q.getSubject().getIdSubject() == subj.getIdSubject()) {
                        qTot++;
                        String nivel = String.valueOf(q.getDifficulty()).toUpperCase();
                        if (nivel.contains("FACIL") || nivel.contains("EASY") || nivel.contains("FÁCIL")) f++;
                        else if (nivel.contains("MEDIO") || nivel.contains("MEDIUM") || nivel.contains("MÉDIO")) m++;
                        else if (nivel.contains("DIFICIL") || nivel.contains("HARD") || nivel.contains("DIFÍCIL")) d++;
                    }
                }

                for (Exam ex : todasProvas) {
                    if (ex.getSubject() != null && ex.getSubject().getIdSubject() == subj.getIdSubject()) {
                        pTot++;
                    }
                }

                // Utiliza o getCode() real da disciplina para exibir o código na tabela
                String codigoExibicao = (subj.getCode() != null && !subj.getCode().isEmpty()) ? subj.getCode() : "DS" + subj.getIdSubject();
                adicionarLinhaDisciplina(subj.getName(), codigoExibicao, qTot, pTot, f, m, d);
            }

            // --- 4. HISTÓRICO DE PROVAS ---
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

            for (Exam ex : todasProvas) {
                // Monta um título baseado no ID real da prova se o título não for um atributo explícito
                String titulo = "Prova #" + ex.getExamId();
                String nomeSubj = ex.getSubject() != null ? ex.getSubject().getName() : "Sem Disciplina";

                // Utiliza o getSemester() real da classe Exam
                String semestre = ex.getSemester() != null ? ex.getSemester() : "2026.1";
                int numQuestoes = ex.getQuestions() != null ? ex.getQuestions().size() : 0;

                // Utiliza o getCreationDate() real da classe Exam formatado bonitinho
                String dataStr = ex.getCreationDate() != null ? ex.getCreationDate().format(formatter) : "Recente";

                adicionarLinhaProva(titulo, nomeSubj, semestre, numQuestoes, dataStr);
            }

        } catch (Exception e) {
            System.err.println("Erro ao processar relatórios: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void adicionarLinhaDisciplina(String nome, String codigo, int questoes, int provas, int faceis, int medias, int dificeis) {
        Label lblNome = new Label(nome);
        lblNome.setWrapText(true);
        lblNome.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #334155;");
        gridDisciplinas.add(lblNome, 0, linhaAtualDisciplina);

        Label lblCodigo = new Label(codigo);
        lblCodigo.setStyle("-fx-font-size: 13px; -fx-text-fill: #64748b;");
        gridDisciplinas.add(lblCodigo, 1, linhaAtualDisciplina);

        gridDisciplinas.add(criarBadgeCinza(String.valueOf(questoes)), 2, linhaAtualDisciplina);
        gridDisciplinas.add(criarBadgeCinza(String.valueOf(provas)), 3, linhaAtualDisciplina);

        Label lblF = new Label(String.valueOf(faceis));
        lblF.setStyle("-fx-background-color: #f0fdf4; -fx-text-fill: #16a34a; -fx-background-radius: 10; -fx-padding: 2 12; -fx-font-weight: bold; -fx-font-size: 11px;");
        GridPane.setHalignment(lblF, HPos.CENTER);
        gridDisciplinas.add(lblF, 4, linhaAtualDisciplina);

        Label lblM = new Label(String.valueOf(medias));
        lblM.setStyle("-fx-background-color: #fefce8; -fx-text-fill: #ca8a04; -fx-background-radius: 10; -fx-padding: 2 12; -fx-font-weight: bold; -fx-font-size: 11px;");
        GridPane.setHalignment(lblM, HPos.CENTER);
        gridDisciplinas.add(lblM, 5, linhaAtualDisciplina);

        Label lblD = new Label(String.valueOf(dificeis));
        lblD.setStyle("-fx-background-color: #fef2f2; -fx-text-fill: #dc2626; -fx-background-radius: 10; -fx-padding: 2 12; -fx-font-weight: bold; -fx-font-size: 11px;");
        GridPane.setHalignment(lblD, HPos.CENTER);
        gridDisciplinas.add(lblD, 6, linhaAtualDisciplina);

        linhaAtualDisciplina++;
    }

    private void adicionarLinhaProva(String titulo, String disciplina, String semestre, int questoes, String dataCriacao) {
        Label lblTitulo = new Label(titulo);
        lblTitulo.setWrapText(true);
        lblTitulo.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #334155;");
        gridProvas.add(lblTitulo, 0, linhaAtualProva);

        Label lblDisc = new Label(disciplina);
        lblDisc.setWrapText(true);
        lblDisc.setStyle("-fx-font-size: 13px; -fx-text-fill: #475569;");
        gridProvas.add(lblDisc, 1, linhaAtualProva);

        Label lblSem = new Label(semestre);
        lblSem.setAlignment(Pos.CENTER);
        lblSem.setMaxWidth(Double.MAX_VALUE);
        lblSem.setStyle("-fx-font-size: 13px; -fx-text-fill: #64748b;");
        gridProvas.add(lblSem, 2, linhaAtualProva);

        gridProvas.add(criarBadgeCinza(String.valueOf(questoes)), 3, linhaAtualProva);

        Label lblData = new Label(dataCriacao);
        lblData.setAlignment(Pos.CENTER_RIGHT);
        lblData.setMaxWidth(Double.MAX_VALUE);
        lblData.setStyle("-fx-font-size: 13px; -fx-text-fill: #475569;");
        gridProvas.add(lblData, 4, linhaAtualProva);

        linhaAtualProva++;
    }

    private Label criarBadgeCinza(String texto) {
        Label badge = new Label(texto);
        badge.setStyle("-fx-background-color: #f8fafc; -fx-background-radius: 10; -fx-padding: 2 10; -fx-font-size: 12px; -fx-border-color: #e2e8f0; -fx-border-radius: 10;");
        GridPane.setHalignment(badge, HPos.CENTER);
        return badge;
    }
}
package presenter.controllers;

import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent;
import javafx.scene.control.Button;
import presenter.utils.SceneManager;

public class SidebarController {

    @FXML
    private Button btnHome;

    @FXML
    private Button btnSubjects;

    @FXML
    private Button btnQuestions;

    @FXML
    private Button btnExams;

    @FXML
    private Button btnReports;

    @FXML
    private Button btnUsers;

    @FXML
    public void initialize() {
        // Vincula as ações de clique às funções de navegação utilizando o SceneManager
        btnHome.setOnMouseClicked(this::handleHome);
        btnSubjects.setOnMouseClicked(this::handleSubjects);
        btnQuestions.setOnMouseClicked(this::handleQuestions);
        btnExams.setOnMouseClicked(this::handleExams);
        btnReports.setOnMouseClicked(this::handleReports);
        btnUsers.setOnMouseClicked(this::handleUsers);
    }

    private void handleHome(MouseEvent event) {
        SceneManager.getInstance().navigateTo("/view/home.fxml", "Página Inicial");
    }

    private void handleSubjects(MouseEvent event) {
        SceneManager.getInstance().navigateTo("/view/subject.fxml", "Gerenciamento de Disciplinas");
    }

    private void handleQuestions(MouseEvent event) {
        SceneManager.getInstance().navigateTo("/view/question.fxml", "Gerenciamento de Questões");
    }

    private void handleExams(MouseEvent event) {
        SceneManager.getInstance().navigateTo("/view/exam.fxml", "Gerenciamento de Provas");
    }

    private void handleReports(MouseEvent event) {
        SceneManager.getInstance().navigateTo("/view/report.fxml", "Gerenciamento de Relatórios");
    }

    private void handleUsers(MouseEvent event) {
        SceneManager.getInstance().navigateTo("/view/user.fxml", "Gerenciamento de Usuários");
    }
}
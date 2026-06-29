package presenter.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import presenter.utils.SceneManager;
import presenter.utils.SessionManager;

import java.net.URL;
import java.util.ResourceBundle;

public class HeaderController implements Initializable {
    @FXML
    private Button btnLogout;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (SessionManager.getInstance().isLoggedIn()) {
            btnLogout.setVisible(true);
        } else {
            btnLogout.setVisible(false);
        }
    }
    @FXML
        public void btnLogout(ActionEvent event)
    {
        SessionManager.getInstance().logout();
        SceneManager.getInstance().navigateTo("/view/login.fxml", "Sistema de Provas - Entrar");
    }
}

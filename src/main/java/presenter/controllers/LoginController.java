package presenter.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import model.DAO.UserDAO;
import model.entities.User;
import model.services.UserService;
import presenter.utils.SceneManager;

public class LoginController {
    @FXML
    private TextField txtEmail;

    @FXML
    private PasswordField txtPassword;

    @FXML
    private Label lbError;
    
    private UserService userService;
    
    public LoginController() {
        this.userService = new UserService(new UserDAO());
    }

    @FXML
    public void onBtnLoginAction(ActionEvent event) {
        String email = txtEmail.getText();
        String password = txtPassword.getText();

        lbError.setText("");

        try {
            User user = userService.authenticate(email, password);

            if (user != null) {
                SceneManager.getInstance().navigateTo("/view/home.fxml", "Página inicial do usuário");
            } else {
                showLoginError();
            }
        } catch (Exception e) {
            lbError.setText(e.getMessage());
        }
    }

    @FXML
    public void onBtnSignUpAction(ActionEvent event) {
        SceneManager.getInstance().navigateTo("/view/sign-up.fxml", "Cadastro de Professor");
    }

    @FXML
    public void onBtnUpdatePasswordAction(ActionEvent event) {
        SceneManager.getInstance().navigateTo("/view/update-password.fxml", "Alteração de senha do usuário");
    }


    private void showLoginError() {

        lbError.setText("E-mail ou senha incorretos.");
        txtPassword.clear();
        txtPassword.requestFocus();
    }
}
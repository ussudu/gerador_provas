package presenter.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import model.DAO.UserDAO;
import model.entities.User;
import model.exceptions.RegraNegocioException;
import model.services.UserService;
import presenter.utils.SceneManager;

public class UpdatePasswordController {

    @FXML
    private TextField txtEmail;

    @FXML
    private TextField txtNewPassword;

    private UserDAO userDAO = new UserDAO();
    private UserService userService = new UserService(userDAO);

    @FXML
    public void btnUpdatePassword(ActionEvent event) {

        String email = txtEmail.getText().trim();
        String newPassword = txtNewPassword.getText().trim();

        if (email.isEmpty() || newPassword.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Campos Vazios", "Por favor, preencha todos os campos.");
            return;
        }

        try {
            User user = userDAO.findByEmail(email);

            if (user == null) {
                showAlert(Alert.AlertType.ERROR, "Erro", "E-mail não encontrado.");
                return;
            }

            userService.updatePassword(user.getIdUser(), newPassword);

            showAlert(Alert.AlertType.INFORMATION, "Sucesso", "Senha alterada!");
            txtEmail.clear();
            txtNewPassword.clear();

            SceneManager.getInstance().navigateTo("/view/login.fxml", "Sistema de Provas - Entrar");

        } catch (RegraNegocioException e) {
            showAlert(Alert.AlertType.WARNING, "Aviso", e.getMessage());
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erro Crítico", "Erro no sistema: " + e.getMessage());
        }
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
    @FXML
    public void btnLogin(ActionEvent event)
    {
        SceneManager.getInstance().navigateTo("/view/login.fxml", "Sistema de Provas - Entrar");
    }
}
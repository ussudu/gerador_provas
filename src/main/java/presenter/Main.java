package presenter;

import javafx.application.Application;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import model.DAO.ConnectionFactory;
import presenter.utils.SceneManager;

import java.sql.Connection;
import java.sql.SQLException;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {

        try (Connection conn = ConnectionFactory.getConnection()) {

            System.out.println("Conexão com o banco de dados realizada perfeitamente!");

            SceneManager.getInstance().setPrimaryStage(primaryStage);
            primaryStage.setMaximized(true);
            SceneManager.getInstance().navigateTo("/view/login.fxml", "Sistema de Provas - Entrar");

        } catch (SQLException e) {
            System.err.println("Não foi possível conectar ao banco de dados.");
            System.err.println("Motivo: " + e.getMessage());

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erro Crítico de Inicialização");
            alert.setHeaderText("Falha na conexão com o Banco de Dados");
            alert.setContentText("O sistema não conseguiu se conectar ao MySQL.\nVerifique se o servidor está ativo.\n\nDetalhe técnico: " + e.getMessage());
            alert.showAndWait();

            System.exit(1);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
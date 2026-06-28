package presenter.utils;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class SceneManager {

    private static SceneManager instance;
    private Stage primaryStage;

    private SceneManager() {
    }

    public static SceneManager getInstance() {
        if (instance == null) {
            instance = new SceneManager();
        }
        return instance;
    }

    public void setPrimaryStage(Stage stage) {
        this.primaryStage = stage;
    }

    public void navigateTo(String fxmlPath, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();

            if (primaryStage.getScene() == null) {
                primaryStage.setScene(new Scene(root));
            } else {
                primaryStage.getScene().setRoot(root);
            }

            primaryStage.setTitle(title);
            primaryStage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
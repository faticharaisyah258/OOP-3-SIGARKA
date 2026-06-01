package sigarka.scenes;

import java.util.Optional;

import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import sigarka.View.AppStyle;

public class LogoutSc {
        
        public static Button createLogoutButton(Stage stage) {
                
                // ===== BUTTON =====
                Button btnLogout = new Button("Logout");
                btnLogout.setStyle("-fx-background-color: transparent; " +
                                "-fx-text-fill: red; " +
                                "-fx-font-weight: bold; " +
                                "-fx-font-size: 14px; " +
                                "-fx-cursor: hand;");
                
                // ===== LABEL TANYA =====
                btnLogout.setOnAction(e -> {
                        
                        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                        alert.setTitle("Konfirmasi Logout");
                        alert.setHeaderText(null);
                        alert.setContentText("Yakin ingin logout?");

                        alert.setGraphic(null);

                // ===== BUTTON =====
                        ButtonType btnYes = new ButtonType("Yes");
                        ButtonType btnNo = new ButtonType("No");
                        alert.getButtonTypes().setAll(btnYes, btnNo);

                        Button btnYesNode = (Button) alert.getDialogPane().lookupButton(btnYes);
                        Button btnNoNode = (Button) alert.getDialogPane().lookupButton(btnNo);

                        btnYesNode.setStyle("-fx-background-color:" + AppStyle.BLUE_COLOR + "; -fx-text-fill: white; -fx-cursor: hand;"); 
                        btnNoNode.setStyle("-fx-background-color: " + AppStyle.LIGHTGREEN_COLOR + "; -fx-text-fill: " + AppStyle.NOTSOBLACK_COLOR + "; -fx-cursor: hand;");

                        Optional<ButtonType> result = alert.showAndWait();

                        if (result.isPresent() && result.get() == btnYes) {
                                stage.setScene(LoginSc.createScene(stage));
                                javafx.application.Platform.runLater(() -> {
                                        stage.setMaximized(false);
                                        stage.setMaximized(true);
                                });
                        }
                });

                return btnLogout;
        }
}
package sigarka.scenes;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import sigarka.View.AppStyle;

public class LoginSc {

    public static Scene createScene(Stage stage) {

        Font gloockLogoFont = Font.loadFont(LoginSc.class.getResourceAsStream("/Assets/Fonts/Gloock-Regular.ttf"), 30);

        // ===== CONTAINER =====
        VBox logoContainer = new VBox(-15);
        logoContainer.setAlignment(Pos.CENTER);
        
        Image logoImg = new Image(LoginSc.class.getResourceAsStream("/Assets/images/Logo/Hanya_Logo.png"));
        ImageView logoView = new ImageView(logoImg);
        logoView.setFitWidth(200);
        logoView.setPreserveRatio(true);

        // teks SIGARKA
        Label appName = new Label("SIGARKA");
        appName.setFont(gloockLogoFont);
        appName.setStyle("-fx-text-fill: " + AppStyle.BLUE_COLOR + ";");

        // Teks Sistem Gaji Karyawan
        Label appSubName = new Label("Sistem Gaji Karyawan");
        appSubName.setFont(Font.font("System", FontWeight.NORMAL, 12));
        appSubName.setStyle("-fx-text-fill: " + AppStyle.TOSKA_COLOR + ";");

        VBox.setMargin(appSubName, new Insets(15, 0, 0, 0));
        logoContainer.getChildren().addAll(logoView, appName, appSubName);


        // ===== LOGIN FORM =====
        Label loginTitle = new Label("Login Admin");
        loginTitle.setFont(Font.font("System", FontWeight.BOLD, 18));
        loginTitle.setStyle("-fx-text-fill: " + AppStyle.BLUE_COLOR + ";");


        // ===== USERNAME =====
        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");
        String fieldBaseStyle = "; -fx-border-color: " + AppStyle.SOFT_GRAY_COLOR + "; -fx-border-radius: 0; -fx-padding: 8;";
        usernameField.setStyle("-fx-background-color: white" + fieldBaseStyle);

        usernameField.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                usernameField.setStyle("-fx-background-color: " + AppStyle.LIGHTGREEN_COLOR + fieldBaseStyle);
            } else {
                usernameField.setStyle("-fx-background-color: white" + fieldBaseStyle);
            }
        });

        // ===== PASSWORD =====
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        passwordField.setStyle("-fx-background-color: white" + fieldBaseStyle);

        passwordField.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                passwordField.setStyle("-fx-background-color: " + AppStyle.LIGHTGREEN_COLOR + fieldBaseStyle);
            } else {
                passwordField.setStyle("-fx-background-color: white" + fieldBaseStyle);
            }
        });


        // ===== BUTTON LOGIN =====
        Button loginBtn = new Button("LOGIN");
        loginBtn.setStyle(AppStyle.BLUE_BUTTON);
        loginBtn.setMaxWidth(Double.MAX_VALUE);


        // ===== INFO =====
        Label info = new Label();
        info.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");

        // ===== LOGIN ACTION =====
        loginBtn.setOnAction(e -> {

            String username = usernameField.getText();
            String password = passwordField.getText();

            String validUsername = "admin";
            String validPassword = "123";

            // Login berhasil
            if (username.equals(validUsername) && 
                password.equals(validPassword)) {

                stage.setScene(MenuSc.createScene(stage));
                javafx.application.Platform.runLater(() -> {
                    stage.setMaximized(false);
                    stage.setMaximized(true);
                });
            }

            // Username atau Password salah
            else if (!username.equals(validUsername) ||
                     !password.equals(validPassword)) {

                
                info.setText("Username atau Password tidak valid!");
            }
        });

        // ===== CARD =====
        VBox card = new VBox(15);
        card.setAlignment(Pos.CENTER); 
        card.setPadding(new Insets(30, 40, 30, 40)); 
        card.setMaxWidth(350); 
        card.setStyle(AppStyle.CARD_STYLE);


        card.getChildren().addAll(
                loginTitle,
                usernameField,
                passwordField,
                loginBtn,
                info
        );

        // ===== WRAPPER =====
        VBox mainWrapper = new VBox(30); 
        mainWrapper.setAlignment(Pos.CENTER);
        mainWrapper.getChildren().addAll(logoContainer, card);


        // ===== ROOT =====
        StackPane root = new StackPane();
        root.setStyle("-fx-background-color: " + AppStyle.NOTSOWHITE_COLOR + ";");
        

        root.getChildren().add(mainWrapper);

        return new Scene(root, 900, 600);
    }
}
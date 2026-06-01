package sigarka.scenes;

import java.io.InputStream;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import sigarka.View.AppStyle;
import sigarka.View.IconLoader;

public class MenuSc {

    private static StackPane createIconWrapper(Node icon) {
        StackPane wrapper = new StackPane(icon);
        wrapper.setPrefWidth(30); 
        wrapper.setAlignment(Pos.CENTER);
        return wrapper;
    }

    public static Scene createScene(Stage stage) {

    // === SIDEBAR ===
        VBox sidebar = new VBox();
        sidebar.setPrefWidth(250);
        sidebar.setStyle("-fx-background-color: transparent;"); 

        HBox logoSection = new HBox(15); 
        logoSection.setAlignment(Pos.CENTER_LEFT);
        logoSection.setPadding(new Insets(20, 20, 20, 25));
        logoSection.setStyle("-fx-background-color: " + AppStyle.NOTSOWHITE_COLOR + ";");

        Font gloockFont = Font.loadFont(MenuSc.class.getResourceAsStream("/Assets/Fonts/Gloock-Regular.ttf"), 22);

        Label sigarkaLabel = new Label("SIGARKA");
        if (gloockFont != null) {
            sigarkaLabel.setFont(gloockFont);
        } else {
            sigarkaLabel.setFont(Font.font("System", FontWeight.BOLD, 20));
        }
        sigarkaLabel.setStyle("-fx-text-fill: " + AppStyle.BLUE_COLOR + ";");

        InputStream logoStream = MenuSc.class.getResourceAsStream("/Assets/images/Logo/Hanya_Logo.png");
        
        if (logoStream != null) {
            Image logoImg = new Image(logoStream);
            ImageView logoView = new ImageView(logoImg);
            logoView.setFitWidth(75); 
            logoView.setPreserveRatio(true);
            
            logoSection.getChildren().addAll(logoView, sigarkaLabel);
        } else {
            logoSection.getChildren().add(sigarkaLabel);
        }

        VBox menuContainer = new VBox(10);
        menuContainer.setPadding(new Insets(30, 0, 20, 20)); 
        VBox.setVgrow(menuContainer, Priority.ALWAYS); 

        menuContainer.setStyle(
            "-fx-background-color: " + AppStyle.TOSKA_COLOR + ";" +
            "-fx-background-radius: 0 20 0 0;" 
        );


    // === BUTTON MENU ===
        Button btnKaryawan = new Button("Data Karyawan");
        Button btnHitung = new Button("Hitung Gaji");
        Button btnSlip = new Button("Slip Gaji");

        btnKaryawan.setMaxWidth(Double.MAX_VALUE);
        btnHitung.setMaxWidth(Double.MAX_VALUE);
        btnSlip.setMaxWidth(Double.MAX_VALUE);

        // Tampilan awal (Default: Karyawan aktif)
        btnKaryawan.setStyle(AppStyle.MENU_DASHBOARD_ACTIVE); 
        btnHitung.setStyle(AppStyle.MENU_DASHBOARD_INACTIVE);
        btnSlip.setStyle(AppStyle.MENU_DASHBOARD_INACTIVE);

        // Ikon awal (Default: Karyawan Biru, sisanya NotSoWhite)
        btnKaryawan.setGraphic(createIconWrapper(IconLoader.loadSvgIcon("/Assets/icons/TambahKaryawan.svg", AppStyle.BLUE_COLOR, 0.6)));
        btnHitung.setGraphic(createIconWrapper(IconLoader.loadSvgIcon("/Assets/icons/Hitunggaji.svg", AppStyle.NOTSOWHITE_COLOR, 0.6)));
        btnSlip.setGraphic(createIconWrapper(IconLoader.loadSvgIcon("/Assets/icons/SlipGaji.svg", AppStyle.NOTSOWHITE_COLOR, 0.6)));

        btnKaryawan.setGraphicTextGap(15);
        btnHitung.setGraphicTextGap(15);
        btnSlip.setGraphicTextGap(9);

        Button btnLogout = LogoutSc.createLogoutButton(stage);
        btnLogout.setMaxWidth(Double.MAX_VALUE);

        btnLogout.setStyle("-fx-background-color: transparent; -fx-text-fill: " + AppStyle.WHITE_COLOR + 
                           "; -fx-font-weight: bold; -fx-cursor: hand; -fx-alignment: CENTER_LEFT; -fx-padding: 12 20;");
        btnLogout.setGraphic(createIconWrapper(IconLoader.loadSvgIcon("/Assets/icons/Logout.svg", AppStyle.WHITE_COLOR, 0.6)));
        btnLogout.setGraphicTextGap(15);
        
        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        menuContainer.getChildren().addAll(btnKaryawan, btnHitung, btnSlip, spacer, btnLogout);
        sidebar.getChildren().addAll(logoSection, menuContainer);


    // === CONTENT AREA ===
        StackPane contentArea = new StackPane();
        HBox.setHgrow(contentArea, Priority.ALWAYS); 
        contentArea.setStyle("-fx-background-color: " + AppStyle.NOTSOWHITE_COLOR + ";");

        // Memuat tampilan default (Data Karyawan) saat aplikasi pertama kali berjalan
        Node defaultKaryawanView = sigarka.scenes.karyawan.KelolaKaryawanSc.getView();
        contentArea.getChildren().add(defaultKaryawanView);


        // === EVENT HANDLERS (INTERAKSI TOMBOL MENU) ===
        // Data Karyawan
        btnKaryawan.setOnAction(e -> {
            // Ubah gaya visual
            btnKaryawan.setStyle(AppStyle.MENU_DASHBOARD_ACTIVE);
            btnHitung.setStyle(AppStyle.MENU_DASHBOARD_INACTIVE);
            btnSlip.setStyle(AppStyle.MENU_DASHBOARD_INACTIVE);
            
            // Muat ulang ikon
            btnKaryawan.setGraphic(createIconWrapper(IconLoader.loadSvgIcon("/Assets/icons/TambahKaryawan.svg", AppStyle.BLUE_COLOR, 0.6)));
            btnHitung.setGraphic(createIconWrapper(IconLoader.loadSvgIcon("/Assets/icons/Hitunggaji.svg", AppStyle.NOTSOWHITE_COLOR, 0.6)));
            btnSlip.setGraphic(createIconWrapper(IconLoader.loadSvgIcon("/Assets/icons/SlipGaji.svg", AppStyle.NOTSOWHITE_COLOR, 0.6)));
            
            // Ganti konten
            contentArea.getChildren().clear();
            Node karyawanView = sigarka.scenes.karyawan.KelolaKaryawanSc.getView();
            contentArea.getChildren().add(karyawanView);
        });

        // Hitung Gaji
        btnHitung.setOnAction(e -> {
            // Ubah gaya visual
            btnKaryawan.setStyle(AppStyle.MENU_DASHBOARD_INACTIVE);
            btnHitung.setStyle(AppStyle.MENU_DASHBOARD_ACTIVE);
            btnSlip.setStyle(AppStyle.MENU_DASHBOARD_INACTIVE);
            
            // Muat ulang ikon
            btnKaryawan.setGraphic(createIconWrapper(IconLoader.loadSvgIcon("/Assets/icons/TambahKaryawan.svg", AppStyle.NOTSOWHITE_COLOR, 0.6)));
            btnHitung.setGraphic(createIconWrapper(IconLoader.loadSvgIcon("/Assets/icons/Hitunggaji.svg", AppStyle.BLUE_COLOR, 0.6)));
            btnSlip.setGraphic(createIconWrapper(IconLoader.loadSvgIcon("/Assets/icons/SlipGaji.svg", AppStyle.NOTSOWHITE_COLOR, 0.6)));
            
            // Ganti konten
            contentArea.getChildren().clear();
            Node hitungView = sigarka.scenes.hitung.HitungGajiSc.getView();
            contentArea.getChildren().add(hitungView);
        });

        // Slip Gaji
        btnSlip.setOnAction(e -> {
            // Ubah gaya visual
            btnKaryawan.setStyle(AppStyle.MENU_DASHBOARD_INACTIVE);
            btnHitung.setStyle(AppStyle.MENU_DASHBOARD_INACTIVE);
            btnSlip.setStyle(AppStyle.MENU_DASHBOARD_ACTIVE);
            
            // Muat ulang ikon
            btnKaryawan.setGraphic(createIconWrapper(IconLoader.loadSvgIcon("/Assets/icons/TambahKaryawan.svg", AppStyle.NOTSOWHITE_COLOR, 0.6)));
            btnHitung.setGraphic(createIconWrapper(IconLoader.loadSvgIcon("/Assets/icons/Hitunggaji.svg", AppStyle.NOTSOWHITE_COLOR, 0.6)));
            btnSlip.setGraphic(createIconWrapper(IconLoader.loadSvgIcon("/Assets/icons/SlipGaji.svg", AppStyle.BLUE_COLOR, 0.6)));
            
            // Ganti konten
            contentArea.getChildren().clear();
            sigarka.scenes.slip.SlipGajiSc slipSc = new sigarka.scenes.slip.SlipGajiSc(stage);
            contentArea.getChildren().add(slipSc.getView());
        });

        
        // === ROOT LAYOUT ===
        HBox root = new HBox(0); 
        root.setStyle("-fx-background-color: " + AppStyle.NOTSOWHITE_COLOR + ";");
        root.getChildren().addAll(sidebar, contentArea);

        return new Scene(root, 900, 600);
    }
}
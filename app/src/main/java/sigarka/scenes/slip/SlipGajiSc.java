package sigarka.scenes.slip;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import sigarka.View.AppStyle;
import sigarka.models.Karyawan;
import sigarka.repository.GajiRepo;
import sigarka.repository.KaryawanRepo;

public class SlipGajiSc {

    private ComboBox<String> cbKaryawan;
    private ListView<String> listPeriode;
    private VBox containerPreview;
    private Button btnPrint;
    private Map<Integer, String> dataMapPeriode;
    private GajiRepo gRepo = new GajiRepo();
    private KaryawanRepo kRepo = new KaryawanRepo();
    private Stage stage;

    public SlipGajiSc(Stage stage) {
        this.stage = stage;
        inisialisasiKomponen();
    }

    private void inisialisasiKomponen() {
        cbKaryawan = new ComboBox<>();
        listPeriode = new ListView<>();
        containerPreview = new VBox();
        btnPrint = new Button("Cetak Slip Gaji");
        
        cbKaryawan.setPromptText("Pilih Karyawan");
        cbKaryawan.setMaxWidth(Double.MAX_VALUE);
        
        listPeriode.setPlaceholder(new Label("Belum ada slip gaji"));
        
        containerPreview.setAlignment(Pos.TOP_CENTER);
        containerPreview.setStyle("-fx-background-color: #f4f4f4;");

        btnPrint.setStyle(AppStyle.LIGHT_GREEN_BUTTON);
        btnPrint.setDisable(true);
    }

    public VBox getView() {
        VBox root = new VBox(20);
        root.setPadding(new Insets(30));
        root.setStyle("-fx-background-color: " + AppStyle.NOTSOWHITE_COLOR + ";");

        // === TITLE ===
        Label title = new Label("SLIP GAJI KARYAWAN");
        Font gloock = Font.loadFont(getClass().getResourceAsStream("/Assets/Fonts/Gloock-Regular.ttf"), 28);
        if (gloock != null) title.setFont(gloock);
        else title.setStyle("-fx-font-size: 28px; -fx-font-weight: bold;");
        title.setStyle("-fx-text-fill: " + AppStyle.BLUE_COLOR + ";");

        HBox titleContainer = new HBox(title);
        titleContainer.setAlignment(Pos.CENTER);

        // === MAIN CONTENT ===
        HBox content = new HBox(30);
        content.setAlignment(Pos.TOP_CENTER);
        VBox.setVgrow(content, Priority.ALWAYS);

        // --- LEFT SIDE ---
        VBox leftSide = new VBox(15);
        leftSide.setMinWidth(320);
        leftSide.setPadding(new Insets(20));
        leftSide.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 5);");

        muatDaftarKaryawan();

        Button btnHapus = new Button("Hapus Slip Terpilih");
        btnHapus.setStyle(AppStyle.BLUE_BUTTON);
        btnHapus.setMaxWidth(Double.MAX_VALUE);

        leftSide.getChildren().addAll(
            new Label("Pilih Karyawan:"), cbKaryawan, 
            new Label("Daftar Periode:"), listPeriode, 
            btnHapus
        );
        VBox.setVgrow(listPeriode, Priority.ALWAYS);

        // --- RIGHT SIDE ---
        VBox rightSide = new VBox(15);
        rightSide.setPadding(new Insets(20));
        rightSide.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 5);");
        HBox.setHgrow(rightSide, Priority.ALWAYS);

        ScrollPane scrollPreview = new ScrollPane(containerPreview);
        scrollPreview.setFitToWidth(true);
        scrollPreview.setStyle("-fx-background-color: transparent; -fx-border-color: #ddd;");
        VBox.setVgrow(scrollPreview, Priority.ALWAYS);

        rightSide.getChildren().addAll(new Label("Pratinjau:"), scrollPreview, btnPrint);

        content.getChildren().addAll(leftSide, rightSide);

        // === EVENT HANDLERS ===
        cbKaryawan.setOnAction(e -> muatDaftarPeriode());

        listPeriode.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                tampilkanPreview(newVal);
                btnPrint.setDisable(false);
            } else {
                containerPreview.getChildren().clear();
                btnPrint.setDisable(true);
            }
        });

        btnHapus.setOnAction(e -> prosesHapus());

        btnPrint.setOnAction(e -> {
            if (!containerPreview.getChildren().isEmpty()) {
                VBox slipVisual = (VBox) containerPreview.getChildren().get(0);
                DesainSlip.cetakKePrinter(slipVisual, stage);
            }
        });

        root.getChildren().addAll(titleContainer, content);
        return root;
    }

    private void muatDaftarKaryawan() {
        List<Karyawan> list = kRepo.ambilSemua();
        List<String> items = list.stream()
            .map(k -> "(" + (k.getTipe().contains("Tetap") ? "Tetap" : "Kontrak") + ") " + k.getNama() + " " + k.getId())
            .collect(Collectors.toList());
        cbKaryawan.setItems(FXCollections.observableArrayList(items));
    }

    private void muatDaftarPeriode() {
        int idx = cbKaryawan.getSelectionModel().getSelectedIndex();
        if (idx < 0) {
            listPeriode.getItems().clear();
            return;
        }

        List<Karyawan> listK = kRepo.ambilSemua();
        String idK = listK.get(idx).getId();

        dataMapPeriode = gRepo.ambilDaftarPeriode(idK);
        listPeriode.setItems(FXCollections.observableArrayList(dataMapPeriode.values()));
        containerPreview.getChildren().clear();
        btnPrint.setDisable(true);
    }

    private void tampilkanPreview(String periode) {
        int idData = -1;
        for (Map.Entry<Integer, String> entry : dataMapPeriode.entrySet()) {
            if (entry.getValue().equals(periode)) {
                idData = entry.getKey();
                break;
            }
        }

        if (idData != -1) {
            Map<String, Object> data = gRepo.ambilDetailGaji(idData);
            if (data != null && !data.isEmpty()) {
                VBox visual = DesainSlip.buatVisualSlip(data);
                containerPreview.getChildren().clear();
                containerPreview.getChildren().add(visual);
            }
        }
    }

    private void prosesHapus() {
        String terpilih = listPeriode.getSelectionModel().getSelectedItem();
        if (terpilih == null) {
            new Alert(Alert.AlertType.WARNING, "Silakan pilih periode slip yang ingin dihapus.").show();
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Konfirmasi Hapus");
        alert.setHeaderText(null);
        alert.setContentText("Yakin ingin menghapus slip gaji periode " + terpilih + "?");

        // Styling Alert (Standar Aplikasi)
        Button btnYes = (Button) alert.getDialogPane().lookupButton(ButtonType.OK);
        if (btnYes != null) btnYes.setStyle("-fx-background-color: " + AppStyle.BLUE_COLOR + "; -fx-text-fill: white;");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            int idHapus = -1;
            for (Map.Entry<Integer, String> entry : dataMapPeriode.entrySet()) {
                if (entry.getValue().equals(terpilih)) {
                    idHapus = entry.getKey();
                    break;
                }
            }
            if (idHapus != -1) {
                gRepo.hapusRiwayatGaji(idHapus);
                muatDaftarPeriode();
            }
        }
    }
}
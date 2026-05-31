package sigarka.scenes.slip;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javafx.collections.FXCollections;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
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
    
    }

    public VBox getView() {
        VBox root = new VBox(20);


        // === TITLE ===
        Label title = new Label("SLIP GAJI KARYAWAN");
        

        HBox titleContainer = new HBox(title);

        // === MAIN CONTENT ===
        HBox content = new HBox(30);
        

        // --- LEFT SIDE ---
        VBox leftSide = new VBox(15);
        

        muatDaftarKaryawan();

        Button btnHapus = new Button("Hapus Slip Terpilih");
       

        // --- RIGHT SIDE ---
        VBox rightSide = new VBox(15);
        

        ScrollPane scrollPreview = new ScrollPane(containerPreview);
       

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

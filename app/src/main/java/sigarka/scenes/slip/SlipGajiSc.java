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

        kustomisasiComboBox(cbKaryawan);
        
        listPeriode.setPlaceholder(new Label("Belum ada slip gaji"));

        kustomisasiListView(listPeriode);
        
        containerPreview.setAlignment(Pos.TOP_CENTER);
        containerPreview.setStyle("-fx-background-color: #f4f4f4;");

        btnPrint.setStyle("-fx-background-color: " + AppStyle.LIGHTGREEN_COLOR + 
                          "; -fx-text-fill: " + AppStyle.NOTSOBLACK_COLOR + 
                          "; -fx-background-radius: 2; -fx-padding: 10; -fx-font-weight: bold; -fx-cursor: hand;");

        btnPrint.setMaxWidth(Double.MAX_VALUE);
        btnPrint.setDisable(true);
    }


    // === TMPILAN UTAMA ===
    public VBox getView() {

        VBox root = new VBox(20);
        root.setPadding(new Insets(30));
        root.setStyle("-fx-background-color: " + AppStyle.NOTSOWHITE_COLOR + ";");


        // === TITLE ===
        Label title = new Label("SLIP GAJI KARYAWAN");
        Font gloock = Font.loadFont(getClass().getResourceAsStream("/Assets/Fonts/Gloock-Regular.ttf"), 20);
        if (gloock != null) {
            title.setFont(gloock);
            title.setStyle("-fx-text-fill: " + AppStyle.BLUE_COLOR + ";"); 
        } else {
            title.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: " + AppStyle.BLUE_COLOR + ";");
        }
        VBox.setMargin(title, new Insets(0, 0, 15, 0)); 


        // === MAIN CONTENT ===
        HBox content = new HBox(30);
        content.setAlignment(Pos.TOP_CENTER);
        VBox.setVgrow(content, Priority.ALWAYS);

        VBox leftSide = new VBox(15);
        leftSide.setMinWidth(320);
        leftSide.setPadding(new Insets(30)); 
        leftSide.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 5);");

        muatDaftarKaryawan();

        Label lblPilih = new Label("Pilih Karyawan:");
        lblPilih.setStyle("-fx-text-fill: black; -fx-font-weight: bold;");
        
        Label lblDaftar = new Label("Daftar Periode:");
        lblDaftar.setStyle("-fx-text-fill: black; -fx-font-weight: bold;");        

        Button btnHapus = new Button("Hapus Slip Terpilih");
        btnHapus.setStyle("-fx-background-color: " + AppStyle.BLUE_COLOR + 
                          "; -fx-text-fill: white; -fx-background-radius: 2; " + 
                          "-fx-padding: 10; -fx-font-weight: bold; -fx-cursor: hand;");     
        btnHapus.setMaxWidth(Double.MAX_VALUE);

        leftSide.getChildren().addAll(
            title, 
            lblPilih, cbKaryawan, 
            lblDaftar, listPeriode, 
            btnHapus
        );
        VBox.setVgrow(listPeriode, Priority.ALWAYS);


        // SLIP GAJI PRATINJAU
        VBox rightSide = new VBox(15);
        rightSide.setPadding(new Insets(30));

        rightSide.setStyle("-fx-background-color: " + AppStyle.TOSKA_COLOR + "; -fx-background-radius: 10; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 5);");
        HBox.setHgrow(rightSide, Priority.ALWAYS);

        ScrollPane scrollPreview = new ScrollPane(containerPreview);
        scrollPreview.setFitToWidth(true);

        scrollPreview.setStyle("-fx-background: #e9ecef; -fx-border-color: transparent;");
        VBox.setVgrow(scrollPreview, Priority.ALWAYS);

        Label lblPratinjau = new Label("Pratinjau Slip Gaji:");
        lblPratinjau.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px;");

        rightSide.getChildren().addAll(lblPratinjau, scrollPreview, btnPrint);

        content.getChildren().addAll(leftSide, rightSide);


        // === SET ACTION ===
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


        root.getChildren().add(content); 
        return root;
    }


    // TAMPILAN KIRI
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

        listPeriode.getSelectionModel().clearSelection();
        List<Karyawan> listK = kRepo.ambilSemua();
        String idK = listK.get(idx).getId();

        dataMapPeriode = gRepo.ambilDaftarPeriode(idK);
        listPeriode.setItems(FXCollections.observableArrayList(dataMapPeriode.values()));
        containerPreview.getChildren().clear();
        btnPrint.setDisable(true);
    }

    private void prosesHapus() {
        String terpilih = listPeriode.getSelectionModel().getSelectedItem();
        if (terpilih == null) {
            tampilkanAlert(Alert.AlertType.WARNING, "Silakan pilih periode slip yang ingin dihapus.");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Konfirmasi Hapus");
        alert.setHeaderText(null);
        alert.setContentText("Yakin ingin menghapus slip gaji periode " + terpilih + "?");

        alert.setGraphic(null);

        ButtonType btnYes = new ButtonType("Yes");
        ButtonType btnNo = new ButtonType("No");
        alert.getButtonTypes().setAll(btnYes, btnNo);

        // === STYLING BUTTON ===
        Button btnYesNode = (Button) alert.getDialogPane().lookupButton(btnYes);
        Button btnNoNode = (Button) alert.getDialogPane().lookupButton(btnNo);

        if (btnYesNode != null) {
            btnYesNode.setStyle("-fx-background-color: " + AppStyle.BLUE_COLOR + "; -fx-text-fill: white; -fx-cursor: hand;");
        }
        
        if (btnNoNode != null) {
            btnNoNode.setStyle("-fx-background-color: " + AppStyle.LIGHTGREEN_COLOR + "; -fx-text-fill: " + AppStyle.NOTSOBLACK_COLOR + "; -fx-cursor: hand;");
        }

        Optional<ButtonType> result = alert.showAndWait();
        
        if (result.isPresent() && result.get() == btnYes) {
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

    // TAMPILKAN SLIP GAJI
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

    // -
    private static void kustomisasiComboBox(ComboBox<String> comboBox) {
        comboBox.setCellFactory(lv -> new javafx.scene.control.ListCell<String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("-fx-background-color: white;");
                } else {
                    setText(item);
                    setStyle("-fx-background-color: white; -fx-text-fill: black;");

                    setOnMouseEntered(e -> {
                        setStyle("-fx-background-color: " + AppStyle.LIGHTGREEN_COLOR + "; -fx-text-fill: black; -fx-cursor: hand;");
                    });

                    setOnMouseExited(e -> {
                        setStyle("-fx-background-color: white; -fx-text-fill: black;");
                    });
                }
            }
        });
    }

    private void tampilkanAlert(Alert.AlertType tipe, String pesan) {
        Alert alert = new Alert(tipe, pesan);
        alert.setHeaderText(null);
        alert.setGraphic(null);
        
        Button btnOkNode = (Button) alert.getDialogPane().lookupButton(ButtonType.OK);
        if (btnOkNode != null) {
            btnOkNode.setStyle("-fx-background-color: " + AppStyle.LIGHTGREEN_COLOR + 
                               "; -fx-text-fill: " + AppStyle.NOTSOBLACK_COLOR + 
                               "; -fx-cursor: hand;"); 
        }
        alert.show();
    }

    // === KUSTOMISASI LISTVIEW ===
    private void kustomisasiListView(ListView<String> listView) {
        listView.setCellFactory(lv -> {
            javafx.scene.control.ListCell<String> cell = new javafx.scene.control.ListCell<String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                        setStyle("-fx-background-color: white;");
                    } else {
                        setText(item);
                        if (isSelected()) {
                            setStyle("-fx-background-color: " + AppStyle.LIGHTGREEN_COLOR + "; -fx-text-fill: black; -fx-font-weight: bold;");
                        } else {
                            setStyle("-fx-background-color: white; -fx-text-fill: black; -fx-font-weight: normal;");
                        }
                    }
                }
            };

            cell.selectedProperty().addListener((obs, wasSelected, isNowSelected) -> {
                if (cell.isEmpty()) return;
                if (isNowSelected) {
                    cell.setStyle("-fx-background-color: " + AppStyle.LIGHTGREEN_COLOR + "; -fx-text-fill: black; -fx-font-weight: bold;");
                } else {
                    cell.setStyle("-fx-background-color: white; -fx-text-fill: black; -fx-font-weight: normal;");
                }
            });

            cell.hoverProperty().addListener((obs, wasHovered, isNowHovered) -> {
                if (cell.isEmpty() || cell.isSelected()) return; 
                if (isNowHovered) {
                    cell.setStyle("-fx-background-color: #e8f5e9; -fx-text-fill: black; -fx-cursor: hand;"); 
                } else {
                    cell.setStyle("-fx-background-color: white; -fx-text-fill: black; -fx-font-weight: normal;");
                }
            });

            return cell;
        });
    }

}
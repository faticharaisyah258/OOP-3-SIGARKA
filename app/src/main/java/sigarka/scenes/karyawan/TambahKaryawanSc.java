package sigarka.scenes.karyawan;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import sigarka.View.AppStyle;
import sigarka.repository.KaryawanRepo; 

public class TambahKaryawanSc {

    public static void tampilkan(Runnable onSimpan) {
        Stage stage = new Stage();
        stage.setTitle("Tambah Data Karyawan");


        // === LAYOUT UTAMA ===
        VBox root = new VBox(25); 
        root.setPadding(new Insets(30));
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: " + AppStyle.TOSKA_COLOR + ";");

        Label title = new Label("Tambah Karyawan Baru");
        title.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: white;");


        // === GRID FORM ===
        GridPane grid = new GridPane();
        grid.setVgap(15);
        grid.setHgap(15);
        grid.setAlignment(Pos.CENTER);

        // === INPUTAN ===
        // ID
        TextField id = new TextField();
        id.setPromptText("ID (5 angka)");
        id.setPrefWidth(220); 

        // NAMA
        TextField nama = new TextField();
        nama.setPromptText("Nama Lengkap");
        nama.setPrefWidth(220);

        // TIPE
        ComboBox<String> cbTipe = new ComboBox<>(FXCollections.observableArrayList("Karyawan Tetap", "Karyawan Kontrak"));
        cbTipe.setPromptText("Pilih Tipe");
        cbTipe.setPrefWidth(220);

        // DIVISI
        ComboBox<String> cbDivisi = new ComboBox<>(FXCollections.observableArrayList("Bisnis Global dan Pemasaran", "Produksi Kreatif", "Artist & Repertoire"));
        cbDivisi.setPromptText("Pilih Divisi");
        cbDivisi.setPrefWidth(220);

        // DIVISI   
        ComboBox<String> cbJabatan = new ComboBox<>(FXCollections.observableArrayList("Manager", "Staf"));
        cbJabatan.setPromptText("Pilih Jabatan");
        cbJabatan.setPrefWidth(220);

        kustomisasiComboBox(cbTipe);
        kustomisasiComboBox(cbDivisi);
        kustomisasiComboBox(cbJabatan);
        
        Label lblInfoGaji = new Label("Gaji/Tarif: -");
        lblInfoGaji.setStyle("-fx-text-fill: white; -fx-font-style: italic;"); 


        // === SET ACTION ===
        cbDivisi.setDisable(true);
        cbJabatan.setDisable(true);

        cbTipe.setOnAction(e -> {
            boolean isTetap = "Karyawan Tetap".equals(cbTipe.getValue());
            cbDivisi.setDisable(!isTetap);
            cbJabatan.setDisable(!isTetap);
            updateInfoGaji(cbTipe, cbDivisi, cbJabatan, lblInfoGaji);
        });

        cbDivisi.setOnAction(e -> updateInfoGaji(cbTipe, cbDivisi, cbJabatan, lblInfoGaji));
        cbJabatan.setOnAction(e -> updateInfoGaji(cbTipe, cbDivisi, cbJabatan, lblInfoGaji));

        
        Button btnSimpan = new Button("Simpan Karyawan");
        btnSimpan.setMaxWidth(Double.MAX_VALUE); 

        btnSimpan.setStyle("-fx-background-color: " + AppStyle.LIGHTGREEN_COLOR + 
                           "; -fx-text-fill: black; -fx-background-radius: 5; -fx-padding: 10; -fx-font-weight: bold; -fx-cursor: hand;");

        btnSimpan.setOnAction(e -> {
            String id_karyawan = id.getText();
            String nama_karyawan = nama.getText();
            String tipe = cbTipe.getValue();

            // === PENGGUNAAN ALERT YANG SUDAH DISESUAIKAN ===
            if (id_karyawan.isEmpty() || !id_karyawan.matches("\\d{5}")) {
                tampilkanAlert(Alert.AlertType.ERROR, "ID harus diisi 5 angka!");
                return;
            }
            if (nama_karyawan.isEmpty()) {
                tampilkanAlert(Alert.AlertType.ERROR, "Nama karyawan harus diisi!");
                return;
            }
            if (tipe == null) {
                tampilkanAlert(Alert.AlertType.ERROR, "Tipe karyawan harus dipilih!");
                return;
            }

            // simpan ke database kRepo
            KaryawanRepo repo = new KaryawanRepo();

            // VALIDASI ID DUPLIKAT
            if (repo.apakahIdSudahAda(id_karyawan)) {
                tampilkanAlert(Alert.AlertType.ERROR, "ID Karyawan '" + id_karyawan + "' sudah terdaftar!");
                return;
            }

            double gajiPokok = 0;
            double tarif = 0;
            
            if ("Karyawan Kontrak".equals(tipe)) {
                tarif = 30000;
            } else {
                if (cbDivisi.getValue() == null || cbJabatan.getValue() == null) {
                    tampilkanAlert(Alert.AlertType.ERROR, "Divisi dan Jabatan harus dipilih untuk Karyawan Tetap!");
                    return;
                }
                gajiPokok = hitungGajiPokok(cbDivisi.getValue(), cbJabatan.getValue());
            }

            repo.tambah(id_karyawan, nama_karyawan, tipe, cbDivisi.getValue(), cbJabatan.getValue(), gajiPokok, tarif);
            stage.close();
            onSimpan.run();
        });


        Label lblId = new Label("ID Karyawan:"); lblId.setStyle("-fx-text-fill: white;");
        Label lblNama = new Label("Nama:"); lblNama.setStyle("-fx-text-fill: white;");
        Label lblTipe = new Label("Tipe:"); lblTipe.setStyle("-fx-text-fill: white;");
        Label lblDiv = new Label("Divisi:"); lblDiv.setStyle("-fx-text-fill: white;");
        Label lblJab = new Label("Jabatan:"); lblJab.setStyle("-fx-text-fill: white;");

        grid.add(lblId, 0, 0); grid.add(id, 1, 0);
        grid.add(lblNama, 0, 1); grid.add(nama, 1, 1);
        grid.add(lblTipe, 0, 2); grid.add(cbTipe, 1, 2);
        grid.add(lblDiv, 0, 3); grid.add(cbDivisi, 1, 3);
        grid.add(lblJab, 0, 4); grid.add(cbJabatan, 1, 4);
        

        root.getChildren().addAll(title, grid, lblInfoGaji, btnSimpan);


        stage.setScene(new Scene(root, 420, 500)); 
        stage.show();
    }

    // === TAMPILKAN INFO GAJI/TARIF
    private static void updateInfoGaji(ComboBox<String> tipe, ComboBox<String> div, ComboBox<String> jab, Label lbl) {
        if ("Karyawan Kontrak".equals(tipe.getValue())) {
            lbl.setText("Tarif: 30.000 / jam");
        } else if ("Karyawan Tetap".equals(tipe.getValue()) && div.getValue() != null && jab.getValue() != null) {
            lbl.setText("Gaji Pokok: Rp " + String.format("%,.0f", hitungGajiPokok(div.getValue(), jab.getValue())));
        } else {
            lbl.setText("Gaji/Tarif: -");
        }
    }

    private static double hitungGajiPokok(String div, String jab) {
        if ("Bisnis Global dan Pemasaran".equals(div)) return "Manager".equals(jab) ? 7000000 : 6000000;
        if ("Produksi Kreatif".equals(div)) return "Manager".equals(jab) ? 6500000 : 5500000;
        if ("Artist & Repertoire".equals(div)) return "Manager".equals(jab) ? 7500000 : 6500000;
        return 0;
    }


    //kustomisasi ComboBox
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


    // === MENGUBAH GAYA ALERT ===
    private static void tampilkanAlert(Alert.AlertType tipe, String pesan) {
        Alert alert = new Alert(tipe, pesan);
        alert.setHeaderText(null);
        alert.setGraphic(null);
        
        Button btnOkNode = (Button) alert.getDialogPane().lookupButton(javafx.scene.control.ButtonType.OK);
        if (btnOkNode != null) {
            btnOkNode.setStyle("-fx-background-color: " + AppStyle.LIGHTGREEN_COLOR + 
                               "; -fx-text-fill: " + AppStyle.NOTSOBLACK_COLOR + 
                               "; -fx-cursor: hand;"); 
        }
        alert.show();
    }
}
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
import sigarka.repository.KaryawanRepo;
import sigarka.View.AppStyle; 

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

        TextField id = new TextField();
        id.setPromptText("ID (5 angka)");
        id.setPrefWidth(220); 

        TextField nama = new TextField();
        nama.setPromptText("Nama Lengkap");
        nama.setPrefWidth(220);

        ComboBox<String> cbTipe = new ComboBox<>(FXCollections.observableArrayList("Karyawan Tetap", "Karyawan Kontrak"));
        cbTipe.setPromptText("Pilih Tipe");
        cbTipe.setPrefWidth(220);

        ComboBox<String> cbDivisi = new ComboBox<>(FXCollections.observableArrayList("Bisnis Global dan Pemasaran", "Produksi Kreatif", "Artist & Repertoire"));
        cbDivisi.setPromptText("Pilih Divisi");
        cbDivisi.setPrefWidth(220);

        ComboBox<String> cbJabatan = new ComboBox<>(FXCollections.observableArrayList("Manager", "Staf"));
        cbJabatan.setPromptText("Pilih Jabatan");
        cbJabatan.setPrefWidth(220);
        
        Label lblInfoGaji = new Label("Gaji/Tarif: -");
  
        lblInfoGaji.setStyle("-fx-text-fill: white; -fx-font-style: italic;"); 

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


            if (id_karyawan.isEmpty() || !id_karyawan.matches("\\d{5}")) {
                new Alert(Alert.AlertType.ERROR, "ID harus diisi 5 angka!").show();
                return;
            }
            if (nama_karyawan.isEmpty()) {
                new Alert(Alert.AlertType.ERROR, "Nama karyawan harus diisi!").show();
                return;
            }
            if (tipe == null) {
                new Alert(Alert.AlertType.ERROR, "Tipe karyawan harus dipilih!").show();
                return;
            }

            KaryawanRepo repo = new KaryawanRepo();
            double gajiPokok = 0;
            double tarif = 0;
            
            if ("Karyawan Kontrak".equals(tipe)) {
                tarif = 30000;
            } else {
  
                if (cbDivisi.getValue() == null || cbJabatan.getValue() == null) {
                    new Alert(Alert.AlertType.ERROR, "Divisi dan Jabatan harus dipilih untuk Karyawan Tetap!").show();
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
}
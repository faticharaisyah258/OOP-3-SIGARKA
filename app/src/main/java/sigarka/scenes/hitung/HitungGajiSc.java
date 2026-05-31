package sigarka.scenes.hitung;

import java.util.List;
import java.util.stream.Collectors;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos; 
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane; 
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import sigarka.models.Karyawan;
import sigarka.models.KaryawanTetap;
import sigarka.repository.GajiRepo;
import sigarka.repository.KaryawanRepo;
import sigarka.View.AppStyle; 

public class HitungGajiSc {

    public static VBox getView() {

        // === CONTAINER UTAMA ===
        VBox root = new VBox();
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: " + AppStyle.NOTSOWHITE_COLOR + ";");
        root.setPadding(new Insets(40));

        // === CARD ===
        VBox card = new VBox(15); 

        card.setPadding(new Insets(35, 40, 35, 40)); 
        
        card.setMaxWidth(500); 
        card.setAlignment(Pos.CENTER);
        card.setStyle("-fx-background-color: " + AppStyle.TOSKA_COLOR + "; -fx-background-radius: 10; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 5);");

        // === JUDUL ===
        Label title = new Label("Hitung Gaji Karyawan");
        Font gloockFont = Font.loadFont(HitungGajiSc.class.getResourceAsStream("/Assets/Fonts/Gloock-Regular.ttf"), 26);
        if (gloockFont != null) {
            title.setFont(gloockFont);
            title.setStyle("-fx-text-fill: white;");
        } else {
            title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: white;");
        }
        VBox.setMargin(title, new Insets(0, 0, 35, 0));

        KaryawanRepo karyawanRepo = new KaryawanRepo();
        GajiRepo gRepo = new GajiRepo();

        final List<Karyawan> daftarKaryawan = karyawanRepo.ambilSemua();
        List<String> displayList = daftarKaryawan.stream()
            .map(karyawan -> "(" + (karyawan.getTipe().contains("Tetap") ? "Tetap" : "Kontrak") + ") " + karyawan.getNama() + " " + karyawan.getId())
            .collect(Collectors.toList());

        // === GRID ===
        GridPane grid = new GridPane();
        grid.setVgap(15);
        grid.setHgap(20);
        grid.setAlignment(Pos.CENTER);

        Label lblKaryawan = new Label("Karyawan:");
        lblKaryawan.setStyle("-fx-text-fill: white;"); 
        ComboBox<String> cbKaryawan = new ComboBox<>(FXCollections.observableArrayList(displayList));
        cbKaryawan.setPromptText("Pilih Karyawan");
        cbKaryawan.setPrefWidth(250);

        Label lblPeriode = new Label("Periode:");
        lblPeriode.setStyle("-fx-text-fill: white;");
        TextField periode = new TextField();
        periode.setPromptText("Contoh: Mei 2026");
        periode.setPrefWidth(250);

        grid.add(lblKaryawan, 0, 0);
        grid.add(cbKaryawan, 1, 0);
        grid.add(lblPeriode, 0, 1);
        grid.add(periode, 1, 1);

        VBox formContainer = new VBox(10);
        formContainer.setAlignment(Pos.CENTER); 

        // === TOMBOL ===
        Button btnHitung = new Button("Hitung dan Simpan");
        btnHitung.setMaxWidth(Double.MAX_VALUE); 
        btnHitung.setStyle("-fx-background-color: " + AppStyle.LIGHTGREEN_COLOR + 
                           "; -fx-text-fill: black; -fx-background-radius: 5; -fx-padding: 12; -fx-font-weight: bold; -fx-cursor: hand;");

        cbKaryawan.setOnAction(e -> {
            formContainer.getChildren().clear();
            int idx = cbKaryawan.getSelectionModel().getSelectedIndex();
            if (idx < 0) return;

            Karyawan k = daftarKaryawan.get(idx);
            if (k instanceof KaryawanTetap) {
                Label lblIzin = new Label("Jumlah Izin:"); lblIzin.setStyle("-fx-text-fill: white;");
                Label lblAlfa = new Label("Jumlah Alfa:"); lblAlfa.setStyle("-fx-text-fill: white;");
                Label lblLembur = new Label("Jumlah Hari Lembur:"); lblLembur.setStyle("-fx-text-fill: white;");
                
                formContainer.getChildren().addAll(
                    lblIzin, new TextField("0"),
                    lblAlfa, new TextField("0"),
                    lblLembur, new TextField("0")
                );
            } else {
                Label lblJamKerja = new Label("Total Jam Kerja 1 Bulan:"); lblJamKerja.setStyle("-fx-text-fill: white;");
                formContainer.getChildren().addAll(
                    lblJamKerja, new TextField("0")
                );
            }
        });

        btnHitung.setOnAction(e -> {
            int idx = cbKaryawan.getSelectionModel().getSelectedIndex();
            if (idx < 0) {
                new Alert(Alert.AlertType.WARNING, "Silakan pilih karyawan terlebih dahulu.").show();
                return;
            }
            if (periode.getText().isEmpty()) {
                new Alert(Alert.AlertType.WARNING, "Silakan isi periode (contoh: Mei 2026).").show();
                return;
            }

            Karyawan karyawan = daftarKaryawan.get(idx);
            double gajiBersih = 0;
            double tunjangan_kesehatan = 0;
            double bonus = 0;
            int izin = 0, alfa = 0, lembur = 0, jamKerja = 0;

            try {
                if (karyawan instanceof KaryawanTetap) {
                    izin = Integer.parseInt(((TextField)formContainer.getChildren().get(1)).getText());
                    alfa = Integer.parseInt(((TextField)formContainer.getChildren().get(3)).getText());
                    lembur = Integer.parseInt(((TextField)formContainer.getChildren().get(5)).getText());

                    // Ambil gaji pokok dari database sesuai divisi dan jabatan yang disimpan
                    double gajiPokok = karyawanRepo.getGajiPokok(karyawan.getId());
                    tunjangan_kesehatan = 300000;
                    
                    // Bonus Badge sesuai kategori
                    if (lembur >= 5) bonus += 150000; // Super Productive
                    if (alfa == 0 && izin == 0) bonus += 100000; // Discipline Master
                    
                    double penghasilan = gajiPokok + tunjangan_kesehatan + (lembur * 100000) + bonus;
                    double potongan = (alfa * 150000) + (izin * 75000);
                    gajiBersih = penghasilan - potongan;

                    // Reset input setelah berhasil
                    ((TextField)formContainer.getChildren().get(1)).setText("0");
                    ((TextField)formContainer.getChildren().get(3)).setText("0");
                    ((TextField)formContainer.getChildren().get(5)).setText("0");

                } else {
                    jamKerja = Integer.parseInt(((TextField)formContainer.getChildren().get(1)).getText());
                    gajiBersih = jamKerja * 30000; // Sesuai tarif 30.000 per jam
                    
                    // Reset input setelah berhasil
                    ((TextField)formContainer.getChildren().get(1)).setText("0");
                }

                gRepo.simpanRiwayat(karyawan.getId(), periode.getText(), tunjangan_kesehatan, bonus, izin, alfa, lembur, jamKerja, gajiBersih);
                
                String hasilFormatted = String.format("%,.0f", gajiBersih).replace(',', '.');
                new Alert(Alert.AlertType.INFORMATION, "Gaji berhasil dihitung dan disimpan.\nTotal Gaji Bersih: Rp " + hasilFormatted).show();
                
            } catch (NumberFormatException ex) {
                new Alert(Alert.AlertType.ERROR, "Input harus berupa angka yang valid.").show();
            }
        });

        card.getChildren().addAll(title, grid, formContainer, btnHitung);
        
        root.getChildren().add(card);

        return root;
    }
}
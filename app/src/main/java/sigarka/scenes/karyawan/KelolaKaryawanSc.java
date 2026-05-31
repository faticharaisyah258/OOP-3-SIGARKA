package sigarka.scenes.karyawan;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import sigarka.models.Karyawan;
import sigarka.repository.KaryawanRepo;
import sigarka.View.AppStyle; 

public class KelolaKaryawanSc {

    private static TableView<Karyawan> tabelTetap = new TableView<>();
    private static TableView<Karyawan> tabelKontrak = new TableView<>();
    private static KaryawanRepo repo = new KaryawanRepo();

    public static VBox getView() {

        VBox root = new VBox(30);
        root.setPadding(new Insets(30));
        root.setStyle("-fx-background-color: " + AppStyle.NOTSOWHITE_COLOR + ";");

        setupTabel();
        muatData();

        tabelTetap.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) tabelKontrak.getSelectionModel().clearSelection();
        }); 
        tabelKontrak.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) tabelTetap.getSelectionModel().clearSelection();
        });

        // === FONT GLOOCK ===
        Font gloockFont = Font.loadFont(KelolaKaryawanSc.class.getResourceAsStream("/Assets/Fonts/Gloock-Regular.ttf"), 20);

        VBox tetapCard = createCardSection("Daftar Karyawan Tetap", gloockFont);
        VBox.setVgrow(tabelTetap, Priority.ALWAYS); 
        tetapCard.getChildren().add(tabelTetap);

        VBox kontrakCard = createCardSection("Daftar Karyawan Kontrak", gloockFont);
        VBox.setVgrow(tabelKontrak, Priority.ALWAYS);
        kontrakCard.getChildren().add(tabelKontrak);

       
        HBox tableContainer = new HBox(30); 
        
  
        HBox.setHgrow(tetapCard, Priority.ALWAYS);
        HBox.setHgrow(kontrakCard, Priority.ALWAYS);
        
    
        VBox.setVgrow(tableContainer, Priority.ALWAYS);
        
        tableContainer.getChildren().addAll(tetapCard, kontrakCard);


        // === TOMBOL ===
        Button btnHapus = new Button("Hapus Karyawan");
        btnHapus.setStyle("-fx-background-color: " + AppStyle.BLUE_COLOR + "; -fx-text-fill: white; -fx-background-radius: 2; -fx-padding: 8 20; -fx-font-size: 13px; -fx-cursor: hand; -fx-font-weight: bold;");

        Button btnTambah = new Button("Tambahkan Karyawan");
        btnTambah.setStyle("-fx-background-color: " + AppStyle.LIGHTGREEN_COLOR + "; -fx-text-fill: " + AppStyle.NOTSOBLACK_COLOR + "; -fx-background-radius: 2; -fx-padding: 8 20; -fx-font-size: 13px; -fx-cursor: hand; -fx-font-weight: bold;");

        btnTambah.setOnAction(e -> TambahKaryawanSc.tampilkan(KelolaKaryawanSc::muatData));
        btnHapus.setOnAction(e -> {
            Karyawan kTerpilih = tabelTetap.getSelectionModel().getSelectedItem();
            if (kTerpilih == null) kTerpilih = tabelKontrak.getSelectionModel().getSelectedItem();
            
            if (kTerpilih != null) {
                if (HapusKaryawanSc.konfirmasiHapus(kTerpilih.getId(), kTerpilih.getNama())) {
                    muatData();
                }
            } else {
                Alert alert = new Alert(Alert.AlertType.WARNING, "Silakan pilih karyawan yang ingin dihapus pada tabel.");
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
        });

        HBox buttonBox = new HBox(15, btnHapus, btnTambah);
        buttonBox.setAlignment(Pos.CENTER_LEFT);

        root.getChildren().addAll(tableContainer, buttonBox);
        return root;
    }

    private static VBox createCardSection(String titleStr, Font customFont) {
        VBox card = new VBox(20);
        card.setPadding(new Insets(25));
        
        card.setStyle("-fx-background-color: white; -fx-background-radius: 8; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 5);");
        
        Label title = new Label(titleStr);

        if (customFont != null) {
            title.setFont(customFont);
        } else {
            title.setFont(Font.font("System", FontWeight.BOLD, 20));
        }
        title.setStyle("-fx-text-fill: " + AppStyle.BLUE_COLOR + ";");
        
        card.getChildren().add(title);
        VBox.setVgrow(card, Priority.ALWAYS); 
        return card;
    }

    private static void setupTabel() {
        tabelTetap.getColumns().clear();
        tabelKontrak.getColumns().clear();


        tabelTetap.setStyle("-fx-border-color: transparent; -fx-background-color: transparent;");
        tabelKontrak.setStyle("-fx-border-color: transparent; -fx-background-color: transparent;");

        try {
            String cssPath = KelolaKaryawanSc.class.getResource("/Assets/css/table-style.css").toExternalForm();
            tabelTetap.getStylesheets().add(cssPath);
            tabelKontrak.getStylesheets().add(cssPath);
        } catch (Exception e) {
            System.out.println("Gagal memuat CSS Tabel. Pastikan path /Assets/css/table-style.css sudah benar.");
        }


        tabelTetap.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tabelKontrak.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Karyawan, String> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        
        TableColumn<Karyawan, String> colNama = new TableColumn<>("Nama");
        colNama.setCellValueFactory(new PropertyValueFactory<>("nama"));
        
        TableColumn<Karyawan, String> colDiv = new TableColumn<>("Divisi");
        colDiv.setCellValueFactory(new PropertyValueFactory<>("divisi"));
        
        TableColumn<Karyawan, String> colJab = new TableColumn<>("Jabatan");
        colJab.setCellValueFactory(new PropertyValueFactory<>("jabatan"));
        
        TableColumn<Karyawan, Double> colGajiPokok = new TableColumn<>("Gaji Pokok");
        colGajiPokok.setCellValueFactory(new PropertyValueFactory<>("gajiPokok"));
        
        tabelTetap.getColumns().addAll(colId, colNama, colDiv, colJab, colGajiPokok);

        TableColumn<Karyawan, String> colIdK = new TableColumn<>("ID Karyawan");
        colIdK.setCellValueFactory(new PropertyValueFactory<>("id"));
        
        TableColumn<Karyawan, String> colNamaK = new TableColumn<>("Nama");
        colNamaK.setCellValueFactory(new PropertyValueFactory<>("nama"));
        
        TableColumn<Karyawan, Double> colTarif = new TableColumn<>("Tarif");
        colTarif.setCellValueFactory(new PropertyValueFactory<>("tarifPerJam"));

        tabelKontrak.getColumns().addAll(colIdK, colNamaK, colTarif);
    }

    public static void muatData() {
        List<Karyawan> semua = repo.ambilSemua();
        
        List<Karyawan> tetap = semua.stream()
            .filter(k -> "Karyawan Tetap".equals(k.getTipe()))
            .sorted(Comparator.comparing((Karyawan k) -> {
                String jab = ((sigarka.models.KaryawanTetap)k).getJabatan();
                return "Manager".equals(jab) ? 0 : 1;
            }).thenComparing(k -> ((sigarka.models.KaryawanTetap)k).getDivisi())
              .thenComparing(k -> k.getNama()))
            .collect(Collectors.toList());

        List<Karyawan> kontrak = semua.stream()
            .filter(k -> "Karyawan Kontrak".equals(k.getTipe()))
            .sorted(Comparator.comparing(Karyawan::getNama))
            .collect(Collectors.toList());

        tabelTetap.setItems(FXCollections.observableArrayList(tetap));
        tabelKontrak.setItems(FXCollections.observableArrayList(kontrak));
        
        tabelTetap.getSelectionModel().clearSelection();
        tabelKontrak.getSelectionModel().clearSelection();
        
        tabelTetap.refresh();
        tabelKontrak.refresh();
    }
}
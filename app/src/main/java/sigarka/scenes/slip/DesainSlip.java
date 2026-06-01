package sigarka.scenes.slip;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.print.PageLayout;
import javafx.print.PageOrientation;
import javafx.print.Paper;
import javafx.print.Printer;
import javafx.print.PrinterJob;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.transform.Scale;
import javafx.stage.Stage;
import sigarka.View.AppStyle;

public class DesainSlip {

    public static VBox buatVisualSlip(Map<String, Object> data) {
        VBox slip = new VBox(20); 
        slip.setPadding(new Insets(50, 60, 50, 60));
        
        slip.setStyle("-fx-background-color: white; -fx-border-color: " + AppStyle.BLUE_COLOR + "; -fx-border-width: 2.5; -fx-border-insets: 15;");
        slip.setMinWidth(595); 
        slip.setMinHeight(842); 
        slip.setAlignment(Pos.TOP_CENTER);

        if (data == null || data.isEmpty()) {
            slip.getChildren().add(new Label("Data tidak valid"));
            return slip;
        }

        // === LOGO ===
        try {
            Image logoImg = new Image(DesainSlip.class.getResourceAsStream("/Assets/images/Logo/logo_login.png"));
            ImageView logoView = new ImageView(logoImg);
            logoView.setFitWidth(160);
            logoView.setPreserveRatio(true);
            slip.getChildren().add(logoView);
        } catch (Exception e) {
            System.out.println("Logo tidak ditemukan.");
        }


        // === HEADER ===
        VBox headerBox = new VBox(8);
        headerBox.setAlignment(Pos.CENTER);

        Label lblJudul = new Label("SLIP GAJI KARYAWAN");
        Font gloock = Font.loadFont(DesainSlip.class.getResourceAsStream("/Assets/Fonts/Gloock-Regular.ttf"), 26);
        if (gloock != null) lblJudul.setFont(gloock);
        else lblJudul.setStyle("-fx-font-size: 26px; -fx-font-weight: bold;");
        lblJudul.setStyle(lblJudul.getStyle() + "-fx-text-fill: " + AppStyle.BLUE_COLOR + ";");

        Label lblPeriode = new Label("PERIODE : " + getString(data, "periode", "-").toUpperCase());
        lblPeriode.setStyle("-fx-background-color: " + AppStyle.TOSKA_COLOR + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 5 25; -fx-background-radius: 15; -fx-font-size: 14px;");

        headerBox.getChildren().addAll(lblJudul, lblPeriode);
        slip.getChildren().add(headerBox);

        Region spacer1 = new Region(); spacer1.setMinHeight(15);
        slip.getChildren().add(spacer1);


        // === INFO KARYAWAN ===
        VBox infoBox = new VBox(5);
        infoBox.setAlignment(Pos.CENTER_LEFT);

        String tipe = getString(data, "tipe", "Tetap");
        String tipeLabel = "Karyawan Tetap".equals(tipe) ? " (Tetap)" : " (Kontrak)";
        
        infoBox.getChildren().addAll(
            buatBarisInfo("Nama Karyawan:", getString(data, "nama", "-")),
            buatBarisInfo("ID Karyawan:", getString(data, "id", "-")),
            buatBarisInfo("Jabatan:", getString(data, "jabatan", "-") + tipeLabel)
        );
        slip.getChildren().add(infoBox);


        // === DATA GAJI ===
        double gajiPokok = getDouble(data, "gaji_pokok");
        double tunjangan = getDouble(data, "tunjangan_kesehatan");
        double bonusBadge = getDouble(data, "bonus_badge");
        int lembur = getInt(data, "lembur");
        double bonusLembur = lembur * 100000.0;
        
        int alpa = getInt(data, "alpa");
        int izin = getInt(data, "izin");
        double potAlpa = alpa * 150000.0;
        double potIzin = izin * 75000.0;
        
        double totalPendapatan = 0;
        double totalPotongan = 0;

        // === TABEL PENDAPATAN ===
        List<String[]> pendRows = new ArrayList<>();
        if ("Karyawan Tetap".equals(tipe)) {
            pendRows.add(new String[]{"Gaji Pokok", formatRp(gajiPokok)});
            pendRows.add(new String[]{"Tunjangan Kesehatan", formatRp(tunjangan)});
            pendRows.add(new String[]{"Bonus Lembur", formatRp(bonusLembur)});
            
            String kat = getKategoriBonus(lembur, alpa, izin);
            String badgeLable = kat.isEmpty() ? "Bonus Badge" : "Bonus Badge" + kat;
            pendRows.add(new String[]{badgeLable, formatRp(bonusBadge)});
            
            totalPendapatan = gajiPokok + tunjangan + bonusLembur + bonusBadge;
        } else {
            double tarif = getDouble(data, "tarif_per_jam");
            pendRows.add(new String[]{"Tarif Kerja / Jam", formatRp(tarif)});
            pendRows.add(new String[]{"Tunjangan Kesehatan", formatRp(0)});
            pendRows.add(new String[]{"Bonus Lembur", formatRp(0)});
            pendRows.add(new String[]{"Bonus Badge", formatRp(0)});
            
            totalPendapatan = getDouble(data, "gaji_bersih");
        }
        VBox tabelPendapatan = buatTabel("Pendapatan", "Pendapatan", "Jumlah (IDR)", pendRows, new String[]{"Total Pendapatan", formatRp(totalPendapatan)});

        // === TABEL POTONGAN ===
        List<String[]> potRows = new ArrayList<>();
        if ("Karyawan Tetap".equals(tipe)) {
            potRows.add(new String[]{"Potongan Alfa", formatRp(potAlpa)});
            potRows.add(new String[]{"Potongan Izin", formatRp(potIzin)});
            totalPotongan = potAlpa + potIzin;
        } else {
            potRows.add(new String[]{"Potongan Alfa", formatRp(0)});
            potRows.add(new String[]{"Potongan Izin", formatRp(0)});
            totalPotongan = 0;
        }
        VBox tabelPotongan = buatTabel("Potongan", "Potongan", "Jumlah (IDR)", potRows, new String[]{"Total Potongan", formatRp(totalPotongan)});

        // === TABEL GAJI BERSIH ===
        List<String[]> bersihRows = new ArrayList<>();
        bersihRows.add(new String[]{"Gaji Bersih", formatRp(getDouble(data, "gaji_bersih"))});
        VBox tabelGajiBersih = buatTabel("Gaji Bersih", "Gaji Bersih", "Jumlah (IDR)", bersihRows, null);

        slip.getChildren().addAll(tabelPendapatan, tabelPotongan, tabelGajiBersih);

        return slip;
    }

    
    // === HELPER METHODS ===
    private static HBox buatBarisInfo(String label, String value) {
        HBox box = new HBox(5);
        Label l = new Label(label);
        l.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: black;");
        Label v = new Label(value);
        v.setStyle("-fx-font-size: 14px; -fx-text-fill: black;");
        box.getChildren().addAll(l, v);
        return box;
    }

    private static VBox buatTabel(String judulTable, String headerKiri, String headerKanan, List<String[]> dataRows, String[] totalRow) {
        VBox container = new VBox(5);
        
        Label judul = new Label(judulTable);
        judul.setStyle("-fx-font-weight: bold; -fx-font-size: 18px; -fx-text-fill: black;");
        
        GridPane grid = new GridPane();
        grid.setStyle("-fx-border-color: #37474f; -fx-border-width: 1.5; -fx-background-color: white;");
        
        ColumnConstraints col1 = new ColumnConstraints();
        col1.setPercentWidth(60);
        ColumnConstraints col2 = new ColumnConstraints();
        col2.setPercentWidth(40);
        grid.getColumnConstraints().addAll(col1, col2);
        
        Label h1 = new Label(headerKiri);
        h1.setMaxWidth(Double.MAX_VALUE); h1.setAlignment(Pos.CENTER);
        h1.setStyle("-fx-background-color: " + AppStyle.TOSKA_COLOR + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 6 10; -fx-border-color: #37474f; -fx-border-width: 0 1.5 1.5 0; -fx-font-size: 14px;");
        
        Label h2 = new Label(headerKanan);
        h2.setMaxWidth(Double.MAX_VALUE); h2.setAlignment(Pos.CENTER);
        h2.setStyle("-fx-background-color: " + AppStyle.TOSKA_COLOR + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 6 10; -fx-border-color: #37474f; -fx-border-width: 0 0 1.5 0; -fx-font-size: 14px;");
        
        grid.add(h1, 0, 0);
        grid.add(h2, 1, 0);
        
        int row = 1;
        for (String[] rowData : dataRows) {
            Label cell1 = new Label(rowData[0]);
            cell1.setMaxWidth(Double.MAX_VALUE);
            
            boolean isLastData = (row == dataRows.size());
            int borderBottom = (isLastData && totalRow == null) ? 0 : 1;
            
            cell1.setStyle("-fx-padding: 5 10; -fx-border-color: #9e9e9e; -fx-border-width: 0 1.5 " + borderBottom + " 0; -fx-text-fill: black; -fx-font-size: 14px;");
            
            Label cell2 = new Label(rowData[1]);
            cell2.setMaxWidth(Double.MAX_VALUE); cell2.setAlignment(Pos.CENTER_RIGHT);
            cell2.setStyle("-fx-padding: 5 10; -fx-border-color: #9e9e9e; -fx-border-width: 0 0 " + borderBottom + " 0; -fx-text-fill: black; -fx-font-size: 14px;");
            
            grid.add(cell1, 0, row);
            grid.add(cell2, 1, row);
            row++;
        }
        
        if (totalRow != null) {
            Label t1 = new Label(totalRow[0]);
            t1.setMaxWidth(Double.MAX_VALUE);
            t1.setStyle("-fx-padding: 5 10; -fx-border-color: #9e9e9e; -fx-border-width: 0 1.5 0 0; -fx-font-weight: bold; -fx-text-fill: black; -fx-font-size: 14px;");
            
            Label t2 = new Label(totalRow[1]);
            t2.setMaxWidth(Double.MAX_VALUE); t2.setAlignment(Pos.CENTER_RIGHT);
            t2.setStyle("-fx-padding: 5 10; -fx-font-weight: bold; -fx-text-fill: black; -fx-font-size: 14px;");
            
            grid.add(t1, 0, row);
            grid.add(t2, 1, row);
        }
        
        container.getChildren().addAll(judul, grid);
        return container;
    }


    // AMBIL DATA UNTUK SLIP GAJI
    private static String getKategoriBonus(int lembur, int alpa, int izin) {
        StringBuilder sb = new StringBuilder();
        if (lembur >= 5) sb.append("\n\t- Super Productive 🔥");
        if (alpa == 0 && izin == 0) {
            sb.append("\n\t- Discipline Master 👑");
        }
        return sb.toString();
    }

    private static String formatRp(double amount) {
        return "Rp " + String.format("%,.0f", amount).replace(',', '.');
    }

    private static String getString(Map<String, Object> map, String key, String def) {
        Object val = map.get(key);
        return val != null ? val.toString() : def;
    }

    private static double getDouble(Map<String, Object> map, String key) {
        Object val = map.get(key);
        if (val instanceof Double) return (double) val;
        if (val instanceof Integer) return ((Integer) val).doubleValue();
        return 0.0;
    }

    private static int getInt(Map<String, Object> map, String key) {
        Object val = map.get(key);
        if (val instanceof Integer) return (int) val;
        if (val instanceof Double) return ((Double) val).intValue();
        return 0;
    }

    // === METHOD PRINT
    public static void cetakKePrinter(VBox node, Stage stage) {
        PrinterJob job = PrinterJob.createPrinterJob();
        if (job != null) {
            boolean successDialog = job.showPrintDialog(stage);
            if (successDialog) {
                Printer printer = job.getPrinter();
                PageLayout pageLayout = printer.createPageLayout(Paper.A4, PageOrientation.PORTRAIT, Printer.MarginType.HARDWARE_MINIMUM);

                double pW = pageLayout.getPrintableWidth();
                double pH = pageLayout.getPrintableHeight();
                double nW = node.getBoundsInParent().getWidth();
                double nH = node.getBoundsInParent().getHeight();

                double scale = Math.min(pW / nW, pH / nH);
                scale *= 0.96; 

                Scale s = new Scale(scale, scale);
                node.getTransforms().add(s);

                if (job.printPage(pageLayout, node)) {
                    job.endJob();
                }
                node.getTransforms().remove(s);
            }
        }
    }
}
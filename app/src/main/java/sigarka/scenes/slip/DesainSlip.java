package sigarka.scenes.slip;

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
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.transform.Scale;
import javafx.stage.Stage;
import sigarka.View.AppStyle;

public class DesainSlip {

    public static VBox buatVisualSlip(Map<String, Object> data) {
        VBox slip = new VBox(15);
        // Margin 3cm standar (113px)
        slip.setPadding(new Insets(100, 90, 100, 90));
        slip.setStyle("-fx-background-color: white;");
        slip.setMinWidth(595); 
        slip.setMinHeight(842); 
        slip.setAlignment(Pos.TOP_CENTER);

        if (data == null || data.isEmpty()) {
            slip.getChildren().add(new Label("Data tidak valid"));
            return slip;
        }

        // === LOGO (CENTERED) ===
        try {
            Image logoImg = new Image(DesainSlip.class.getResourceAsStream("/Assets/images/Logo/logo_login.png"));
            ImageView logoView = new ImageView(logoImg);
            logoView.setFitWidth(140);
            logoView.setPreserveRatio(true);
            slip.getChildren().add(logoView);
        } catch (Exception e) {
            System.out.println("Logo tidak ditemukan");
        }

        // === HEADER (CENTERED) ===
        VBox headerBox = new VBox(3);
        headerBox.setAlignment(Pos.CENTER);

        Label lblJudul = new Label("SLIP GAJI KARYAWAN");
        Font gloock = Font.loadFont(DesainSlip.class.getResourceAsStream("/Assets/Fonts/Gloock-Regular.ttf"), 24);
        if (gloock != null) lblJudul.setFont(gloock);
        else lblJudul.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");
        lblJudul.setStyle(lblJudul.getStyle() + "-fx-text-fill: " + AppStyle.BLUE_COLOR + ";");

        Label lblPeriode = new Label("Periode: " + getString(data, "periode", "-"));
        lblPeriode.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

        headerBox.getChildren().addAll(lblJudul, lblPeriode);
        slip.getChildren().add(headerBox);

        // Space
        Region spacer1 = new Region(); spacer1.setMinHeight(30);
        slip.getChildren().add(spacer1);

        // === INFO KARYAWAN (LEFT) ===
        GridPane infoGrid = new GridPane();
        infoGrid.setHgap(10);
        infoGrid.setVgap(6);
        infoGrid.setAlignment(Pos.CENTER_LEFT);

        tambahBarisInfo(infoGrid, "Nama", getString(data, "nama", "-"), 0);
        tambahBarisInfo(infoGrid, "ID Karyawan", getString(data, "id", "-"), 1);
        
        String tipe = getString(data, "tipe", "Tetap");
        tambahBarisInfo(infoGrid, "Tipe", tipe, 2);

        if ("Karyawan Tetap".equals(tipe)) {
            tambahBarisInfo(infoGrid, "Divisi", getString(data, "divisi", "-"), 3);
            tambahBarisInfo(infoGrid, "Jabatan", getString(data, "jabatan", "-"), 4);
        }

        slip.getChildren().add(infoGrid);

        // Space
        Region spacer2 = new Region(); spacer2.setMinHeight(25);
        slip.getChildren().add(spacer2);

        // === RINCIAN GAJI (TABLE) ===
        VBox areaTabel = new VBox(10);
        Line l1 = new Line(0, 0, 415, 0); l1.setStrokeWidth(1.5);
        
        HBox headerTabel = new HBox(0);
        Label h1 = new Label("DESKRIPSI PENDAPATAN"); h1.setPrefWidth(280);
        Label h2 = new Label("JUMLAH (RP)");
        headerTabel.getChildren().addAll(h1, h2);
        headerTabel.setStyle("-fx-font-weight: bold; -fx-font-size: 12px;");

        VBox listPendapatan = new VBox(8);
        double gajiPokok = getDouble(data, "gaji_pokok");
        double tunjangan = getDouble(data, "tunjangan_kesehatan");
        double bonusBadge = getDouble(data, "bonus_badge");
        int lembur = getInt(data, "lembur");
        double bonusLembur = lembur * 100000.0;

        if ("Karyawan Tetap".equals(tipe)) {
            listPendapatan.getChildren().add(buatBarisGaji("Gaji Pokok", gajiPokok));
            listPendapatan.getChildren().add(buatBarisGaji("Tunjangan Kesehatan", tunjangan));
            listPendapatan.getChildren().add(buatBarisGaji("Bonus Lembur (" + lembur + " Hari)", bonusLembur));
            
            VBox badgeBox = new VBox(1);
            badgeBox.getChildren().add(buatBarisGaji("Bonus Badge", bonusBadge));
            
            String kat = getKategoriBonus(lembur, getInt(data, "alpa"), getInt(data, "izin"));
            if (!kat.isEmpty()) {
                Label lblKat = new Label("(" + kat + ")");
                lblKat.setStyle("-fx-font-size: 10px; -fx-font-style: italic; -fx-text-fill: #555;");
                lblKat.setPadding(new Insets(0, 0, 0, 5));
                badgeBox.getChildren().add(lblKat);
            }
            listPendapatan.getChildren().add(badgeBox);
        } else {
            double tarif = getDouble(data, "tarif_per_jam");
            listPendapatan.getChildren().add(buatBarisGaji("Tarif Kerja / Jam", tarif));
            listPendapatan.getChildren().add(buatBarisGaji("Tunjangan Kesehatan", 0));
            listPendapatan.getChildren().add(buatBarisGaji("Bonus Lembur", 0));
            listPendapatan.getChildren().add(buatBarisGaji("Bonus Badge", 0));
        }

        Line l2 = new Line(0, 0, 415, 0);
        
        VBox listPotongan = new VBox(8);
        Label h3 = new Label("POTONGAN KEHADIRAN"); h3.setStyle("-fx-font-weight: bold; -fx-font-size: 12px;");
        
        if ("Karyawan Tetap".equals(tipe)) {
            int alpa = getInt(data, "alpa");
            int izin = getInt(data, "izin");
            listPotongan.getChildren().addAll(
                buatBarisGaji("Potongan Alpa (" + alpa + " Kali)", alpa * 150000.0),
                buatBarisGaji("Potongan Izin (" + izin + " Kali)", izin * 75000.0)
            );
        } else {
            listPotongan.getChildren().addAll(buatBarisGaji("Potongan Alpa", 0), buatBarisGaji("Potongan Izin", 0));
        }

        Line l3 = new Line(0, 0, 415, 0); l3.setStrokeWidth(1.5);
        
        HBox rowGajiBersih = new HBox(0);
        Label lblTeksTotal = new Label("TOTAL GAJI BERSIH"); lblTeksTotal.setPrefWidth(280);
        lblTeksTotal.setStyle("-fx-font-weight: bold; -fx-font-size: 13px;");
        Label lblAngkaTotal = new Label("Rp " + String.format("%,.0f", getDouble(data, "gaji_bersih")));
        lblAngkaTotal.setStyle("-fx-font-weight: bold; -fx-font-size: 13px;");
        rowGajiBersih.getChildren().addAll(lblTeksTotal, lblAngkaTotal);

        areaTabel.getChildren().addAll(l1, headerTabel, listPendapatan, l2, h3, listPotongan, l3, rowGajiBersih);
        slip.getChildren().add(areaTabel);

        // === FOOTER (SIGNATURE) ===
        Region pushBottom = new Region(); VBox.setVgrow(pushBottom, Priority.ALWAYS);
        slip.getChildren().add(pushBottom);

        HBox footerContainer = new HBox();
        footerContainer.setAlignment(Pos.BOTTOM_RIGHT);
        
        VBox signBox = new VBox(50);
        signBox.setAlignment(Pos.CENTER);
        Label lblTanda = new Label("Tertanda,");
        Label lblNamaMgr = new Label("( Manager HRD )");
        lblNamaMgr.setStyle("-fx-font-weight: bold;");
        signBox.getChildren().addAll(lblTanda, lblNamaMgr);
        
        footerContainer.getChildren().add(signBox);
        slip.getChildren().add(footerContainer);

        return slip;
    }

    private static void tambahBarisInfo(GridPane grid, String label, String value, int row) {
        Label lbl = new Label(label);
        lbl.setMinWidth(100);
        lbl.setStyle("-fx-font-weight: bold; -fx-font-size: 12px;");
        grid.add(lbl, 0, row);
        Label val = new Label(": " + value);
        val.setStyle("-fx-font-size: 12px;");
        grid.add(val, 1, row);
    }

    private static String getKategoriBonus(int lembur, int alpa, int izin) {
        StringBuilder sb = new StringBuilder();
        if (lembur >= 5) sb.append("Super Productive🔥");
        if (alpa == 0 && izin == 0) {
            if (sb.length() > 0) sb.append(", ");
            sb.append("Discipline Master👑");
        }
        return sb.toString();
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

    private static HBox buatBarisGaji(String nama, double jumlah) {
        HBox row = new HBox(0);
        Label lblNama = new Label(nama); lblNama.setPrefWidth(280);
        lblNama.setStyle("-fx-font-size: 12px;");
        Label lblJml = new Label("Rp " + String.format("%,.0f", jumlah));
        lblJml.setStyle("-fx-font-size: 12px;");
        row.getChildren().addAll(lblNama, lblJml);
        return row;
    }

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
                scale *= 0.96; // Margin aman

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
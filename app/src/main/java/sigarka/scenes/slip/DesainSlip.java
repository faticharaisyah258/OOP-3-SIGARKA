package sigarka.scenes.slip;

import java.util.Map;

import javafx.print.PageLayout;
import javafx.print.PageOrientation;
import javafx.print.Paper;
import javafx.print.Printer;
import javafx.print.PrinterJob;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.transform.Scale;
import javafx.stage.Stage;

public class DesainSlip {

    public static VBox buatVisualSlip(Map<String, Object> data) {
        VBox slip = new VBox(15);

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

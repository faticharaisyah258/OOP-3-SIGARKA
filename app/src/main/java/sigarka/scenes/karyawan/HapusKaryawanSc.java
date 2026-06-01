package sigarka.scenes.karyawan;

import java.util.Optional;

import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import sigarka.View.AppStyle;
import sigarka.repository.KaryawanRepo;

public class HapusKaryawanSc {

    public static boolean konfirmasiHapus(String id, String nama) {

        // === ALERT ===
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Konfirmasi Hapus");
        alert.setHeaderText(null);
        alert.setContentText("Yakin ingin menghapus " + nama + "?");

        alert.setGraphic(null);

        // ===== STYLING BUTTON =====
        ButtonType btnYes = new ButtonType("Yes");
        ButtonType btnNo = new ButtonType("No");
        alert.getButtonTypes().setAll(btnYes, btnNo);
        
        Button btnYesNode = (Button) alert.getDialogPane().lookupButton(btnYes);
        Button btnNoNode = (Button) alert.getDialogPane().lookupButton(btnNo);

        btnYesNode.setStyle("-fx-background-color: " + AppStyle.BLUE_COLOR + "; -fx-text-fill: white; -fx-cursor: hand;");
        btnNoNode.setStyle("-fx-background-color: " + AppStyle.LIGHTGREEN_COLOR + "; -fx-text-fill: " + AppStyle.NOTSOBLACK_COLOR + "; -fx-cursor: hand;");

        
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == btnYes) {
            KaryawanRepo repo = new KaryawanRepo();
            repo.hapus(id);
            return true;
        }
        return false;
    }
}
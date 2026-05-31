package sigarka.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import sigarka.database.KoneksiDatabase;
import sigarka.models.Karyawan;
import sigarka.models.KaryawanKontrak;
import sigarka.models.KaryawanTetap;

// ===== PERINTAH SQL QUERY =====
public class KaryawanRepo {

    // ===== MENAMBAH KE DATABASE =====
    public void tambah(String id, String nama, String tipe, String divisi, String jabatan, double gajiPokok, double tarif) {
        String sql = "INSERT INTO karyawan(id, nama, tipe, divisi, jabatan, gaji_pokok, tarif_per_jam) VALUES(?,?,?,?,?,?,?)";
        try (Connection conn = KoneksiDatabase.sambung();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, id);
            pstmt.setString(2, nama);
            pstmt.setString(3, tipe);
            pstmt.setString(4, divisi);
            pstmt.setString(5, jabatan);
            pstmt.setDouble(6, gajiPokok);
            pstmt.setDouble(7, tarif);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

     // ===== MENGHAPUS DARI DATABASE =====
    public void hapus(String id) {
        String sqlGaji = "DELETE FROM riwayat_gaji WHERE karyawan_id = ?";
        String sqlKaryawan = "DELETE FROM karyawan WHERE id = ?";
        try (Connection conn = KoneksiDatabase.sambung()) {
             conn.setAutoCommit(false);
             try (PreparedStatement pstmtG = conn.prepareStatement(sqlGaji);
                  PreparedStatement pstmtK = conn.prepareStatement(sqlKaryawan)) {
                 pstmtG.setString(1, id);
                 pstmtG.executeUpdate();
                 
                 pstmtK.setString(1, id);
                 pstmtK.executeUpdate();
                 
                 conn.commit();
             } catch (SQLException e) {
                 conn.rollback();
                 throw e;
             }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // ===== MENGAMBIL DATA KARYAWAN =====
    public List<Karyawan> ambilSemua() {
        List<Karyawan> list = new ArrayList<>();
        String sql = "SELECT * FROM karyawan";
        try (Connection conn = KoneksiDatabase.sambung();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                String tipe = rs.getString("tipe");
                if ("Karyawan Tetap".equals(tipe)) {
                    list.add(new KaryawanTetap(
                        rs.getString("id"),
                        rs.getString("nama"),
                        tipe,
                        rs.getString("divisi"),
                        rs.getString("jabatan"),
                        0, 0, 0,
                        rs.getDouble("gaji_pokok")
                    ));
                } else {
                    list.add(new KaryawanKontrak(
                        rs.getString("id"),
                        rs.getString("nama"),
                        tipe,
                        0,
                        rs.getDouble("tarif_per_jam")
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // ===== MENGAMBIL GAJI (K.TETAP) =====
    public double getGajiPokok(String id) {
        String sql = "SELECT gaji_pokok FROM karyawan WHERE id = ?";
        try (Connection conn = KoneksiDatabase.sambung();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) return rs.getDouble("gaji_pokok");
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }

     // ===== MENGAMBIL TARIF (K.KONTRAK) =====
    public double getTarif(String id) {
        String sql = "SELECT tarif_per_jam FROM karyawan WHERE id = ?";
        try (Connection conn = KoneksiDatabase.sambung();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) return rs.getDouble("tarif_per_jam");
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }
}
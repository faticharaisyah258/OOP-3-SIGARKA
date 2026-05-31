package sigarka.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import sigarka.database.KoneksiDatabase;

public class GajiRepo {

    // ===== MENYIMPAN RIWAYAT GAJI =====
    public void simpanRiwayat(String karyawanId, String periode, double tunjangan, double bonus, int izin, int alpa, int lembur, int jamKerja, double gajiBersih) {
        String sql = "INSERT INTO riwayat_gaji(karyawan_id, periode, tunjangan_kesehatan, bonus_badge, izin, alpa, lembur, jam_kerja, gaji_bersih) VALUES(?,?,?,?,?,?,?,?,?)";
        try (Connection conn = KoneksiDatabase.sambung();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, karyawanId);
            pstmt.setString(2, periode);
            pstmt.setDouble(3, tunjangan);
            pstmt.setDouble(4, bonus);
            pstmt.setInt(5, izin);
            pstmt.setInt(6, alpa);
            pstmt.setInt(7, lembur);
            pstmt.setInt(8, jamKerja);
            pstmt.setDouble(9, gajiBersih);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // ===== CEK APAKAH PERIODE SUDAH ADA =====
    public boolean apakahPeriodeSudahAda(String karyawanId, String periode) {
        String sql = "SELECT COUNT(*) FROM riwayat_gaji WHERE karyawan_id = ? AND periode = ?";
        try (Connection conn = KoneksiDatabase.sambung();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, karyawanId);
            pstmt.setString(2, periode);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // ===== AMBIL DAFTAR PERIODE BERDASARKAN KARYAWAN =====
    public Map<Integer, String> ambilDaftarPeriode(String karyawanId) {
        Map<Integer, String> list = new HashMap<>();
        String sql = "SELECT id, periode FROM riwayat_gaji WHERE karyawan_id = ? ORDER BY id DESC";
        try (Connection conn = KoneksiDatabase.sambung();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, karyawanId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                list.put(rs.getInt("id"), rs.getString("periode"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // ===== AMBIL DETAIL DATA GAJI UNTUK SLIP =====
    public Map<String, Object> ambilDetailGaji(int riwayatId) {
        Map<String, Object> data = new HashMap<>();
        String sql = "SELECT r.*, k.nama, k.tipe, k.divisi, k.jabatan, k.gaji_pokok, k.tarif_per_jam " +
                     "FROM riwayat_gaji r " +
                     "JOIN karyawan k ON r.karyawan_id = k.id " +
                     "WHERE r.id = ?";
        try (Connection conn = KoneksiDatabase.sambung();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, riwayatId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                data.put("id", rs.getString("karyawan_id"));
                data.put("nama", rs.getString("nama"));
                data.put("tipe", rs.getString("tipe"));
                data.put("divisi", rs.getString("divisi"));
                data.put("jabatan", rs.getString("jabatan"));
                data.put("periode", rs.getString("periode"));
                data.put("gaji_pokok", rs.getDouble("gaji_pokok"));
                data.put("tunjangan_kesehatan", rs.getDouble("tunjangan_kesehatan"));
                data.put("bonus_badge", rs.getDouble("bonus_badge"));
                data.put("izin", rs.getInt("izin"));
                data.put("alpa", rs.getInt("alpa"));
                data.put("lembur", rs.getInt("lembur"));
                data.put("jam_kerja", rs.getInt("jam_kerja"));
                data.put("gaji_bersih", rs.getDouble("gaji_bersih"));
                data.put("tarif_per_jam", rs.getDouble("tarif_per_jam"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return data;
    }

    // ===== HAPUS RIWAYAT GAJI =====
    public void hapusRiwayatGaji(int riwayatId) {
        String sql = "DELETE FROM riwayat_gaji WHERE id = ?";
        try (Connection conn = KoneksiDatabase.sambung();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, riwayatId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

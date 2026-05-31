package sigarka.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Set;

public class KoneksiDatabase {

    // ===== LOKASI DATABASE =====
    private static final String URL = "jdbc:sqlite:sigarka.db";

    // ===== MENGHUBUNGKAN KE DATABASE =====
    public static Connection sambung() {
        try {
            // Memastikan driver SQLite terisi
            Class.forName("org.sqlite.JDBC");
            Connection conn = DriverManager.getConnection(URL);
            buatTabel(conn);
            perbaruiSkema(conn);
            return conn;
        } catch (ClassNotFoundException e) {
            System.err.println("Driver SQLite tidak ditemukan: " + e.getMessage());
            return null;
        } catch (SQLException e) {
            System.err.println("Gagal menyambung ke database: " + e.getMessage());
            return null;
        }
    }

    // ===== MEMBUAT TABEL DATA =====
    private static void buatTabel(Connection conn) {
        try (Statement stmt = conn.createStatement()) {
            // Tabel Karyawan
            stmt.execute("CREATE TABLE IF NOT EXISTS karyawan (" +
                         "id TEXT PRIMARY KEY, " +
                         "nama TEXT NOT NULL, " +
                         "tipe TEXT NOT NULL, " +
                         "divisi TEXT, " +
                         "jabatan TEXT, " +
                         "gaji_pokok REAL, " +
                         "tarif_per_jam REAL" +
                         ");");
            
            // Tabel Riwayat Gaji
            stmt.execute("CREATE TABLE IF NOT EXISTS riwayat_gaji (" +
                         "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                         "karyawan_id TEXT, " +
                         "periode TEXT, " +
                         "tunjangan_kesehatan REAL, " +
                         "bonus_badge REAL, " +
                         "izin INTEGER, " +
                         "alpa INTEGER, " +
                         "lembur INTEGER, " +
                         "jam_kerja INTEGER, " +
                         "gaji_bersih REAL, " +
                         "FOREIGN KEY(karyawan_id) REFERENCES karyawan(id)" +
                         ");");
        } catch (SQLException e) {
            System.err.println("Gagal membuat tabel: " + e.getMessage());
        }
    }

    // ===== MEMASTIKAN KOLOM BARU TERSEDIA =====
    private static void perbaruiSkema(Connection conn) {
        try {
            // 1. Cek kolom di tabel riwayat_gaji
            Set<String> kolomGaji = ambilDaftarKolom(conn, "riwayat_gaji");
            tambahKolomJikaTidakAda(conn, "riwayat_gaji", "alpa", "INTEGER DEFAULT 0", kolomGaji);
            tambahKolomJikaTidakAda(conn, "riwayat_gaji", "izin", "INTEGER DEFAULT 0", kolomGaji);
            tambahKolomJikaTidakAda(conn, "riwayat_gaji", "lembur", "INTEGER DEFAULT 0", kolomGaji);
            tambahKolomJikaTidakAda(conn, "riwayat_gaji", "jam_kerja", "INTEGER DEFAULT 0", kolomGaji);
            tambahKolomJikaTidakAda(conn, "riwayat_gaji", "tunjangan_kesehatan", "REAL DEFAULT 0", kolomGaji);
            tambahKolomJikaTidakAda(conn, "riwayat_gaji", "bonus_badge", "REAL DEFAULT 0", kolomGaji);

            // 2. Cek kolom di tabel karyawan
            Set<String> kolomKaryawan = ambilDaftarKolom(conn, "karyawan");
            tambahKolomJikaTidakAda(conn, "karyawan", "gaji_pokok", "REAL DEFAULT 0", kolomKaryawan);
            tambahKolomJikaTidakAda(conn, "karyawan", "tarif_per_jam", "REAL DEFAULT 0", kolomKaryawan);

        } catch (Exception e) {
            System.err.println("Gagal memperbarui skema: " + e.getMessage());
        }
    }

    private static Set<String> ambilDaftarKolom(Connection conn, String namaTabel) throws SQLException {
        Set<String> kolom = new HashSet<>();
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("PRAGMA table_info(" + namaTabel + ")")) {
            while (rs.next()) {
                kolom.add(rs.getString("name").toLowerCase());
            }
        }
        return kolom;
    }

    private static void tambahKolomJikaTidakAda(Connection conn, String namaTabel, String namaKolom, String tipe, Set<String> kolomAda) {
        if (!kolomAda.contains(namaKolom.toLowerCase())) {
            try (Statement stmt = conn.createStatement()) {
                stmt.execute("ALTER TABLE " + namaTabel + " ADD COLUMN " + namaKolom + " " + tipe);
                System.out.println("Skema diperbarui: Menambahkan kolom " + namaKolom + " ke " + namaTabel);
            } catch (SQLException e) {
                System.err.println("Gagal menambah kolom " + namaKolom + ": " + e.getMessage());
            }
        }
    }
}

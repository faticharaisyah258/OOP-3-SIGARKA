package sigarka.models;

public class KaryawanTetap extends Karyawan {
    private int alfa;
    private int izin;
    private int lembur;
    private String divisi;
    private String jabatan;
    private double gajiPokok;

    public KaryawanTetap(String id, String nama, String tipe, String divisi, String jabatan, int alfa, int izin, int lembur, double gajiPokok) {
        super(id, nama, tipe);
        this.divisi = divisi;
        this.jabatan = jabatan;
        this.alfa = alfa;
        this.izin = izin;
        this.lembur = lembur;
        this.gajiPokok = gajiPokok;
    }

    public String getDivisi() {
        return divisi;
    }

    public String getJabatan() {
        return jabatan;
    }

     public double getGajiPokok() {
        return gajiPokok;
    }

    @Override
    public double hitungGaji() {
        double gajiPokok = this.gajiPokok;
        
        // Jika gajiPokok belum diset (misal 0), gunakan logika lama sebagai fallback
        if (gajiPokok <= 0) {
            if (getDivisi().equals("Bisnis Global dan Pemasaran")){
                gajiPokok = getJabatan().equals("Manager") ? 7000000 : 6000000;
            } else if (getDivisi().equals("Produksi Kreatif")){
                gajiPokok = getJabatan().equals("Manager") ? 6500000 : 5500000;
            } else if (getDivisi().equals("Artist & Repertoire")){
                gajiPokok = getJabatan().equals("Manager") ? 7500000 : 6500000;
            }
        }

        double potongan = (alfa * 150000) + (izin * 75000); 
        double bonus = 0;
        if (lembur >= 5) bonus += 150000;
        if (alfa == 0 && izin == 0) bonus += 100000;

        double tunjanganKesehatan = 300000;

        return gajiPokok - potongan + bonus + (lembur * 100000) + tunjanganKesehatan;
    }
}
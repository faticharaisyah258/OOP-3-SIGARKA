package sigarka.models;

public class KaryawanKontrak extends Karyawan {
    private int jamKerja;
    private double tarifPerJam;

    public KaryawanKontrak(String id, String nama, String tipe, int jamKerja, double tarifPerJam) {
        super(id, nama, tipe);    
        this.jamKerja = jamKerja;
        this.tarifPerJam = tarifPerJam;
    }

    public double getTarifPerJam() {
        return tarifPerJam;
    }

    @Override
    public double hitungGaji() {
        double tarif = this.tarifPerJam > 0 ? this.tarifPerJam : 30000;
        return jamKerja * tarif; 
    }
}
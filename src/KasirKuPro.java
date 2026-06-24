import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.*;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class KasirKuPro extends JFrame {

    private static final String DB_URL = "jdbc:sqlite:" + System.getProperty("user.dir") + "/kasirku.db";    private Connection conn;

    private DefaultTableModel tableModelBarang, tableModelKeranjang, tableModelLaporan;
    private JLabel lblTotalHarga, lblKembalian, lblTotalOmzetHariIni, lblTotalOmzetKeseluruhan;
    private JTextField txtUangBayar;
    private double totalBelanja = 0;

    private NumberFormat formatRp = NumberFormat.getCurrencyInstance(new Locale("in", "ID"));

    public KasirKuPro() {
        setTitle("KasirKu Pro - Sistem Point of Sale");
        setSize(950, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        initDatabase();

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 14));

        tabbedPane.addTab("Menu Kasir", createKasirPanel());
        tabbedPane.addTab("Manajemen Stok", createStokPanel());
        tabbedPane.addTab("Laporan Harian", createLaporanPanel());

        add(tabbedPane);
    }

    private void initDatabase() {
        try {
            Class.forName("org.sqlite.JDBC");
            conn = DriverManager.getConnection(DB_URL);
            Statement stmt = conn.createStatement();

            stmt.execute("CREATE TABLE IF NOT EXISTS barang (id INTEGER PRIMARY KEY AUTOINCREMENT, nama TEXT NOT NULL, harga REAL NOT NULL, stok INTEGER NOT NULL);");
            stmt.execute("CREATE TABLE IF NOT EXISTS transaksi (id_transaksi TEXT PRIMARY KEY, tanggal DATETIME DEFAULT CURRENT_TIMESTAMP, total REAL NOT NULL, bayar REAL NOT NULL, kembali REAL NOT NULL);");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error DB: " + e.getMessage(), "Kesalahan", JOptionPane.ERROR_MESSAGE);
        }
    }

    // --- PANEL 1: MENU KASIR ---
    private JPanel createKasirPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));

        JPanel formPanel = new JPanel(new GridLayout(2, 3, 10, 5));
        JTextField txtCariId = new JTextField();
        JTextField txtQty = new JTextField("1");
        JButton btnTambahKeranjang = new JButton("Tambahkan");
        btnTambahKeranjang.setBackground(new Color(25, 118, 210));
        btnTambahKeranjang.setForeground(Color.black);

        formPanel.add(new JLabel("ID Barang / Nama:"));
        formPanel.add(new JLabel("Jumlah (Qty):"));
        formPanel.add(new JLabel(""));
        formPanel.add(txtCariId); formPanel.add(txtQty); formPanel.add(btnTambahKeranjang);
        panel.add(formPanel, BorderLayout.NORTH);

        tableModelKeranjang = new DefaultTableModel(new String[]{"ID", "Nama Barang", "Harga", "Qty", "Subtotal"}, 0);
        JTable tableKeranjang = new JTable(tableModelKeranjang);
        tableKeranjang.setRowHeight(30);
        panel.add(new JScrollPane(tableKeranjang), BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new BorderLayout(15, 15));

        JPanel infoPanel = new JPanel(new GridLayout(2, 1, 0, 5));
        lblTotalHarga = new JLabel("Total: Rp 0,00");
        lblTotalHarga.setFont(new Font("Segoe UI", Font.BOLD, 32));
        lblTotalHarga.setForeground(new Color(198, 40, 40));
        lblKembalian = new JLabel("Kembalian: Rp 0,00");
        lblKembalian.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblKembalian.setForeground(new Color(46, 125, 50));
        infoPanel.add(lblTotalHarga); infoPanel.add(lblKembalian);

        JPanel actionPanel = new JPanel(new BorderLayout(10, 0));
        JPanel bayarPanel = new JPanel(new GridLayout(2, 1));
        bayarPanel.add(new JLabel("Uang Dibayar (Rp):          ")); // Petunjuk diperbarui
        txtUangBayar = new JTextField();
        txtUangBayar.setFont(new Font("Segoe UI", Font.BOLD, 18));
        bayarPanel.add(txtUangBayar);

        JButton btnBayar = new JButton("BAYAR & SELESAI");
        btnBayar.setFont(new Font("Segoe UI", Font.BOLD, 18));
        btnBayar.setBackground(new Color(46, 125, 50));
        btnBayar.setForeground(Color.BLACK);
        btnBayar.setPreferredSize(new Dimension(220, 60));

        actionPanel.add(bayarPanel, BorderLayout.WEST); actionPanel.add(btnBayar, BorderLayout.EAST);
        bottomPanel.add(infoPanel, BorderLayout.WEST); bottomPanel.add(actionPanel, BorderLayout.EAST);
        panel.add(bottomPanel, BorderLayout.SOUTH);

        txtCariId.addActionListener(e -> {
            tambahKeKeranjang(txtCariId.getText(), txtQty.getText());
            txtCariId.setText(""); txtQty.setText("1"); txtCariId.requestFocus();
        });

        btnTambahKeranjang.addActionListener(e -> {
            tambahKeKeranjang(txtCariId.getText(), txtQty.getText());
            txtCariId.setText(""); txtQty.setText("1"); txtCariId.requestFocus();
        });

        txtUangBayar.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) { hitungKembalian(); }
        });

        txtUangBayar.addActionListener(e -> prosesPembayaran());

        btnBayar.addActionListener(e -> prosesPembayaran());

        return panel;
    }

    private JPanel createStokPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));

        JPanel formPanel = new JPanel(new GridLayout(2, 4, 10, 5));
        JTextField txtNama = new JTextField();
        JTextField txtHarga = new JTextField();
        JTextField txtStok = new JTextField();
        JButton btnSimpan = new JButton("Tambah Barang");

        formPanel.add(new JLabel("Nama Barang:")); formPanel.add(new JLabel("Harga Jual (Rp):"));
        formPanel.add(new JLabel("Jumlah Stok:")); formPanel.add(new JLabel(""));
        formPanel.add(txtNama); formPanel.add(txtHarga); formPanel.add(txtStok); formPanel.add(btnSimpan);
        panel.add(formPanel, BorderLayout.NORTH);

        tableModelBarang = new DefaultTableModel(new String[]{"ID", "Nama Barang", "Harga", "Stok Tersedia"}, 0);
        JTable tableBarang = new JTable(tableModelBarang);
        tableBarang.setRowHeight(25);
        panel.add(new JScrollPane(tableBarang), BorderLayout.CENTER);

        btnSimpan.addActionListener(e -> {
            simpanBarang(txtNama.getText(), txtHarga.getText(), txtStok.getText());
            txtNama.setText(""); txtHarga.setText(""); txtStok.setText(""); txtNama.requestFocus();
        });

        loadDataBarang();
        return panel;
    }

    // --- PANEL 3: LAPORAN HARIAN ---
    private JPanel createLaporanPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));

        tableModelLaporan = new DefaultTableModel(new String[]{"ID Transaksi", "Waktu Transaksi", "Total Belanja"}, 0);
        JTable tableLaporan = new JTable(tableModelLaporan);
        tableLaporan.setRowHeight(25);
        panel.add(new JScrollPane(tableLaporan), BorderLayout.CENTER);

        JPanel omzetPanel = new JPanel(new GridLayout(2, 1, 5, 5));

        lblTotalOmzetHariIni = new JLabel("Omzet Hari Ini: Rp 0,00");
        lblTotalOmzetHariIni.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblTotalOmzetHariIni.setForeground(new Color(46, 125, 50));

        lblTotalOmzetKeseluruhan = new JLabel("Omzet Keseluruhan: Rp 0,00");
        lblTotalOmzetKeseluruhan.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTotalOmzetKeseluruhan.setForeground(new Color(25, 118, 210));

        omzetPanel.add(lblTotalOmzetHariIni);
        omzetPanel.add(lblTotalOmzetKeseluruhan);
        panel.add(omzetPanel, BorderLayout.SOUTH);

        panel.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent e) { loadLaporan(); }
        });

        return panel;
    }

    // --- FUNGSI LOGIKA SISTEM ---

    private void tambahKeKeranjang(String keyword, String strQty) {
        if (keyword.isEmpty() || strQty.isEmpty()) return;
        try {
            int qty = Integer.parseInt(strQty);
            PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM barang WHERE id = ? OR nama LIKE ?");
            pstmt.setString(1, keyword); pstmt.setString(2, "%" + keyword + "%");
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                if (rs.getInt("stok") < qty) {
                    JOptionPane.showMessageDialog(this, "Stok tidak cukup!", "Peringatan", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                double subtotal = rs.getDouble("harga") * qty;
                tableModelKeranjang.addRow(new Object[]{rs.getInt("id"), rs.getString("nama"), formatRp.format(rs.getDouble("harga")), qty, formatRp.format(subtotal)});
                totalBelanja += subtotal;
                lblTotalHarga.setText("Total: " + formatRp.format(totalBelanja));
                hitungKembalian();
            } else {
                JOptionPane.showMessageDialog(this, "Barang tidak ditemukan!");
            }
        } catch (Exception ex) {}
    }

    private void hitungKembalian() {
        try {
            String strBayar = txtUangBayar.getText().replace(".", "").trim();
            if (strBayar.isEmpty()) {
                lblKembalian.setText("Kembalian: Rp 0,00");
                return;
            }
            double bayar = Double.parseDouble(strBayar);
            double kembalian = bayar - totalBelanja;
            if (kembalian < 0) {
                lblKembalian.setText("Uang Kurang!");
                lblKembalian.setForeground(Color.RED);
            } else {
                lblKembalian.setText("Kembalian: " + formatRp.format(kembalian));
                lblKembalian.setForeground(new Color(46, 125, 50));
            }
        } catch (Exception ex) {}
    }

    private void prosesPembayaran() {
        if (tableModelKeranjang.getRowCount() == 0) return;
        try {
            double bayar = Double.parseDouble(txtUangBayar.getText().replace(".", "").trim());
            double kembalian = bayar - totalBelanja;
            if (kembalian < 0) {
                JOptionPane.showMessageDialog(this, "Uang pembayaran kurang!"); return;
            }

            String idTrx = "TRX-" + new SimpleDateFormat("yyyyMMdd-HHmmss").format(new Date());
            PreparedStatement pstmtTrx = conn.prepareStatement("INSERT INTO transaksi (id_transaksi, total, bayar, kembali) VALUES (?, ?, ?, ?)");
            pstmtTrx.setString(1, idTrx); pstmtTrx.setDouble(2, totalBelanja); pstmtTrx.setDouble(3, bayar); pstmtTrx.setDouble(4, kembalian);
            pstmtTrx.executeUpdate();

            PreparedStatement pstmtStok = conn.prepareStatement("UPDATE barang SET stok = stok - ? WHERE id = ?");
            for (int i = 0; i < tableModelKeranjang.getRowCount(); i++) {
                pstmtStok.setInt(1, (int) tableModelKeranjang.getValueAt(i, 3));
                pstmtStok.setInt(2, (int) tableModelKeranjang.getValueAt(i, 0));
                pstmtStok.executeUpdate();
            }

            String struk = "Pembayaran Berhasil!\n\n" +
                    "ID Transaksi : " + idTrx + "\n" +
                    "Total Belanja : " + formatRp.format(totalBelanja) + "\n" +
                    "Uang Tunai : " + formatRp.format(bayar) + "\n" +
                    "Kembalian : " + formatRp.format(kembalian);

            JOptionPane.showMessageDialog(this, struk, "Cetak Struk", JOptionPane.INFORMATION_MESSAGE);

            tableModelKeranjang.setRowCount(0); totalBelanja = 0;
            lblTotalHarga.setText("Total: Rp 0,00"); lblKembalian.setText("Kembalian: Rp 0,00"); txtUangBayar.setText("");
            loadDataBarang();
        } catch (Exception ex) {}
    }

    private void loadDataBarang() {
        tableModelBarang.setRowCount(0);
        try {
            ResultSet rs = conn.createStatement().executeQuery("SELECT * FROM barang ORDER BY id DESC");
            while (rs.next()) {
                tableModelBarang.addRow(new Object[]{rs.getInt("id"), rs.getString("nama"), formatRp.format(rs.getDouble("harga")), rs.getInt("stok")});
            }
        } catch (Exception e) {}
    }

    private void simpanBarang(String nama, String strHarga, String strStok) {
        if (nama.isEmpty() || strHarga.isEmpty() || strStok.isEmpty()) return;
        try {
            PreparedStatement pstmt = conn.prepareStatement("INSERT INTO barang (nama, harga, stok) VALUES (?, ?, ?)");
            pstmt.setString(1, nama); pstmt.setDouble(2, Double.parseDouble(strHarga.replace(".", ""))); pstmt.setInt(3, Integer.parseInt(strStok));
            pstmt.executeUpdate();
            loadDataBarang();
        } catch (Exception e) {}
    }

    private void loadLaporan() {
        tableModelLaporan.setRowCount(0);
        double omzetKeseluruhan = 0;
        double omzetHariIni = 0;

        String tanggalHariIni = new SimpleDateFormat("yyyy-MM-dd").format(new Date());

        try {
            ResultSet rs = conn.createStatement().executeQuery("SELECT id_transaksi, datetime(tanggal, 'localtime') AS waktu_lokal, total FROM transaksi ORDER BY tanggal DESC");

            while (rs.next()) {
                double total = rs.getDouble("total");
                String waktuLokal = rs.getString("waktu_lokal");

                if (waktuLokal == null) waktuLokal = rs.getString("tanggal");

                tableModelLaporan.addRow(new Object[]{rs.getString("id_transaksi"), waktuLokal, formatRp.format(total)});

                omzetKeseluruhan += total;

                if (waktuLokal.startsWith(tanggalHariIni)) {
                    omzetHariIni += total;
                }
            }

            lblTotalOmzetHariIni.setText("Omzet Hari Ini: " + formatRp.format(omzetHariIni));
            lblTotalOmzetKeseluruhan.setText("Omzet Keseluruhan: " + formatRp.format(omzetKeseluruhan));

        } catch (Exception e) {}
    }

    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception e) {}
        SwingUtilities.invokeLater(() -> new KasirKuPro().setVisible(true));
    }
}
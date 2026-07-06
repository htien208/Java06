package com.shop.ui;

import com.shop.util.SessionManager;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {

    private JPanel panel1;

    private JPanel pnlLeftButton;
    private JLabel lblBrand;
    private JButton butProd;
    private JButton butInvoice;
    private JButton butCus;
    private JButton butLogout;
    private JButton butSetting;

    private JPanel pnlCardLayout;
    private JPanel pnlContent;
    private JPanel pnlProd;
    private JPanel pnlInvoice;
    private JPanel pnlCus;
    private JPanel pnlSetting;

    private ProductPanel productPanel;

    public MainFrame() {
        setContentPane(panel1);
        setTitle("hamiti's Shop");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 680);
        setMinimumSize(new Dimension(900, 580));
        setLocationRelativeTo(null);

        productPanel = new ProductPanel();
        pnlProd.setLayout(new BorderLayout());
        pnlProd.add(productPanel, BorderLayout.CENTER);

        pnlInvoice.setLayout(new BorderLayout());
        pnlInvoice.add(new InvoicePanel(), BorderLayout.CENTER);

        pnlCus.setLayout(new BorderLayout());
        pnlCus.add(new CusPanel(), BorderLayout.CENTER);

        pnlSetting.setLayout(new BorderLayout());
        pnlSetting.add(new SettingPanel(), BorderLayout.CENTER);

        // Gắn CardLayout cho pnlContent
        pnlContent.setLayout(new CardLayout());
        pnlContent.add(pnlProd,    "prod");
        pnlContent.add(pnlInvoice, "invoice");
        pnlContent.add(pnlCus,     "cus");
        pnlContent.add(pnlSetting, "setting");


        // Tăng kích thước icon
        int iconSize = 48; // tăng từ 20 lên 32
        ImageIcon icProd    = loadIcon("product.png",  iconSize);
        ImageIcon icInvoice = loadIcon("invoice.png",  iconSize);
        ImageIcon icCus     = loadIcon("customer.png", iconSize);
        ImageIcon icSetting = loadIcon("setting.png",  iconSize);
        ImageIcon icLogout  = loadIcon("logout.png",   iconSize);

// Với mỗi button — đổi cấu hình như sau:
        if (icProd != null) {
            butProd.setIcon(icProd);
            butProd.setHorizontalAlignment(SwingConstants.CENTER);      // căn giữa theo chiều ngang
            butProd.setVerticalTextPosition(SwingConstants.BOTTOM);     // text ở dưới icon
            butProd.setHorizontalTextPosition(SwingConstants.CENTER);   // text căn giữa so với icon
            butProd.setIconTextGap(6);
        }

        if (icInvoice != null) {
            butInvoice.setIcon(icInvoice);
            butInvoice.setHorizontalAlignment(SwingConstants.CENTER);
            butInvoice.setVerticalTextPosition(SwingConstants.BOTTOM);
            butInvoice.setHorizontalTextPosition(SwingConstants.CENTER);
            butInvoice.setIconTextGap(6);
        }

        if (icCus != null) {
            butCus.setIcon(icCus);
            butCus.setHorizontalAlignment(SwingConstants.CENTER);
            butCus.setVerticalTextPosition(SwingConstants.BOTTOM);
            butCus.setHorizontalTextPosition(SwingConstants.CENTER);
            butCus.setIconTextGap(6);
        }

        if (icSetting != null) {
            butSetting.setIcon(icSetting);
            butSetting.setHorizontalAlignment(SwingConstants.CENTER);
            butSetting.setVerticalTextPosition(SwingConstants.BOTTOM);
            butSetting.setHorizontalTextPosition(SwingConstants.CENTER);
            butSetting.setIconTextGap(6);
        }

        if (icLogout != null) {
            butLogout.setIcon(icLogout);
            butLogout.setHorizontalAlignment(SwingConstants.CENTER);
            butLogout.setVerticalTextPosition(SwingConstants.BOTTOM);
            butLogout.setHorizontalTextPosition(SwingConstants.CENTER);
            butLogout.setIconTextGap(6);
        }
        // Hiển thị tên user
        if (SessionManager.getCurrentUser() != null) {
            lblBrand.setText(SessionManager.getCurrentUser().getFullName() + " đang làm việc");
        }

        // Ẩn nút Cài đặt nếu không phải MANAGER
        if (!SessionManager.isManager()) {
            butSetting.setVisible(false);
        }

        // Sự kiện sidebar
        butProd.addActionListener(e -> {
            showCard("prod");
            if (productPanel != null) productPanel.refresh();
        });
        butInvoice.addActionListener(e -> showCard("invoice"));
        butCus    .addActionListener(e -> showCard("cus"));
        butSetting.addActionListener(e -> showCard("setting"));

        // Đăng xuất
        butLogout.addActionListener(e -> {
            int ok = JOptionPane.showConfirmDialog(
                    this,
                    "Bạn có chắc muốn đăng xuất?",
                    "Đăng xuất",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE
            );
            if (ok == JOptionPane.YES_OPTION) {
                SessionManager.logout();
                new LoginForm().setVisible(true);
                this.dispose();
            }
        });

        // Mặc định hiển thị Sản phẩm
        showCard("prod");
    }

    public void showCard(String name) {
        CardLayout cl = (CardLayout) pnlContent.getLayout();
        cl.show(pnlContent, name);
    }

    private ImageIcon loadIcon(String fileName, int size) {
        try {
            java.net.URL url = getClass().getResource("/resources/icons/" + fileName);
            if (url == null) return null;
            ImageIcon raw = new ImageIcon(url);
            Image scaled = raw.getImage().getScaledInstance(size, size, Image.SCALE_SMOOTH);
            return new ImageIcon(scaled);
        } catch (Exception e) {
            return null;
        }
    }
}
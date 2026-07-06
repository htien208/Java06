package com.shop.util;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


public class ExportUtil {

    public static void exportCSV(java.awt.Component parent,
                                 JTable table,
                                 String fileName) {

        DefaultTableModel model = (DefaultTableModel) table.getModel();

        if (model.getRowCount() == 0) {
            JOptionPane.showMessageDialog(parent,
                    "Không có dữ liệu để xuất!",
                    "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Lưu file CSV");

        // Tên file mặc định có kèm timestamp
        String timestamp = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        fileChooser.setSelectedFile(new File(fileName + "_" + timestamp + ".csv"));

        int result = fileChooser.showSaveDialog(parent);
        if (result != JFileChooser.APPROVE_OPTION) return;

        File file = fileChooser.getSelectedFile();

        // Đảm bảo file có đuôi .csv
        if (!file.getName().endsWith(".csv")) {
            file = new File(file.getAbsolutePath() + ".csv");
        }

        // Ghi dữ liệu ra file
        try (BufferedWriter bw = new BufferedWriter(
                new FileWriter(file, java.nio.charset.StandardCharsets.UTF_8))) {

            // Ghi BOM để Excel đọc được tiếng Việt
            bw.write('\uFEFF');

            // Ghi header (tên cột)
            StringBuilder header = new StringBuilder();
            for (int col = 0; col < model.getColumnCount(); col++) {
                // Bỏ qua cột ẩn (width = 0)
                if (table.getColumnModel().getColumn(col).getWidth() == 0) continue;
                if (header.length() > 0) header.append(",");
                header.append(escapeCSV(model.getColumnName(col)));
            }
            bw.write(header.toString());
            bw.newLine();

            // Ghi từng dòng dữ liệu
            for (int row = 0; row < model.getRowCount(); row++) {
                StringBuilder line = new StringBuilder();
                for (int col = 0; col < model.getColumnCount(); col++) {
                    if (table.getColumnModel().getColumn(col).getWidth() == 0) continue;
                    if (line.length() > 0) line.append(",");
                    Object value = model.getValueAt(row, col);
                    line.append(escapeCSV(value != null ? value.toString() : ""));
                }
                bw.write(line.toString());
                bw.newLine();
            }

            JOptionPane.showMessageDialog(parent,
                    "Xuất CSV thành công!\nĐã lưu tại: " + file.getAbsolutePath(), "Thành công", JOptionPane.INFORMATION_MESSAGE);

        } catch (IOException e) {
            JOptionPane.showMessageDialog(parent,
                    "Xuất CSV thất bại!\nLỗi: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static String escapeCSV(String value) {
        if (value == null) return "";
        // Nếu có dấu phẩy, ngoặc kép hoặc xuống dòng → bọc trong ngoặc kép
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            value = value.replace("\"", "\"\"");
            return "\"" + value + "\"";
        }
        return value;
    }
}
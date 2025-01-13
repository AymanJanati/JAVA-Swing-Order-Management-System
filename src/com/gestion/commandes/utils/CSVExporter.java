package com.gestion.commandes.utils;

import javax.swing.*;
import javax.swing.table.TableModel;
import java.io.FileWriter;
import java.io.IOException;

public class CSVExporter {

    public static void exportToCSV(JTable table, String filePath) {
        try (FileWriter writer = new FileWriter(filePath)) {
            TableModel model = table.getModel();

            // Write column headers
            for (int i = 0; i < model.getColumnCount(); i++) {
                writer.write(model.getColumnName(i));
                if (i < model.getColumnCount() - 1) {
                    writer.write(",");
                }
            }
            writer.write("\n");

            // Write table data
            for (int i = 0; i < model.getRowCount(); i++) {
                for (int j = 0; j < model.getColumnCount(); j++) {
                    writer.write(model.getValueAt(i, j).toString());
                    if (j < model.getColumnCount() - 1) {
                        writer.write(",");
                    }
                }
                writer.write("\n");
            }

            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Erreur lors de l'exportation en CSV: " + e.getMessage());
        }
    }
}
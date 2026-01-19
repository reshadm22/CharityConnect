/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author cobs
 */
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SearchPanel extends JPanel {

    private final List<Charity> allCharities;
    private final List<Charity> filtered = new ArrayList<>();

    private final JTextField txtQuery = new JTextField();
    private final JComboBox<String> cmbSearchBy = new JComboBox<>(new String[]{"City", "Charity Name"});
    private final JComboBox<String> cmbCategory = new JComboBox<>(new String[]{
        "All", "Hunger/Poverty", "Education", "Animals", "Housing", "Health", "Social Services"
    });

    private final JLabel lblStatus = new JLabel("Ready.");
    private final JButton btnClear = new JButton("Clear");

    private final CharityTableModel tableModel = new CharityTableModel();
    private final JTable tblResults = new JTable(tableModel);
    private final TableRowSorter<CharityTableModel> sorter = new TableRowSorter<>(tableModel);

    private final Timer debounceTimer;

    public SearchPanel(List<Charity> charities) {
        this.allCharities = charities;

        setLayout(new BorderLayout(10, 10));

        // --- Top controls ---
        JPanel top = new JPanel(new GridBagLayout());
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(4, 4, 4, 4);
        gc.fill = GridBagConstraints.HORIZONTAL;

        gc.gridx = 0;
        gc.gridy = 0;
        gc.weightx = 0;
        top.add(new JLabel("Search:"), gc);

        gc.gridx = 1;
        gc.gridy = 0;
        gc.weightx = 1.0;
        txtQuery.setToolTipText("Type to search...");
        top.add(txtQuery, gc);

        gc.gridx = 2;
        gc.gridy = 0;
        gc.weightx = 0;
        top.add(btnClear, gc);

        gc.gridx = 0;
        gc.gridy = 1;
        gc.weightx = 0;
        top.add(new JLabel("Search By:"), gc);

        gc.gridx = 1;
        gc.gridy = 1;
        gc.weightx = 1.0;
        top.add(cmbSearchBy, gc);

        gc.gridx = 0;
        gc.gridy = 2;
        gc.weightx = 0;
        top.add(new JLabel("Category:"), gc);

        gc.gridx = 1;
        gc.gridy = 2;
        gc.weightx = 1.0;
        top.add(cmbCategory, gc);

        add(top, BorderLayout.NORTH);

        // --- Table ---
        tblResults.setRowSorter(sorter);
        tblResults.setFillsViewportHeight(true);
        tblResults.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        add(new JScrollPane(tblResults), BorderLayout.CENTER);

        // --- Status line ---
        JPanel bottom = new JPanel(new BorderLayout());
        bottom.add(lblStatus, BorderLayout.WEST);
        add(bottom, BorderLayout.SOUTH);

        // --- Debounced live search ---
        debounceTimer = new Timer(250, e -> runSearch());
        debounceTimer.setRepeats(false);

        txtQuery.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) {
                scheduleSearch();
            }

            public void removeUpdate(DocumentEvent e) {
                scheduleSearch();
            }

            public void changedUpdate(DocumentEvent e) {
                scheduleSearch();
            }
        });

        cmbSearchBy.addActionListener(e -> runSearch());
        cmbCategory.addActionListener(e -> runSearch());

        btnClear.addActionListener(e -> {
            txtQuery.setText("");
            cmbSearchBy.setSelectedIndex(0);
            cmbCategory.setSelectedIndex(0);
            runSearch();
            txtQuery.requestFocusInWindow();
        });

        // Double-click row to see details
        tblResults.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && tblResults.getSelectedRow() != -1) {
                    Charity c = tableModel.getAtViewRow(tblResults.getSelectedRow(), tblResults);
                    showDetailsDialog(c);
                }
            }
        });

        // Initial load: show all (or show none—your choice)
        runSearch();
    }

    private void scheduleSearch() {
        debounceTimer.restart();
    }

    private void runSearch() {
        String query = normalize(txtQuery.getText());
        String searchBy = (String) cmbSearchBy.getSelectedItem();
        String category = (String) cmbCategory.getSelectedItem();

        filtered.clear();

        for (Charity c : allCharities) {
            if (!matchesCategory(c, category)) {
                continue;
            }
            if (!matchesQuery(c, searchBy, query)) {
                continue;
            }
            filtered.add(c);
        }

        tableModel.setRows(filtered);
        updateStatus(filtered.size());
    }

    private boolean matchesCategory(Charity c, String category) {
        if (category == null || category.equals("All")) {
            return true;
        }
        return normalize(c.getCategory()).equals(normalize(category));
    }

    private boolean matchesQuery(Charity c, String searchBy, String query) {
        if (query.isEmpty()) {
            return true;
        }

        if ("City".equals(searchBy)) {
            // upgraded from equalsIgnoreCase to partial match:
            return normalize(c.getCity()).contains(query);
        } else {
            return normalize(c.getName()).contains(query);
        }
    }

    private void updateStatus(int count) {
        if (count == 0) {
            lblStatus.setText("No charities found.");
        } else if (count == 1) {
            lblStatus.setText("1 charity found.");
        } else {
            lblStatus.setText(count + " charities found.");
        }
    }

    private void showDetailsDialog(Charity c) {
        JTextArea area = new JTextArea();
        area.setEditable(false);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);

        area.setText(
                c.getName() + "\n\n"
                + "Category: " + c.getCategory() + "\n"
                + "City: " + c.getCity() + "\n\n"
                + c.getDescription()
        );

        JScrollPane scroll = new JScrollPane(area);
        scroll.setPreferredSize(new Dimension(420, 260));

        JOptionPane.showMessageDialog(
                this,
                scroll,
                "Charity Details",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    private static String normalize(String s) {
        if (s == null) {
            return "";
        }
        return s.trim().toLowerCase(Locale.ROOT);
    }

    // ---------------- Table Model ----------------
    private static class CharityTableModel extends AbstractTableModel {

        private final String[] cols = {"Name", "City", "Category"};
        private List<Charity> rows = new ArrayList<>();

        public void setRows(List<Charity> newRows) {
            rows = new ArrayList<>(newRows);
            fireTableDataChanged();
        }

        @Override
        public int getRowCount() {
            return rows.size();
        }

        @Override
        public int getColumnCount() {
            return cols.length;
        }

        @Override
        public String getColumnName(int col) {
            return cols[col];
        }

        @Override
        public Class<?> getColumnClass(int columnIndex) {
            return String.class;
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            Charity c = rows.get(rowIndex);
            return switch (columnIndex) {
                case 0 ->
                    c.getName();
                case 1 ->
                    c.getCity();
                case 2 ->
                    c.getCategory();
                default ->
                    "";
            };
        }

        public Charity getAtViewRow(int viewRow, JTable table) {
            int modelRow = table.convertRowIndexToModel(viewRow);
            return rows.get(modelRow);
        }
    }
}

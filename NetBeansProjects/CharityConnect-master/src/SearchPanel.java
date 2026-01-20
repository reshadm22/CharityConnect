import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class SearchPanel extends JPanel {

    private final List<Charity> allCharities;
    private final List<Charity> filtered = new ArrayList<>();
    private final Set<String> favorites = new HashSet<>();

    private final JTextField txtQuery = new JTextField();
    private final JComboBox<String> cmbSearchBy = new JComboBox<>(new String[]{"City", "Charity Name"});
    private final JComboBox<String> cmbCategory = new JComboBox<>(new String[]{
            "All", "Hunger/Poverty", "Education", "Animals", "Housing", "Health", "Social Services"
    });
    private final JCheckBox chkStarredOnly = new JCheckBox("Show only starred");

    private final JLabel lblStatus = new JLabel("Ready.");
    private final JButton btnClear = new JButton("Clear");

    private final CharityTableModel tableModel = new CharityTableModel();
    private final JTable tblResults = new JTable(tableModel);
    private final TableRowSorter<CharityTableModel> sorter = new TableRowSorter<>(tableModel);

    private final Timer debounceTimer;

    public SearchPanel(List<Charity> charities) {
        this.allCharities = charities;

        setLayout(new BorderLayout(10, 10));
        setPreferredSize(new Dimension(450, 400));

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

        gc.gridx = 1;
        gc.gridy = 3;
        gc.weightx = 1.0;
        top.add(chkStarredOnly, gc);

        add(top, BorderLayout.NORTH);

        tblResults.setRowSorter(sorter);
        tblResults.setFillsViewportHeight(true);
        tblResults.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        TableCellRenderer highlight = new HighlightSubstringRenderer(() -> txtQuery.getText());
        tblResults.setDefaultRenderer(String.class, highlight);

        tblResults.getColumnModel().getColumn(0).setCellRenderer(new StarRenderer());
        tblResults.getColumnModel().getColumn(0).setMaxWidth(45);
        tblResults.getColumnModel().getColumn(0).setMinWidth(45);
        tblResults.getColumnModel().getColumn(0).setPreferredWidth(45);

        add(new JScrollPane(tblResults), BorderLayout.CENTER);

        JPanel bottom = new JPanel(new BorderLayout());
        bottom.add(lblStatus, BorderLayout.WEST);
        add(bottom, BorderLayout.SOUTH);

        debounceTimer = new Timer(250, e -> runSearch());
        debounceTimer.setRepeats(false);

        txtQuery.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { scheduleSearch(); }
            public void removeUpdate(DocumentEvent e) { scheduleSearch(); }
            public void changedUpdate(DocumentEvent e) { scheduleSearch(); }
        });

        cmbSearchBy.addActionListener(e -> runSearch());
        cmbCategory.addActionListener(e -> runSearch());
        chkStarredOnly.addActionListener(e -> runSearch());

        btnClear.addActionListener(e -> {
            txtQuery.setText("");
            cmbSearchBy.setSelectedIndex(0);
            cmbCategory.setSelectedIndex(0);
            chkStarredOnly.setSelected(false);
            runSearch();
            txtQuery.requestFocusInWindow();
        });

        tblResults.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int viewRow = tblResults.rowAtPoint(e.getPoint());
                int viewCol = tblResults.columnAtPoint(e.getPoint());
                if (viewRow < 0 || viewCol < 0) return;

                if (viewCol == 0 && e.getClickCount() == 1) {
                    Charity c = tableModel.getAtViewRow(viewRow, tblResults);
                    toggleFavorite(c);
                    if (chkStarredOnly.isSelected()) runSearch();
                    else {
                        int modelRow = tblResults.convertRowIndexToModel(viewRow);
                        tableModel.fireTableRowsUpdated(modelRow, modelRow);
                        updateStatus(filtered.size());
                    }
                    return;
                }

                if (e.getClickCount() == 2 && viewCol != 0) {
                    Charity c = tableModel.getAtViewRow(viewRow, tblResults);
                    showDetailsDialog(c);
                }
            }
        });

        runSearch();
    }

    private static class HighlightSubstringRenderer extends javax.swing.table.DefaultTableCellRenderer {
        private final java.util.function.Supplier<String> querySupplier;

        HighlightSubstringRenderer(java.util.function.Supplier<String> querySupplier) {
            this.querySupplier = querySupplier;
        }

        @Override
        public Component getTableCellRendererComponent(
                JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {

            JLabel lbl = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            String text = value == null ? "" : value.toString();
            String queryRaw = querySupplier.get();
            String query = queryRaw == null ? "" : queryRaw.trim();

            if (query.isEmpty()) {
                lbl.setText(escapeHtml(text));
                return lbl;
            }

            String lowerText = text.toLowerCase(Locale.ROOT);
            String lowerQuery = query.toLowerCase(Locale.ROOT);

            int idx = lowerText.indexOf(lowerQuery);

            if (idx < 0 || isSelected) {
                lbl.setText(escapeHtml(text));
                return lbl;
            }

            String before = text.substring(0, idx);
            String match = text.substring(idx, idx + query.length());
            String after = text.substring(idx + query.length());

            String html =
                    "<html>"
                            + escapeHtml(before)
                            + "<span style='background-color: #fff3a0; font-weight: 700;'>"
                            + escapeHtml(match)
                            + "</span>"
                            + escapeHtml(after)
                            + "</html>";

            lbl.setText(html);
            return lbl;
        }

        private static String escapeHtml(String s) {
            if (s == null) return "";
            return s.replace("&", "&amp;")
                    .replace("<", "&lt;")
                    .replace(">", "&gt;")
                    .replace("\"", "&quot;");
        }
    }

    private static class StarRenderer extends JLabel implements TableCellRenderer {
        StarRenderer() {
            setHorizontalAlignment(SwingConstants.CENTER);
            setOpaque(true);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                      boolean isSelected, boolean hasFocus,
                                                      int row, int column) {
            setText(value == null ? "☆" : value.toString());
            setFont(getFont().deriveFont(Font.BOLD, 18f));

            if (isSelected) {
                setBackground(table.getSelectionBackground());
                setForeground(table.getSelectionForeground());
            } else {
                setBackground(table.getBackground());
                setForeground(table.getForeground());
            }
            return this;
        }
    }

    private void scheduleSearch() {
        debounceTimer.restart();
    }

    private void runSearch() {
        String query = normalize(txtQuery.getText());
        String searchBy = (String) cmbSearchBy.getSelectedItem();
        String category = (String) cmbCategory.getSelectedItem();
        boolean starredOnly = chkStarredOnly.isSelected();

        filtered.clear();

        for (Charity c : allCharities) {
            if (starredOnly && !isFavorite(c)) continue;
            if (!matchesCategory(c, category)) continue;
            if (!matchesQuery(c, searchBy, query)) continue;
            filtered.add(c);
        }

        tableModel.setRows(filtered);
        updateStatus(filtered.size());
    }

    private boolean matchesCategory(Charity c, String category) {
        if (category == null || category.equals("All")) return true;
        return normalize(c.getCategory()).equals(normalize(category));
    }

    private boolean matchesQuery(Charity c, String searchBy, String query) {
        if (query.isEmpty()) return true;

        if ("City".equals(searchBy)) {
            return normalize(c.getCity()).contains(query);
        } else {
            return normalize(c.getName()).contains(query);
        }
    }

    private void toggleFavorite(Charity c) {
        String key = favoriteKey(c);
        if (favorites.contains(key)) favorites.remove(key);
        else favorites.add(key);
    }

    private boolean isFavorite(Charity c) {
        return favorites.contains(favoriteKey(c));
    }

    private String favoriteKey(Charity c) {
        return normalize(c.getName()) + "|" + normalize(c.getCity());
    }

    private void updateStatus(int count) {
        String queryRaw = txtQuery.getText() == null ? "" : txtQuery.getText().trim();
        String query = normalize(queryRaw);
        String searchBy = (String) cmbSearchBy.getSelectedItem();
        String category = (String) cmbCategory.getSelectedItem();
        boolean starredOnly = chkStarredOnly.isSelected();

        int favCount = favorites.size();

        if (count == 0) {
            if (starredOnly && favCount == 0) {
                lblStatus.setText("No starred charities yet. Click ☆ to build your favorites.");
                return;
            }
            if (starredOnly) {
                if (!query.isEmpty()) {
                    lblStatus.setText("No starred matches for \"" + queryRaw + "\". Try broadening your search.");
                } else if (category != null && !"All".equals(category)) {
                    lblStatus.setText("No starred charities in " + category + ". Try selecting All categories.");
                } else {
                    lblStatus.setText("No starred charities match your filters.");
                }
                return;
            }
            if (!query.isEmpty()) {
                lblStatus.setText("No matches for \"" + queryRaw + "\" (" + searchBy + "). Try a broader term.");
                return;
            }
            if (category != null && !"All".equals(category)) {
                lblStatus.setText("No charities found in " + category + ". Try selecting All categories.");
                return;
            }
            lblStatus.setText("No charities found.");
            return;
        }

        String base = (count == 1) ? "1 charity found." : (count + " charities found.");
        String fav = (favCount == 1) ? "  |  1 starred" : ("  |  " + favCount + " starred");
        if (starredOnly) base = (count == 1) ? "1 starred charity shown." : (count + " starred charities shown.");
        lblStatus.setText(base + fav);
    }

    private void showDetailsDialog(Charity c) {
        JTextArea area = new JTextArea();
        area.setEditable(false);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);

        String star = isFavorite(c) ? "★ " : "☆ ";

        area.setText(
                star + c.getName() + "\n\n"
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
        if (s == null) return "";
        return s.trim().toLowerCase(Locale.ROOT);
    }

    private class CharityTableModel extends AbstractTableModel {

        private final String[] cols = {"★", "Name", "City", "Category"};
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
                case 0 -> isFavorite(c) ? "★" : "☆";
                case 1 -> c.getName();
                case 2 -> c.getCity();
                case 3 -> c.getCategory();
                default -> "";
            };
        }

        public Charity getAtViewRow(int viewRow, JTable table) {
            int modelRow = table.convertRowIndexToModel(viewRow);
            return rows.get(modelRow);
        }
    }
}

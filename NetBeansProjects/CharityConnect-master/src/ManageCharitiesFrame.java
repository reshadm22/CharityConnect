import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ManageCharitiesFrame extends JFrame {

    private final UserManager userManager;
    private final ArrayList<Charity> charityList;
    private final String dbPath;

    private final CharityAdminTableModel tableModel = new CharityAdminTableModel();
    private final JTable tbl = new JTable(tableModel);

    private final JButton btnEdit = new JButton("Edit");
    private final JButton btnDelete = new JButton("Delete");
    private final JButton btnClose = new JButton("Close");

    public ManageCharitiesFrame(UserManager userManager, ArrayList<Charity> charityList, String dbPath) {
        this.userManager = userManager;
        this.charityList = charityList;
        this.dbPath = dbPath;

        // Hard gate: admin only
        if (userManager == null || !userManager.isAdminLoggedIn()) {
            JOptionPane.showMessageDialog(null, "Admin access required.");
            dispose();
            return;
        }

        setTitle("Admin - Manage Charities");
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        tbl.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tbl.getSelectionModel().addListSelectionListener(e -> updateButtons());

        add(new JScrollPane(tbl), BorderLayout.CENTER);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 8));
        actions.add(btnEdit);
        actions.add(btnDelete);
        actions.add(btnClose);
        add(actions, BorderLayout.SOUTH);

        btnEdit.addActionListener(e -> onEdit());
        btnDelete.addActionListener(e -> onDelete());
        btnClose.addActionListener(e -> dispose());

        refresh();
        setSize(800, 450);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void updateButtons() {
        boolean hasSelection = tbl.getSelectedRow() != -1;
        btnEdit.setEnabled(hasSelection);
        btnDelete.setEnabled(hasSelection);
    }

    private void refresh() {
        tableModel.setRows(charityList);
        updateButtons();
    }

    private void onDelete() {
        int row = tbl.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a charity first.");
            return;
        }

        Charity c = tableModel.getAtViewRow(row, tbl);

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Delete:\n" + c.getName() + " (" + c.getCity() + ")?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm != JOptionPane.YES_OPTION) return;

        charityList.remove(c);
        CharityFileHandler.saveCharities(dbPath, charityList);
        refresh();

        JOptionPane.showMessageDialog(this, "Charity deleted.");
    }

    private void onEdit() {
        int row = tbl.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a charity first.");
            return;
        }

        Charity c = tableModel.getAtViewRow(row, tbl);

        JTextField txtName = new JTextField(c.getName());
        JTextField txtCity = new JTextField(c.getCity());

        JComboBox<String> cmbCategory = new JComboBox<>(new String[]{
                "Hunger/Poverty", "Education", "Animals", "Housing", "Health", "Social Services"
        });
        cmbCategory.setSelectedItem(c.getCategory());

        JTextArea txtDesc = new JTextArea(c.getDescription(), 6, 30);
        txtDesc.setLineWrap(true);
        txtDesc.setWrapStyleWord(true);

        JPanel p = new JPanel(new GridBagLayout());
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(4, 4, 4, 4);
        gc.fill = GridBagConstraints.HORIZONTAL;

        gc.gridx = 0; gc.gridy = 0; p.add(new JLabel("Name:"), gc);
        gc.gridx = 1; gc.gridy = 0; gc.weightx = 1.0; p.add(txtName, gc);

        gc.gridx = 0; gc.gridy = 1; gc.weightx = 0; p.add(new JLabel("City:"), gc);
        gc.gridx = 1; gc.gridy = 1; gc.weightx = 1.0; p.add(txtCity, gc);

        gc.gridx = 0; gc.gridy = 2; gc.weightx = 0; p.add(new JLabel("Category:"), gc);
        gc.gridx = 1; gc.gridy = 2; gc.weightx = 1.0; p.add(cmbCategory, gc);

        gc.gridx = 0; gc.gridy = 3; gc.weightx = 0; gc.anchor = GridBagConstraints.NORTHWEST;
        p.add(new JLabel("Description:"), gc);

        gc.gridx = 1; gc.gridy = 3; gc.weightx = 1.0;
        gc.fill = GridBagConstraints.BOTH;
        p.add(new JScrollPane(txtDesc), gc);

        int res = JOptionPane.showConfirmDialog(this, p, "Edit Charity", JOptionPane.OK_CANCEL_OPTION);
        if (res != JOptionPane.OK_OPTION) return;

        String newName = txtName.getText().trim();
        String newCity = txtCity.getText().trim();
        String newCategory = (String) cmbCategory.getSelectedItem();
        String newDesc = txtDesc.getText().trim();

        if (newName.isEmpty() || newCity.isEmpty() || newCategory == null || newDesc.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields are required.");
            return;
        }

        int idx = charityList.indexOf(c);
        charityList.set(idx, new Charity(newName, newCategory, newCity, newDesc));
        CharityFileHandler.saveCharities(dbPath, charityList);

        refresh();
        JOptionPane.showMessageDialog(this, "Charity updated.");
    }

    private static class CharityAdminTableModel extends AbstractTableModel {
        private final String[] cols = {"Name", "City", "Category"};
        private List<Charity> rows = new ArrayList<>();

        public void setRows(List<Charity> newRows) {
            rows = new ArrayList<>(newRows);
            fireTableDataChanged();
        }

        @Override public int getRowCount() { return rows.size(); }
        @Override public int getColumnCount() { return cols.length; }
        @Override public String getColumnName(int col) { return cols[col]; }
        @Override public Class<?> getColumnClass(int columnIndex) { return String.class; }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            Charity c = rows.get(rowIndex);
            return switch (columnIndex) {
                case 0 -> c.getName();
                case 1 -> c.getCity();
                case 2 -> c.getCategory();
                default -> "";
            };
        }

        public Charity getAtViewRow(int viewRow, JTable table) {
            int modelRow = table.convertRowIndexToModel(viewRow);
            return rows.get(modelRow);
        }
    }
}

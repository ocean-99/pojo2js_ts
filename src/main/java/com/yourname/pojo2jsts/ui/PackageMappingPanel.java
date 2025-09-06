package com.yourname.pojo2jsts.ui;

import com.intellij.ui.AnActionButton;
import com.intellij.ui.AnActionButtonRunnable;
import com.intellij.ui.ToolbarDecorator;
import com.intellij.ui.components.JBCheckBox;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBTextField;
import com.intellij.ui.table.JBTable;
import com.yourname.pojo2jsts.config.PackageMapping;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Panel for managing package mappings configuration
 */
public class PackageMappingPanel extends JPanel {
    
    private JBTable table;
    private PackageMappingTableModel tableModel;
    
    public PackageMappingPanel() {
        initComponents();
        layoutComponents();
    }
    
    private void initComponents() {
        tableModel = new PackageMappingTableModel();
        table = new JBTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        // Set column widths
        table.getColumnModel().getColumn(0).setPreferredWidth(200); // Pattern
        table.getColumnModel().getColumn(1).setPreferredWidth(100); // JSON Type
        table.getColumnModel().getColumn(2).setPreferredWidth(100); // TS Type
        table.getColumnModel().getColumn(3).setPreferredWidth(80);  // Recursive
        table.getColumnModel().getColumn(4).setPreferredWidth(60);  // Enabled
    }
    
    private void layoutComponents() {
        setLayout(new BorderLayout());
        
        // Title and description
        JPanel headerPanel = new JPanel(new BorderLayout());
        JBLabel titleLabel = new JBLabel("Package Mappings");
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD, titleLabel.getFont().getSize() + 2));
        headerPanel.add(titleLabel, BorderLayout.NORTH);
        
        JTextArea description = new JTextArea(3, 0);
        description.setOpaque(false);
        description.setEditable(false);
        description.setWrapStyleWord(true);
        description.setLineWrap(true);
        description.setFont(description.getFont().deriveFont(Font.ITALIC));
        description.setText(
            "Package mappings allow you to override the default type generation for classes in specific packages. " +
            "Use wildcards (*) to match multiple packages. For example, 'com.example.*' matches all classes " +
            "in com.example and its subpackages when 'Recursive' is enabled."
        );
        description.setBorder(BorderFactory.createEmptyBorder(5, 0, 10, 0));
        headerPanel.add(description, BorderLayout.CENTER);
        
        add(headerPanel, BorderLayout.NORTH);
        
        // Table with toolbar
        JPanel tablePanel = ToolbarDecorator.createDecorator(table)
            .setAddAction(new AnActionButtonRunnable() {
                @Override
                public void run(AnActionButton button) {
                    addNewMapping();
                }
            })
            .setRemoveAction(new AnActionButtonRunnable() {
                @Override
                public void run(AnActionButton button) {
                    removeSelectedMapping();
                }
            })
            .setEditAction(new AnActionButtonRunnable() {
                @Override
                public void run(AnActionButton button) {
                    editSelectedMapping();
                }
            })
            .createPanel();
        
        add(tablePanel, BorderLayout.CENTER);
        
        // Examples panel
        JPanel examplePanel = new JPanel(new BorderLayout());
        JBLabel exampleTitle = new JBLabel("Examples:");
        exampleTitle.setFont(exampleTitle.getFont().deriveFont(Font.BOLD));
        exampleTitle.setBorder(BorderFactory.createEmptyBorder(10, 0, 5, 0));
        examplePanel.add(exampleTitle, BorderLayout.NORTH);
        
        JTextArea examples = new JTextArea(4, 0);
        examples.setOpaque(false);
        examples.setEditable(false);
        examples.setFont(examples.getFont().deriveFont(Font.PLAIN, examples.getFont().getSize() - 1));
        examples.setText(
            "com.example.dto.*         → Convert all DTOs to string\\n" +
            "org.springframework.*     → Convert Spring classes to any\\n" +
            "com.company.model.User    → Convert specific class\\n" +
            "*.legacy.*                → Convert legacy packages"
        );
        examples.setBorder(BorderFactory.createEtchedBorder());
        examplePanel.add(examples, BorderLayout.CENTER);
        
        add(examplePanel, BorderLayout.SOUTH);
    }
    
    private void addNewMapping() {
        PackageMappingDialog dialog = new PackageMappingDialog(this, "Add Package Mapping", null);
        if (dialog.showAndGet()) {
            PackageMapping mapping = dialog.getPackageMapping();
            tableModel.addMapping(mapping);
        }
    }
    
    private void removeSelectedMapping() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow >= 0) {
            tableModel.removeMapping(selectedRow);
        }
    }
    
    private void editSelectedMapping() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow >= 0) {
            PackageMapping mapping = tableModel.getMapping(selectedRow);
            PackageMappingDialog dialog = new PackageMappingDialog(this, "Edit Package Mapping", mapping);
            if (dialog.showAndGet()) {
                PackageMapping updatedMapping = dialog.getPackageMapping();
                tableModel.updateMapping(selectedRow, updatedMapping);
            }
        }
    }
    
    public boolean isModified(List<PackageMapping> originalMappings) {
        return !tableModel.getMappings().equals(originalMappings);
    }
    
    public List<PackageMapping> getPackageMappings() {
        return new ArrayList<>(tableModel.getMappings());
    }
    
    public void reset(List<PackageMapping> mappings) {
        tableModel.setMappings(mappings);
    }
    
    private static class PackageMappingTableModel extends AbstractTableModel {
        private static final String[] COLUMN_NAMES = {
            "Package Pattern", "JSON Type", "TypeScript Type", "Recursive", "Enabled"
        };
        
        private final List<PackageMapping> mappings = new ArrayList<>();
        
        @Override
        public int getRowCount() {
            return mappings.size();
        }
        
        @Override
        public int getColumnCount() {
            return COLUMN_NAMES.length;
        }
        
        @Override
        public String getColumnName(int column) {
            return COLUMN_NAMES[column];
        }
        
        @Override
        public Class<?> getColumnClass(int columnIndex) {
            switch (columnIndex) {
                case 3: // Recursive
                case 4: // Enabled
                    return Boolean.class;
                default:
                    return String.class;
            }
        }
        
        @Override
        public boolean isCellEditable(int row, int column) {
            return column == 3 || column == 4; // Only recursive and enabled columns are editable inline
        }
        
        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            PackageMapping mapping = mappings.get(rowIndex);
            switch (columnIndex) {
                case 0: return mapping.getPackagePattern();
                case 1: return mapping.getJsonTargetType();
                case 2: return mapping.getTsTargetType();
                case 3: return mapping.isRecursive();
                case 4: return mapping.isEnabled();
                default: return null;
            }
        }
        
        @Override
        public void setValueAt(Object value, int rowIndex, int columnIndex) {
            PackageMapping mapping = mappings.get(rowIndex);
            switch (columnIndex) {
                case 3:
                    mapping.setRecursive((Boolean) value);
                    break;
                case 4:
                    mapping.setEnabled((Boolean) value);
                    break;
            }
            fireTableCellUpdated(rowIndex, columnIndex);
        }
        
        public void addMapping(PackageMapping mapping) {
            mappings.add(mapping);
            fireTableRowsInserted(mappings.size() - 1, mappings.size() - 1);
        }
        
        public void removeMapping(int index) {
            mappings.remove(index);
            fireTableRowsDeleted(index, index);
        }
        
        public void updateMapping(int index, PackageMapping mapping) {
            mappings.set(index, mapping);
            fireTableRowsUpdated(index, index);
        }
        
        public PackageMapping getMapping(int index) {
            return mappings.get(index);
        }
        
        public List<PackageMapping> getMappings() {
            return new ArrayList<>(mappings);
        }
        
        public void setMappings(List<PackageMapping> newMappings) {
            mappings.clear();
            if (newMappings != null) {
                mappings.addAll(newMappings);
            }
            fireTableDataChanged();
        }
    }
}
package com.yourname.pojo2jsts.ui;

import com.intellij.ui.AnActionButton;
import com.intellij.ui.AnActionButtonRunnable;
import com.intellij.ui.ToolbarDecorator;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.table.JBTable;
import com.yourname.pojo2jsts.config.TypeMapping;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Panel for managing type mappings configuration
 */
public class TypeMappingPanel extends JPanel {
    
    private JBTable table;
    private TypeMappingTableModel tableModel;
    
    public TypeMappingPanel() {
        initComponents();
        layoutComponents();
    }
    
    private void initComponents() {
        tableModel = new TypeMappingTableModel();
        table = new JBTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        // Set column widths
        table.getColumnModel().getColumn(0).setPreferredWidth(200); // Source Type
        table.getColumnModel().getColumn(1).setPreferredWidth(150); // JSON Pattern
        table.getColumnModel().getColumn(2).setPreferredWidth(100); // TS Type
        table.getColumnModel().getColumn(3).setPreferredWidth(200); // Description
        table.getColumnModel().getColumn(4).setPreferredWidth(60);  // Enabled
    }
    
    private void layoutComponents() {
        setLayout(new BorderLayout());
        
        // Title and description
        JPanel headerPanel = new JPanel(new BorderLayout());
        JBLabel titleLabel = new JBLabel("Type Mappings");
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD, titleLabel.getFont().getSize() + 2));
        headerPanel.add(titleLabel, BorderLayout.NORTH);
        
        JTextArea description = new JTextArea(4, 0);
        description.setOpaque(false);
        description.setEditable(false);
        description.setWrapStyleWord(true);
        description.setLineWrap(true);
        description.setFont(description.getFont().deriveFont(Font.ITALIC));
        description.setText(
            "Type mappings allow you to customize how specific Java types are converted to JSON values and TypeScript types. " +
            "Use template patterns like {{random_date}}, {{random_number}} for dynamic JSON values. " +
            "You can also specify custom generator classes for complex scenarios."
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
        
        // Template patterns help
        JPanel helpPanel = new JPanel(new BorderLayout());
        JBLabel helpTitle = new JBLabel("Available Template Patterns:");
        helpTitle.setFont(helpTitle.getFont().deriveFont(Font.BOLD));
        helpTitle.setBorder(BorderFactory.createEmptyBorder(10, 0, 5, 0));
        helpPanel.add(helpTitle, BorderLayout.NORTH);
        
        JTextArea patterns = new JTextArea(6, 0);
        patterns.setOpaque(false);
        patterns.setEditable(false);
        patterns.setFont(patterns.getFont().deriveFont(Font.PLAIN, patterns.getFont().getSize() - 1));
        patterns.setText(
            "{{random_date}}        → Random date in configured format\\n" +
            "{{random_datetime}}    → Random date and time\\n" +
            "{{random_date_only}}   → Random date without time\\n" +
            "{{random_time_only}}   → Random time without date\\n" +
            "{{random_number}}      → Random decimal number\\n" +
            "{{random_integer}}     → Random integer\\n" +
            "{{random_uuid}}        → Random UUID string\\n" +
            "{{current_timestamp}}  → Current timestamp\\n" +
            "\"custom_value\"        → Fixed string value"
        );
        patterns.setBorder(BorderFactory.createEtchedBorder());
        helpPanel.add(patterns, BorderLayout.CENTER);
        
        add(helpPanel, BorderLayout.SOUTH);
    }
    
    private void addNewMapping() {
        TypeMappingDialog dialog = new TypeMappingDialog(this, "Add Type Mapping", null);
        if (dialog.showAndGet()) {
            TypeMapping mapping = dialog.getTypeMapping();
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
            TypeMapping mapping = tableModel.getMapping(selectedRow);
            TypeMappingDialog dialog = new TypeMappingDialog(this, "Edit Type Mapping", mapping);
            if (dialog.showAndGet()) {
                TypeMapping updatedMapping = dialog.getTypeMapping();
                tableModel.updateMapping(selectedRow, updatedMapping);
            }
        }
    }
    
    public boolean isModified(List<TypeMapping> originalMappings) {
        return !tableModel.getMappings().equals(originalMappings);
    }
    
    public List<TypeMapping> getTypeMappings() {
        return new ArrayList<>(tableModel.getMappings());
    }
    
    public void reset(List<TypeMapping> mappings) {
        tableModel.setMappings(mappings);
    }
    
    private static class TypeMappingTableModel extends AbstractTableModel {
        private static final String[] COLUMN_NAMES = {
            "Source Type", "JSON Pattern", "TypeScript Type", "Description", "Enabled"
        };
        
        private final List<TypeMapping> mappings = new ArrayList<>();
        
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
            if (columnIndex == 4) { // Enabled
                return Boolean.class;
            }
            return String.class;
        }
        
        @Override
        public boolean isCellEditable(int row, int column) {
            return column == 4; // Only enabled column is editable inline
        }
        
        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            TypeMapping mapping = mappings.get(rowIndex);
            switch (columnIndex) {
                case 0: return mapping.getSourceType();
                case 1: return mapping.getJsonValuePattern();
                case 2: return mapping.getTsType();
                case 3: return mapping.getDescription();
                case 4: return mapping.isEnabled();
                default: return null;
            }
        }
        
        @Override
        public void setValueAt(Object value, int rowIndex, int columnIndex) {
            if (columnIndex == 4) {
                TypeMapping mapping = mappings.get(rowIndex);
                mapping.setEnabled((Boolean) value);
                fireTableCellUpdated(rowIndex, columnIndex);
            }
        }
        
        public void addMapping(TypeMapping mapping) {
            mappings.add(mapping);
            fireTableRowsInserted(mappings.size() - 1, mappings.size() - 1);
        }
        
        public void removeMapping(int index) {
            mappings.remove(index);
            fireTableRowsDeleted(index, index);
        }
        
        public void updateMapping(int index, TypeMapping mapping) {
            mappings.set(index, mapping);
            fireTableRowsUpdated(index, index);
        }
        
        public TypeMapping getMapping(int index) {
            return mappings.get(index);
        }
        
        public List<TypeMapping> getMappings() {
            return new ArrayList<>(mappings);
        }
        
        public void setMappings(List<TypeMapping> newMappings) {
            mappings.clear();
            if (newMappings != null) {
                mappings.addAll(newMappings);
            }
            fireTableDataChanged();
        }
    }
}
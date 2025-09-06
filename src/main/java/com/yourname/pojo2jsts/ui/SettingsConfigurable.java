package com.yourname.pojo2jsts.ui;

import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.components.JBTabbedPane;
import com.yourname.pojo2jsts.config.DateConfig;
import com.yourname.pojo2jsts.config.PackageMapping;
import com.yourname.pojo2jsts.config.PluginSettings;
import com.yourname.pojo2jsts.config.TypeMapping;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * Complete settings configurable with tabbed interface
 */
public class SettingsConfigurable implements Configurable {

    private JPanel mainPanel;
    private GeneralSettingsPanel generalPanel;
    private PackageMappingPanel packageMappingPanel;
    private TypeMappingPanel typeMappingPanel;
    private DateConfigPanel dateConfigPanel;
    
    private PluginSettings settings;

    @Nls(capitalization = Nls.Capitalization.Title)
    @Override
    public String getDisplayName() {
        return "POJO to JSON/TS";
    }

    @Override
    public @Nullable JComponent createComponent() {
        if (mainPanel == null) {
            settings = PluginSettings.getInstance();
            createUIComponents();
        }
        return mainPanel;
    }

    private void createUIComponents() {
        mainPanel = new JPanel(new BorderLayout());
        
        // Create tabbed pane
        JBTabbedPane tabbedPane = new JBTabbedPane();
        
        // General settings tab
        generalPanel = new GeneralSettingsPanel();
        tabbedPane.addTab("General", generalPanel);
        
        // Date configuration tab
        dateConfigPanel = new DateConfigPanel();
        tabbedPane.addTab("Date Settings", dateConfigPanel);
        
        // Package mapping tab
        packageMappingPanel = new PackageMappingPanel();
        JBScrollPane packageScrollPane = new JBScrollPane(packageMappingPanel);
        packageScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        tabbedPane.addTab("Package Mappings", packageScrollPane);
        
        // Type mapping tab
        typeMappingPanel = new TypeMappingPanel();
        JBScrollPane typeScrollPane = new JBScrollPane(typeMappingPanel);
        typeScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        tabbedPane.addTab("Type Mappings", typeScrollPane);
        
        mainPanel.add(tabbedPane, BorderLayout.CENTER);
        
        // Add info panel at bottom
        JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel infoLabel = new JLabel("<html><i>Settings are automatically saved when you click Apply or OK</i></html>");
        infoLabel.setFont(infoLabel.getFont().deriveFont(Font.ITALIC, infoLabel.getFont().getSize() - 1));
        infoPanel.add(infoLabel);
        mainPanel.add(infoPanel, BorderLayout.SOUTH);
    }

    @Override
    public boolean isModified() {
        if (settings == null || generalPanel == null) return false;
        
        return generalPanel.isModified(settings) ||
               dateConfigPanel.isModified(settings.getDateConfig()) ||
               packageMappingPanel.isModified(settings.getPackageMappings()) ||
               typeMappingPanel.isModified(settings.getTypeMappings());
    }

    @Override
    public void apply() throws ConfigurationException {
        if (settings == null) return;
        
        // Validate all settings first
        List<String> errors = validateSettings();
        if (!errors.isEmpty()) {
            throw new ConfigurationException(String.join("\\n", errors));
        }
        
        // Apply general settings
        generalPanel.apply(settings);
        
        // Apply date config
        dateConfigPanel.apply(settings.getDateConfig());
        
        // Apply package mappings
        settings.setPackageMappings(packageMappingPanel.getPackageMappings());
        
        // Apply type mappings
        settings.setTypeMappings(typeMappingPanel.getTypeMappings());
    }

    @Override
    public void reset() {
        if (settings == null) return;
        
        generalPanel.reset(settings);
        dateConfigPanel.reset(settings.getDateConfig());
        packageMappingPanel.reset(settings.getPackageMappings());
        typeMappingPanel.reset(settings.getTypeMappings());
    }
    
    private List<String> validateSettings() {
        return settings.validateSettings();
    }

    @Override
    public void disposeUIResources() {
        mainPanel = null;
        generalPanel = null;
        dateConfigPanel = null;
        packageMappingPanel = null;
        typeMappingPanel = null;
        settings = null;
    }
}
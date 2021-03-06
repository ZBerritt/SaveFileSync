package com.github.nitrogen2oxygen.savefilesync.ui.dialog;

import com.github.nitrogen2oxygen.savefilesync.client.Settings;
import com.github.nitrogen2oxygen.savefilesync.client.theme.Theme;
import com.github.nitrogen2oxygen.savefilesync.util.Themes;
import com.github.nitrogen2oxygen.savefilesync.util.Constants;
import com.github.nitrogen2oxygen.savefilesync.util.FileLocations;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;

public class ChangeSettings extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JComboBox<Theme> themeSelector;
    private JLabel themeLabel;
    private JCheckBox makeBackupsCheckBox;
    private JCheckBox forceOverwriteCheckBox;
    private JButton openDataDirectoryButton;

    private final Settings settings;
    public Boolean saveChanges;

    public ChangeSettings(Settings settings) {
        this.settings = settings;
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);
        setLocationRelativeTo(null);
        setTitle(Constants.APP_NAME + " - Settings");
        pack();

        /* Set theme selector model */
        DefaultComboBoxModel<Theme> themeModel = new DefaultComboBoxModel<>();
        for (Theme theme : Theme.values()) {
            themeModel.addElement(theme);
        }
        themeModel.setSelectedItem(settings.getTheme());
        themeSelector.setModel(themeModel);
        themeSelector.setRenderer((list, value, index, isSelected, cellHasFocus) -> {
            JLabel label = new JLabel();
            label.setText(Themes.getName(value));
            label.setOpaque(true);
            if (isSelected) {
                label.setForeground(list.getSelectionForeground());
                label.setBackground(list.getSelectionBackground());
            } else {
                label.setForeground(list.getForeground());
                label.setBackground(list.getBackground());
            }
            return label;
        });

        /* Set current settings */
        makeBackupsCheckBox.setSelected(settings.shouldMakeBackups());
        forceOverwriteCheckBox.setSelected(settings.shouldForceOverwrite());

        buttonOK.addActionListener(e -> onOK());
        buttonCancel.addActionListener(e -> onCancel());
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });
        openDataDirectoryButton.addActionListener(e -> {
            Desktop desktop = Desktop.getDesktop();
            try {
                desktop.open(new File(FileLocations.getDataDirectory()));
            } catch (IOException ee) {
                ee.printStackTrace();
            }
        });
    }

    private void onOK() {
        // add your code here
        saveChanges = true;

        // Settings warnings
        if (!makeBackupsCheckBox.isSelected() && forceOverwriteCheckBox.isSelected()) {
            int confirm = JOptionPane.showConfirmDialog(this, "File import backups SHOULD be enabled when using force directory overwrites to prevent data loss." +
                    " Are you sure you want to continue with these settings?", "Waring!", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (confirm != 0) {
                return;
            }
        }

        // Set the theme
        settings.setTheme((Theme) themeSelector.getSelectedItem());
        settings.setMakeBackups(makeBackupsCheckBox.isSelected());
        settings.setForceOverwrites(forceOverwriteCheckBox.isSelected());
        dispose();
    }

    private void onCancel() {
        saveChanges = false;
        dispose();
    }

    public Settings getSettings() {
        return settings;
    }

    public static Settings main(Settings settings) {
        ChangeSettings dialog = new ChangeSettings(settings);
        dialog.pack();
        dialog.setVisible(true);
        if (!dialog.saveChanges) return null;
        return dialog.getSettings();
    }

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        contentPane = new JPanel();
        contentPane.setLayout(new GridLayoutManager(2, 1, new Insets(10, 10, 10, 10), -1, -1));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        contentPane.add(panel1, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, 1, null, null, null, 0, false));
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        panel1.add(panel2, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        buttonCancel = new JButton();
        buttonCancel.setText("Cancel");
        panel2.add(buttonCancel, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        openDataDirectoryButton = new JButton();
        openDataDirectoryButton.setText("Open Data Directory");
        panel2.add(openDataDirectoryButton, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        buttonOK = new JButton();
        buttonOK.setText("OK");
        panel1.add(buttonOK, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new GridLayoutManager(4, 2, new Insets(0, 0, 0, 0), -1, -1));
        contentPane.add(panel3, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        themeLabel = new JLabel();
        themeLabel.setText("Theme:");
        panel3.add(themeLabel, new GridConstraints(0, 0, 2, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        themeSelector = new JComboBox();
        panel3.add(themeSelector, new GridConstraints(0, 1, 2, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        makeBackupsCheckBox = new JCheckBox();
        makeBackupsCheckBox.setText("Make save data backups on import?");
        panel3.add(makeBackupsCheckBox, new GridConstraints(2, 0, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        forceOverwriteCheckBox = new JCheckBox();
        forceOverwriteCheckBox.setText("Delete old save directories to forcibly overwrite the save data?");
        panel3.add(forceOverwriteCheckBox, new GridConstraints(3, 0, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return contentPane;
    }
}

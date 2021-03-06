package com.github.nitrogen2oxygen.savefilesync.ui.dialog;

import com.github.nitrogen2oxygen.savefilesync.client.save.Save;
import com.github.nitrogen2oxygen.savefilesync.server.DataServer;
import com.github.nitrogen2oxygen.savefilesync.util.Constants;
import com.github.nitrogen2oxygen.savefilesync.util.Saves;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import org.apache.commons.io.FileUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class ServerImport extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JList<String> importList;
    private JButton browseButton;
    private JTextField saveLocationTextField;
    private JLabel descriptionTextField;

    private final DataServer dataServer;
    private Boolean cancelled;
    private Save save;

    public ServerImport(DataServer dataServer, List<String> names) {
        this.dataServer = dataServer;
        setContentPane(contentPane);
        setModal(true);
        setTitle(Constants.APP_NAME + " - Import Save");
        getRootPane().setDefaultButton(buttonOK);
        setLocationRelativeTo(null);
        buttonCancel.addActionListener(e -> onCancel());
        buttonOK.addActionListener(e -> onOK());

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        /* Do the fun stuff */
        DefaultListModel<String> model = new DefaultListModel<>();
        for (String name : names) {
            model.addElement(name);
        }
        importList.setModel(model);
        importList.addListSelectionListener(e -> {
            saveLocationTextField.setText("");
            descriptionTextField.setText("");
            browseButton.setEnabled(true);
            saveLocationTextField.setEnabled(true);
        });
        browseButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            int chooseFile = fileChooser.showOpenDialog(this);
            if (chooseFile == JFileChooser.APPROVE_OPTION) {
                saveLocationTextField.setText(fileChooser.getSelectedFile().toString());
                descriptionTextField.setText("The file/directory inside of the zip will be saved inside: " + fileChooser.getSelectedFile().toString());
                pack();
            }
        });
    }

    private void onCancel() {
        cancelled = true;
        dispose();
    }

    private void onOK() {
        cancelled = false;
        // Get the save file
        String name = importList.getSelectedValue();
        File tempSave;
        ZipFile zipFile;
        try {
            File savePath = new File(saveLocationTextField.getText());
            savePath.mkdirs();
            tempSave = Files.createTempFile("SaveFileSync", ".zip").toFile();
            tempSave.deleteOnExit();
            byte[] saveData = dataServer.getSaveData(name);
            FileUtils.writeByteArrayToFile(tempSave, saveData);
            zipFile = new ZipFile(tempSave);
            // Check if directory or file, then get the name
            Enumeration<? extends ZipEntry> entries = zipFile.entries();
            String firstEntry = entries.nextElement().getName();
            boolean hasMoreEntries = entries.hasMoreElements();
            if (hasMoreEntries) {
                save = Saves.build(true, name, savePath);
            } else {
                File saveFile = new File(savePath, firstEntry);
                saveFile.createNewFile();
                save = Saves.build(false, name, savePath);
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Cannot import save data! If this continues, please submit an issue on the GitHub!",
                    "Error!",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        /* Cleanup */
        try {
            zipFile.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            tempSave.delete();
            dispose();
        }
    }

    public Save getSave() {
        return save;
    }

    public static Save main(DataServer dataServer, List<String> names) {
        if (names.size() == 0) return null;
        ServerImport dialog = new ServerImport(dataServer, names);
        dialog.pack();
        dialog.setVisible(true);
        if (dialog.cancelled) return null;
        return dialog.getSave();
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
        final Spacer spacer1 = new Spacer();
        panel1.add(spacer1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1, true, false));
        panel1.add(panel2, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        buttonOK = new JButton();
        buttonOK.setText("OK");
        panel2.add(buttonOK, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        buttonCancel = new JButton();
        buttonCancel.setText("Cancel");
        panel2.add(buttonCancel, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1));
        contentPane.add(panel3, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JScrollPane scrollPane1 = new JScrollPane();
        panel3.add(scrollPane1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        importList = new JList();
        importList.setSelectionMode(1);
        scrollPane1.setViewportView(importList);
        final JPanel panel4 = new JPanel();
        panel4.setLayout(new GridLayoutManager(2, 3, new Insets(0, 0, 0, 0), -1, -1));
        panel3.add(panel4, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label1 = new JLabel();
        label1.setText("Save Location");
        panel4.add(label1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        browseButton = new JButton();
        browseButton.setEnabled(false);
        browseButton.setText("Browse...");
        panel4.add(browseButton, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        saveLocationTextField = new JTextField();
        saveLocationTextField.setEnabled(false);
        panel4.add(saveLocationTextField, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        descriptionTextField = new JLabel();
        descriptionTextField.setText("");
        panel4.add(descriptionTextField, new GridConstraints(1, 0, 1, 3, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return contentPane;
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.spreadsheet;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 *
 * @author bsoren
 */
public class SwingFileUpload extends JFrame implements
      PropertyChangeListener {

        JFilePicker filePicker = new JFilePicker("Choose a pcb xml file: ", "Browse");

        JButton buttonUpload = new JButton("Submit");
         JLabel labelProgress = new JLabel("Progress:");
         JProgressBar progressBar = new JProgressBar(0, 100);
         JCheckBox firstQuestionAnswer = new JCheckBox("First Question/Answer");
       

        public SwingFileUpload() {
                super("Spreadsheet Generator");

                // set up layout
                setLayout(new GridBagLayout());
                GridBagConstraints constraints = new GridBagConstraints();
                constraints.anchor = GridBagConstraints.WEST;
                constraints.insets = new Insets(5, 5, 5, 5);

                // set up components
                filePicker.setMode(JFilePicker.MODE_OPEN);

                buttonUpload.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent event) {
                                buttonUploadActionPerformed(event);
                        }

                });

                progressBar.setPreferredSize(new Dimension(200, 30));
                progressBar.setStringPainted(true);

                // add components to the frame
                constraints.gridx = 0;
                constraints.gridy = 0;
                constraints.weightx = 0.0;
                constraints.gridwidth = 2;
                constraints.fill = GridBagConstraints.HORIZONTAL;
                add(filePicker, constraints);
                
                GridBagConstraints gbc2 = new GridBagConstraints();
                gbc2.gridx = 0;
                gbc2.gridy = 1;
                gbc2.anchor = GridBagConstraints.LINE_START;
                gbc2.fill = GridBagConstraints.HORIZONTAL;
                add(firstQuestionAnswer,gbc2);

                constraints.gridx = 0;
                constraints.gridy = 2;
                constraints.anchor = GridBagConstraints.CENTER;
                constraints.fill = GridBagConstraints.NONE;
                add(buttonUpload, constraints);

//                constraints.gridx = 0;
//                constraints.gridy = 2;
//                constraints.gridwidth = 2;
//                //  constraints.anchor = GridBagConstraints.CENTER;
//                add(progressBar, constraints);

                constraints.gridx = 0;
                 constraints.gridy = 3;
                constraints.weightx = 0.0;
                constraints.fill = GridBagConstraints.HORIZONTAL;
                add(progressBar, constraints);

                pack();
                setLocationRelativeTo(null);	// center on screen
                setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        }

        private void buttonUploadActionPerformed(ActionEvent event) {

                String filePath = filePicker.getSelectedFilePath();
                File sourceFile = new File(filePath);
                if (filePath.equals("") || !sourceFile.exists()) {
                        JOptionPane.showMessageDialog(this,
                              "Please choose a valid xml file to process!", "Error",
                              JOptionPane.ERROR_MESSAGE);
                        return;
                }

                try {

                        progressBar.setValue(0);

                        File uploadFile = new File(filePath);
                        System.out.println(" selected file name : " + uploadFile.getName());
                        System.out.println(" selected file absolute path  : " + uploadFile.getParent());

                        String parentDir = uploadFile.getParent();
                        String fileName = uploadFile.getName().replaceFirst("[.][^.]+$", "");

                        System.out.println("FileName without extension : " + fileName);
                        // source file for excel generation

                        // new SpreadsheetGenerator(uploadFile, fileName, parentDir).runIt();  // old implementation
                        FileProcessTask task = new FileProcessTask(uploadFile, parentDir,fileName,firstQuestionAnswer.isSelected());
                        task.addPropertyChangeListener(this);
                       // Thread taskThread = new Thread(task);
                       // taskThread.start();
                        task.execute();
                      //  taskThread.join();
                        

                      //  SpreadsheetGeneratorBase sgb = new SpreadsheetGeneratorImp(uploadFile, parentDir);
                        //  sgb.startFileProcessing();
                        // generating excel
//                        File sourceFileforExcel = new File(fileName + "_output.txt");
//                        String excelFileName = fileName + ".xlsx";
//                        ExcelWriter2.writeExcel(sourceFileforExcel, excelFileName);
                } catch (Exception ex) {
                        JOptionPane.showMessageDialog(this,
                              "Error processing file: " + ex.getMessage(), "Error",
                              JOptionPane.ERROR_MESSAGE);

                        ex.printStackTrace();
                }

        }

        /**
         * Update the progress bar's state whenever the progress of download changes.
         */
        @Override
        public void propertyChange(PropertyChangeEvent evt) {
                if (evt.getPropertyName().equals("progress")) {
                        int progress = (Integer) evt.getNewValue();
                        progressBar.setValue(progress);
                }
        }

        /**
         * @param args the command line arguments
         */
        public static void main(String[] args) {
                try {
                        // set look and feel to system dependent
                        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } catch (Exception ex) {
                        ex.printStackTrace();
                }

                SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                                new SwingFileUpload().setVisible(true);
                        }
                });
        }

}

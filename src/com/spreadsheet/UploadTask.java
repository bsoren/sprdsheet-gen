package com.spreadsheet;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

/**
 * Executes the file upload in a background thread and updates progress to listeners that implement the
 * java.beans.PropertyChangeListener interface.
 *
 * @author www.codejava.net
 *
 */
public class UploadTask extends SwingWorker<Void, Integer> {

        private File uploadFile;
        private File jarFile;

        public UploadTask(File jarFile, File uploadFile) {
                this.jarFile = jarFile;
                this.uploadFile = uploadFile;
        }

        /**
         * Executed in background thread
         */
        @Override
        protected Void doInBackground() throws Exception {
                try {

                        System.out.println(" file exists " + jarFile.getAbsolutePath());
                        String jarFilePath = jarFile.getAbsolutePath();
                        Process proc = Runtime.getRuntime().
                              exec("java -jar " + jarFilePath + " " + uploadFile.getName());
                        BufferedReader stdInput = new BufferedReader(new InputStreamReader(proc.getInputStream()));

                        BufferedReader stdError = new BufferedReader(new InputStreamReader(proc.getErrorStream()));

// read the output from the command
                        System.out.println("Here is the standard output of the command:\n");
                        String s = null;
                        while ((s = stdInput.readLine()) != null) {
                                System.out.println(s);
                        }

// read any errors from the attempted command
                        System.out.println("Here is the standard error of the command (if any):\n");
                        while ((s = stdError.readLine()) != null) {
                                System.out.println(s);
                        }
                        //OutputStream outStream = proc.getOutputStream();

                } catch (Exception ex) {
                        JOptionPane.showMessageDialog(null, "Error uploading file: " + ex.getMessage(),
                              "Error", JOptionPane.ERROR_MESSAGE);
                        ex.printStackTrace();
                        cancel(true);
                }

                return null;
        }

        /**
         * Executed in Swing's event dispatching thread
         */
        @Override
        protected void done() {
                if (!isCancelled()) {
                        JOptionPane.showMessageDialog(null,
                              "File has been uploaded successfully!", "Message",
                              JOptionPane.INFORMATION_MESSAGE);
                }
        }
}

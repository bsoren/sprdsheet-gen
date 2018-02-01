/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.spreadsheet;

import java.io.File;

/**
 *
 * @author bsoren
 */
public class Client {
        public static void main(String[] args) {
                
                System.out.println("Running.....");
                String fileName = "C:\\Users\\bsoren\\Desktop\\IMS_tools\\OCHS_SAASHOSTAPP_LANGPACK_IF80063.xml";
                
                File uploadedFile = new File(fileName);
                String parentDir = uploadedFile.getParent();
                
                FileProcessTask task =  new FileProcessTask(uploadedFile, null,"testexcel",false);
                //task.addPropertyChangeListener(this);
                task.startFileProcessing();
                task.generateExcelSheet(uploadedFile.getName().replaceFirst("[.][^.]+$", ""),false);
                task.deleteTempFiles();
               // task.execute();
        }
}

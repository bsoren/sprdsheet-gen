/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.spreadsheet;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 *
 * @author bsoren
 */
public class ExcelWriter2 {

        private static CellStyle cellStyleBold;
        
        private static boolean isFirstQuestionAnswer = false;

        public static void writeExcel(File sourceFile, String excelFile, boolean firstQuestionAnswer) throws IOException, InvalidFormatException {
                Workbook workbook = new XSSFWorkbook();
                isFirstQuestionAnswer = firstQuestionAnswer;

                Sheet sheet = workbook.createSheet();

                cellStyleBold = sheet.getWorkbook().createCellStyle();
                org.apache.poi.ss.usermodel.Font font = sheet.getWorkbook().createFont();
                font.setBold(true);
                //font.setFontHeightInPoints((short) 16);
                cellStyleBold.setFont(font);

                int rowCount = 0;

                Row row0 = sheet.createRow(rowCount);
                Cell cell1 = row0.createCell(0);
                if (isFirstQuestionAnswer) {
                        cell1.setCellValue("First Question");
                } else {
                        cell1.setCellValue("Last Question");
                }

                cell1.setCellStyle(cellStyleBold);

                Cell cell2 = row0.createCell(1);
                cell2.setCellValue("Answer");
                cell2.setCellStyle(cellStyleBold);
                
                // column 3
                Cell cell3 = row0.createCell(2);
                cell3.setCellValue("Reference IT flow");
                cell3.setCellStyle(cellStyleBold);
                

                try (BufferedReader br = new BufferedReader(new FileReader(sourceFile))) {
                        String line;
                        while ((line = br.readLine()) != null) {
                                Row row = sheet.createRow(++rowCount);
                                writeRow(line, row);
                        }
                }

                try (FileOutputStream outputStream = new FileOutputStream(excelFile)) {
                        workbook.write(outputStream);
                }
        }

        public static void writeRow(String lineString, Row row) {

                String[] lineArray = lineString.split(";");

                if (lineArray != null) {
                        
                        /* old code
                        Cell cell1 = row.createCell(0);
                        cell1.setCellValue(lineArray[lineArray.length - 2]);
                       // cell1.setCellStyle(cellStyleBold);

                        Cell cell2 = row.createCell(1);
                        cell2.setCellValue(lineArray[lineArray.length - 1]);
                        //cell2.setCellStyle(cellStyleBold);
                                */
                        
                        
                        // Last node should be always be a Diagnostics node, throw error if last node is not diagnostic node.
                        if (lineArray[lineArray.length - 1].equals("END_NODE")) {  
                                
                                //Diagnotic node, check if previous node is UserInputNode.
                                if(lineArray[lineArray.length - 2].startsWith("USERINPUTNODE_")){
                                       // removing text "USERINPUTNODE_" from the begining.
                                        writeLastQuestionAnswer(lineArray[lineArray.length - 2].substring(14),"*",row);
                                        
                                }else if(lineArray[lineArray.length - 2].startsWith("RR_")){  // Reference Node is last node.
                                        writeLastQuestionAnswerBeforeReferenceRule(lineArray[lineArray.length - 4],
                                                lineArray[lineArray.length - 3], lineArray[lineArray.length - 2],row);
                                }else{
                                        writeLastQuestionAnswer(lineArray[lineArray.length - 3],lineArray[lineArray.length - 2],row);
                                }
                        }else {
                                
                                if(isFirstQuestionAnswer)
                                        writeLastQuestionAnswer(lineArray[lineArray.length - 2], lineArray[lineArray.length - 1],row);
                                else
                                        writeLastQuestionAnswer(" *** Error: " , "*** Error:",row);
                        }
                }
        }

        public static void writeLastQuestionAnswer(String lastQuestion, String lastAnswer, Row row) {

                Cell cell1 = row.createCell(0);
                cell1.setCellValue(lastQuestion);

                Cell cell2 = row.createCell(1);
                cell2.setCellValue(lastAnswer);

        }
        
        public static void writeLastQuestionAnswerBeforeReferenceRule(String lastQuestion, String lastAnswer, String refRuleName, Row row) {

                Cell cell1 = row.createCell(0);
                cell1.setCellValue(lastQuestion);

                Cell cell2 = row.createCell(1);
                cell2.setCellValue(lastAnswer);
                
                Cell cell3 = row.createCell(2);
                cell3.setCellValue(refRuleName);

        }

        public static void main(String[] args) {

                try {
                        ExcelWriter2.writeExcel(new File("test_output.txt"), "Blah.xlsx", false);
                } catch (IOException ex) {
                        ex.printStackTrace();
                } catch (InvalidFormatException ex) {
                        ex.printStackTrace();
                }
        }

}

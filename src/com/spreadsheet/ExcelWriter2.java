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

        public static void writeExcel(File sourceFile, String excelFile,boolean firstQuestionAnswer) throws IOException, InvalidFormatException {
                Workbook workbook = new XSSFWorkbook();

                Sheet sheet = workbook.createSheet();

                cellStyleBold = sheet.getWorkbook().createCellStyle();
                org.apache.poi.ss.usermodel.Font font = sheet.getWorkbook().createFont();
                font.setBold(true);
                //font.setFontHeightInPoints((short) 16);
                cellStyleBold.setFont(font);

                int rowCount = 0;
                
                Row row0 = sheet.createRow(rowCount);
                Cell cell1 = row0.createCell(0);
                if(firstQuestionAnswer){
                        cell1.setCellValue("First Question");
                }else{
                       cell1.setCellValue("Last Question");
                }
 
                cell1.setCellStyle(cellStyleBold);
                
                Cell cell2 = row0.createCell(1);
                cell2.setCellValue("Answer");
                cell2.setCellStyle(cellStyleBold);
                
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
                        Cell cell1 = row.createCell(0);
                        cell1.setCellValue(lineArray[lineArray.length - 2]);
                       // cell1.setCellStyle(cellStyleBold);

                        Cell cell2 = row.createCell(1);
                        cell2.setCellValue(lineArray[lineArray.length - 1]);
                        //cell2.setCellStyle(cellStyleBold);
                }
        }

        public static void main(String[] args) {

                try {
                        ExcelWriter2.writeExcel(new File("test_output.txt"), "Blah.xlsx",false);
                } catch (IOException ex) {
                        ex.printStackTrace();
                } catch (InvalidFormatException ex) {
                        ex.printStackTrace();
                }
        }

}

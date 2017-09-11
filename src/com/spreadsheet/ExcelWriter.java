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
public class ExcelWriter {

        private static CellStyle cellStyleBold;

        public static void writeExcel(File sourceFile, String excelFile) throws IOException, InvalidFormatException {
                Workbook workbook = new XSSFWorkbook();

                Sheet sheet = workbook.createSheet();

                cellStyleBold = sheet.getWorkbook().createCellStyle();
                org.apache.poi.ss.usermodel.Font font = sheet.getWorkbook().createFont();
                font.setBold(true);
                //font.setFontHeightInPoints((short) 16);
                cellStyleBold.setFont(font);

                int rowCount = 0;

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

                for (int i = 0; i < lineArray.length; i++) {
                        Cell cell = row.createCell(i);
                        cell.setCellValue(lineArray[i]);

                        if (i == lineArray.length - 1 || i == lineArray.length - 2) {
                                cell.setCellStyle(cellStyleBold);
                        }

                }
        }

        public static void main(String[] args) {

                try {
                        ExcelWriter.writeExcel(new File("test_output.txt"), "Blah.xlsx");
                } catch (IOException ex) {
                        ex.printStackTrace();
                } catch (InvalidFormatException ex) {
                        ex.printStackTrace();
                }
        }

}

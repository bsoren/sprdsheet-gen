package com.spreadsheet;

import com.spreadsheet.pattern.nodes.ConditionEvaluatorRule;
import com.spreadsheet.pattern.nodes.DiagnosticNode;
import com.spreadsheet.pattern.nodes.DummyNode;
import com.spreadsheet.pattern.nodes.EventCodeElement;
import com.spreadsheet.pattern.nodes.FileUploadNode;
import com.spreadsheet.pattern.nodes.NodeElement;
import static com.spreadsheet.pattern.nodes.NodeElement.SEPARATOR_CHARACTER;
import com.spreadsheet.pattern.nodes.PromptNode;
import com.spreadsheet.pattern.nodes.ReferenceRuleNode;
import com.spreadsheet.pattern.nodes.StartingNode;
import com.spreadsheet.pattern.nodes.UserInputNode;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Executes the file upload in a background thread and updates progress to listeners that implement the
 * java.beans.PropertyChangeListener interface.
 *
 * @author www.codejava.net
 *
 */
public class FileProcessTask extends SwingWorker<Void, Integer> {

        protected File inputFile;
        protected String inputFileAbsolutePath;
        protected String inputFileCopy;
        protected String outputTextFileNameTemp;
        protected String outputTextFileName;

        private String parentDir;

        private static PrintWriter out;
        private static Document doc;
        String fileName;
        
        File fileInitialCopy;

        private Stack<Node> stack;
        
        boolean isFirstQuestionAnswer = false;

        public FileProcessTask(File inputFile, String parentDir, String fileName,boolean selected) {

                setFirstQuestionAnswer(selected);
                this.inputFile = inputFile;
                this.inputFileAbsolutePath = inputFile.getAbsolutePath();
                System.out.println("absolute path : " + inputFileAbsolutePath);
                this.inputFileCopy = inputFile.getName().replaceFirst("[.][^.]+$", "");
                this.parentDir = parentDir;
                this.fileName = fileName;

                try {
                        fileInitialCopy = new File(inputFileCopy);
                        copyFile(inputFile,fileInitialCopy );
                } catch (Exception ex) {
                        System.out.println("Error is copying file : " + ex.getMessage());
                        ex.printStackTrace();
                }

                outputTextFileNameTemp = inputFileCopy + "_temp.txt";
                outputTextFileName = inputFileCopy + "_output.txt";

                System.out.println("OuputFile : " + outputTextFileNameTemp + " " + outputTextFileName);

        }

        public void generateExcelSheet(String fileName,boolean isFirstQuestionAnswer) {

                // generating excel
                File sourceFileforExcel = new File(fileName + "_output.txt");
                String excelFileName = fileName + ".xlsx";
                try {
                        ExcelWriter2.writeExcel(sourceFileforExcel, excelFileName, isFirstQuestionAnswer);
                } catch (IOException ex) {
                        Logger.getLogger(FileProcessTask.class.getName()).log(Level.SEVERE, null, ex);
                } catch (InvalidFormatException ex) {
                        Logger.getLogger(FileProcessTask.class.getName()).log(Level.SEVERE, null, ex);
                }
        }

        private void copyFile(File source, File dest) throws IOException {
                InputStream is = null;
                OutputStream os = null;
                try {
                        is = new FileInputStream(source);
                        os = new FileOutputStream(dest);
                        byte[] buffer = new byte[1024];
                        int length;
                        while ((length = is.read(buffer)) > 0) {
                                os.write(buffer, 0, length);
                        }
                } finally {
                        is.close();
                        os.close();
                }
        }
        
        public  void deleteTempFiles(){
                
                System.out.println("OuputFile : " + outputTextFileNameTemp + " " + outputTextFileName);
                
                File fileTemp = new File(outputTextFileNameTemp);
                File fileTempOutput = new File(outputTextFileName);
                
                boolean isInitialFileDeleteSuccess = fileInitialCopy.delete();
                System.out.println("Initial File Delete Success : " + isInitialFileDeleteSuccess);
                
               // boolean isTempFileDeleteSuccess = fileTemp.delete();
               // System.out.println("isTempFileDeleteSuccess : " + isTempFileDeleteSuccess);
                
               // boolean isTempOutputDeleteSuccess = fileTempOutput.delete();
                //System.out.println("isTempOutputDeleteSuccess : " + isTempOutputDeleteSuccess);
        }

        public void startFileProcessing() {

                beginXmlFile(inputFileAbsolutePath);

                PrintWriter out2 = null;
                BufferedReader br = null;

                try {
                        out2 = new PrintWriter(outputTextFileName);
                        br = new BufferedReader(new FileReader(outputTextFileNameTemp));
                        String line;
                        while ((line = br.readLine()) != null) {
                                String replacedLine = line.replaceAll("^;+", "");
                                out2.println(replacedLine);
                        }
                } catch (Exception ex) {

                } finally {
                        out2.close();
                        try {
                                br.close();
                        } catch (IOException ex) {
                                Logger.getLogger(FileProcessTask.class.getName()).log(Level.SEVERE, null, ex);
                        }
                }

        }

        public void beginXmlFile(String input) {

                try {

                        File fXmlFile = new File(input);
                        String fileName = fXmlFile.getName();

                        int pos = fileName.lastIndexOf(".");
                        if (pos > 0) {
                                fileName = fileName.substring(0, pos);
                        }

                        System.out.println("outputfilename : " + outputTextFileNameTemp);
                        out = new PrintWriter(outputTextFileNameTemp);

                        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
                        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
                        doc = dBuilder.parse(fXmlFile);

                        //optional, but recommended
                        //read this - http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
                        doc.getDocumentElement().normalize();

                        Node startNode = findNodeByName("initial");

                        StringBuilder builder1 = new StringBuilder();

                        NodeElement nodeElem = createNode(startNode, builder1);
                        //nodeElem.processNode();
                        NodeElement nodeElem2 = new StartingNode(this, startNode, builder1);
                        //nodeElem.processNode();
                        Element startingElement = (Element) startNode;
                        processStartingNode(startingElement, builder1);

                } catch (Exception e) {
                        e.printStackTrace();
                } finally {
                        out.close();
                }

        }

        public void processStartingNode(Element elem, StringBuilder builder) {

                String prompt = elem.getElementsByTagName("prompt").item(0).getTextContent();
                System.out.println("prompt : " + prompt);

                NodeList suElements = elem.getElementsByTagName("successorUserSelectionOption");
                System.out.println("sucessor Node list size :" + suElements.getLength());

                StringBuilder builder2 = new StringBuilder();
                builder2.append(builder.toString());
                builder2.append(SEPARATOR_CHARACTER + prompt);

                for (int temp = 0; temp < suElements.getLength(); temp++) {

                        setProgress((int) (temp * 100) / (suElements.getLength()));

                        System.out.println(" Value : " + temp + " Progress: " + (int) (temp * 100) / (suElements.
                              getLength() - 1));

                        Node node = suElements.item(temp);
                        String currentElemText = ((Element) node).getElementsByTagName("selectionText").item(0).
                              getTextContent();

                        System.out.println("               sucessorUserSelectionOption : " + currentElemText);

                        String next = ((Element) node).getElementsByTagName("next").item(0).getTextContent();
                        //System.out.println("next node : "+next);

                        Node node1 = nodeFinder(next);
                        NodeElement nodeElem;

                        StringBuilder builder3 = new StringBuilder();
                        builder3.append(builder2.toString());
                        builder3.append(SEPARATOR_CHARACTER + currentElemText);
                        
                        if(isFirstQuestionAnswer()){
                                this.printLine(builder3.toString());
                        }else{
                                 nodeElem = createNode(node1, builder3);
                                 nodeElem.processNode();
                        }
                }

        }

        public NodeElement createNode(Node node, StringBuilder builder) {

                if (node == null) {
                        return new DummyNode(this, node, builder);
                }

                String nodeName = node.getNodeName();

                if (nodeName.equals("MultiChoiceSelectionRule")) {

                        return new PromptNode(this, node, builder);

                } else if (nodeName.equals("DiagnosisRule")) {

                        return new DiagnosticNode(this, node, builder);

                } else if (nodeName.equals("EventCodeRule")) {

                        return new EventCodeElement(this, node, builder);

                } else if (nodeName.equals("UserInputRule")) {

                        return new UserInputNode(this, node, builder);

                } else if (nodeName.equals("ReferenceRule")) {

                        return new ReferenceRuleNode(this, node, builder);

                } else if (nodeName.equals("ConditionEvaluatorRule")) {

                        return new ConditionEvaluatorRule(this, node, builder);

                } else if (nodeName.equals("FileUploadRule")) {

                        return new FileUploadNode(this, node, builder);
                }

                return new DummyNode(this, node, builder);
        }

        public Node nodeFinder(String searchNode) {

                StringBuilder sb = new StringBuilder(searchNode);
                String searchStr = sb.substring(0, 2);
                System.out.println("Nodefinder SearchString : " + searchStr);

                switch (searchStr) {
                        case "D_":
                                return searchDiagnosticNode(searchNode);
                        case "UI":
                                return searchUserInputNode(searchNode);
                        case "S_":
                                return findNodeByName(searchNode);
                        case "EC":
                                return findEventCodeNodeByName(searchNode);
                        case "RR":
                                return searchRefRuleNode(searchNode);
                        case "CE":
                                return searchConditionEvaluatorNode(searchNode);
                        case "FU":
                                return searchFileUploadNode(searchNode);
                        default:
                                return null;

                }
        }

        // prompt node
        public Node findNodeByName(String searchNode) {

                NodeList nList = doc.getElementsByTagName("MultiChoiceSelectionRule");

                for (int temp = 0; temp < nList.getLength(); temp++) {

                        Node nNode = nList.item(temp);

                        if (nNode.getNodeType() == Node.ELEMENT_NODE) {

                                Element eElement = (Element) nNode;

                                String nodeName = eElement.getElementsByTagName("name").item(0).getTextContent();
                                String promptName = eElement.getElementsByTagName("prompt").item(0).
                                      getTextContent();

                                if (nodeName.equals(searchNode)) {
                                        //System.out.println("node : " + promptName);
                                        return nNode;
                                }
                        }
                }

                return null;
        }

        // event code node
        public Node findEventCodeNodeByName(String searchNode) {
                NodeList nList = doc.getElementsByTagName("EventCodeRule");

                for (int temp = 0; temp < nList.getLength(); temp++) {

                        Node nNode = nList.item(temp);

                        //System.out.println("\nCurrent Element :" + nNode.getNodeName());
                        if (nNode.getNodeType() == Node.ELEMENT_NODE) {

                                Element eElement = (Element) nNode;

                                String nodeName = eElement.getElementsByTagName("name").item(0).getTextContent();

                                if (nodeName.equals(searchNode)) {

                                        return nNode;

                                }
                        }
                }

                return null;
        }

        public void printLine(String str) {
                out.println(str);
        }

        //Diagnostic node
        private Node searchDiagnosticNode(String searchNode) {
                NodeList nList = doc.getElementsByTagName("DiagnosisRule");

                for (int temp = 0; temp < nList.getLength(); temp++) {

                        Node nNode = nList.item(temp);

                        if (nNode.getNodeType() == Node.ELEMENT_NODE) {

                                Element eElement = (Element) nNode;

                                String nodeName = eElement.getElementsByTagName("name").item(0).getTextContent();

                                if (nodeName.equals(searchNode)) {
                                        //System.out.println("node : " + promptName);

                                        return nNode;
                                }
                        }
                }

                return null;
        }

        private Node searchUserInputNode(String searchNode) {
                NodeList nList = doc.getElementsByTagName("UserInputRule");

                for (int temp = 0; temp < nList.getLength(); temp++) {

                        Node nNode = nList.item(temp);

                        if (nNode.getNodeType() == Node.ELEMENT_NODE) {

                                Element eElement = (Element) nNode;

                                String nodeName = eElement.getElementsByTagName("name").item(0).getTextContent();

                                if (nodeName.equals(searchNode)) {
                                        //System.out.println("node : " + promptName);

                                        return nNode;
                                }
                        }
                }

                return null;
        }

        private Node searchRefRuleNode(String searchNode) {
                NodeList nList = doc.getElementsByTagName("ReferenceRule");

                for (int temp = 0; temp < nList.getLength(); temp++) {

                        Node nNode = nList.item(temp);

                        if (nNode.getNodeType() == Node.ELEMENT_NODE) {

                                Element eElement = (Element) nNode;

                                String nodeName = eElement.getElementsByTagName("name").item(0).getTextContent();

                                if (nodeName.equals(searchNode)) {
                                        //System.out.println("node : " + promptName);

                                        return nNode;
                                }
                        }
                }

                return null;
        }

        private Node searchConditionEvaluatorNode(String searchNode) {
                NodeList nList = doc.getElementsByTagName("ConditionEvaluatorRule");

                for (int temp = 0; temp < nList.getLength(); temp++) {

                        Node nNode = nList.item(temp);

                        if (nNode.getNodeType() == Node.ELEMENT_NODE) {

                                Element eElement = (Element) nNode;

                                String nodeName = eElement.getElementsByTagName("name").item(0).getTextContent();

                                if (nodeName.equals(searchNode)) {
                                        //System.out.println("node : " + promptName);

                                        return nNode;
                                }
                        }
                }

                return null;
        }

        private Node searchFileUploadNode(String searchNode) {
                NodeList nList = doc.getElementsByTagName("FileUploadRule");

                for (int temp = 0; temp < nList.getLength(); temp++) {

                        Node nNode = nList.item(temp);

                        if (nNode.getNodeType() == Node.ELEMENT_NODE) {

                                Element eElement = (Element) nNode;

                                String nodeName = eElement.getElementsByTagName("name").item(0).getTextContent();

                                if (nodeName.equals(searchNode)) {
                                        //System.out.println("node : " + promptName);

                                        return nNode;
                                }
                        }
                }

                return null;
        }

        /**
         * Executed in background thread
         */
        @Override
        protected Void doInBackground() {
                try {
                        setProgress(0);

                        startFileProcessing();
                        generateExcelSheet(fileName,isFirstQuestionAnswer);
                        deleteTempFiles();
                        
                        setProgress(100);

                } catch (Exception ex) {
                        JOptionPane.showMessageDialog(null, "Error uploading file: " + ex.getMessage(),
                              "Error", JOptionPane.ERROR_MESSAGE);
                        ex.printStackTrace();
                        cancel(true);
                }

                return null;
        }

        public void setProgressValue(int value) {
                setProgress(value);
        }

        private void generatingExcel(String fileName, boolean isFirstQuestionAnswer) {

                try {
                        // generating excel
                        File sourceFileforExcel = new File(fileName + "_output.txt");
                        String excelFileName = fileName + ".xlsx";
                        ExcelWriter2.writeExcel(sourceFileforExcel, excelFileName,isFirstQuestionAnswer);

                } catch (Exception ex) {
                        JOptionPane.showMessageDialog(null, "Error writing excel file: " + ex.getMessage(),
                              "Error", JOptionPane.ERROR_MESSAGE);
                        ex.printStackTrace();
                        cancel(true);
                }
        }

        /**
         * Executed in Swing's event dispatching thread
         */
        @Override
        protected void done() {
                if (!isCancelled()) {
                        JOptionPane.showMessageDialog(null,
                              "File has been processed successfully!", "Message",
                              JOptionPane.INFORMATION_MESSAGE);
                }
        }

        private boolean isFirstQuestionAnswer() {
                return isFirstQuestionAnswer;
        }
        
        private void setFirstQuestionAnswer(boolean bvalue){
                this.isFirstQuestionAnswer = bvalue;
        }

}

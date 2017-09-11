/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.spreadsheet;

/**
 *
 * @author bsoren
 */
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class SpreadsheetGenerator {

        private static PrintWriter out;

        private static Document doc;

        private static File inputFile;

        private static String outputFileNameTemp;
        private static String outputFileName;
        private static String inputFileOut;

        public SpreadsheetGenerator(File inputFile, String fileNameWithoutExt, String parentDir) {

                this.inputFile = inputFile;
                inputFileOut = inputFile.getName();

                System.out.println(inputFileOut);

                try {
                        copyFile(inputFile, new File(inputFileOut));
                } catch (Exception ex) {
                        System.out.println("Error is copying file : " + ex.getMessage());
                        ex.printStackTrace();
                }

                outputFileNameTemp = fileNameWithoutExt + "_temp.txt";
                outputFileName = fileNameWithoutExt + "_output.txt";

        }

        private static void copyFile(File source, File dest) throws IOException {
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

        public static void runIt() {
        

                String input = inputFile.getAbsolutePath();

                processXmlFile(input);

                PrintWriter out2 = null;
                BufferedReader br = null;

                try {
                        out2 = new PrintWriter(outputFileName);
                        br = new BufferedReader(new FileReader(outputFileNameTemp));
                        String line;
                        while ((line = br.readLine()) != null) {
                                String replacedLine = line.replaceAll("^;+", "");
                                out2.println(replacedLine);
                        }
                } catch (Exception ex) {

                } finally {
                        out2.close();
                }

        }

        private static void processXmlFile(String input) {

                try {

                        File fXmlFile = new File(input);
                        String fileName = fXmlFile.getName();

                        int pos = fileName.lastIndexOf(".");
                        if (pos > 0) {
                                fileName = fileName.substring(0, pos);
                        }

                        System.out.println("outputfilename : " + outputFileNameTemp);
                        out = new PrintWriter(outputFileNameTemp);

                        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
                        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
                        doc = dBuilder.parse(fXmlFile);

                        //optional, but recommended
                        //read this - http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
                        doc.getDocumentElement().normalize();

                        StringBuilder builder1 = new StringBuilder();

                        Node startNode = findNodeByName("initial");
                        processPrompt((Element) startNode, builder1);

                } catch (Exception e) {
                        e.printStackTrace();
                } finally {
                        out.close();
                }

        }

        private static void processPrompt(Element initialElement, StringBuilder builder) {

//                String removeLeadingSemiColon = builder.toString().replaceAll("^;+", "");
//                builder = new StringBuilder(removeLeadingSemiColon);
                String prompt = initialElement.getElementsByTagName("prompt").item(0).getTextContent();

                builder.append(";" + prompt);
                System.out.println("Question: " + prompt);

                NodeList suElements = initialElement.getElementsByTagName("successorUserSelectionOption");
                System.out.println("sucessor Node list size :" + suElements.getLength());

                StringBuilder builder2 = new StringBuilder();
                builder2.append(builder.toString());

                for (int temp = 0; temp < suElements.getLength(); temp++) {
               

                        System.out.println(" node : " + temp);

                        Node node1 = suElements.item(temp);

                        if (node1.getNodeType() == Node.ELEMENT_NODE) {

                                Element elem = (Element) suElements.item(temp);

                                String currentNodeText = elem.getElementsByTagName("selectionText").item(0).
                                      getTextContent();

                                System.out.println("Current Node Text - " + currentNodeText);

                                String next = elem.getElementsByTagName("next").item(0).getTextContent();
                                System.out.println("Next Node - " + next);

                                if (next.charAt(0) == 'D' || next.charAt(0) == 'U') {

                                        System.out.println("Next Node - Diagnostic or UserInput");
                                        String selectionText = elem.getElementsByTagName("selectionText").item(0).
                                              getTextContent();

                                        StringBuilder stringBuilder3 = new StringBuilder();
                                        stringBuilder3.append(builder2.toString());
                                        stringBuilder3.append(";" + selectionText);
                                        String output = stringBuilder3.toString();
                                        System.out.println(" Answer : " + output);
                                        out.println(output);
                                        //System.out.println("Answer : " + selectionText);
                                        continue;

                                }

                                if (next.charAt(0) == 'S') {
                                        System.out.println("Next Node - Prompt");
                                        String selectionText = elem.getElementsByTagName("selectionText").item(0).
                                              getTextContent();

                                        StringBuilder builder3 = new StringBuilder();
                                        builder3.append(builder2.toString());
                                        builder3.append(";" + selectionText);
                                        System.out.println(" Prompt: " + builder3.toString());

                                        //System.out.println("Prompt : "+builder3.toString());
                                        //System.out.println("Answer : " + selectionText);
                                        Node nextNode = findNodeByName(next);
                                        if (nextNode != null) {
                                                processPrompt((Element) nextNode, builder3);
                                        } else {
                                                System.out.println("findNodeByName() is null for : " + next);
                                        }

                                }

                                if (next.charAt(0) == 'E') {
                                        System.out.println("Next Node - Event Code");
                                        String selectionText = elem.getElementsByTagName("selectionText").item(0).
                                              getTextContent();

                                        StringBuilder builder3 = new StringBuilder();
                                        builder3.append(builder2.toString());
                                        builder3.append(";" + selectionText);
                                        System.out.println(" EventCode: " + builder3.toString());

                                        // System.out.println(" Answer : "+builder3.toString());
                                        // System.out.println("Answer : " + selectionText);
                                        String nextNodeName = findEventCodeNodeByName(next);

                                        if (nextNodeName.charAt(0) == 'D') {
                                                System.out.println(" Evencode : " + builder3.toString());
                                                out.println(builder3.toString());
                                                continue;
                                        }

                                        Node nextNode1 = findNodeByName(nextNodeName);
                                        if (nextNode1 != null) {
                                                processPrompt((Element) nextNode1, builder3);
                                        } else {
                                                System.out.println("findNodeByName() is null for : " + next);
                                        }

                                }
                        }

                }
        }

        private static Node findNodeByName(String searchNode) {

                NodeList nList = doc.getElementsByTagName("MultiChoiceSelectionRule");

                for (int temp = 0; temp < nList.getLength(); temp++) {

                        Node nNode = nList.item(temp);

                        //System.out.println("\nCurrent Element :" + nNode.getNodeName());
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

        private static String findEventCodeNodeByName(String searchNode) {
                NodeList nList = doc.getElementsByTagName("EventCodeRule");

                for (int temp = 0; temp < nList.getLength(); temp++) {

                        Node nNode = nList.item(temp);

                        //System.out.println("\nCurrent Element :" + nNode.getNodeName());
                        if (nNode.getNodeType() == Node.ELEMENT_NODE) {

                                Element eElement = (Element) nNode;

                                String nodeName = eElement.getElementsByTagName("name").item(0).getTextContent();

                                if (nodeName.equals(searchNode)) {
                                        Element sucDefault = (Element) eElement.getElementsByTagName("successorDefault").
                                              item(0);
                                        String nextNode = sucDefault.getElementsByTagName("next").item(0).
                                              getTextContent();

                                        return nextNode;
                                        // return findNodeByName(nextNode);
                                }
                        }
                }

                return null;
        }

}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.spreadsheet.pattern.nodes;

import com.spreadsheet.FileProcessTask;
import static com.spreadsheet.pattern.nodes.NodeElement.SEPARATOR_CHARACTER;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *
 * @author bsoren
 */
public class DiagnosticNode extends NodeElement {

        public DiagnosticNode(Node node, StringBuilder builder) {
                super(node, builder);
        }

        public DiagnosticNode(FileProcessTask sgb, Node node, StringBuilder builder) {
                super(sgb, node, builder);
        }

        @Override
        public void processNode() {
                //System.out.println("Diagnosis node");
                //sgb.printLine(builder.toString());

                System.out.println("Processing - Diagnostic node");

                // String prompt = elem.getElementsByTagName("name").item(0).getTextContent();
                String prompt = elem.getElementsByTagName("name").item(0).getTextContent();
                System.out.println("Diagnostics_Node_Name : " + prompt);
                prompt = "*";

                NodeList suElements = elem.getElementsByTagName("successorDefault");

                // check if next node is END node. Stop processing and print.
                String next = ((Element) suElements.item(0)).getElementsByTagName("next").item(0).getTextContent();

                if (next.equals("END")) {
                        System.out.println("**** END NODE ****");
                        builder.append(SEPARATOR_CHARACTER + "END_NODE");
                        sgb.printLine(builder.toString());
                } else {
                        StringBuilder builder2 = new StringBuilder();
                        builder2.append(builder.toString());
                        //builder2.append(SEPARATOR_CHARACTER + prompt);
                        System.out.println("next node : " + next);

                        Node node1 = sgb.nodeFinder(next);
                        NodeElement nodeElem;
                        nodeElem = sgb.createNode(node1, builder2);
                        nodeElem.processNode();
                }

        }

}

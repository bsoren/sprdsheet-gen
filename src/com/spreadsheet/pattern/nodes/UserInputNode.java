/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.spreadsheet.pattern.nodes;

import com.spreadsheet.FileProcessTask;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *
 * @author bsoren
 */
public class UserInputNode extends NodeElement {

        public UserInputNode(Node node, StringBuilder builder) {
                super(node, builder);
        }

        public UserInputNode(FileProcessTask sgb, Node node, StringBuilder builder) {
                super(sgb, node, builder);
        }

        @Override
        public void processNode() {
                System.out.println("Processing - UserInput node");

                // String prompt = elem.getElementsByTagName("name").item(0).getTextContent();
                String prompt = elem.getElementsByTagName("prompt").item(0).getTextContent();
                System.out.println("UserInput_Name : " + prompt);

                NodeList suElements = elem.getElementsByTagName("successorDefault");
                String next = ((Element) suElements.item(0)).getElementsByTagName("next").item(0).getTextContent();

                if (next.equals("END")) {
                        System.out.println("**** END NODE ****");
                        builder.append(SEPARATOR_CHARACTER + "END_NODE");
                        sgb.printLine(builder.toString());
                } else {
                        StringBuilder builder2 = new StringBuilder();
                        builder2.append(builder.toString());
                        builder2.append(SEPARATOR_CHARACTER + "USERINPUTNODE_" + prompt);
                        System.out.println("next node : " + next);

                        Node node1 = sgb.nodeFinder(next);
                        NodeElement nodeElem;
                        nodeElem = sgb.createNode(node1, builder2);
                        nodeElem.processNode();

                }

        }

}

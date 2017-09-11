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
public class ReferenceRuleNode extends NodeElement {

        public ReferenceRuleNode(Node node, StringBuilder builder) {
                super(node, builder);
        }

        public ReferenceRuleNode(FileProcessTask sgb, Node node, StringBuilder builder) {
                super(sgb, node, builder);
        }

        @Override
        public void processNode() {
                System.out.println("Processing - Reference Rule node");
   
                String prompt = elem.getElementsByTagName("name").item(0).getTextContent();
                System.out.println("Ref_Rule_Name : " + prompt);

                NodeList suElements = elem.getElementsByTagName("successorDefault");
                String next = ((Element) suElements.item(0)).getElementsByTagName("next").item(0).getTextContent();

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

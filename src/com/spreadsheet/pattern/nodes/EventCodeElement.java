/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.spreadsheet.pattern.nodes;

import com.spreadsheet.FileProcessTask;
import org.w3c.dom.Node;

/**
 *
 * @author bsoren
 */
public class EventCodeElement extends NodeElement {

        public EventCodeElement(Node node, StringBuilder builder) {
                super(node, builder);
        }

        public EventCodeElement(FileProcessTask sgb, Node node, StringBuilder builder) {
                super(sgb, node, builder);
        }

        @Override
        public void processNode() {

                System.out.println("Event Code Node");
                StringBuilder builder2 = new StringBuilder();
                builder2.append(builder.toString());
                //builder2.append(SEPARATOR_CHARACTER + elem.getElementsByTagName("eventCode").item(0).getTextContent());
                //builder2.append(SEPARATOR_CHARACTER);
                String next = elem.getElementsByTagName("next").item(0).getTextContent();
                Node node1 = sgb.nodeFinder(next);

                NodeElement nodeElem = sgb.createNode(node1, builder2);
                nodeElem.processNode();

        }

}

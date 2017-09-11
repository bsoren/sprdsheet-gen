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
public class DummyNode extends NodeElement {

        public DummyNode(FileProcessTask sgb, Node node, StringBuilder builder) {
                super(sgb, node, builder);
        }

        @Override
        public void processNode() {
                System.out.println("Unrecognizable node");
                sgb.printLine(builder.toString());
        }

}

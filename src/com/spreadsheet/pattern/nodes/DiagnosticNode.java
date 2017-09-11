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
public class DiagnosticNode extends NodeElement{

        public DiagnosticNode(Node node, StringBuilder builder) {
                super(node, builder);
        }

        public DiagnosticNode(FileProcessTask sgb, Node node, StringBuilder builder) {
                super(sgb, node, builder);
        }

        @Override
        public void processNode() {
             System.out.println("Diagnosis node");
             sgb.printLine(builder.toString());
        }
        
}

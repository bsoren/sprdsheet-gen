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
public class ConditionEvaluatorRule extends NodeElement{

        public ConditionEvaluatorRule(Node node, StringBuilder builder) {
                super(node, builder);
        }

        public ConditionEvaluatorRule(FileProcessTask sgb, Node node, StringBuilder builder) {
                super(sgb, node, builder);
        }

        @Override
        public void processNode() {
             System.out.println("Codition Evaluator node");
             sgb.printLine(builder.toString());
        }
        
}

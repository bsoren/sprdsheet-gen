/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.spreadsheet.pattern.nodes;

import com.spreadsheet.FileProcessTask;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 *
 * @author bsoren
 */
public abstract class NodeElement {

        public Element elem;
        public Node node;
        public StringBuilder builder;
        public FileProcessTask sgb;
        
        public static final String SEPARATOR_CHARACTER = ";";
        
        
        

        public NodeElement(Node node, StringBuilder builder) {
                this.elem = (Element) node;
                this.node = node;
                this.builder = builder;
        }

        public NodeElement(FileProcessTask sgb, Node node, StringBuilder builder) {
                this.elem = (Element) node;
                this.node = node;
                this.builder = builder;
                this.sgb = sgb;
        }

        public abstract void processNode();

}

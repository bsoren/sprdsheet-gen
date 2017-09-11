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
public class PromptNode extends NodeElement {
        
        int progress = 0;

        public PromptNode(Node node, StringBuilder builder) {
                super(node, builder);

        }

        public PromptNode(FileProcessTask sgb, Node node, StringBuilder builder) {
                super(sgb, node, builder);
        }
        
        
        public synchronized void setProgress(int progressValue){
                this.progress = progressValue;
                
        }
        
        public synchronized int getProgress(){      
                return this.progress;
        }

        @Override
        public void processNode() {

                String prompt = elem.getElementsByTagName("prompt").item(0).getTextContent();
                System.out.println("prompt : " + prompt);

                NodeList suElements = elem.getElementsByTagName("successorUserSelectionOption");
                System.out.println("sucessor Node list size :" + suElements.getLength());

                StringBuilder builder2 = new StringBuilder();
                builder2.append(builder.toString());
                builder2.append(SEPARATOR_CHARACTER + prompt);

                for (int temp = 0; temp < suElements.getLength(); temp++) {

                      
                        
                        Node node = suElements.item(temp);
                        String currentElemText = ((Element) node).getElementsByTagName("selectionText").item(0).
                              getTextContent();

                        System.out.println("sucessorUserSelectionOption : " + currentElemText);

                        String next = ((Element) node).getElementsByTagName("next").item(0).getTextContent();
                        //System.out.println("next node : "+next);

                        Node node1 = sgb.nodeFinder(next);
                        NodeElement nodeElem;

                        StringBuilder builder3 = new StringBuilder();
                        builder3.append(builder2.toString());
                        builder3.append(SEPARATOR_CHARACTER + currentElemText);

                        nodeElem = sgb.createNode(node1, builder3);
                        nodeElem.processNode();

                }
        }
}

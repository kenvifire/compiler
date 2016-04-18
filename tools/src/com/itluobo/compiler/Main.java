package com.itluobo.compiler;

import org.graphstream.graph.Graph;

/**
 * Created by kenvi on 16/4/18.
 */
public class Main {

    public static void main(String[] args) {
        SingleInputProcessor sip = new SingleInputProcessor();

        State state = sip.process("A");

        Graph graph = state.dump("test");
        graph.display();
    }

}

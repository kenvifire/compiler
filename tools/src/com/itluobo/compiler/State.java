package com.itluobo.compiler;

import org.graphstream.graph.*;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.ui.spriteManager.Sprite;
import org.graphstream.ui.spriteManager.SpriteManager;

import java.util.*;

/**
 * Created by kenvi on 16/4/18.
 */
public class State {
    private static int stateCount = 0;
    private static Set<String> inputSet = new HashSet<String>();
    private StateTypeEnum type;
    private int state;
    private List<Transition> transitionList = new ArrayList<Transition>();
    private boolean isVisited;

    static {
        inputSet.add("E_CLOSURE");
    }

    public State() {
        this.state = stateCount++;
    }

    public StateTypeEnum getType() {
        return type;
    }

    public void setType(StateTypeEnum type) {
        this.type = type;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public List<Transition> getTransitionList() {
        return transitionList;
    }

    public void setTransitionList(List<Transition> transitionList) {
        this.transitionList = transitionList;
    }

    public void addTransition(Transition transition) {
        transitionList.add(transition);
    }

    public boolean isVisited() {
        return isVisited;
    }

    public void setVisited(boolean isVisited) {
        this.isVisited = isVisited;
    }

    public static void addInput(String input) {
        inputSet.add(input);
    }

    public Graph dump(String name) {
        Graph graph = new SingleGraph(name);
        for (int i= 0; i < stateCount; i++) {
            graph.addNode( i + "").addAttribute("ui.label", i);
        }

        SpriteManager sman = new SpriteManager(graph);
        Map<String, Sprite> name2SpringMap = new HashMap<String, Sprite>();
        for(String input : inputSet) {
            name2SpringMap.put(input, sman.addSprite(input));
        }

        Queue<State> queue = new ArrayDeque<State>();

        queue.add(this);

        while(!queue.isEmpty()) {
            State front = queue.poll();
            for (Transition transition: front.getTransitionList()) {
                Edge edge =  graph.addEdge(transition.toString(), transition.getBeginState().getState() + "",
                        transition.getEndState().getState() + "", true);
                Sprite sprite = name2SpringMap.get(transition.getInput());
                sprite.attachToEdge(edge.getId());

                edge.addAttribute("ui.label",transition.getInput());

                if(!transition.getEndState().isVisited) {
                    queue.add(transition.getEndState());
                }

                front.setVisited(true);
            }
        }

        return graph;
    }



}

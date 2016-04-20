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
    private State end;
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
        if(transition.getBeginState().getState() != state)  {
            throw new RuntimeException("can not add transition");
        }
        if(transitionList.contains(transition)) {
            throw new RuntimeException("duplicate transition");
        }
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
            if(front.isVisited) {
                continue;
            }
            System.out.println("front:" + front.getState() + ",status:" + front.isVisited);
            for (Transition transition: front.getTransitionList()) {
                System.out.println(String.format("\tadd adge[%s-->%s,%s]from state[%s] ", transition.getBeginState().getState(),transition.getEndState().getState(), transition.getInput()
                    ,front.getState()));
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

    public static State andState(State start, State end) {
        if(start == null)  return end;
        if(end == null) return start;

        Transition transition = new Transition();
        transition.setType(TransitionType.E_CLOSURE);
        transition.setBeginState(start.getEnd());
        transition.setEndState(end);
        start.end.addTransition(transition);

        start.end = end.end;

        return start;

    }

    public static State orState(State statA, State statB) {
        if(statA == null) return statB;
        if(statB == null) return statA;

        State init = new State();
        init.setType(StateTypeEnum.START);

        //init --> A
        Transition aTrans = new Transition();
        aTrans.setType(TransitionType.E_CLOSURE);
        aTrans.setBeginState(init);
        aTrans.setEndState(statA);
        init.addTransition(aTrans);

        //init -->B
        Transition bTrans = new Transition();
        bTrans.setType(TransitionType.E_CLOSURE);
        bTrans.setBeginState(init);
        bTrans.setEndState(statB);
        init.addTransition(bTrans);

        State end = new State();
        end.setType(StateTypeEnum.ACCEPT);

        // A-->end
        Transition aEndTrans = new Transition();
        aEndTrans.setType(TransitionType.E_CLOSURE);
        aEndTrans.setBeginState(statA.end);
        aEndTrans.setEndState(end);
        statA.end.addTransition(aEndTrans);

        //B-->
        Transition bEndTrans = new Transition();
        bEndTrans.setType(TransitionType.E_CLOSURE);
        bEndTrans.setType(TransitionType.E_CLOSURE);
        bEndTrans.setBeginState(statB.end);
        bEndTrans.setEndState(end);
        statB.end.addTransition(bEndTrans);

        init.end = end;

        return init;

    }

    public State getEnd() {
        return end;
    }

    public void setEnd(State end) {
        this.end = end;
    }
}

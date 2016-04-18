package com.itluobo.compiler;

/**
 * Created by kenvi on 16/4/18.
 */
public class Transition {

    private State beginState;
    private State endState;
    private String input;
    private TransitionType type;

    public State getBeginState() {
        return beginState;
    }

    public void setBeginState(State beginState) {
        this.beginState = beginState;
    }

    public State getEndState() {
        return endState;
    }

    public void setEndState(State endState) {
        this.endState = endState;
    }

    public String getInput() {
        switch (type) {
            case NODE:
                return input;
            case E_CLOSURE:
                return "E_CLOSURE";
            default:
                throw new RuntimeException("unsupported type");
        }
    }

    public void setInput(String input) {
        this.input = input;
    }

    public TransitionType getType() {
        return type;
    }

    public void setType(TransitionType type) {
        this.type = type;
    }
}

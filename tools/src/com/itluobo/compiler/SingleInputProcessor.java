package com.itluobo.compiler;

/**
 * Created by kenvi on 16/4/18.
 */


/** c => [int] --> [c] --> [accept] **/
public class SingleInputProcessor implements InputProcessor{

    @Override
    public State process(String input) {
        if(input.length() != 1) {
            throw new RuntimeException("invalid input for singleInputProcessor");
        }
        State.addInput(input);
        State init = new State();
        init.setType(StateTypeEnum.START);
        Transition eTrans = new Transition();
        eTrans.setType(TransitionType.E_CLOSURE);

        State inputState = new State();
        eTrans.setBeginState(init);
        eTrans.setEndState(inputState);
        init.addTransition(eTrans);

        State acceptState = new State();

        Transition inputTrans  = new Transition();
        inputTrans.setInput(input);
        inputTrans.setBeginState(inputState);
        inputTrans.setEndState(acceptState);
        inputTrans.setType(TransitionType.NODE);

        inputState.addTransition(inputTrans);
        acceptState.setType(StateTypeEnum.ACCEPT);

        init.setEnd(acceptState);

        return init;
    }
}

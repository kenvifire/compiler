package com.itluobo.compiler;

/**
 * Created by kenvi on 16/4/19.
 */
public class StarInputProcessor {
    public State process(State state) {
        State init = new State();// init
        init.setType(StateTypeEnum.START);
        init.setTag("star-init");


        State accept = new State();
        accept.setType(StateTypeEnum.ACCEPT);
        accept.setTag("star-end");

        System.out.println("accept=>" + accept.getState());

        //init --> state, init-->accept
        Transition init2State = new Transition();
        init2State.setBeginState(init);
        init2State.setEndState(state);
        init2State.setType(TransitionType.E_CLOSURE);

        Transition init2Accept = new Transition();
        init2Accept.setBeginState(init);
        init2Accept.setEndState(accept);
        init2Accept.setType(TransitionType.E_CLOSURE);

        init.addTransition(init2State);
        init.addTransition(init2Accept);

        //state.end-->state.start, state.end-->accept
        Transition state2State = new Transition();
        state2State.setBeginState(state.getEnd());
        state2State.setEndState(state);
        state2State.setType(TransitionType.E_CLOSURE);

        Transition state2Accept = new Transition();
        state2Accept.setBeginState(state.getEnd());
        state2Accept.setEndState(accept);
        state2Accept.setType(TransitionType.E_CLOSURE);

        state.getEnd().addTransition(state2State);
        state.getEnd().addTransition(state2Accept);

        init.setEnd(accept);

        return init;

    }
}

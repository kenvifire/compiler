package com.itluobo.compiler;


import java.util.Stack;

/**
 * Created by kenvi on 16/4/18.
 */
public class Main {

    public static void main(String[] args) {
        String input = "AB";

        int i = 0;
        SingleInputProcessor sip = new SingleInputProcessor();
        StarInputProcessor starInputProcessor = new StarInputProcessor();
        Stack<Character> operatorStack = new Stack<Character>();
        Stack<State> operandStack = new Stack<State>();
        Character lastChar = null;
        while(i < input.length()) {
            char c = input.charAt(i);
            if(c == '*') { //calc
                operandStack.push(starInputProcessor.process(operandStack.pop() + ""));
            }else if (c == '|') {
                operatorStack.push('|');
            }else if( c == '(') {
                operatorStack.push('(');
            }else if(c == ')'){
                char top = operatorStack.peek();
                if(top != '(') {
                    throw  new RuntimeException("unexpected token:" + top);
                }
            }else {
                operandStack.push(sip.process(c+""));
                if (lastChar != null && TokenUtils.isNotToken(lastChar)) {
                    State stateB = operandStack.pop();
                    State stateA = operandStack.pop();
                    operandStack.push(State.orState(stateA, stateB));
                }
            }
            lastChar = c;
            i++;
        }

        operandStack.pop().dump("test").display();

    }

}

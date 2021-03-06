package com.itluobo.compiler;


import java.util.Stack;

/**
 * Created by kenvi on 16/4/18.
 */
public class Main {

    public static void main(String[] args) {
        String input = "A|(BC)";

        int i = 0;
        SingleInputProcessor sip = new SingleInputProcessor();
        StarInputProcessor starInputProcessor = new StarInputProcessor();
        Stack<Character> operatorStack = new Stack<Character>();
        Stack<State> operandStack = new Stack<State>();
        InputWrapper inputWrapper = new InputWrapper(input);
        CharWrapper currentChar = null;
        CharWrapper nextChar = null;
        while(inputWrapper.hasMore()) {
            currentChar = inputWrapper.getNextChar();
            nextChar = inputWrapper.peek();
            if(currentChar.getCh() == '*') { //calc
                operandStack.push(starInputProcessor.process(operandStack.pop()));
            }else if (currentChar.getCh() == '|') {
                if(nextChar != CharWrapper.EOF && TokenUtils.isNotToken(nextChar.getCh())) {
                    State oprandA = operandStack.pop();
                    State oprandB = sip.process(inputWrapper.getNextChar().getCh() + "");
                    operandStack.push(State.orState(oprandA, oprandB));
                }else if(nextChar != CharWrapper.EOF) {
                    operatorStack.push('|');
                }else {
                    throw new RuntimeException("unterminated expression");
                }

            }else if( currentChar.getCh() == '(') {
                operatorStack.push('(');
            }else if( currentChar.getCh() == '&') {
                if(nextChar != CharWrapper.EOF && TokenUtils.isNotToken(nextChar.getCh())) {
                    State oprandA = operandStack.pop();
                    State oprandB = sip.process(inputWrapper.getNextChar().getCh() + "");
                    operandStack.push(State.andState(oprandA, oprandB));
                }else if(nextChar != CharWrapper.EOF) {
                    operatorStack.push('&');
                }else {
                    throw new RuntimeException("unterminated expression");
                }
            }
            else if(currentChar.getCh() == ')'){//pop until (, calc
                Character operator = null;
                while(!operatorStack.isEmpty() && (operator = operatorStack.pop()) != '(')  {
                    State oprandB = operandStack.pop();
                    State oprandA = operandStack.pop();

                    if(operator == '|') operandStack.push(State.orState(oprandA, oprandB));
                    if(operator == '&') operandStack.push(State.andState(oprandA, oprandB));
                }
                if(nextChar != CharWrapper.EOF && TokenUtils.isNotToken(nextChar.getCh())) {
                    inputWrapper.pushBack('&');
                }
            }else {
                operandStack.push(sip.process(currentChar.getCh() + ""));
                if (nextChar != CharWrapper.EOF && TokenUtils.isNotToken(nextChar.getCh())) {
                    inputWrapper.pushBack('&');
                }
            }


        }

        while(!operatorStack.isEmpty())  {
            Character operator = operatorStack.pop();
            State oprandB = operandStack.pop();
            State oprandA = operandStack.pop();

            if(operator == '|') operandStack.push(State.orState(oprandA, oprandB));
            if(operator == '&') operandStack.push(State.andState(oprandA, oprandB));
        }

        State result = operandStack.pop();
        result.setTag("=====>>>");
        result.getEnd().setTag("<<<<===");
        result.dump("test").display();

    }

}

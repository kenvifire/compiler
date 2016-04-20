package com.itluobo.compiler;

/**
 * Created by kenvi on 16/4/20.
 */
public class InputWrapper {
    private String input;
    private int pos = 0;
    private boolean pushed = false;
    private Character pushedChar = null;

    public InputWrapper(String input) {
        this.input = input;
        this.pos = 0;
    }

    public boolean hasMore() {
        return pos < input.length();
    }

    public CharWrapper getNextChar() {
       if(pushed){
            CharWrapper pushedWrapper = new CharWrapper(pushedChar, false);
            pushed = false;
            pushedChar = null;
           return pushedWrapper;
       }
       else if(pos  < input.length())  {
           return new CharWrapper(input.charAt(pos++),false);
       }else {
           return CharWrapper.EOF;
       }
    }

    public CharWrapper peek() {
        if(pos  < input.length())  {
            return new CharWrapper(input.charAt(pos),false);
        }else {
            return CharWrapper.EOF;
        }
    }

    public void pushBack(Character ch) {
        if(this.pushed) {
            throw new RuntimeException("can not push more than one char.");
        }
        this.pushed = true;
        this.pushedChar = ch;

    }

}

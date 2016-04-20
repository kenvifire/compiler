package com.itluobo.compiler;

/**
 * Created by kenvi on 16/4/20.
 */
public class CharWrapper {
    private Character ch;
    private boolean eof;
    public static final CharWrapper EOF = new CharWrapper(null, true);

    public CharWrapper(Character ch) {
        this(ch, false);
    }

    public CharWrapper(Character ch, boolean eof) {
        this.ch = ch;
        this.eof = eof;
    }

    public Character getCh() {
        return ch;
    }

    public void setCh(Character ch) {
        this.ch = ch;
    }

    public boolean isEof() {
        return eof;
    }

    public void setEof(boolean eof) {
        this.eof = eof;
    }
}

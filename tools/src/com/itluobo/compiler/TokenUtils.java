package com.itluobo.compiler;

/**
 * Created by hannahzhang on 16/4/19.
 */
public class TokenUtils {
    public static boolean isNotToken(Character c) {
        return   c != '*' && c != '(' && c != ')' && c != '|';
    }
}

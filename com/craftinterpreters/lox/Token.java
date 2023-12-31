package com.craftinterpreters.lox;

/**
 * 标记对象类
 */
class Token {
    final TokenType type;
    final String lexeme;
    final Object literal;
    final int line; // line属性为错误信息服务

    public Token(TokenType type, String lexeme, Object literal, int line) {
        this.type = type;
        this.lexeme = lexeme;
        this.literal = literal;
        this.line = line;
    }

    public String toString() {
        return type + " " + lexeme + " " + literal;
    }
}
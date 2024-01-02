package com.craftinterpreters.lox;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.HashMap;

import static com.craftinterpreters.lox.TokenType.*;

/**
 * 扫描器类
 * start和current字段是指向字符串的偏移量。
 * start字段指向被扫描的词素中的第一个字符，
 * current字段指向当前正在处理的字符。
 * line字段跟踪的是current所在的源文件行数
 */
class Scanner {
    private final String source;
    private final ArrayList<Token> tokens = new ArrayList<>(); // 词素列表

    private int start = 0;
    private int current = 0;
    private int line = 1;

    public Scanner(String source) {
        this.source = source;
    }

    /**
     * 从命令中扫描词素
     * 
     * @return 词素列表
     */
    List<Token> scansTokens() {
        while (!isAtEnd()) {
            start = current;
            scansToken();
        }
        tokens.add(new Token(TokenType.EOF, "", null, line));
        return tokens;

    }

    /**
     * 辅助函数
     * 
     * @return 判断当前命令是否扫描到末尾
     */
    boolean isAtEnd() {
        return current >= source.length();
    }

    /**
     * 匹配并识别词素
     */
    private void scansToken() {
        char c = advance();
        switch (c) {
            // 判断词素是否是由单个字符组成的标记
            case '(':
                addToken(LEFT_PAREN);
                break;
            case ')':
                addToken(RIGHT_PAREN);
                break;
            case '{':
                addToken(LEFT_BRACE);
                break;
            case '}':
                addToken(RIGHT_BRACE);
                break;
            case ',':
                addToken(COMMA);
                break;
            case '.':
                addToken(DOT);
                break;
            case '-':

                addToken(MINUS);
                break;
            case '+':
                addToken(PLUS);
                break;
            case ';':
                addToken(SEMICOLON);
                break;
            case '*':
                addToken(STAR);
                break;
            // 判断词素是否是由一个或者两个字符组成的标记
            case '!':
                addToken(match('=') ? BANG_EQUAL : BANG);
                break;
            case '=':
                addToken(match('=') ? EQUAL_EQUAL : EQUAL);
                break;
            case '>':
                addToken(match('=') ? GREATER_EQUAL : GREATER);
                break;
            case '<':
                addToken(match('=') ? LESS_EQUAL : LESS);
                break;
            // '/'标记需要单独识别，因为它有可能是'//'注释
            case '/':
                if (match('/')) {
                    // 注释代码需要忽略
                    while (peek() != '\n' && isAtEnd()) // 要求peek预读的字符不是换行符，因为换行符会在后文进行识别。
                        advance();
                } else {
                    addToken(SLASH);
                }
                break;
            // 跳过无意义的字符.直接开始扫描识别下一个词素
            case ' ':
            case '\t':
            case '\r':
                break;
            // 跳过无意义的字符.直接开始扫描识别下一个词素。跳过换行字符需要递增行数。
            case '\n':
                line++;
                break;
            default:
                Lox.error(line, "Unexpected character.");
                break;
        }
    }

    /**
     * 辅助 判断可能由一个或者两个字符组成的标记
     * 
     * @param c
     * @return
     */
    private boolean match(char expected) {
        if (isAtEnd() || source.charAt(current) != expected)
            return false;

        current++;
        return true;

    }

    /**
     * lookahead(前瞻)函数。又称预读函数。前瞻函数不会消费字符。
     * 消费字符的意思：advance中的current++操作。即移动指针跳过一个字符。
     * 
     * @return 返回当前指针的后一个字符。
     */
    private char peek() {
        if (isAtEnd()) {
            return '\0';
        }
        return source.charAt(current);
    }

    /**
     * 递进并返回一个字符
     * 
     * @return 一个字符
     */
    private char advance() {
        current++;
        return source.charAt(current - 1);
    }

    /**
     * 将识别出来的标记加入标记列表中
     * 
     * @param type TokenType
     */
    private void addToken(TokenType type) {
        addToken(type, null);
    }

    /**
     * 将识别出来的标记加入标记列表中
     * 
     * @param type
     * @param literal 字面量
     */
    private void addToken(TokenType type, Object literal) {
        String text = source.substring(start, current);
        tokens.add(new Token(type, text, literal, line));
    }

}
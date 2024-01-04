package com.craftinterpreters.lox;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
     * 保留字哈希列表
     */
    private static final Map<String, TokenType> keywords;

    static {
        keywords = new HashMap<>();
        keywords.put("and", AND);
        keywords.put("class", CLASS);
        keywords.put("else", ELSE);
        keywords.put("false", FALSE);
        keywords.put("for", FOR);
        keywords.put("fun", FUN);
        keywords.put("if", IF);
        keywords.put("nil", NIL);
        keywords.put("or", OR);
        keywords.put("print", PRINT);
        keywords.put("return", RETURN);
        keywords.put("super", SUPER);
        keywords.put("this", THIS);
        keywords.put("true", TRUE);
        keywords.put("var", VAR);
        keywords.put("while", WHILE);
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
            // 处理字面量
            case '"':
                string();
                break;
            default:
                // 处理数字字面量
                // 放在default分支里以避免写多个case
                if (isDigit(c)) {
                    number();
                } else if (isAlpha(c)) {
                    identifier();
                } else {
                    Lox.error(line, "Unexpected character.");
                }
                break;
        }
    }

    /**
     * 辅助 处理官方或者自定义的标识符
     */
    private void identifier() {
        while (isAlphaNumber(peek()) && !isAtEnd()) {
            advance();
        }
        var keyword = source.subSequence(start, current);
        var type = keywords.get(keyword);
        if (type == null) {
            addToken(IDENTIFIER);
        } else {
            addToken(type);
        }

    }

    /**
     * 辅助 判断字符是否为字母或者下划线开头
     * 
     * @param c
     * @return 布尔
     */
    private boolean isAlpha(char c) {
        if (c >= 'a' && c <= 'z') {
            return true;
        } else if (c >= 'A' && c <= 'Z') {
            return true;
        } else if (c == '_') {
            return true;
        }
        return false;
    }

    /**
     * 辅助 判断是否是字母或者数字或者下划线
     * 
     * @param c
     * @return
     */
    private boolean isAlphaNumber(char c) {
        if (isAlpha(c)) {
            return true;
        } else if (isDigit(c)) {
            return true;
        }
        return false;
    }

    /**
     * 辅助 判断预读字符是不是数字
     * 
     * @return
     */
    private boolean isDigit(char c) {
        if (c >= '0' && c <= '9')
            return true;
        return false;
    }

    /**
     * 辅助 处理数字字面量
     */
    private void number() {
        while (isDigit(peek())) {
            advance();
        }
        if (peek() == '.' && isDigit(peekNext())) {
            advance();
            while (isDigit(peek())) {
                advance();
            }
        }
        addToken(NUMBER, Double.parseDouble(source.substring(start, current)));
    }

    /**
     * 辅助 处理字符串字面量
     */
    private void string() {

        while (peek() != '"' && !isAtEnd()) {
            if (peek() == '\n') {
                line++;
            }
            advance();
        }
        if (isAtEnd()) {
            Lox.error(line, "Unterminated string.");
            return;
        }
        advance(); // 右双引号
        String value = source.substring(start + 1, current - 1); // 省略双引号
        addToken(STRING, value);
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
     * lookahead(前瞻)函数。又称预读函数。前瞻函数不会消费字符。
     * 预读两个字符。
     * 
     * @return
     */
    private char peekNext() {
        if (current + 1 >= source.length()) {
            return '\0';
        }
        return source.charAt(current + 1);
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
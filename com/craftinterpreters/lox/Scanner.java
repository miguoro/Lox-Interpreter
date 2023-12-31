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
    private final ArrayList<Token> tokens = new ArrayList<>();

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
     * 匹配词素
     */
    private void scansToken() {
        // TODO:
    }

}
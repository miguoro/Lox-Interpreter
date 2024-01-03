package com.craftinterpreters.lox;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.charset.Charset;
import java.util.List;
import java.util.stream.Stream;
import com.craftinterpreters.lox.Scanner;

public class Lox {

    static boolean hadError = false; // 处理错误的标识

    public static void main(String[] args) throws IOException {
        if (args.length > 1) {
            System.out.println("Usage: jlox [scripts]");
            System.exit(64); // 退出码依据UNIX“sysexits.h”标头中定义的约定

        } else if (args.length == 1) {
            runFile(args[0]);
        } else {
            runPrompt();
        }
    }

    /**
     * 命令行批处理
     * 
     * @param file 批命令文件路径
     * @throws IOException
     */
    private static void runFile(String file) throws IOException {
        var bytes = Files.readAllBytes(Paths.get(file));
        run(new String(bytes, Charset.defaultCharset()));
        if (hadError) {
            System.exit(65); // 运行批处理文件如果报错，则直接退出
        }
    }

    /**
     * 交互式命令互动，命令代码输入一行就执行一行
     * 
     * @throws IOException
     */
    private static void runPrompt() throws IOException {
        InputStreamReader inputStreamReader = new InputStreamReader(System.in);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        while (true) {
            String line = bufferedReader.readLine();
            if (line == null) {
                break; // 若输入代码为空，则退出
            }
            run(line);
            hadError = false; // 交互式命令编程，发生错误不应该退出交互式模式，而是应该重置错误标识。

        }

    }

    /**
     * 最基础的运行函数，负责运行输入的命令
     * 
     * @param source 命令代码
     */
    private static void run(String source) {
        Scanner scanner = new Scanner(source);
        List<Token> tokens = scanner.scansTokens();// TODO: 1 标记类 Token 之后写； 2 Scanner类 中没有定义scanTokens方法，之后补充。
        // TODO: tokens的处理目前只是简单的打印出来。
        for (Token token : tokens) {
            System.out.println(token);
        }
    }

    /**
     * 错误逻辑方法
     * 
     * @param line    错误所在行
     * @param message 错误信息
     */
    static void error(int line, String message) {
        report(line, "", message);
    }

    /**
     * 错误报告方法
     * 
     * @param line    错误所在行
     * @param where   错误所在位置
     * @param message 错误信息
     */
    private static void report(int line, String where, String message) {
        System.err.println(
                "[line " + line + "] Error" + where + ":" + message); // 输出错误形式：[line 32] Error in function run :
                                                                      // Illegal comma.
        hadError = true;
    }

}
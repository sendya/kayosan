package com.loacg.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;

/**
 * Project: kayosan
 * Author: Sendya <18x@loacg.com>
 * Time: 8/12/2016 3:09 PM
 */
public class SudoExecutor {

    private static Logger logger = LoggerFactory.getLogger(SudoExecutor.class);

    protected static String sudoCmd = "";
    protected static String shellName = "/bin/bash";
    protected static String shellParam = "-c";

    public static String run(String cmd) throws IOException, InterruptedException {
        String sudoCmd = "sudo " + cmd;
        logger.info(sudoCmd);
        Process process = Runtime.getRuntime().exec(sudoCmd);
        InputStreamReader ir = new InputStreamReader(process.getInputStream());
        LineNumberReader input = new LineNumberReader(ir);
        StringBuffer sb = new StringBuffer();
        String line;
        while ((line = input.readLine()) != null) {
            sb.append(line);
            logger.info(line);
        }
        return sb.toString();
    }

    public static String run(String[] cmds) throws IOException, InterruptedException {
        for (String cmd : cmds) {
            logger.info(cmd);
        }

        Process process = Runtime.getRuntime().exec(cmds);
        InputStreamReader ir = new InputStreamReader(process.getInputStream());
        LineNumberReader input = new LineNumberReader(ir);
        StringBuffer sb = new StringBuffer();
        String line;
        while ((line = input.readLine()) != null) {
            sb.append(line);
            logger.info(line);
        }
        input = new LineNumberReader(new InputStreamReader(process.getErrorStream()));
        while ((line = input.readLine()) != null) {
            sb.append(line);
            logger.error(line);
        }
        // process.waitFor();
        return sb.toString();
    }

    /**
     * @param cmd
     * @return
     */
    public static String[] buildCommands(String cmd)   // to use this method, you should modify /etc/sudoers
    {
        String[] cmds = {shellName, shellParam, sudoCmd + " " + cmd};
        return cmds;
    }

    public static String[] buildCommands(String cmd, String sudoPasswd) {
        String[] cmds = {shellName, shellParam, "echo \"" + sudoPasswd + "\" | " + sudoCmd + " -S " + cmd};
        return cmds;
    }

    private void testExecuteCommand() throws IOException, InterruptedException {
        String cmd = "cat /etc/sudoers";
        //      SudoExecutor.run(cmd);  // should modify /etc/sudoers
        //      SudoExecutor.run(buildCommands(cmd));  // should modify /etc/sudoers
        SudoExecutor.run(buildCommands(cmd, "123456"));    // don't need modify /etc/sudoers
    }

    public void test() throws IOException, InterruptedException {
        SudoExecutor se = new SudoExecutor();
        se.testExecuteCommand();
    }

}

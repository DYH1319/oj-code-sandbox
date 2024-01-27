package asia.dyh1319.ojcodesandbox.utils;

import asia.dyh1319.ojcodesandbox.model.ExecuteMessage;
import org.springframework.util.StopWatch;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * cmd执行工具类
 */
public class CmdUtil {
    
    public static ExecuteMessage runArgumentCmd(String cmd, Long timeLimit) {
        ExecuteMessage executeMessage = new ExecuteMessage();
        try {
            StopWatch stopWatch = new StopWatch();
            stopWatch.start();
            Process process = Runtime.getRuntime().exec(cmd);
            // 超时控制
            Thread thread = null;
            if (timeLimit != null && timeLimit > 0) {
                thread = new Thread(() -> {
                    try {
                        Thread.sleep(timeLimit);
                        process.destroy();
                        System.out.println("程序运行超时");
                    } catch (InterruptedException ignore) {
                    }
                });
                thread.start();
            }
            // 等待程序执行，获取退出码
            int exitValue = process.waitFor();
            stopWatch.stop();
            // 销毁超时控制
            if (thread != null) {
                thread.interrupt();
            }
            executeMessage.setTime(stopWatch.getLastTaskTimeMillis());
            executeMessage.setExitValue(exitValue);
            // 获取程序输出流
            BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream(), System.getProperty("sun.jnu.encoding")));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line).append('\n');
            }
            br.close();
            executeMessage.setMessage(sb.toString());
            // 程序异常退出
            if (exitValue != 0) {
                br = new BufferedReader(new InputStreamReader(process.getErrorStream(), System.getProperty("sun.jnu.encoding")));
                sb = new StringBuilder();
                while ((line = br.readLine()) != null) {
                    sb.append(line).append('\n');
                }
                br.close();
                executeMessage.setErrorMessage(sb.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return executeMessage;
    }
}

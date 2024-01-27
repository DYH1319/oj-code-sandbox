package asia.dyh1319.ojcodesandbox.utils;

import asia.dyh1319.ojcodesandbox.enums.JudgeInfoMessageEnum;
import asia.dyh1319.ojcodesandbox.model.ExecuteCodeResponse;
import asia.dyh1319.ojcodesandbox.model.JudgeInfo;

import java.util.List;

/**
 * 执行代码响应工具类
 */
public class ExecuteCodeResponseUtil {
    
    public static ExecuteCodeResponse success(JudgeInfoMessageEnum judgeInfoMessage, Integer codeLength, Long time, Long memory, List<String> outputList) {
        JudgeInfo judgeInfo = JudgeInfo.builder()
            .message(judgeInfoMessage.getValue())
            .codeLength(codeLength)
            .memory(memory)
            .time(time)
            .build();
        return ExecuteCodeResponse.builder()
            .judgeInfo(judgeInfo)
            .outputList(outputList)
            .status(0)
            .build();
    }
    
    public static ExecuteCodeResponse success(JudgeInfoMessageEnum judgeInfoMessage, Integer codeLength, Long time, Long memory, List<String> outputList, String message) {
        JudgeInfo judgeInfo = JudgeInfo.builder()
            .message(judgeInfoMessage.getValue())
            .codeLength(codeLength)
            .memory(memory)
            .time(time)
            .build();
        return ExecuteCodeResponse.builder()
            .message(message)
            .judgeInfo(judgeInfo)
            .outputList(outputList)
            .status(0)
            .build();
    }
    
    public static ExecuteCodeResponse fail(String message) {
        return ExecuteCodeResponse.builder()
            .message(message)
            .status(-1)
            .build();
    }
    
    public static ExecuteCodeResponse fail(int status, String message) {
        return ExecuteCodeResponse.builder()
            .message(message)
            .status(status)
            .build();
    }
}

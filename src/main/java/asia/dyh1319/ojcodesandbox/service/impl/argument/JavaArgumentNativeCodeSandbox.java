package asia.dyh1319.ojcodesandbox.service.impl.argument;

import asia.dyh1319.ojcodesandbox.enums.JudgeInfoMessageEnum;
import asia.dyh1319.ojcodesandbox.model.ExecuteCodeRequest;
import asia.dyh1319.ojcodesandbox.model.ExecuteCodeResponse;
import asia.dyh1319.ojcodesandbox.model.ExecuteMessage;
import asia.dyh1319.ojcodesandbox.model.JudgeConfig;
import asia.dyh1319.ojcodesandbox.service.CodeSandbox;
import asia.dyh1319.ojcodesandbox.utils.CmdUtil;
import asia.dyh1319.ojcodesandbox.utils.ExecuteCodeResponseUtil;
import asia.dyh1319.ojcodesandbox.utils.SystemUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.dfa.WordTree;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Java命令行参数原生代码沙箱
 */
@Service
public class JavaArgumentNativeCodeSandbox implements CodeSandbox {
    
    /**
     * 全局代码存储目录
     */
    private static final String GLOBAL_CODE_DIR_NAME = "tempCode";
    
    /**
     * 全局Java类名
     */
    private static final String GLOBAL_JAVA_CLASS_NAME = "Main.java";
    
    /**
     * 安全管理器目录相对于项目的相对路径
     */
    private static final String SECURITY_MANAGER_RELATIVE_DIR_PATH = "src" + File.separator + "main" + File.separator + "resources" + File.separator + "security";
    
    /**
     * 安全管理器类名
     */
    private static final String SECURITY_MANAGER_CLASS_NAME = "DefaultSecurityManager";
    
    /**
     * 黑名单字符串列表
     */
    private static final List<String> BLACK_WORD_LIST;
    
    /**
     * 黑名单字符串树
     */
    private static final WordTree BLACK_WORD_TREE;
    
    static {
        BLACK_WORD_LIST = Arrays.asList("File", "exec");
        
        BLACK_WORD_TREE = new WordTree();
        BLACK_WORD_TREE.addWords(BLACK_WORD_LIST);
    }
    
    @Override
    public ExecuteCodeResponse execute(ExecuteCodeRequest executeCodeRequest) {
        // 1. 获取判题所必需的变量
        String code = executeCodeRequest.getCode();
        JudgeConfig judgeConfig = executeCodeRequest.getJudgeConfig();
        Long timeLimit = judgeConfig.getTimeLimit();
        Long memoryLimit = judgeConfig.getMemoryLimit();
        Long stackLimit = judgeConfig.getStackLimit();
        List<String> inputList = executeCodeRequest.getInputList();
        // 2. 校验代码是否含有黑名单中的字符串
        String blackWord = BLACK_WORD_TREE.match(code);
        if (blackWord != null) {
            return ExecuteCodeResponseUtil.fail("代码中包含违禁词");
        }
        // 3. 计算代码字节数（代码长度）
        int codeLength = code.length();
        // 4. 将用户的代码保存为文件
        String rootDirPath = System.getProperty("user.dir");
        String globalCodeDirPath = rootDirPath + File.separator + GLOBAL_CODE_DIR_NAME;
        // 判断全局代码目录是否存在，没有则新建
        if (!FileUtil.exist(globalCodeDirPath)) {
            FileUtil.mkdir(globalCodeDirPath);
        }
        // 把不同提交的代码隔离存放
        String userCodeDirPath = globalCodeDirPath + File.separator + new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()) + '-' + UUID.randomUUID();
        String userCodePath = userCodeDirPath + File.separator + GLOBAL_JAVA_CLASS_NAME;
        try {
            File userCodeFile = FileUtil.writeString(code, userCodePath, StandardCharsets.UTF_8);
            // 5. 编译代码，得到class文件
            String compileCmd = String.format("javac -encoding utf-8 %s", userCodeFile.getAbsolutePath());
            ExecuteMessage compileExecuteMessage = CmdUtil.runArgumentCmd(compileCmd, null);
            if (compileExecuteMessage.getExitValue() != 0) {
                return ExecuteCodeResponseUtil.success(JudgeInfoMessageEnum.COMPILE_ERROR, codeLength, null, null, null, compileExecuteMessage.getErrorMessage());
            }
            // 6. 执行代码，得到输出结果
            List<ExecuteMessage> executeMessageList = new ArrayList<>();
            long maxTime = 0; // 若成功执行所有测试用例，返回最长的执行时间作为执行时间
            for (String input : inputList) {
                String runCmd;
                if (SystemUtil.isWindows()) {
                    runCmd = String.format("java -Xmx%sk -Dfile.encoding=UTF-8 -cp %s;%s -Djava.security.manager=%s Main %s", memoryLimit.toString(), userCodeDirPath, rootDirPath + File.separator + SECURITY_MANAGER_RELATIVE_DIR_PATH, SECURITY_MANAGER_CLASS_NAME, input);
                } else if (SystemUtil.isLinux()) {
                    runCmd = String.format("java -Xmx%sk -Dfile.encoding=UTF-8 -cp %s:%s -Djava.security.manager=%s Main %s", memoryLimit.toString(), userCodeDirPath, rootDirPath + File.separator + SECURITY_MANAGER_RELATIVE_DIR_PATH, SECURITY_MANAGER_CLASS_NAME, input);
                } else {
                    return ExecuteCodeResponseUtil.fail("内部服务错误：不支持的操作系统");
                }
                ExecuteMessage runExecuteMessage = CmdUtil.runArgumentCmd(runCmd, timeLimit);
                // 程序没有正常退出
                if (runExecuteMessage.getExitValue() != 0) {
                    if (runExecuteMessage.getErrorMessage().contains("java.lang.OutOfMemoryError")) {
                        return ExecuteCodeResponseUtil.success(JudgeInfoMessageEnum.MEMORY_LIMIT_EXCEEDED, codeLength, runExecuteMessage.getTime(), null, null);
                    } else if (StrUtil.isBlank(runExecuteMessage.getMessage()) && StrUtil.isBlank(runExecuteMessage.getErrorMessage())) {
                        return ExecuteCodeResponseUtil.success(JudgeInfoMessageEnum.TIME_LIMIT_EXCEEDED, codeLength, runExecuteMessage.getTime(), null, null);
                    } else if (runExecuteMessage.getErrorMessage().contains("java.lang.SecurityException")) {
                        return ExecuteCodeResponseUtil.success(JudgeInfoMessageEnum.DANGEROUS_OPERATION, codeLength, runExecuteMessage.getTime(), null, null, runExecuteMessage.getErrorMessage());
                    } else {
                        return ExecuteCodeResponseUtil.success(JudgeInfoMessageEnum.RUNTIME_ERROR, codeLength, runExecuteMessage.getTime(), null, null, runExecuteMessage.getErrorMessage());
                    }
                }
                executeMessageList.add(runExecuteMessage);
                if (runExecuteMessage.getTime() != null) {
                    maxTime = Math.max(maxTime, runExecuteMessage.getTime());
                }
            }
            return ExecuteCodeResponseUtil.success(JudgeInfoMessageEnum.ACCEPTED, codeLength, maxTime, null, executeMessageList.stream().map(ExecuteMessage::getMessage).collect(Collectors.toList()));
        } finally {
            // 7. 文件清理（放到finally块中确保无论何时返回，tempCode都会被清理）
            if (FileUtil.file(userCodePath).getParentFile() != null) {
                if (!FileUtil.del(userCodeDirPath)) {
                    System.err.println("删除临时代码文件夹出错，文件夹路径为：" + FileUtil.file(userCodePath).getParentFile().getAbsolutePath());
                }
            }
        }
    }
}

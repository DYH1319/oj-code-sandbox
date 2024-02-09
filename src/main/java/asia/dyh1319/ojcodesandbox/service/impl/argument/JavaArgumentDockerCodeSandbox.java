package asia.dyh1319.ojcodesandbox.service.impl.argument;

import asia.dyh1319.ojcodesandbox.enums.JudgeInfoMessageEnum;
import asia.dyh1319.ojcodesandbox.model.ExecuteCodeRequest;
import asia.dyh1319.ojcodesandbox.model.ExecuteCodeResponse;
import asia.dyh1319.ojcodesandbox.model.ExecuteMessage;
import asia.dyh1319.ojcodesandbox.model.JudgeConfig;
import asia.dyh1319.ojcodesandbox.service.CodeSandbox;
import asia.dyh1319.ojcodesandbox.utils.CmdUtil;
import asia.dyh1319.ojcodesandbox.utils.ExecuteCodeResponseUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.dfa.WordTree;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.async.ResultCallback;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.command.ExecCreateCmdResponse;
import com.github.dockerjava.api.model.*;
import com.github.dockerjava.core.DockerClientBuilder;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;

import java.io.Closeable;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import static asia.dyh1319.ojcodesandbox.constant.CodeSandboxConstant.GLOBAL_CODE_DIR_PATH;

/**
 * Java命令行参数docker代码沙箱
 */
@Service
public class JavaArgumentDockerCodeSandbox implements CodeSandbox {
    
    /**
     * 全局Java类名
     */
    private static final String GLOBAL_JAVA_CLASS_NAME = "Main.java";
    
    /**
     * 黑名单字符串列表
     */
    private static final List<String> BLACK_WORD_LIST;
    
    /**
     * 黑名单字符串树
     */
    private static final WordTree BLACK_WORD_TREE;
    
    static {
        BLACK_WORD_LIST = Arrays.asList("File", "exec", "sleep");
        
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
        if (timeLimit == null || timeLimit <= 0 || memoryLimit == null || memoryLimit <= 0) {
            return ExecuteCodeResponseUtil.fail("判题配置中的时间限制和内存限制不符合要求");
        }
        // 3. 计算代码字节数（代码长度）
        int codeLength = code.length();
        // 4. 将用户的代码保存为文件
        // 把不同提交的代码隔离存放
        String userCodeDirPath = GLOBAL_CODE_DIR_PATH + File.separator + new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()) + '-' + UUID.randomUUID();
        String userCodePath = userCodeDirPath + File.separator + GLOBAL_JAVA_CLASS_NAME;
        String containerId = null;
        // 获取默认的Docker Client
        DockerClient dockerClient = DockerClientBuilder.getInstance().build();
        try {
            File userCodeFile = FileUtil.writeString(code, userCodePath, StandardCharsets.UTF_8);
            // 5. 编译代码，得到class文件
            String compileCmd = String.format("javac -encoding utf-8 %s", userCodeFile.getAbsolutePath());
            ExecuteMessage compileExecuteMessage = CmdUtil.runArgumentCmd(compileCmd, null);
            if (compileExecuteMessage.getExitValue() != 0) {
                return ExecuteCodeResponseUtil.success(JudgeInfoMessageEnum.COMPILE_ERROR, codeLength, null, null, null, compileExecuteMessage.getErrorMessage());
            }
            // 6. 创建容器，把相关文件复制到容器内
            // 创建容器
            HostConfig hostConfig = HostConfig.newHostConfig()
                .withReadonlyRootfs(true)
                .withMemory(memoryLimit * 1000)
                .withCpuCount(1L)
                .withBinds(new Bind(userCodeDirPath, new Volume("/code")));
            CreateContainerResponse createContainerResponse = dockerClient.createContainerCmd("openjdk:8-alpine")
                .withHostConfig(hostConfig)
                .withNetworkDisabled(true)
                .withAttachStdin(true)
                .withAttachStdout(true)
                .withAttachStderr(true)
                .withTty(true)
                .withName("code-sandbox-" + UUID.randomUUID())
                .exec();
            containerId = createContainerResponse.getId();
            // 7. 启动容器
            dockerClient.startContainerCmd(containerId).exec();
            // 8. 执行代码，得到输出结果
            List<ExecuteMessage> executeMessageList = new ArrayList<>();
            long maxTime = 0; // 若成功执行所有测试用例，返回最长的执行时间作为执行时间
            long maxMemory = 0; // 若成功执行所有测试用例，返回最大的内存消耗作为内存消耗
            long time = 0; // 单个样例的时间消耗
            final long[] memory = {0}; // 单个样例的最大内存消耗
            for (String input : inputList) {
                List<String> message = new ArrayList<>();
                List<String> errorMessage = new ArrayList<>();
                ExecuteMessage runExecuteMessage = new ExecuteMessage();
                String[] runCmd = ArrayUtil.append(new String[]{"java", "-Xmx" + memoryLimit + "k", "-Dfile.encoding=UTF-8", "-cp", "/code", "Main"}, input.split(" "));
                ExecCreateCmdResponse execCreateCmdResponse = dockerClient.execCreateCmd(containerId)
                    .withCmd(runCmd)
                    .withAttachStdin(true)
                    .withAttachStdout(true)
                    .withAttachStderr(true)
                    .exec();
                // 重置memory
                memory[0] = 0;
                StopWatch stopWatch = new StopWatch();
                String finalContainerId = containerId;
                boolean completed = true;
                try {
                    dockerClient.execStartCmd(execCreateCmdResponse.getId()).exec(new ResultCallback.Adapter<Frame>(){
                        @Override
                        public void onStart(Closeable stream) {
                            if (!stopWatch.isRunning()) {
                                stopWatch.start();
                            }
                            super.onStart(stream);
                        }
                        
                        @Override
                        public void onNext(Frame frame) {
                            if (stopWatch.isRunning()) {
                                stopWatch.stop();
                            }
                            StreamType streamType = frame.getStreamType();
                            if (StreamType.STDERR.equals(streamType)) {
                                errorMessage.add(new String(frame.getPayload()));
                            } else {
                                message.add(new String(frame.getPayload()));
                            }
                            try {
                                dockerClient.statsCmd(finalContainerId).withNoStream(true).exec(new Adapter<Statistics>() {
                                    @Override
                                    public void onNext(Statistics statistics) {
                                        super.onNext(statistics);
                                        System.out.println(statistics.getMemoryStats().getUsage());
                                        if (statistics.getMemoryStats().getUsage() != null) {
                                            memory[0] = Math.max(memory[0], statistics.getMemoryStats().getUsage());
                                        }
                                    }
                                }).awaitCompletion();
                            } catch (InterruptedException e) {
                                throw new RuntimeException(e);
                            }
                            if (stopWatch.getTotalTimeMillis() > timeLimit) {
                                throw new RuntimeException();
                            }
                            if (!stopWatch.isRunning()) {
                                stopWatch.start();
                            }
                            super.onNext(frame);
                        }
                        
                        @Override
                        public void onComplete() {
                            if (stopWatch.isRunning()) {
                                stopWatch.stop();
                            }
                            super.onComplete();
                        }
                    }).awaitCompletion();
                } catch (Exception ignore) {
                    completed = false;
                }
                if (stopWatch.isRunning()) {
                    stopWatch.stop();
                }
                time = stopWatch.getTotalTimeMillis();
                // 更新最大时间和最大内存
                maxTime = Math.max(maxTime, time);
                maxMemory = Math.max(maxMemory, memory[0]) / 1000;
                // 保存并添加运行结果
                runExecuteMessage.setTime(time);
                runExecuteMessage.setErrorMessage(CollectionUtil.join(errorMessage, "").trim());
                runExecuteMessage.setMessage(CollectionUtil.join(message, "").trim());
                runExecuteMessage.setMemory(memory[0]);
                executeMessageList.add(runExecuteMessage);
                System.out.println(runExecuteMessage);
                // 程序没有正常退出
                if (!completed) {
                    return ExecuteCodeResponseUtil.success(JudgeInfoMessageEnum.TIME_LIMIT_EXCEEDED, codeLength, maxTime, maxMemory, null);
                } else if (runExecuteMessage.getErrorMessage().contains("java.lang.OutOfMemoryError")) {
                    return ExecuteCodeResponseUtil.success(JudgeInfoMessageEnum.MEMORY_LIMIT_EXCEEDED, codeLength, maxTime, maxMemory, null);
                } else if (!runExecuteMessage.getErrorMessage().isEmpty()) {
                    return ExecuteCodeResponseUtil.success(JudgeInfoMessageEnum.RUNTIME_ERROR, codeLength, maxTime, maxMemory, null, runExecuteMessage.getErrorMessage());
                }
            }
            return ExecuteCodeResponseUtil.success(JudgeInfoMessageEnum.ACCEPTED, codeLength, maxTime, maxMemory, executeMessageList.stream().map(ExecuteMessage::getMessage).collect(Collectors.toList()));
        } finally {
            // 9. 停止并销毁创建的容器
            if (containerId != null) {
                dockerClient.killContainerCmd(containerId).exec();
                dockerClient.removeContainerCmd(containerId).exec();
            }
            // 10. 文件清理（放到finally块中确保无论何时返回，tempCode都会被清理）
            if (FileUtil.file(userCodePath).getParentFile() != null) {
                if (!FileUtil.del(userCodeDirPath)) {
                    System.err.println("删除临时代码文件夹出错，文件夹路径为：" + FileUtil.file(userCodePath).getParentFile().getAbsolutePath());
                }
            }
        }
    }
}

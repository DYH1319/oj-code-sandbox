package asia.dyh1319.ojcodesandbox;

import asia.dyh1319.ojcodesandbox.enums.JudgeTypeEnum;
import asia.dyh1319.ojcodesandbox.enums.LanguageEnum;
import asia.dyh1319.ojcodesandbox.model.ExecuteCodeRequest;
import asia.dyh1319.ojcodesandbox.model.JudgeConfig;
import asia.dyh1319.ojcodesandbox.service.CodeSandboxManager;
import cn.hutool.core.io.resource.ResourceUtil;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.Arrays;

@SpringBootTest
class OjCodeSandboxApplicationTests {
    
    @Resource
    private CodeSandboxManager codeSandboxManager;
    
    @Test
    void testJudge() {
        JudgeConfig judgeConfig = new JudgeConfig();
        judgeConfig.setTimeLimit(5000L);
        judgeConfig.setMemoryLimit(10240L);
        judgeConfig.setStackLimit(1024L);
        judgeConfig.setJudgeType(JudgeTypeEnum.ARGUMENT.getValue());
        
        ExecuteCodeRequest executeCodeRequest = new ExecuteCodeRequest();
        executeCodeRequest.setCode(ResourceUtil.readUtf8Str("testCode/argumentCode/Main.java"));
        // executeCodeRequest.setCode(ResourceUtil.readUtf8Str("testCode/unsafeCode/timeOverflow/Main.java"));
        // executeCodeRequest.setCode(ResourceUtil.readUtf8Str("testCode/unsafeCode/memoryOverflow/Main.java"));
        // executeCodeRequest.setCode(ResourceUtil.readUtf8Str("testCode/unsafeCode/readFile/Main.java"));
        executeCodeRequest.setLanguage(LanguageEnum.JAVA.getValue());
        executeCodeRequest.setJudgeConfig(judgeConfig);
        executeCodeRequest.setInputList(Arrays.asList("1 2", "3 4"));
        
        System.out.println(codeSandboxManager.execute(executeCodeRequest));
        // System.out.println(System.getProperty("sun.jnu.encoding"));
    }
}

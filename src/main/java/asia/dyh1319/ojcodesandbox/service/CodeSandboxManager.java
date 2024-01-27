package asia.dyh1319.ojcodesandbox.service;

import asia.dyh1319.ojcodesandbox.enums.JudgeTypeEnum;
import asia.dyh1319.ojcodesandbox.enums.LanguageEnum;
import asia.dyh1319.ojcodesandbox.model.ExecuteCodeRequest;
import asia.dyh1319.ojcodesandbox.model.ExecuteCodeResponse;
import asia.dyh1319.ojcodesandbox.service.impl.argument.JavaArgumentNativeCodeSandbox;
import asia.dyh1319.ojcodesandbox.utils.ExecuteCodeResponseUtil;
import org.springframework.stereotype.Service;

/**
 * 沙箱调用管理
 */
@Service
public class CodeSandboxManager {
    
    public ExecuteCodeResponse execute(ExecuteCodeRequest executeCodeRequest) {
        LanguageEnum language = LanguageEnum.getEnumByValue(executeCodeRequest.getLanguage());
        JudgeTypeEnum judgeType = JudgeTypeEnum.getEnumByValue(executeCodeRequest.getJudgeConfig().getJudgeType());
        if (language == null || judgeType == null) {
            return ExecuteCodeResponseUtil.fail("编程语言或判题类型为空");
        }
        
        CodeSandbox codeSandbox = null;
        if (judgeType == JudgeTypeEnum.ARGUMENT) {
            switch (language) {
                case JAVA:
                    codeSandbox = new JavaArgumentNativeCodeSandbox();
                    break;
                case CPP:
                    break;
            }
        } else if (judgeType == JudgeTypeEnum.INTERACTIVE) {
            switch (language) {
                case JAVA:
                    break;
                case CPP:
                    break;
            }
        }
        
        if (codeSandbox != null) {
            return codeSandbox.execute(executeCodeRequest);
        } else {
            return ExecuteCodeResponseUtil.fail("不支持的编程语言或判题类型");
        }
    }
}

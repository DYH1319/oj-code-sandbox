package asia.dyh1319.ojcodesandbox.service;

import asia.dyh1319.ojcodesandbox.model.ExecuteCodeRequest;
import asia.dyh1319.ojcodesandbox.model.ExecuteCodeResponse;

/**
 * 代码沙箱接口定义
 */
public interface CodeSandbox {
    
    /**
     * 执行代码
     */
    ExecuteCodeResponse execute(ExecuteCodeRequest executeCodeRequest);
}

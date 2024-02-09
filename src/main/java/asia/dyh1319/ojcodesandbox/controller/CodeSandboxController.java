package asia.dyh1319.ojcodesandbox.controller;

import asia.dyh1319.ojcodesandbox.model.ExecuteCodeRequest;
import asia.dyh1319.ojcodesandbox.model.ExecuteCodeResponse;
import asia.dyh1319.ojcodesandbox.service.CodeSandboxManager;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author DYH
 * @version 1.0
 * @since 2024/1/29 21:28
 */
@RestController
public class CodeSandboxController {
    
    @Resource
    private CodeSandboxManager codeSandboxManager;
    
    @PostMapping("/executeCode")
    public ExecuteCodeResponse executeCode(@RequestBody ExecuteCodeRequest executeCodeRequest) {
        if (executeCodeRequest == null) {
            return null;
        }
        return codeSandboxManager.execute(executeCodeRequest);
    }
}

package asia.dyh1319.ojcodesandbox.security;

/**
 * @author DYH
 * @version 1.0
 * @since 2024/1/26 22:46
 */
public class DefaultSecurityManager extends SecurityManager {
    
    @Override
    public void checkRead(String file) {
        throw new SecurityException("不允许读取文件");
    }
    
    // @Override
    // public void checkExec(String cmd) {
    //     throw new SecurityException("不允许执行文件");
    // }
}

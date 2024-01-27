import java.security.Permission;

public class DefaultSecurityManager extends SecurityManager {
    
    @Override
    public void checkRead(String file) {
        throw new SecurityException("不允许读取文件");
    }
    
    @Override
    public void checkPermission(Permission perm) {
    }
}
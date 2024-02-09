package asia.dyh1319.ojcodesandbox.constant;

import java.io.File;

/**
 * @author DYH
 * @version 1.0
 * @since 2024/1/29 16:56
 */
public class CodeSandboxConstant {
    
    /**
     * 全局代码存储目录名称
     */
    public static final String GLOBAL_CODE_DIR_NAME = "tempCode";
    
    /**
     * 项目根目录
     */
    public static final String ROOT_DIR_PATH = System.getProperty("user.dir");
    
    /**
     * 全局代码存储目录路径
     */
    public static final String GLOBAL_CODE_DIR_PATH = ROOT_DIR_PATH + File.separator + GLOBAL_CODE_DIR_NAME;
}

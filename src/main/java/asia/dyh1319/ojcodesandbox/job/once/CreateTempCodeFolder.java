package asia.dyh1319.ojcodesandbox.job.once;

import cn.hutool.core.io.FileUtil;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import static asia.dyh1319.ojcodesandbox.constant.CodeSandboxConstant.GLOBAL_CODE_DIR_PATH;

/**
 * @author DYH
 * @version 1.0
 * @since 2024/1/29 16:55
 */
@Component
public class CreateTempCodeFolder implements CommandLineRunner {
    @Override
    public void run(String... args) throws Exception {
        // 判断全局代码目录是否存在，没有则新建
        if (!FileUtil.exist(GLOBAL_CODE_DIR_PATH)) {
            FileUtil.mkdir(GLOBAL_CODE_DIR_PATH);
            System.out.println("已创建临时代码目录");
        } else {
            System.out.println("临时代码目录已存在，无需创建");
        }
    }
}

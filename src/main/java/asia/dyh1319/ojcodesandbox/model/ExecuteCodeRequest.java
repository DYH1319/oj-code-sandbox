package asia.dyh1319.ojcodesandbox.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author DYH
 * @version 1.0
 * @since 2024/1/25 15:35
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExecuteCodeRequest {
    
    /**
     * 代码
     */
    private String code;
    
    /**
     * 语言
     */
    private String language;
    
    /**
     * 判题配置
     */
    private JudgeConfig judgeConfig;
    
    /**
     * 输入列表
     */
    private List<String> inputList;
}

package asia.dyh1319.ojcodesandbox.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author DYH
 * @version 1.0
 * @since 2024/1/25 15:36
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExecuteCodeResponse {
    
    /**
     * 接口返回信息
     */
    private String message;
    
    /**
     * 执行状态（0：成功，非0：失败（沙箱内部的问题，导致判题无法完成））
     */
    private long status;
    
    /**
     * 判题信息
     */
    private JudgeInfo judgeInfo;
    
    /**
     * 输出列表
     */
    private List<String> outputList;
}

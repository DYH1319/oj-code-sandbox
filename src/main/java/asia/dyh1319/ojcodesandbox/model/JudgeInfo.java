package asia.dyh1319.ojcodesandbox.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author DYH
 * @version 1.0
 * @since 2024/1/20 18:01
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JudgeInfo implements Serializable {
    
    private static final long serialVersionUID = -7149376623007815513L;
    
    /**
     * 程序执行信息
     */
    private String message;
    
    /**
     * 执行时间
     */
    private Long time;
    
    /**
     * 占用内存
     */
    private Long memory;
    
    /**
     * 代码长度
     */
    private String codeLength;
}

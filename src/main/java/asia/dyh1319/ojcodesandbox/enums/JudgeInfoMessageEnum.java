package asia.dyh1319.ojcodesandbox.enums;

import cn.hutool.core.util.ObjectUtil;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 判题结果信息枚举
 */
@Getter
public enum JudgeInfoMessageEnum {
    
    ACCEPTED("答案正确", "Accepted"),
    WRONG_ANSWER("答案错误", "Wrong Answer"),
    COMPILE_ERROR("编译错误", "Compile Error"),
    MEMORY_LIMIT_EXCEEDED("内存超限", "Memory Limit Exceeded"),
    TIME_LIMIT_EXCEEDED("时间超限", "Time Limit Exceeded"),
    PRESENTATION_ERROR("格式错误", "Presentation Error"),
    OUTPUT_LIMIT_EXCEEDED("输出超限", "Output Limit Exceeded"),
    RUNTIME_ERROR("运行错误", "Runtime Error"),
    WAITING("等待中", "Waiting"),
    DANGEROUS_OPERATION("危险操作", "Dangerous Operation"),
    SYSTEM_ERROR("系统错误", "System Error");
    
    private final String text;
    
    private final String value;
    
    JudgeInfoMessageEnum(String text, String value) {
        this.text = text;
        this.value = value;
    }
    
    /**
     * 获取值列表
     */
    public static List<String> getValues() {
        return Arrays.stream(values()).map(item -> item.value).collect(Collectors.toList());
    }
    
    /**
     * 根据 value 获取枚举
     */
    public static JudgeInfoMessageEnum getEnumByValue(String value) {
        if (ObjectUtil.isEmpty(value)) {
            return null;
        }
        for (JudgeInfoMessageEnum anEnum : JudgeInfoMessageEnum.values()) {
            if (anEnum.value.equals(value)) {
                return anEnum;
            }
        }
        return null;
    }
}

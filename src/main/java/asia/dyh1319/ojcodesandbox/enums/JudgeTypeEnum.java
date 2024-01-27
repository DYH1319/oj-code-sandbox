package asia.dyh1319.ojcodesandbox.enums;

import cn.hutool.core.util.ObjectUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 判题类型枚举
 */
@Getter
@AllArgsConstructor
public enum JudgeTypeEnum {
    
    ARGUMENT("命令行参数评测", "Argument"),
    INTERACTIVE("输入流交互评测", "Interactive");
    
    private final String text;
    private final String value;
    
    /**
     * 根据 value 获取枚举
     */
    public static JudgeTypeEnum getEnumByValue(String value) {
        if (ObjectUtil.isEmpty(value)) {
            return null;
        }
        for (JudgeTypeEnum anEnum : JudgeTypeEnum.values()) {
            if (anEnum.value.equals(value)) {
                return anEnum;
            }
        }
        return null;
    }
}

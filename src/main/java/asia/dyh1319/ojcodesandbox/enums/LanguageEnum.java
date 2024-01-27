package asia.dyh1319.ojcodesandbox.enums;

import cn.hutool.core.util.ObjectUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 判题语言枚举
 */
@Getter
@AllArgsConstructor
public enum LanguageEnum {
    
    JAVA("Java"),
    CPP("C++");
    
    private final String value;
    
    /**
     * 根据 value 获取枚举
     */
    public static LanguageEnum getEnumByValue(String value) {
        if (ObjectUtil.isEmpty(value)) {
            return null;
        }
        for (LanguageEnum anEnum : LanguageEnum.values()) {
            if (anEnum.value.equals(value)) {
                return anEnum;
            }
        }
        return null;
    }
}

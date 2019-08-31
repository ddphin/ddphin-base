package com.ddphin.base.swagger.plugin;

import com.ddphin.base.common.constant.CodeNameEnum;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringJoiner;

/**
 * EnumPropertyPlugin
 *
 * @Date 2019/8/30 上午11:26
 * @Author ddphin
 */
public abstract class AbstractEnumDescriptionPlugin {

    protected String getDescription(Class clazz) {
        Object[] values = clazz.getEnumConstants();
        List<String> list = new ArrayList<>(values.length);
        if (CodeNameEnum.class.isAssignableFrom(clazz)) {
            Arrays.stream(values).forEach(o -> {
                int CODE = ((CodeNameEnum)o).getCode();
                String NAME = ((CodeNameEnum)o).getName();
                list.add(toString(CODE, NAME));
            });
        }
        else {
            Arrays.stream(values).forEach(o -> {
                int CODE = ((Enum)o).ordinal();
                String NAME = ((Enum)o).name();
                list.add(toString(CODE, NAME));
            });
        }
        return toString(list);
    }

    private String toString(int code, String name) {
        return code + "：" + name;
    }
    private String toString(List<String> list) {
        StringJoiner sj = new StringJoiner(", ","[","]");
        list.forEach(sj::add);
        return sj.toString();
    }
}

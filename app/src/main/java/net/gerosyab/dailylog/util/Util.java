package net.gerosyab.dailylog.util;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

/**
 * Created by tremolo on 2016-06-08.
 */
public class Util {
    public static List<Field> getAllFields(List<Field> fields, Class<?> type) {
        fields.addAll(Arrays.asList(type.getDeclaredFields()));

        if (type.getSuperclass() != null) {
            fields = getAllFields(fields, type.getSuperclass());
        }

        return fields;
    }
}

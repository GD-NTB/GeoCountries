package me.rntb.geoCountries.util;

import java.util.List;
import java.util.stream.Stream;

public class EnumUtil {

    public static <T extends Enum<T>> List<String> EnumToStringArray(Class<T> enumClass) {
        return Stream.of(enumClass.getEnumConstants())
                                  .map(Enum::name).toList();
    }
}

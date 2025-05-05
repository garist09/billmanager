package com.rg.billmanager.utility;

import com.rg.billmanager.dto.template.ValueProperty;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.function.Function;

@Component
@RequiredArgsConstructor
public class OptionalUtility {

    public static String safeGet(ValueProperty prop) {
        return Optional.ofNullable(prop).map(ValueProperty::getValue).orElse("");
    }

    public static <T> String safeExtract(Function<T, ValueProperty> getter, T source) {
        return Optional.ofNullable(source)
                .map(getter)
                .map(ValueProperty::getValue)
                .orElse("");
    }
}

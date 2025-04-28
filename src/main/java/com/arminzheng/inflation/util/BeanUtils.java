package com.arminzheng.inflation.util;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;
import java.util.stream.Collectors;

public abstract class BeanUtils {

    private static final ConcurrentMap<Class<?>, DescriptorCache> STRONG_CLASS_CACHE = new ConcurrentHashMap<>();

    public static DescriptorCache extractClass(Class<?> beanClass) {
        return STRONG_CLASS_CACHE.computeIfAbsent(beanClass, clazz -> {
            try {
                PropertyDescriptor[] propertyDescriptors = Introspector.getBeanInfo(beanClass,
                        Object.class).getPropertyDescriptors();
                // O(n) -> O(1)
                Map<String, PropertyDescriptor> propertyDescriptorMap = Arrays.stream(
                                propertyDescriptors)
                        .collect(Collectors.toMap(PropertyDescriptor::getName, Function.identity(),
                                (oldValue, newValue) -> newValue));
                return DescriptorCache.builder()
                        .propertyDescriptors(propertyDescriptors)
                        .propertyDescriptorMap(propertyDescriptorMap)
                        .build();
            } catch (IntrospectionException ignored) {
            }
            return null;
        });
    }

}

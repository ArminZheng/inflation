package com.arminzheng.inflation.util;

import java.beans.PropertyDescriptor;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class DescriptorCache {
    /**
     * All PropertyDescriptor
     */
    private PropertyDescriptor[] propertyDescriptors;

    /**
     * Mapping PropertyDescriptor for accelerate
     */
    private Map<String, PropertyDescriptor> propertyDescriptorMap;

}

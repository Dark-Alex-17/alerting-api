package com.alexjclarke.alerting.api;

import java.beans.FeatureDescriptor;
import java.util.List;
import java.util.stream.Stream;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

public final class PropertyCopier {
    private PropertyCopier() {
    }

    public static void copyNonNullProperties(final Object source, final Object target, final List<String> ignoredProperties) {
        BeanUtils.copyProperties(source, target, getNullPropertyNames(source, ignoredProperties));
    }

    private static String[] getNullPropertyNames(final Object source, final List<String> ignoredProperties) {
        final BeanWrapper wrappedSource = new BeanWrapperImpl(source);
        return Stream.of(wrappedSource.getPropertyDescriptors())
                     .map(FeatureDescriptor::getName)
                     .filter(propertyName -> wrappedSource.getPropertyValue(propertyName) == null || (ignoredProperties != null && ignoredProperties.contains(propertyName)))
                     .toArray(String[]::new);
    }
}

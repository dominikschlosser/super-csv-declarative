/*
 * Copyright 2016 Dominik Schlosser.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.dkschlos.supercsv.model;

import com.github.dkschlos.supercsv.io.declarative.annotation.CsvAccessType;
import com.github.dkschlos.supercsv.io.declarative.annotation.CsvAccessorType;
import com.github.dkschlos.supercsv.io.declarative.annotation.CsvMappingMode;
import com.github.dkschlos.supercsv.io.declarative.annotation.CsvMappingModeType;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Meta-Description of a SuperCSV-bean
 *
 * @since 3.0.0
 * @author Dominik Schlosser
 */
public class BeanDescriptor {

    private static final Map<Class<?>, BeanDescriptor> CACHE = new ConcurrentHashMap<Class<?>, BeanDescriptor>();

    private final Class<?> beanType;
    private final CsvAccessType accessType;
    private final CsvMappingModeType mappingMode;

    private BeanDescriptor(Class<?> beanType, CsvAccessType accessType, CsvMappingModeType mappingMode) {
        this.beanType = beanType;
        this.accessType = accessType;
        this.mappingMode = mappingMode;
    }

    public static BeanDescriptor create(Class<?> clazz) {
        if (CACHE.containsKey(clazz)) {
            return CACHE.get(clazz);
        }

        CsvAccessType accessType = getAccessType(clazz);
        CsvMappingModeType mappingMode = getMappingMode(clazz);

        BeanDescriptor beanDescriptor = new BeanDescriptor(clazz, accessType, mappingMode);
        CACHE.put(clazz, beanDescriptor);
        return beanDescriptor;
    }

    public Class<?> getBeanType() {
        return beanType;
    }

    public CsvAccessType getAccessType() {
        return accessType;
    }

    public CsvMappingModeType getMappingMode() {
        return mappingMode;
    }

    private static CsvAccessType getAccessType(Class<?> clazz) {
        CsvAccessorType accessorTypeAnnotation = clazz.getAnnotation(CsvAccessorType.class);
        if (accessorTypeAnnotation != null) {
            return accessorTypeAnnotation.value();
        }

        return CsvAccessType.PROPERTY;
    }

    private static CsvMappingModeType getMappingMode(Class<?> clazz) {
        CsvMappingMode mappingModeAnnotation = clazz.getAnnotation(CsvMappingMode.class);
        if (mappingModeAnnotation != null) {
            return mappingModeAnnotation.value();
        }

        return CsvMappingModeType.STRICT;
    }
}

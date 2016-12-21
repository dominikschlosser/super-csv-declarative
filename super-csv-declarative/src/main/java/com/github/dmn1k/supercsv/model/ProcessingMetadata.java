/*
 * Copyright 2016 dominik.
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

package com.github.dmn1k.supercsv.model;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

/**
 * Metadata to describe a field with SuperCSV-Declarative annotation(s)
 * 
 *
 * @param <T> The annotation type
 * @since 3.0.0
 * @author Dominik Schlosser
 */
public class ProcessingMetadata<T extends Annotation> {
    private final T annotation;
    private final Field field;
    private final BeanDescriptor beanDescriptor;

    public ProcessingMetadata(T annotation, Field field, BeanDescriptor beanDescriptor) {
        this.annotation = annotation;
        this.field = field;
        this.beanDescriptor = beanDescriptor;
    }

    public T getAnnotation() {
        return annotation;
    }

    public Field getField() {
        return field;
    }

    public BeanDescriptor getBeanDescriptor() {
        return beanDescriptor;
    }

    
    
}

/*
 * Copyright 2007 Kasper B. Graversen
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.dkschlos.supercsv.io.declarative;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.github.dkschlos.supercsv.io.declarative.provider.CellProcessorFactory;
import com.github.dkschlos.supercsv.io.declarative.provider.DeclarativeCellProcessorProvider;
import com.github.dkschlos.supercsv.util.ReflectionUtilsExt;
import org.supercsv.cellprocessor.CellProcessorAdaptor;
import org.supercsv.cellprocessor.ParseEnum;
import org.supercsv.cellprocessor.ift.BoolCellProcessor;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.cellprocessor.ift.DateCellProcessor;
import org.supercsv.cellprocessor.ift.DoubleCellProcessor;
import org.supercsv.cellprocessor.ift.LongCellProcessor;
import org.supercsv.cellprocessor.ift.StringCellProcessor;
import org.supercsv.exception.SuperCsvReflectionException;
import org.supercsv.util.CsvContext;
import com.github.dkschlos.supercsv.util.Form;

/**
 * Extracts all cellprocessor from all fields of the provided class
 *
 * @since 2.5
 * @author Dominik Schlosser
 */
class BeanCellProcessorExtractor {

    private Map<CacheKey, List<CellProcessor>> CELL_PROCESSOR_CACHE = new HashMap<CacheKey, List<CellProcessor>>();

    private Map<Class<?>, CellProcessor> defaultProcessors;

    /**
     * Constructor without default processors
     */
    public BeanCellProcessorExtractor() {
        this(new HashMap<Class<?>, CellProcessor>());
    }

    /**
     * Constructor that gets a map with default processors
     *
     * @param defaultProcessors default processors which are used when no {@link CellProcessorAnnotationDescriptor}
     * -annotations can be found on a field
     */
    public BeanCellProcessorExtractor(Map<Class<?>, CellProcessor> defaultProcessors) {
        this.defaultProcessors = defaultProcessors;
    }

    /**
     * Extracts all cell processors from all fields of the provided class, including all superclass-fields
     *
     * @param the class to extract processors from
     * @return all found cell processors
     */
    public <T> List<CellProcessor> getCellProcessors(Class<T> clazz, String context) {
        CacheKey cacheKey = new CacheKey(clazz, context);
        if (CELL_PROCESSOR_CACHE.containsKey(cacheKey)) {
            return CELL_PROCESSOR_CACHE.get(cacheKey);
        }

        List<CellProcessor> cellProcessors = new ArrayList<CellProcessor>();
        for (Field field : FieldExtractor.getFields(clazz)) {
            cellProcessors.add(createCellProcessorFor(field, context));
        }

        CELL_PROCESSOR_CACHE.put(cacheKey, cellProcessors);

        return cellProcessors;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private CellProcessor createCellProcessorFor(Field field, String context) {
        List<Annotation> annotations = Arrays.asList(field.getAnnotations());
        Collections.reverse(annotations);

        List<CellProcessorFactory> factories = new ArrayList<CellProcessorFactory>();
        CellProcessor root = new Transient();
        boolean foundCellProcessorAnnotation = false;

        for (Annotation annotation : annotations) {
            CellProcessorAnnotationDescriptor cellProcessorMarker = annotation
                    .annotationType().getAnnotation(CellProcessorAnnotationDescriptor.class);
            if (cellProcessorMarker != null && Arrays.asList(cellProcessorMarker.contexts()).contains(context)) {
                DeclarativeCellProcessorProvider provider = ReflectionUtilsExt.instantiateBean(cellProcessorMarker
                        .provider());
                if (!provider.getType().isAssignableFrom(annotation.getClass())) {
                    throw new SuperCsvReflectionException(
                            Form.at(
                                    "Provider declared in annotation of type '{}' cannot be used since accepted annotation-type is not compatible",
                                    annotation.getClass().getName()));
                }

                factories.add(provider.create(annotation));
                foundCellProcessorAnnotation = true;
            }
        }

        if (!foundCellProcessorAnnotation) {
            return mapFieldToDefaultProcessor(field);
        }

        Collections.sort(factories, new OrderComparator());

        for (CellProcessorFactory factory : factories) {
            root = factory.create(root);
        }

        return root;
    }

    @SuppressWarnings("unchecked")
    private CellProcessor mapFieldToDefaultProcessor(Field field) {
        if (field.getType().isEnum()) {
            return new ParseEnum((Class<? extends Enum<?>>) field.getType());
        }

        CellProcessor cellProcessor = defaultProcessors.get(field.getType());
        if (cellProcessor == null) {
            return new Transient();
        }

        return cellProcessor;
    }

    private static final class OrderComparator implements Comparator<CellProcessorFactory> {

        public int compare(CellProcessorFactory o1, CellProcessorFactory o2) {
            return o2.getIndex() - o1.getIndex();
        }
    }

    private static class Transient extends CellProcessorAdaptor implements LongCellProcessor, DoubleCellProcessor,
            StringCellProcessor, DateCellProcessor, BoolCellProcessor {

        public <T> T execute(Object value, CsvContext context) {
            return next.execute(value, context);
        }

    }

    private static class CacheKey {

        private Class<?> fieldClass;
        private String context;

        public CacheKey(Class<?> fieldClass, String context) {
            this.fieldClass = fieldClass;
            this.context = context;
        }

        public Class<?> getFieldClass() {
            return fieldClass;
        }

        public String getContext() {
            return context;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((context == null) ? 0 : context.hashCode());
            result = prime * result + ((fieldClass == null) ? 0 : fieldClass.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            CacheKey other = (CacheKey) obj;
            if (context == null) {
                if (other.context != null) {
                    return false;
                }
            } else if (!context.equals(other.context)) {
                return false;
            }
            if (fieldClass == null) {
                if (other.fieldClass != null) {
                    return false;
                }
            } else if (!fieldClass.equals(other.fieldClass)) {
                return false;
            }
            return true;
        }

    }
}

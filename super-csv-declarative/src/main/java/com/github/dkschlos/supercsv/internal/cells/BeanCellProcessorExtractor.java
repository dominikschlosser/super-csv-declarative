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
package com.github.dkschlos.supercsv.internal.cells;

import com.github.dkschlos.supercsv.internal.util.Form;
import com.github.dkschlos.supercsv.internal.util.ReflectionUtilsExt;
import com.github.dkschlos.supercsv.io.declarative.CellProcessorAnnotationDescriptor;
import com.github.dkschlos.supercsv.io.declarative.provider.CellProcessorFactory;
import com.github.dkschlos.supercsv.io.declarative.provider.DeclarativeCellProcessorProvider;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.supercsv.cellprocessor.CellProcessorAdaptor;
import org.supercsv.cellprocessor.ParseBigDecimal;
import org.supercsv.cellprocessor.ParseBool;
import org.supercsv.cellprocessor.ParseChar;
import org.supercsv.cellprocessor.ParseDouble;
import org.supercsv.cellprocessor.ParseEnum;
import org.supercsv.cellprocessor.ParseInt;
import org.supercsv.cellprocessor.ParseLong;
import org.supercsv.cellprocessor.ift.BoolCellProcessor;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.cellprocessor.ift.DateCellProcessor;
import org.supercsv.cellprocessor.ift.DoubleCellProcessor;
import org.supercsv.cellprocessor.ift.LongCellProcessor;
import org.supercsv.cellprocessor.ift.StringCellProcessor;
import org.supercsv.exception.SuperCsvReflectionException;
import org.supercsv.util.CsvContext;

/**
 * Extracts all cellprocessor from all fields of the provided class
 *
 * @since 2.5
 * @author Dominik Schlosser
 */
final class BeanCellProcessorExtractor {

    private static final Map<Class<?>, CellProcessor> DEFAULT_PROCESSORS = new HashMap<Class<?>, CellProcessor>();

    static {
        DEFAULT_PROCESSORS.put(BigDecimal.class, new ParseBigDecimal());
        DEFAULT_PROCESSORS.put(Boolean.class, new ParseBool());
        DEFAULT_PROCESSORS.put(boolean.class, new ParseBool());
        DEFAULT_PROCESSORS.put(Character.class, new ParseChar());
        DEFAULT_PROCESSORS.put(char.class, new ParseChar());
        DEFAULT_PROCESSORS.put(Double.class, new ParseDouble());
        DEFAULT_PROCESSORS.put(double.class, new ParseDouble());
        DEFAULT_PROCESSORS.put(Integer.class, new ParseInt());
        DEFAULT_PROCESSORS.put(int.class, new ParseInt());
        DEFAULT_PROCESSORS.put(Long.class, new ParseLong());
        DEFAULT_PROCESSORS.put(long.class, new ParseLong());
    }

    private BeanCellProcessorExtractor() {
        // no instances allowed
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static CellProcessor createCellProcessorFor(Field field, String context) {
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
    private static CellProcessor mapFieldToDefaultProcessor(Field field) {
        if (field.getType().isEnum()) {
            return new ParseEnum((Class<? extends Enum<?>>) field.getType());
        }

        CellProcessor cellProcessor = DEFAULT_PROCESSORS.get(field.getType());
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
}

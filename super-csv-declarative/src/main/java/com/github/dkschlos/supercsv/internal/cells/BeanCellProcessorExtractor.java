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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.supercsv.cellprocessor.CellProcessorAdaptor;
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

    private BeanCellProcessorExtractor() {
        // no instances allowed
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static CellProcessor createCellProcessorFor(Field field, String context) {
        List<Annotation> annotations = Arrays.asList(field.getAnnotations());
        Collections.reverse(annotations);

        List<CellProcessorDefinition> factories = new ArrayList<CellProcessorDefinition>();

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

                factories.add(new CellProcessorDefinition(provider.create(annotation), cellProcessorMarker));
            }
        }

        Collections.sort(factories, new OrderComparator());

        return buildProcessorChain(factories);
    }

    private static CellProcessor buildProcessorChain(List<CellProcessorDefinition> definitions) {
        CellProcessor root = new Transient();

        for (CellProcessorDefinition definition : definitions) {
            root = definition.getFactory().create(root);
        }
        return root;
    }

    private static final class OrderComparator implements Comparator<CellProcessorDefinition> {

        @Override
        public int compare(CellProcessorDefinition o1, CellProcessorDefinition o2) {
            return o2.getFactory().getIndex() - o1.getFactory().getIndex();
        }
    }

    private static class Transient extends CellProcessorAdaptor implements LongCellProcessor, DoubleCellProcessor,
            StringCellProcessor, DateCellProcessor, BoolCellProcessor {

        @Override
        public <T> T execute(Object value, CsvContext context) {
            return next.execute(value, context);
        }

    }

    private static class CellProcessorDefinition {

        private final CellProcessorFactory factory;
        private final CellProcessorAnnotationDescriptor descriptor;

        public CellProcessorDefinition(CellProcessorFactory factory, CellProcessorAnnotationDescriptor descriptor) {
            this.factory = factory;
            this.descriptor = descriptor;
        }

        public CellProcessorFactory getFactory() {
            return factory;
        }

        public CellProcessorAnnotationDescriptor getDescriptor() {
            return descriptor;
        }

    }
}

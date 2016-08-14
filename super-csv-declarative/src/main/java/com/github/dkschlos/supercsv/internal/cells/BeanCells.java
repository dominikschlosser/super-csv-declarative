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
import com.github.dkschlos.supercsv.io.declarative.CsvField;
import com.github.dkschlos.supercsv.io.declarative.annotation.CsvAccessType;
import com.github.dkschlos.supercsv.io.declarative.annotation.CsvAccessorType;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.exception.SuperCsvException;

public final class BeanCells {

    private static final Map<CacheKey, BeanCells> FIELD_CACHE = new HashMap<CacheKey, BeanCells>();

    private final Map<Integer, BeanCell> mappedFields;

    private BeanCells(Map<Integer, BeanCell> mappedFields) {
        this.mappedFields = mappedFields;
    }

    /**
     * Returns all fields of the given class including those of superclasses.
     *
     * @param clazz the class to get the fields of
     * @return all fields of the class and its hierarchy
     */
    public static BeanCells getFields(Class<?> clazz, String context) {
        CacheKey cacheKey = new CacheKey(clazz, context);
        if (FIELD_CACHE.containsKey(cacheKey)) {
            return FIELD_CACHE.get(cacheKey);
        }
        FieldExtractor fieldExtractor = new FieldExtractor(clazz);
        List<Field> fields = fieldExtractor.getFields();

        CsvAccessType accessType = getAccessType(clazz);
        BeanCells result = null;
        Map<Integer, BeanCell> fieldsByExplicitIndex = getFieldsByExplicitIndex(fields, accessType, context);
        if (fieldsByExplicitIndex.isEmpty()) {
            Map<Integer, BeanCell> fieldsByImplicitIndex = getFieldsByImplicitIndex(fields, accessType, context);
            result = new BeanCells(fieldsByImplicitIndex);
        } else {
            result = new BeanCells(fieldsByExplicitIndex);
        }

        FIELD_CACHE.put(cacheKey, result);
        return result;
    }

    public BeanCell getCell(int index) {
        BeanCell mapped = mappedFields.get(index);
        if (mapped != null) {
            return mapped;
        }

        NullBeanCell nullFieldWrapper = new NullBeanCell();
        mappedFields.put(index, nullFieldWrapper);

        return nullFieldWrapper;
    }

    public List<BeanCell> getAll() {
        return new ArrayList<BeanCell>(mappedFields.values());
    }

    private static Map<Integer, BeanCell> getFieldsByExplicitIndex(List<Field> fields, CsvAccessType accessType, String context) {
        Map<Integer, BeanCell> result = new HashMap<Integer, BeanCell>();
        for (Field field : fields) {
            CsvField fieldAnnotation = field.getAnnotation(CsvField.class);
            if (fieldAnnotation != null) {
                if (result.containsKey(fieldAnnotation.index())) {
                    throw new SuperCsvException(Form.at("Explicit order-index {} was declared twice (Field: {}", fieldAnnotation.index(),
                            field.getName()));
                }

                CellProcessor cellProcessor = BeanCellProcessorExtractor.createCellProcessorFor(field, context);
                FieldAccessStrategy fieldAccessStrategy = createFieldAccessStrategy(accessType);
                result.put(fieldAnnotation.index(), new ExistingBeanCell(field, cellProcessor, fieldAccessStrategy));
            }
        }

        return result;
    }

    private static Map<Integer, BeanCell> getFieldsByImplicitIndex(List<Field> fields, CsvAccessType accessType, String context) {
        Map<Integer, BeanCell> result = new HashMap<Integer, BeanCell>();
        for (int i = 0; i < fields.size(); i++) {
            Field field = fields.get(i);
            CellProcessor cellProcessor = BeanCellProcessorExtractor.createCellProcessorFor(field, context);
            FieldAccessStrategy fieldAccessStrategy = createFieldAccessStrategy(accessType);
            result.put(i, new ExistingBeanCell(field, cellProcessor, fieldAccessStrategy));
        }

        return result;
    }

    private static CsvAccessType getAccessType(Class<?> clazz) {
        CsvAccessorType accessorTypeAnnotation = clazz.getAnnotation(CsvAccessorType.class);
        if (accessorTypeAnnotation != null) {
            return accessorTypeAnnotation.value();
        }

        return CsvAccessType.PROPERTY;
    }

    private static FieldAccessStrategy createFieldAccessStrategy(CsvAccessType type) {
        return CsvAccessType.FIELD.equals(type) ? new DirectFieldAccessStrategy() : new PropertyFieldAccessStrategy();
    }

    private static class CacheKey {

        private final Class<?> fieldClass;
        private final String context;

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

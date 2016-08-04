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
package com.github.dkschlos.supercsv.internal.fields;

import com.github.dkschlos.supercsv.io.declarative.CsvField;
import com.github.dkschlos.supercsv.internal.util.Form;
import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Ordering;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.supercsv.exception.SuperCsvException;

final class FieldSorter {
    private FieldSorter() {
        // no instances allowed
    }

    static List<Field> sort(List<Field> fields) {
        final Map<Field, Integer> order = getFieldOrder(fields);

        Ordering<Field> ordering = Ordering.natural().onResultOf(new Function<Field, Integer>() {

            public Integer apply(Field field) {
                return order.get(field);
            }
        });

        return ordering.immutableSortedCopy(fields);
    }

    private static Map<Field, Integer> getFieldOrder(List<Field> fields) {
        final BiMap<Field, Integer> order = HashBiMap.create();
        BiMap<Integer, Field> inverse = order.inverse();

        List<Field> fieldsWithoutCsvFieldAnnotation = new ArrayList<Field>();
        for (int i = 0; i < fields.size(); i++) {
            Field field = fields.get(i);
            int orderVal = i;
            CsvField fieldAnnotation = field.getAnnotation(CsvField.class);
            if (fieldAnnotation != null) {
                orderVal = fieldAnnotation.index();
            } else {
                fieldsWithoutCsvFieldAnnotation.add(field);
            }

            if (inverse.containsKey(orderVal)) {
                throw new SuperCsvException(Form.at("Explicit order-index {} was declared twice (Field: {}", orderVal,
                        field.getName()));
            }

            order.put(field, orderVal);
        }

        if (!fieldsWithoutCsvFieldAnnotation.isEmpty() && fieldsWithoutCsvFieldAnnotation.size() < fields.size()) {
            String missingFields = Joiner.on(", ").join(fieldsWithoutCsvFieldAnnotation);
            throw new SuperCsvException(
                    Form.at(
                            "If you use @CsvField to explicitly define field-order, you have to do it on all fields. Missing on: {}",
                            missingFields));
        }
        return order;
    }
}

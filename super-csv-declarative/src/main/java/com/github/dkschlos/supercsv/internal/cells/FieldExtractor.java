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

import com.github.dkschlos.supercsv.io.declarative.CsvField;
import com.github.dkschlos.supercsv.io.declarative.CsvTransient;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

final class FieldExtractor {

    private static final Logger LOGGER = LoggerFactory.getLogger(FieldExtractor.class);

    private final Class<?> clazz;
    private final List<Field> withCsvFieldAnnotation = new ArrayList<Field>();
    private final List<Field> withoutCsvFieldAnnotation = new ArrayList<Field>();

    public FieldExtractor(Class<?> clazz) {
        this.clazz = clazz;
    }

    List<Field> getFields() {
        withCsvFieldAnnotation.clear();
        withoutCsvFieldAnnotation.clear();

        extractFields(clazz);

        if (withCsvFieldAnnotation.isEmpty()) {
            return withoutCsvFieldAnnotation;
        }

        if (!withoutCsvFieldAnnotation.isEmpty()) {
            List<String> ignoredFieldNames = new ArrayList<String>();
            for (Field withoutAnnotation : withoutCsvFieldAnnotation) {
                ignoredFieldNames.add(withoutAnnotation.getName());
            }
            LOGGER.warn("You used @CsvField somewhere in the type hierarchy of {} but there are fields without it."
                    + " Those fields will be ignored by SuperCSV: {}", clazz.getName(), String.join(", ", ignoredFieldNames));
        }

        return withCsvFieldAnnotation;
    }

    private void extractFields(Class<?> clazz) {
        if (clazz.getSuperclass() != Object.class) {
            extractFields(clazz.getSuperclass());
        }

        for (Field field : clazz.getDeclaredFields()) {
            if (field.getAnnotation(CsvTransient.class) == null && !Modifier.isStatic(field.getModifiers())) {
                if (field.getAnnotation(CsvField.class) == null) {
                    withoutCsvFieldAnnotation.add(field);
                } else {
                    withCsvFieldAnnotation.add(field);
                }
            }
        }
    }
}

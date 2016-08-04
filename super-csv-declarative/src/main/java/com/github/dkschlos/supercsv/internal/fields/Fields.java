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

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class Fields {

    private static final Map<Class<?>, List<Field>> FIELD_CACHE = new HashMap<Class<?>, List<Field>>();

    private Fields() {
        // no instances allowed
    }

    /**
     * Returns all fields of the given class including those of superclasses.
     *
     * @param clazz the class to get the fields of
     * @return all fields of the class and its hierarchy
     */
    public static List<Field> getFields(Class<?> clazz) {
        if (FIELD_CACHE.containsKey(clazz)) {
            return FIELD_CACHE.get(clazz);
        }
        FieldExtractor fieldExtractor = new FieldExtractor(clazz);
        List<Field> fields = fieldExtractor.getFields();
        List<Field> orderedFields = FieldSorter.sort(fields);

        FIELD_CACHE.put(clazz, orderedFields);
        return orderedFields;
    }
}

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
package com.github.dkschlos.supercsv.internal.cells;

import com.github.dkschlos.supercsv.internal.util.AbstractForm;
import java.lang.reflect.Field;
import org.supercsv.exception.SuperCsvReflectionException;

/**
 *
 * @author Dominik Schlosser
 */
public class DirectFieldAccessStrategy implements FieldAccessStrategy {

    @Override
    public void setValue(Field field, Object obj, Object value) {
        try {
            field.setAccessible(true);
            field.set(obj, value);
        } catch (IllegalAccessException e) {
            throw new SuperCsvReflectionException(AbstractForm.at("Cannot set value on field '{}'", field.getName()), e);
        }
    }

    @Override
    public Object getValue(Field field, Object obj) {
        try {
            field.setAccessible(true);
            return field.get(obj);
        } catch (IllegalAccessException e) {
            throw new SuperCsvReflectionException(AbstractForm.at("Error extracting bean value for field {}",
                    field.getName()), e);
        }
    }
}

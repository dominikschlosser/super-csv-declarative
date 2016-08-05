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

import com.github.dkschlos.supercsv.internal.util.Form;
import java.lang.reflect.Field;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.exception.SuperCsvReflectionException;

class BeanFieldWrapper implements FieldWrapper {

    private final Field field;
    private CellProcessor cellProcessor;

    public BeanFieldWrapper(Field field, CellProcessor cellProcessor) {
        this.field = field;
        this.cellProcessor = cellProcessor;
    }

    @Override
    public CellProcessor getCellProcessor() {
        return cellProcessor;
    }



    @Override
    public void setValue(Object obj, Object value) {
        try {
            field.setAccessible(true);
            field.set(obj, value);
        } catch (IllegalAccessException e) {
            throw new SuperCsvReflectionException(Form.at("Cannot set value on field '{}'", field.getName()), e);
        }
    }

    @Override
    public Object getValue(Object obj) {
        try {
            field.setAccessible(true);
            return field.get(obj);
        } catch (IllegalAccessException e) {
            throw new SuperCsvReflectionException(Form.at("Error extracting bean value for field {}",
                    field.getName()), e);
        }
    }

    @Override
    public Class<?> getType() {
        return field.getType();
    }


}

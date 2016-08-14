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

import java.lang.reflect.Field;
import org.supercsv.cellprocessor.ift.CellProcessor;

class ExistingBeanCell implements BeanCell {

    private FieldAccessStrategy fieldAccessStrategy;
    private final Field field;
    private final CellProcessor cellProcessor;

    public ExistingBeanCell(Field field, CellProcessor cellProcessor, FieldAccessStrategy fieldAccessStrategy) {
        this.field = field;
        this.cellProcessor = cellProcessor;
        this.fieldAccessStrategy = fieldAccessStrategy;
    }

    @Override
    public CellProcessor getProcessor() {
        return cellProcessor;
    }

    @Override
    public void setValue(Object obj, Object value) {
        fieldAccessStrategy.setValue(field, obj, value);
    }

    @Override
    public Object getValue(Object obj) {
        return fieldAccessStrategy.getValue(field, obj);
    }

    @Override
    public Class<?> getType() {
        return field.getType();
    }

}

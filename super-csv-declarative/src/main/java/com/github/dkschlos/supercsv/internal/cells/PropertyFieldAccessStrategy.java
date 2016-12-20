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

import com.github.dkschlos.supercsv.internal.util.Form;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.supercsv.exception.SuperCsvReflectionException;

/**
 *
 * @author Dominik Schlosser
 */
public class PropertyFieldAccessStrategy implements FieldAccessStrategy {

    private Method readMethod;
    private Method writeMethod;

    @Override
    public Object getValue(Field field, Object obj) {
        try {
            Method method = getReadMethod(field, obj);
            return method.invoke(obj);
        } catch (IntrospectionException | IllegalAccessException | InvocationTargetException e) {
            throw new SuperCsvReflectionException(Form.at("Error extracting bean value via getter for field {}",
                    field.getName()), e);
        }
    }

    @Override
    public void setValue(Field field, Object obj, Object value) {
        try {
            Method method = getWriteMethod(field, obj);
            method.invoke(obj, value);
        } catch (IntrospectionException | IllegalAccessException | InvocationTargetException e) {
            throw new SuperCsvReflectionException(Form.at("Cannot set value via setter on field '{}'", field.getName()), e);
        }
    }

    private Method getReadMethod(Field field, Object obj) throws IntrospectionException {
        if (readMethod != null) {
            return readMethod;
        }

        readMethod = new PropertyDescriptor(field.getName(), obj.getClass()).getReadMethod();
        return readMethod;
    }

    private Method getWriteMethod(Field field, Object obj) throws IntrospectionException {
        if (writeMethod != null) {
            return writeMethod;
        }

        writeMethod = new PropertyDescriptor(field.getName(), obj.getClass()).getWriteMethod();
        return writeMethod;
    }
}

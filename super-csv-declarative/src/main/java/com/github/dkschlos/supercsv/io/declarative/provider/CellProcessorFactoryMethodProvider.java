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
package com.github.dkschlos.supercsv.io.declarative.provider;

import com.github.dkschlos.supercsv.internal.util.Form;
import com.github.dkschlos.supercsv.io.declarative.annotation.CellProcessorFactoryMethod;
import com.github.dkschlos.supercsv.model.CellProcessorFactory;
import com.github.dkschlos.supercsv.model.DeclarativeCellProcessorProvider;
import com.github.dkschlos.supercsv.model.ProcessingMetadata;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.exception.SuperCsvReflectionException;

public class CellProcessorFactoryMethodProvider implements DeclarativeCellProcessorProvider<CellProcessorFactoryMethod> {

    /**
     * {@inheritDoc}
     */
    @Override
    public CellProcessorFactory create(ProcessingMetadata<CellProcessorFactoryMethod> metadata) {
        return new CellProcessorFactory() {

            @Override
            public int getOrder() {
                return metadata.getAnnotation().order();
            }

            @Override
            public CellProcessor create(CellProcessor next) {
                CellProcessorFactoryMethod annotation = metadata.getAnnotation();
                Class<?> type = annotation.type().equals(CellProcessorFactoryMethod.DeclaredType.class) ? metadata.getBeanDescriptor().getBeanType() : annotation.type();
                try {
                    Method method = type.getDeclaredMethod(annotation.methodName(), CellProcessor.class);
                    return (CellProcessor) method.invoke(null, next);
                } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException ex) {
                    throw new SuperCsvReflectionException(Form.at("Can not find CellProcessorFactoryMethod '{}' - it needs to be static, defined in '{}' and accept exactly one parameter of type CellProcessor!",
                            annotation.methodName(), type.getName()), ex);
                }
            }
        };
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<CellProcessorFactoryMethod> getType() {
        return CellProcessorFactoryMethod.class;
    }

}

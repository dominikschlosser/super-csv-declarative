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
package com.github.dkschlos.supercsv.io.declarative.constraint.provider;

import com.github.dkschlos.supercsv.io.declarative.constraint.annotation.StrNotNullOrEmpty;
import com.github.dkschlos.supercsv.model.CellProcessorFactory;
import com.github.dkschlos.supercsv.model.ProcessingMetadata;
import com.github.dkschlos.supercsv.model.DeclarativeCellProcessorProvider;
import org.supercsv.cellprocessor.ift.CellProcessor;

/**
 * CellProcessorProvider for StrNotNullOrEmpty
 *
 * @since 2.5
 * @author Dominik Schlosser
 */
public class StrNotNullOrEmptyCellProcessorProvider implements DeclarativeCellProcessorProvider<StrNotNullOrEmpty> {

    /**
     * {@inheritDoc}
     */
    @Override
    public CellProcessorFactory create(ProcessingMetadata<StrNotNullOrEmpty> metadata) {
        return new CellProcessorFactory() {

            @Override
            public int getOrder() {
                return metadata.getAnnotation().order();
            }

            @Override
            public CellProcessor create(CellProcessor next) {
                return new org.supercsv.cellprocessor.constraint.StrNotNullOrEmpty(next);
            }
        };
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<StrNotNullOrEmpty> getType() {
        return StrNotNullOrEmpty.class;
    }

}

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

import com.github.dkschlos.supercsv.io.declarative.constraint.annotation.StrRegEx;
import com.github.dkschlos.supercsv.io.declarative.provider.DeclarativeCellProcessorProvider;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.cellprocessor.ift.StringCellProcessor;
import com.github.dkschlos.supercsv.io.declarative.provider.CellProcessorFactory;

/**
 * CellProcessorProvider for StrRegEx
 *
 * @since 2.5
 * @author Dominik Schlosser
 */
public class StrRegExCellProcessorProvider implements DeclarativeCellProcessorProvider<StrRegEx> {

    /**
     * {@inheritDoc}
     */
    public CellProcessorFactory create(final StrRegEx annotation) {
        return new CellProcessorFactory() {

            public int getIndex() {
                return annotation.index();
            }

            public CellProcessor create(CellProcessor next) {
                return new org.supercsv.cellprocessor.constraint.StrRegEx(annotation.regex(),
                        (StringCellProcessor) next);
            }
        };
    }

    /**
     * {@inheritDoc}
     */
    public Class<StrRegEx> getType() {
        return StrRegEx.class;
    }

}

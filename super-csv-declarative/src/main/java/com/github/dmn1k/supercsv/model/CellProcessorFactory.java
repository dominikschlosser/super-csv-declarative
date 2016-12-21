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
package com.github.dmn1k.supercsv.model;

import org.supercsv.cellprocessor.ift.CellProcessor;

/**
 * SuperCSV-Declarative defines annotations which point to {@link DeclarativeCellProcessorProvider}s 
 * which create implementations of this interface.
 * 
 *
 * @since 3.0.0
 * @author Dominik Schlosser
 */
public interface CellProcessorFactory {

    /**
     * Creates a concrete {@link CellProcessor}-instance
     * @param next The next processor in the chain
     * 
     * @return 
     */
    CellProcessor create(CellProcessor next);

    /**
     * Where in the chain should this processor be? Is normally defined via an annotation 
     * or implicitly via annotation-order
     * @return 
     */
    int getOrder();
}

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

import org.supercsv.cellprocessor.Optional;
import org.supercsv.cellprocessor.ift.CellProcessor;

class NullFieldWrapper implements FieldWrapper {

    @Override
    public CellProcessor getCellProcessor() {
        return new Optional();
    }


    @Override
    public void setValue(Object obj, Object value) {
        // do nothing
    }

    @Override
    public Object getValue(Object obj) {
        return null;
    }

    @Override
    public Class<?> getType() {
        return void.class;
    }

}

/*
 * Copyright 2016 dominik.
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

package com.github.dkschlos.supercsv.testbeans;

import com.github.dkschlos.supercsv.io.declarative.annotation.CsvAccessType;
import com.github.dkschlos.supercsv.io.declarative.annotation.CsvAccessorType;
import com.github.dkschlos.supercsv.io.declarative.annotation.CsvMappingMode;
import com.github.dkschlos.supercsv.io.declarative.annotation.CsvMappingModeType;
import com.github.dkschlos.supercsv.io.declarative.annotation.Optional;

@CsvMappingMode(CsvMappingModeType.STRICT)
@CsvAccessorType(CsvAccessType.FIELD)
public class BeanWithOptionalFieldAndFieldAccess {
    @Optional
    private java.util.Optional<String> field;

    private String otherField;
    
    public java.util.Optional<String> getField() {
        return field;
    }

    public String getOtherField() {
        return otherField;
    }
    
    
    
}

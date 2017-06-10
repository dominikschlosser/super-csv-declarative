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
package com.github.dmn1k.supercsv.testbeans;

import com.github.dmn1k.supercsv.io.declarative.annotation.CsvAccessType;
import com.github.dmn1k.supercsv.io.declarative.annotation.CsvAccessorType;
import com.github.dmn1k.supercsv.io.declarative.constraint.annotation.UniqueHashCode;

/**
 *
 * @author Dominik Schlosser
 */
@CsvAccessorType(CsvAccessType.FIELD)
public class UniqueHashCodeBean {

    @UniqueHashCode
    private String content;

    public UniqueHashCodeBean() {

    }

    public UniqueHashCodeBean(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    

}

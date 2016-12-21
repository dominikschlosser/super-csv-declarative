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
package com.github.dmn1k.supercsv.testbeans;

import com.github.dmn1k.supercsv.io.declarative.CsvField;
import com.github.dmn1k.supercsv.io.declarative.annotation.CsvAccessType;
import com.github.dmn1k.supercsv.io.declarative.annotation.CsvAccessorType;
import com.github.dmn1k.supercsv.io.declarative.annotation.CsvMappingMode;
import com.github.dmn1k.supercsv.io.declarative.annotation.CsvMappingModeType;
import com.github.dmn1k.supercsv.io.declarative.annotation.Trim;

/**
 * Test class for declarative mapping
 *
 * @since 2.5
 * @author Dominik Schlosser
 */
@CsvMappingMode(CsvMappingModeType.STRICT)
@CsvAccessorType(CsvAccessType.FIELD)
public class StrictBeanWithPartialColumnMapping {

    @Trim
    @CsvField(index = 1)
    private String lastName;

    @CsvField(index = 2)
    private int age;

    public StrictBeanWithPartialColumnMapping() {
    }

    public StrictBeanWithPartialColumnMapping(String lastName, int age) {
        this.lastName = lastName;
        this.age = age;
    }

    public String getLastName() {
        return lastName;
    }

    public int getAge() {
        return age;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 73 * hash + (this.lastName != null ? this.lastName.hashCode() : 0);
        hash = 73 * hash + this.age;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final StrictBeanWithPartialColumnMapping other = (StrictBeanWithPartialColumnMapping) obj;
        if (this.age != other.age) {
            return false;
        }
        if ((this.lastName == null) ? (other.lastName != null) : !this.lastName.equals(other.lastName)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "StrictBeanWithPartialColumnMapping{" + "lastName=" + lastName + ", age=" + age + '}';
    }

}

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
package com.github.dkschlos.supercsv.testbeans.order;

import com.github.dkschlos.supercsv.io.declarative.CsvField;
import com.github.dkschlos.supercsv.io.declarative.annotation.Optional;
import com.github.dkschlos.supercsv.io.declarative.annotation.Trim;
import com.github.dkschlos.supercsv.io.declarative.annotation.Truncate;

/**
 * Test class for declarative mapping
 *
 * @since 2.5
 * @author Dominik Schlosser
 */
public class BeanWithPartiallyExplicitlyOrderedFields {

    @CsvField(index = 1)
    @Optional
    @Trim
    @Truncate(maxSize = 3)
    private String lastName;

    @CsvField(index = 0)
    @Optional
    @Trim
    private String name;

    @CsvField(index = 2)
    private int age;

    @Optional
    private String toIgnore;

    @CsvField(index = 3)
    private double weight;

    public BeanWithPartiallyExplicitlyOrderedFields() {
    }

    public BeanWithPartiallyExplicitlyOrderedFields(String name, String lastName, int age, double weight) {
        this.name = name;
        this.lastName = lastName;
        this.age = age;
        this.weight = weight;
    }

    public String getName() {
        return name;
    }

    public String getLastName() {
        return lastName;
    }

    public int getAge() {
        return age;
    }

    public double getWeight() {
        return weight;
    }

    public String getToIgnore() {
        return toIgnore;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + age;
        result = prime * result + ((lastName == null) ? 0 : lastName.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        long temp;
        temp = Double.doubleToLongBits(weight);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        return result;
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
        BeanWithPartiallyExplicitlyOrderedFields other = (BeanWithPartiallyExplicitlyOrderedFields) obj;
        if (age != other.age) {
            return false;
        }
        if (lastName == null) {
            if (other.lastName != null) {
                return false;
            }
        } else if (!lastName.equals(other.lastName)) {
            return false;
        }
        if (name == null) {
            if (other.name != null) {
                return false;
            }
        } else if (!name.equals(other.name)) {
            return false;
        }
        return Double.doubleToLongBits(weight) == Double.doubleToLongBits(other.weight);
    }

    @Override
    public String toString() {
        return "SimpleBeanWithSimpleAnnotations [name=" + name + ", lastName=" + lastName + ", age=" + age
                + ", weight=" + weight + "]";
    }

}

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
package com.github.dkschlos.supercsv.internal.typeconversion;

import java.util.HashMap;
import java.util.Map;

/**
 * Holds a list of known TypeConverters
 *
 * @author Dominik Schlosser
 */
public class TypeConverterRegistry {

    private Map<RegistryKey, TypeConverter<?, ?>> converters = new HashMap<RegistryKey, TypeConverter<?, ?>>();

    public final <I, O> TypeConverter<I, O> getConverter(Class<I> inputClass, Class<O> outputClass) {
        return (TypeConverter<I, O>) converters.get(new RegistryKey(inputClass, outputClass));
    }

    public final void register(TypeConverter<?, ?> converter, Class<?> inputClass, Class<?> outputClass) {
        converters.put(new RegistryKey(inputClass, outputClass), converter);
        converters.put(new RegistryKey(outputClass, inputClass), converter);
    }

    private static class RegistryKey {

        private final Class<?> inClass;
        private final Class<?> outClass;

        public RegistryKey(Class<?> inClass, Class<?> outClass) {
            this.inClass = inClass;
            this.outClass = outClass;
        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 37 * hash + (this.inClass != null ? this.inClass.hashCode() : 0);
            hash = 37 * hash + (this.outClass != null ? this.outClass.hashCode() : 0);
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
            final RegistryKey other = (RegistryKey) obj;
            if (this.inClass != other.inClass && (this.inClass == null || !this.inClass.equals(other.inClass))) {
                return false;
            }
            if (this.outClass != other.outClass && (this.outClass == null || !this.outClass.equals(other.outClass))) {
                return false;
            }
            return true;
        }

    }
}

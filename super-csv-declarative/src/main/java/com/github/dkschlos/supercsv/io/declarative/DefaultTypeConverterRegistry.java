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
package com.github.dkschlos.supercsv.io.declarative;

import com.github.dkschlos.supercsv.internal.typeconversion.IdentityConverter;
import com.github.dkschlos.supercsv.internal.typeconversion.OptionalConverter;
import com.github.dkschlos.supercsv.internal.typeconversion.StringBigDecimalConverter;
import com.github.dkschlos.supercsv.internal.typeconversion.StringCharConverter;
import com.github.dkschlos.supercsv.internal.typeconversion.StringDoubleConverter;
import com.github.dkschlos.supercsv.internal.typeconversion.StringIntConverter;
import com.github.dkschlos.supercsv.internal.typeconversion.StringLongConverter;
import com.github.dkschlos.supercsv.internal.typeconversion.TypeConverterRegistry;
import java.math.BigDecimal;

/**
 * A TypeConverterRegistry with all default Converters registered
 *
 * @author Dominik Schlosser
 */
public class DefaultTypeConverterRegistry extends TypeConverterRegistry {

    public DefaultTypeConverterRegistry() {
        register(new IdentityConverter(), Object.class, Object.class);
        register(new OptionalConverter(), Object.class, java.util.Optional.class);
        register(new StringIntConverter(), String.class, Integer.class);
        register(new StringIntConverter(), String.class, int.class);
        register(new StringDoubleConverter(), String.class, Double.class);
        register(new StringDoubleConverter(), String.class, double.class);
        register(new StringBigDecimalConverter(), String.class, BigDecimal.class);
        register(new StringLongConverter(), String.class, Long.class);
        register(new StringLongConverter(), String.class, long.class);
        register(new StringCharConverter(), String.class, Character.class);
        register(new StringCharConverter(), String.class, char.class);
    }

}

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
package com.github.dkschlos.supercsv.io.declarative;

import com.github.dkschlos.supercsv.testbeans.BeanForWriteTypeConversion;
import java.io.IOException;
import java.io.StringWriter;

import com.github.dkschlos.supercsv.testbeans.BeanWithSimpleAnnotations;
import com.github.dkschlos.supercsv.testbeans.BeanWithoutAnnotations;
import org.junit.After;
import org.junit.Test;
import org.supercsv.prefs.CsvPreference;
import com.github.dkschlos.supercsv.testbeans.ReadAndWriteBeanWithPropertyAccess;
import com.github.dkschlos.supercsv.testbeans.BeanWithChainedAnnotations;
import com.github.dkschlos.supercsv.testbeans.BeanWithEnum;
import com.github.dkschlos.supercsv.testbeans.BeanWithInheritedProperties;
import com.github.dkschlos.supercsv.testbeans.BeanWithPartialColumnMapping;
import com.github.dkschlos.supercsv.testbeans.TestEnum;
import static org.junit.Assert.assertEquals;

/**
 * @since 2.5
 * @author Dominik Schlosser
 */
public class CsvDeclarativeBeanWriterTest {

    private static final CsvPreference PREFS = CsvPreference.STANDARD_PREFERENCE;

    private final StringWriter result = new StringWriter();
    private CsvDeclarativeBeanWriter beanWriter = new CsvDeclarativeBeanWriter(result, PREFS);

    @After
    public void tearDown() throws IOException {
        if (beanWriter != null) {
            beanWriter.close();
        }
    }

    @Test
    public void writeSimpleBeanWithoutAnnotations() throws IOException {
        BeanWithoutAnnotations john = new BeanWithoutAnnotations("John", "Doe", 42, 100.5);
        BeanWithoutAnnotations max = new BeanWithoutAnnotations("Max", "Mustermann", 22, 21.4);

        beanWriter.write(john);
        beanWriter.write(max);

        assertEquals("John,Doe,42,100.5\r\nMax,Mustermann,22,21.4\r\n", result.toString());
    }

    @Test
    public void writeSimpleBeanWithSimpleAnnotations() throws IOException {
        BeanWithSimpleAnnotations john = new BeanWithSimpleAnnotations(null, "Doe", 42, 100.5);
        BeanWithSimpleAnnotations max = new BeanWithSimpleAnnotations("Max", "Mustermann ", 22, 21.4);

        beanWriter.write(john);
        beanWriter.write(max);

        assertEquals(",Doe,42,100.5\r\nMax,Mustermann,22,21.4\r\n", result.toString());
    }

    @Test
    public void writeSimpleBeanWithChainedAnnotations() throws IOException {
        BeanWithChainedAnnotations john = new BeanWithChainedAnnotations(null, "Doe", 42, 100.5);
        BeanWithChainedAnnotations max = new BeanWithChainedAnnotations("Max", "Mustermann", 22, 21.4);

        beanWriter.write(john);
        beanWriter.write(max);

        assertEquals(",Doe,42,100.5\r\nMax,Mus,22,21.4\r\n", result.toString());
    }

    @Test
    public void writeSimpleBeanWithPartialMapping() throws IOException {
        BeanWithPartialColumnMapping john = new BeanWithPartialColumnMapping("Doe", 42);
        BeanWithPartialColumnMapping max = new BeanWithPartialColumnMapping("Mustermann", 22);

        beanWriter.write(john);
        beanWriter.write(max);

        assertEquals("Doe,42\r\nMustermann,22\r\n", result.toString());
    }

    @Test
    public void writeBeanWithInheritedProperties() throws IOException {
        BeanWithInheritedProperties john = new BeanWithInheritedProperties("John", "Doe", 42, 100.5, "Note 1");
        BeanWithInheritedProperties max = new BeanWithInheritedProperties("Max", "Mustermann", 22, 21.4, "Note 2");

        beanWriter.write(john);
        beanWriter.write(max);

        assertEquals("John,Doe,42,100.5,Note 1\r\nMax,Mustermann,22,21.4,Note 2\r\n", result.toString());
    }

    @Test
    public void writeBeanWithReadAndWriteAnnotations() throws IOException {
        ReadAndWriteBeanWithPropertyAccess beanForReadAndWrite = new ReadAndWriteBeanWithPropertyAccess(false);
        beanWriter = new CsvDeclarativeBeanWriter(result, CsvPreference.STANDARD_PREFERENCE);

        beanWriter.write(beanForReadAndWrite);

        assertEquals("falsch\r\n", result.toString());
    }

    @Test
    public void writeBeanWithAutomaticTypeConversion() throws IOException {
        BeanForWriteTypeConversion bean = new BeanForWriteTypeConversion(42);
        beanWriter = new CsvDeclarativeBeanWriter(result, CsvPreference.STANDARD_PREFERENCE);

        beanWriter.write(bean);

        assertEquals("42\r\n", result.toString());
    }

    @Test
    public void writeBeanWithAutomaticEnumTypeConversion() throws IOException {
        BeanWithEnum bean = new BeanWithEnum(TestEnum.Blubb);
        beanWriter = new CsvDeclarativeBeanWriter(result, CsvPreference.STANDARD_PREFERENCE);

        beanWriter.write(bean);

        assertEquals("Blubb\r\n", result.toString());
    }

    @Test(expected = IllegalArgumentException.class)
    public void writeWithNullBeanClass() throws IOException {
        beanWriter.write(null);
    }

    @SuppressWarnings("resource")
    @Test(expected = NullPointerException.class)
    public void writerConstructorWithNullwriter() {
        new CsvDeclarativeBeanWriter(null, PREFS);
    }

    @SuppressWarnings("resource")
    @Test(expected = NullPointerException.class)
    public void writerConstructorWithNullPreferences() {
        new CsvDeclarativeBeanWriter(new StringWriter(), null);
    }
}

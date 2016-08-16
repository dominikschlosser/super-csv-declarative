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

import com.github.dkschlos.supercsv.internal.typeconversion.TypeConverterRegistry;
import com.github.dkschlos.supercsv.testbeans.BeanForDefaultOverridingTest;
import com.github.dkschlos.supercsv.testbeans.ReadAndWriteBeanWithPropertyAccess;
import com.github.dkschlos.supercsv.testbeans.BeanWithChainedAnnotations;
import com.github.dkschlos.supercsv.testbeans.BeanWithEnum;
import com.github.dkschlos.supercsv.testbeans.BeanWithInheritedProperties;
import com.github.dkschlos.supercsv.testbeans.BeanWithPartialColumnMapping;
import com.github.dkschlos.supercsv.testbeans.BeanWithSimpleAnnotations;
import com.github.dkschlos.supercsv.testbeans.BeanWithoutAnnotations;
import com.github.dkschlos.supercsv.testbeans.BeanWithoutExplicitParseAnnotations;
import com.github.dkschlos.supercsv.testbeans.StrictBeanWithPartialColumnMapping;
import com.github.dkschlos.supercsv.testbeans.TestEnum;
import com.github.dkschlos.supercsv.testbeans.order.BeanWithExplicitlyOrderedAnnotations;
import com.github.dkschlos.supercsv.testbeans.order.BeanWithExplicitlyOrderedFields;
import com.github.dkschlos.supercsv.testbeans.order.BeanWithIllegalExplicitFieldOrder;
import com.github.dkschlos.supercsv.testbeans.order.BeanWithPartiallyExplicitlyOrderedFields;
import com.github.dkschlos.supercsv.testbeans.order.StrictBeanWithPartiallyExplicitlyOrderedFields;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import org.junit.After;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;
import org.supercsv.exception.SuperCsvException;
import org.supercsv.exception.SuperCsvReflectionException;
import org.supercsv.io.CsvBeanReader;
import org.supercsv.io.Tokenizer;
import org.supercsv.prefs.CsvPreference;

/**
 * Tests the {@link CsvDeclarativeBeanReader}
 *
 * @since 2.5
 * @author Dominik Schlosser
 */
public class CsvDeclarativeBeanReaderTest {

    private static final CsvPreference PREFS = CsvPreference.STANDARD_PREFERENCE;

    private static final String SIMPLE_BEAN_CSV = "/simpleBean.csv";
    private static final String SIMPLE_BEAN_SIMPLE_ANNOTATIONS_CSV = "/simpleBeanWithSimpleAnnotations.csv";
    private static final String BEAN_WITH_INHERITED_PROPERTIES = "/beanWithInheritedProperties.csv";
    private static final String BEAN_FOR_DEFAULT_OVERRIDING_TEST = "/beanForDefaultOverridingTest.csv";
    private static final String BEAN_WITHOUT_EXPLICIT_PARSE_ANNOTATIONS_TEST = "/beanWithoutExplicitParseAnnotations.csv";
    private static final String BEAN_WITH_ENUM = "/beanWithEnumTest.csv";

    private CsvDeclarativeBeanReader beanReader;

    @After
    public void tearDown() throws IOException {
        if (beanReader != null) {
            beanReader.close();
        }
    }

    @Test
    public void readSimpleBeanWithoutAnnotations() throws IOException {
        setupBeanReader(SIMPLE_BEAN_CSV);
        BeanWithoutAnnotations john = new BeanWithoutAnnotations("John", "Doe", 42, 100.5);
        BeanWithoutAnnotations max = new BeanWithoutAnnotations("Max", "Mustermann", 22, 21.4);

        assertEquals(john, beanReader.read(BeanWithoutAnnotations.class));
        assertEquals(max, beanReader.read(BeanWithoutAnnotations.class));
        assertNull(beanReader.read(BeanWithoutAnnotations.class));
    }

    @Test
    public void readSimpleBeanWithSimpleAnnotations() throws IOException {
        setupBeanReader(SIMPLE_BEAN_SIMPLE_ANNOTATIONS_CSV);
        BeanWithSimpleAnnotations john = new BeanWithSimpleAnnotations(null, "Doe", 42, 100.5);
        BeanWithSimpleAnnotations max = new BeanWithSimpleAnnotations("Max", "Mustermann", 22, 21.4);

        assertEquals(john, beanReader.read(BeanWithSimpleAnnotations.class));
        assertEquals(max, beanReader.read(BeanWithSimpleAnnotations.class));
        assertNull(beanReader.read(BeanWithSimpleAnnotations.class));
    }

    @Test
    public void readSimpleBeanWithChainedAnnotations() throws IOException {
        setupBeanReader(SIMPLE_BEAN_SIMPLE_ANNOTATIONS_CSV);
        BeanWithChainedAnnotations john = new BeanWithChainedAnnotations(null, "Doe", 42, 100.5);
        BeanWithChainedAnnotations max = new BeanWithChainedAnnotations("Max", "Mus", 22, 21.4);

        assertEquals(john, beanReader.read(BeanWithChainedAnnotations.class));
        assertEquals(max, beanReader.read(BeanWithChainedAnnotations.class));
        assertNull(beanReader.read(BeanWithChainedAnnotations.class));
    }

    @Test
    public void readBeanWithReadAndWriteAnnotations() throws IOException {
        ReadAndWriteBeanWithPropertyAccess beanForReadAndWrite = new ReadAndWriteBeanWithPropertyAccess(true);
        beanReader = new CsvDeclarativeBeanReader(new StringReader("j"), CsvPreference.STANDARD_PREFERENCE);

        assertEquals(beanForReadAndWrite, beanReader.read(ReadAndWriteBeanWithPropertyAccess.class));
    }

    @Test
    public void readSimpleBeanWithExplicitlyOrderedAnnotations() throws IOException {
        setupBeanReader(SIMPLE_BEAN_SIMPLE_ANNOTATIONS_CSV);
        BeanWithExplicitlyOrderedAnnotations john = new BeanWithExplicitlyOrderedAnnotations(null, "Doe", 42, 100.5);
        BeanWithExplicitlyOrderedAnnotations max = new BeanWithExplicitlyOrderedAnnotations("Max", "Mus", 22, 21.4);

        assertEquals(john, beanReader.read(BeanWithExplicitlyOrderedAnnotations.class));
        assertEquals(max, beanReader.read(BeanWithExplicitlyOrderedAnnotations.class));
        assertNull(beanReader.read(BeanWithExplicitlyOrderedAnnotations.class));
    }

    @Test
    public void readBeanWithExplicitFieldOrdering() throws IOException {
        setupBeanReader(SIMPLE_BEAN_SIMPLE_ANNOTATIONS_CSV);
        BeanWithExplicitlyOrderedFields john = new BeanWithExplicitlyOrderedFields(null, "Doe", 42, 100.5);
        BeanWithExplicitlyOrderedFields max = new BeanWithExplicitlyOrderedFields("Max", "Mus", 22, 21.4);

        assertEquals(john, beanReader.read(BeanWithExplicitlyOrderedFields.class));
        assertEquals(max, beanReader.read(BeanWithExplicitlyOrderedFields.class));
        assertNull(beanReader.read(BeanWithExplicitlyOrderedFields.class));
    }

    @Test
    public void readBeanWithPartialFieldMapping() throws IOException {
        setupBeanReader(SIMPLE_BEAN_SIMPLE_ANNOTATIONS_CSV);
        BeanWithPartialColumnMapping john = new BeanWithPartialColumnMapping("Doe", 42);
        BeanWithPartialColumnMapping max = new BeanWithPartialColumnMapping("Mustermann", 22);

        assertEquals(john, beanReader.read(BeanWithPartialColumnMapping.class));
        assertEquals(max, beanReader.read(BeanWithPartialColumnMapping.class));
        assertNull(beanReader.read(BeanWithPartialColumnMapping.class));
    }

    @Test(expected = SuperCsvException.class)
    public void readStrictBeanWithPartialFieldMapping() throws IOException {
        setupBeanReader(SIMPLE_BEAN_SIMPLE_ANNOTATIONS_CSV);

        beanReader.read(StrictBeanWithPartialColumnMapping.class);
    }

    @Test
    public void readBeanWithoutExplicitParseAnnotations() throws IOException {
        setupBeanReader(BEAN_WITHOUT_EXPLICIT_PARSE_ANNOTATIONS_TEST);
        BeanWithoutExplicitParseAnnotations john = new BeanWithoutExplicitParseAnnotations(0, 100.5);
        BeanWithoutExplicitParseAnnotations max = new BeanWithoutExplicitParseAnnotations(22, 21.4);

        assertEquals(john, beanReader.read(BeanWithoutExplicitParseAnnotations.class));
        assertEquals(max, beanReader.read(BeanWithoutExplicitParseAnnotations.class));
        assertNull(beanReader.read(BeanWithoutExplicitParseAnnotations.class));
    }

    @Test(expected = SuperCsvException.class)
    public void readBeanWithIllegalExplicitFieldOrdering() throws IOException {
        setupBeanReader(SIMPLE_BEAN_SIMPLE_ANNOTATIONS_CSV);
        BeanWithIllegalExplicitFieldOrder john = new BeanWithIllegalExplicitFieldOrder(null, "Doe", 42, 100.5);
        BeanWithIllegalExplicitFieldOrder max = new BeanWithIllegalExplicitFieldOrder("Max", "Mus", 22, 21.4);

        assertEquals(john, beanReader.read(BeanWithIllegalExplicitFieldOrder.class));
        assertEquals(max, beanReader.read(BeanWithIllegalExplicitFieldOrder.class));
        assertNull(beanReader.read(BeanWithIllegalExplicitFieldOrder.class));
    }

    @Test
    public void readBeanWithPartialExplicitFieldOrdering() throws IOException {
        setupBeanReader(SIMPLE_BEAN_SIMPLE_ANNOTATIONS_CSV);
        BeanWithPartiallyExplicitlyOrderedFields john = new BeanWithPartiallyExplicitlyOrderedFields(null, "Doe", 42,
                100.5);
        BeanWithPartiallyExplicitlyOrderedFields max = new BeanWithPartiallyExplicitlyOrderedFields("Max", "Mus", 22,
                21.4);

        assertEquals(john, beanReader.read(BeanWithPartiallyExplicitlyOrderedFields.class));
        assertEquals(max, beanReader.read(BeanWithPartiallyExplicitlyOrderedFields.class));
        assertNull(beanReader.read(BeanWithPartiallyExplicitlyOrderedFields.class));
    }

    @Test(expected = SuperCsvException.class)
    public void readStrictBeanWithPartialExplicitFieldOrdering() throws IOException {
        setupBeanReader(SIMPLE_BEAN_SIMPLE_ANNOTATIONS_CSV);

        beanReader.read(StrictBeanWithPartiallyExplicitlyOrderedFields.class);
    }

    @Test
    public void readBeanWithInheritedProperties() throws IOException {
        setupBeanReader(BEAN_WITH_INHERITED_PROPERTIES);
        BeanWithInheritedProperties john = new BeanWithInheritedProperties("John", "Doe", 42, 100.5, "Note 1");
        BeanWithInheritedProperties max = new BeanWithInheritedProperties("Max", "Mustermann", 22, 21.4, "Note 2");

        assertEquals(john, beanReader.read(BeanWithInheritedProperties.class));
        assertEquals(max, beanReader.read(BeanWithInheritedProperties.class));
        assertNull(beanReader.read(BeanWithInheritedProperties.class));
    }

    @Test
    public void overrideDefaultProcessor() throws IOException {
        setupBeanReader(BEAN_FOR_DEFAULT_OVERRIDING_TEST);
        BeanForDefaultOverridingTest firstRow = new BeanForDefaultOverridingTest(true);
        BeanForDefaultOverridingTest secondRow = new BeanForDefaultOverridingTest(false);

        assertEquals(firstRow, beanReader.read(BeanForDefaultOverridingTest.class));
        assertEquals(secondRow, beanReader.read(BeanForDefaultOverridingTest.class));
        assertNull(beanReader.read(BeanWithInheritedProperties.class));
    }

    @Test
    public void automaticEnumConversion() throws IOException {
        setupBeanReader(BEAN_WITH_ENUM);
        BeanWithEnum bla = new BeanWithEnum(TestEnum.Bla);
        BeanWithEnum blubb = new BeanWithEnum(TestEnum.Blubb);

        assertEquals(bla, beanReader.read(BeanWithEnum.class));
        assertEquals(blubb, beanReader.read(BeanWithEnum.class));
    }

    @Test(expected = SuperCsvReflectionException.class)
    public void readWithNonJavabean() throws IOException {
        setupBeanReader(BEAN_WITH_INHERITED_PROPERTIES);
        beanReader.read(Integer.class);
    }

    @Test(expected = IllegalArgumentException.class)
    public void readWithNullBeanClass() throws IOException {
        setupBeanReader(BEAN_WITH_INHERITED_PROPERTIES);
        beanReader.read(null);
    }

    @Test(expected = NullPointerException.class)
    public void readProcessorsWithNullBeanClass() throws IOException {
        beanReader.read(null);
    }

    @SuppressWarnings("resource")
    @Test(expected = NullPointerException.class)
    public void readerConstructorWithNullReader() {
        new CsvDeclarativeBeanReader((Reader) null, PREFS);
    }

    @SuppressWarnings("resource")
    @Test(expected = NullPointerException.class)
    public void readerConstructorWithNullPreferences() {
        new CsvDeclarativeBeanReader(new StringReader(""), null);
    }

    @SuppressWarnings("resource")
    @Test(expected = NullPointerException.class)
    public void readerConstructorWithNullTypeConverterRegistry() {
        new CsvDeclarativeBeanReader(new StringReader(""), null, PREFS);
    }

    @SuppressWarnings("resource")
    @Test(expected = NullPointerException.class)
    public void tokenizerConstructorWithNullReader() {
        new CsvDeclarativeBeanReader((Tokenizer) null, PREFS);
    }

    @SuppressWarnings("resource")
    @Test(expected = NullPointerException.class)
    public void tokenizerConstructorWithNullPreferences() {
        new CsvDeclarativeBeanReader(new Tokenizer(new StringReader(""), PREFS), null);
    }

    @SuppressWarnings("resource")
    @Test(expected = NullPointerException.class)
    public void tokenizerConstructorWithNullTypeConverterRegistry() {
        new CsvDeclarativeBeanReader(new Tokenizer(new StringReader(""), PREFS), null, PREFS);
    }

    @Test(expected = SuperCsvReflectionException.class)
    public void beanInstantationThrowingIllegalAccessException() throws IOException {
        setupBeanReader(BEAN_WITH_INHERITED_PROPERTIES);
        beanReader.read(IllegalAccessBean.class);
    }

    public static class IllegalAccessBean {

        public IllegalAccessBean() throws IllegalAccessException {
            throw new IllegalAccessException("naughty naughty!");
        }

    }

    private void setupBeanReader(String inputFileName) {
        beanReader = new CsvDeclarativeBeanReader(new InputStreamReader(
                CsvDeclarativeBeanReaderTest.class.getResourceAsStream(inputFileName)), PREFS);
    }
}

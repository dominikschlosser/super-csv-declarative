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

import com.github.dkschlos.supercsv.internal.cells.BeanCell;
import com.github.dkschlos.supercsv.internal.cells.BeanCells;
import com.github.dkschlos.supercsv.model.BeanDescriptor;
import com.github.dkschlos.supercsv.internal.typeconversion.TypeConverter;
import com.github.dkschlos.supercsv.internal.typeconversion.TypeConverterRegistry;
import com.github.dkschlos.supercsv.internal.util.Form;
import com.github.dkschlos.supercsv.internal.util.ReflectionUtilsExt;
import com.github.dkschlos.supercsv.io.declarative.annotation.CsvMappingModeType;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.apache.commons.lang.ClassUtils;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.exception.SuperCsvException;
import org.supercsv.exception.SuperCsvReflectionException;
import org.supercsv.io.AbstractCsvReader;
import org.supercsv.io.ITokenizer;
import org.supercsv.prefs.CsvPreference;

/**
 * This reader maps csv files to beans via conventions and
 * {@link CellProcessorAnnotationDescriptor} -annotations. The fields in the
 * bean must match the csv's fields in type and order. {@link CellProcessor}s
 * are created automatically for all known types. Additional processors can be
 * added by annotating fields with their respective annotations.
 * Annotation-order defines processor call-order.
 *
 * @since 2.5
 * @author Dominik Schlosser
 */
public class CsvDeclarativeBeanReader extends AbstractCsvReader {

    private TypeConverterRegistry typeConverterRegistry = new DefaultTypeConverterRegistry();

    /**
     * Constructs a new <tt>CsvBeanReader</tt> with the supplied Reader and CSV
     * preferences. Note that the
     * <tt>reader</tt> will be wrapped in a <tt>BufferedReader</tt> before
     * accessed.
     *
     * @param reader the reader
     * @param preferences the CSV preferences
     * @throws NullPointerException if reader or preferences are null
     */
    public CsvDeclarativeBeanReader(final Reader reader, final CsvPreference preferences) {
        super(reader, preferences);
    }

    /**
     * Constructs a new <tt>CsvBeanReader</tt> with the supplied Reader and CSV
     * preferences. Note that the
     * <tt>reader</tt> will be wrapped in a <tt>BufferedReader</tt> before
     * accessed.
     *
     * @param reader the reader
     * @param typeConverterRegistry the TypeConverterRegistry to use
     * @param preferences the CSV preferences
     * @throws NullPointerException if reader or preferences are null
     */
    public CsvDeclarativeBeanReader(final Reader reader, TypeConverterRegistry typeConverterRegistry, final CsvPreference preferences) {
        super(reader, preferences);
        this.typeConverterRegistry = Objects.requireNonNull(typeConverterRegistry, "typeConverterRegistry");
    }

    /**
     * Constructs a new <tt>CsvBeanReader</tt> with the supplied (custom)
     * Tokenizer and CSV preferences. The tokenizer should be set up with the
     * Reader (CSV input) and CsvPreference beforehand.
     *
     * @param tokenizer the tokenizer
     * @param preferences the CSV preferences
     * @throws NullPointerException if tokenizer or preferences are null
     */
    public CsvDeclarativeBeanReader(final ITokenizer tokenizer, final CsvPreference preferences) {
        super(tokenizer, preferences);
    }

    /**
     * Constructs a new <tt>CsvBeanReader</tt> with the supplied (custom)
     * Tokenizer and CSV preferences. The tokenizer should be set up with the
     * Reader (CSV input) and CsvPreference beforehand.
     *
     * @param tokenizer the tokenizer
     * @param typeConverterRegistry the TypeConverterRegistry to use
     * @param preferences the CSV preferences
     * @throws NullPointerException if tokenizer or preferences are null
     */
    public CsvDeclarativeBeanReader(final ITokenizer tokenizer, TypeConverterRegistry typeConverterRegistry, final CsvPreference preferences) {
        super(tokenizer, preferences);
        this.typeConverterRegistry = Objects.requireNonNull(typeConverterRegistry, "typeConverterRegistry");
    }

    /**
     * Reads a row of a CSV file and populates an instance of the specified
     * class, using the conventional mappings and provided
     * {@link CellProcessorAnnotationDescriptor}-annotations
     *
     * @param clazz the type to instantiate. If the type is a class then a new
     * instance will be created using the default no-args constructor. If the
     * type is an interface, a proxy object which implements the interface will
     * be created instead.
     * @param <T> the bean type
     * @return a populated bean or null if EOF
     * @throws IOException if an I/O error occurred
     * @throws IllegalArgumentException if nameMapping.length != number of
     * columns read or clazz is null
     * @throws SuperCsvException if there was a general exception while
     * reading/processing
     * @throws SuperCsvReflectionException if there was an reflection exception
     * while mapping the values to the bean
     * @since 2.5
     */
    public <T> T read(final Class<T> clazz) throws IOException {
        if (clazz == null) {
            throw new IllegalArgumentException("clazz should not be null");
        }

        BeanDescriptor beanDescriptor = BeanDescriptor.create(clazz);
        BeanCells fields = BeanCells.getFields(beanDescriptor, StandardCsvContexts.READ);

        return readIntoBean(ReflectionUtilsExt.instantiateBean(clazz), beanDescriptor, fields);
    }

    private <T> T populateBean(final T resultBean, List<Object> processedColumns, BeanCells cells) {
        for (int i = 0; i < processedColumns.size(); i++) {
            final Object fieldValue = processedColumns.get(i);

            BeanCell cell = cells.getCell(i);
            if (cell == null || cell.getType() == null) {
                continue;
            }

            // ClassUtils handles boxed types
            if (fieldValue != null && ClassUtils.isAssignable(fieldValue.getClass(), cell.getType(), true)) {
                cell.setValue(resultBean, fieldValue);
            } else {
                Class<?> fieldValueClass = fieldValue == null ? Object.class : fieldValue.getClass();
                TypeConverter<Object, Object> converter
                        = (TypeConverter<Object, Object>) typeConverterRegistry.getConverter(fieldValueClass, cell.getType());
                if (converter == null) {
                    throw new SuperCsvException(Form.at("No converter registered from type {} to type {}. Add one or fix your CellProcessor-annotations to return the field's type",
                            fieldValueClass.getName(), cell.getType().getName()));
                }
                cell.setValue(resultBean, converter.convert(fieldValue));
            }
        }

        return resultBean;
    }

    private <T> T readIntoBean(final T bean, BeanDescriptor beanDescriptor, BeanCells cells)
            throws IOException {

        if (readRow()) {
            if (CsvMappingModeType.STRICT.equals(beanDescriptor.getMappingMode()) && cells.getCorrectlyMappedFieldCount() != length()) {
                throw new SuperCsvException(Form.at("MappingMode.STRICT: Number of mapped bean-fields ({}] and csv-cells ({}) does not match.", cells.getCorrectlyMappedFieldCount(), length()));
            }
            List<CellProcessor> rowProcessors = new ArrayList<>();
            for (int i = 0; i < length(); i++) {
                rowProcessors.add(cells.getCell(i).getProcessor());
            }

            List<Object> processedColumns = new ArrayList<>();
            executeProcessors(processedColumns, rowProcessors.toArray(new CellProcessor[rowProcessors.size()]));

            return populateBean(bean, processedColumns, cells);
        }

        return null; // EOF
    }

}

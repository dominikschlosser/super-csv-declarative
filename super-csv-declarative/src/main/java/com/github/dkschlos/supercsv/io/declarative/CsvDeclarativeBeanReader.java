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
import com.github.dkschlos.supercsv.internal.util.ReflectionUtilsExt;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.exception.SuperCsvException;
import org.supercsv.exception.SuperCsvReflectionException;
import org.supercsv.io.AbstractCsvReader;
import org.supercsv.io.ITokenizer;
import org.supercsv.prefs.CsvPreference;

/**
 * This reader maps csv files to beans via conventions and {@link CellProcessorAnnotationDescriptor} -annotations. The
 * fields in the bean must match the csv's fields in type and order. {@link CellProcessor}s are created automatically
 * for all known types. Additional processors can be added by annotating fields with their respective annotations.
 * Annotation-order defines processor call-order.
 *
 * @since 2.5
 * @author Dominik Schlosser
 */
public class CsvDeclarativeBeanReader extends AbstractCsvReader {


    /**
     * Constructs a new <tt>CsvBeanReader</tt> with the supplied Reader and CSV preferences. Note that the
     * <tt>reader</tt> will be wrapped in a <tt>BufferedReader</tt> before accessed.
     *
     * @param reader the reader
     * @param preferences the CSV preferences
     * @throws NullPointerException if reader or preferences are null
     */
    public CsvDeclarativeBeanReader(final Reader reader, final CsvPreference preferences) {
        super(reader, preferences);
    }

    /**
     * Constructs a new <tt>CsvBeanReader</tt> with the supplied (custom) Tokenizer and CSV preferences. The tokenizer
     * should be set up with the Reader (CSV input) and CsvPreference beforehand.
     *
     * @param tokenizer the tokenizer
     * @param preferences the CSV preferences
     * @throws NullPointerException if tokenizer or preferences are null
     */
    public CsvDeclarativeBeanReader(final ITokenizer tokenizer, final CsvPreference preferences) {
        super(tokenizer, preferences);
    }

    /**
     * Reads a row of a CSV file and populates an instance of the specified class, using the conventional mappings and
     * provided {@link CellProcessorAnnotationDescriptor}-annotations
     *
     * @param clazz the type to instantiate. If the type is a class then a new instance will be created using the
     * default no-args constructor. If the type is an interface, a proxy object which implements the interface will be
     * created instead.
     * @param <T> the bean type
     * @return a populated bean or null if EOF
     * @throws IOException if an I/O error occurred
     * @throws IllegalArgumentException if nameMapping.length != number of columns read or clazz is null
     * @throws SuperCsvException if there was a general exception while reading/processing
     * @throws SuperCsvReflectionException if there was an reflection exception while mapping the values to the bean
     * @since 2.5
     */
    public <T> T read(final Class<T> clazz) throws IOException {
        if (clazz == null) {
            throw new IllegalArgumentException("clazz should not be null");
        }

        BeanCells fields = BeanCells.getFields(clazz, StandardCsvContexts.READ);

        return readIntoBean(ReflectionUtilsExt.instantiateBean(clazz), fields);
    }

    private <T> T populateBean(final T resultBean, List<Object> processedColumns, BeanCells cells) {
        for (int i = 0; i < processedColumns.size(); i++) {
            final Object fieldValue = processedColumns.get(i);

            BeanCell cell = cells.getCell(i);
            if (cell == null || fieldValue == null) {
                continue;
            }

            cell.setValue(resultBean, fieldValue);
        }

        return resultBean;
    }

    private <T> T readIntoBean(final T bean, BeanCells cells)
            throws IOException {

        if (readRow()) {
            List<CellProcessor> rowProcessors = new ArrayList<CellProcessor>();
            for (int i = 0; i < length(); i++) {
                rowProcessors.add(cells.getCell(i).getProcessor());
            }

            List<Object> processedColumns = new ArrayList<Object>();
            executeProcessors(processedColumns, rowProcessors.toArray(new CellProcessor[rowProcessors.size()]));

            return populateBean(bean, processedColumns, cells);
        }

        return null; // EOF
    }

}

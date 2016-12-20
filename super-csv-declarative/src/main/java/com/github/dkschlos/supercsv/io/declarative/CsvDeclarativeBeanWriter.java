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
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.exception.SuperCsvException;
import org.supercsv.exception.SuperCsvReflectionException;
import org.supercsv.io.AbstractCsvWriter;
import org.supercsv.prefs.CsvPreference;
import org.supercsv.util.Util;

/**
 * CsvDeclarativeBeanWriter writes a CSV file via conventions and {@link CellProcessorAnnotationDescriptor}
 * -annotations.
 *
 * @author Dominik Schlosser
 */
public class CsvDeclarativeBeanWriter extends AbstractCsvWriter {

    /**
     * Constructs a new <tt>CsvDeclarativeBeanWriter</tt> with the supplied Writer and CSV preferences. Note that the
     * <tt>writer</tt> will be wrapped in a <tt>BufferedWriter</tt> before accessed.
     *
     * @param writer the writer
     * @param preference the CSV preferences
     * @throws NullPointerException if writer or preference are null
     */
    public CsvDeclarativeBeanWriter(final Writer writer, final CsvPreference preference) {
        super(writer, preference);
    }

    /**
     * Writes a row of a CSV file, using the conventions and mappings provided
     * {@link CellProcessorAnnotationDescriptor}-annotations
     *
     * @param source The bean-instance to write
     * @throws IOException if an I/O error occurred
     * @throws IllegalArgumentException if source is null
     * @throws SuperCsvException if there was a general exception while writing/processing
     * @throws SuperCsvReflectionException if there was an reflection exception
     * @since 2.5
     */
    public void write(final Object source) throws IOException {
        if (source == null) {
            throw new IllegalArgumentException("source must not be null");
        }

        incrementRowAndLineNo();

        BeanDescriptor beanDescriptor = BeanDescriptor.create(source.getClass());
        BeanCells cells = BeanCells.getFields(beanDescriptor, StandardCsvContexts.WRITE);
        List<Object> beanValues = extractBeanValues(source, cells);

        List<Object> processedColumns = new ArrayList<>();

        List<CellProcessor> rowProcessors = new ArrayList<>();
        for (BeanCell cell : cells.getAll()) {
            rowProcessors.add(cell.getProcessor());
        }

        Util.executeCellProcessors(processedColumns, beanValues,
                rowProcessors.toArray(new CellProcessor[rowProcessors.size()]), getLineNumber(), getRowNumber());

        writeRow(processedColumns);
        flush();
    }

    private List<Object> extractBeanValues(final Object source, BeanCells cells) {

        List<Object> beanValues = new ArrayList<>();

        for (BeanCell cell : cells.getAll()) {
            beanValues.add(cell.getValue(source));
        }

        return beanValues;
    }
}

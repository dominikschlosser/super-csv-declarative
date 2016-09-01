package com.github.dkschlos.supercsv.io.declarative;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface CsvField {

    /**
     * Defines the zero based index number of the csv column.
     *
     * @return column number
     */
    int index();
}

package org.supercsv.io.declarative;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import org.supercsv.io.declarative.provider.CellProcessorProvider;

@Retention(RetentionPolicy.RUNTIME)
public @interface CellProcessor {
	Class<? extends CellProcessorProvider> value();
}

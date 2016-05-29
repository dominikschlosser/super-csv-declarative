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
package org.supercsv.io.declarative.provider;

import java.lang.reflect.Field;

import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.exception.SuperCsvReflectionException;
import org.supercsv.io.declarative.annotation.ParseEnum;
import org.supercsv.util.Form;

/**
 * CellProcessorProvider for {@link ParseEnum}
 * 
 * @since 2.5
 * @author Dominik Schlosser
 */
public class ParseEnumCellProcessorProvider implements CellProcessorByAnnotationProvider<ParseEnum>,
	CellProcessorProvider {
	
	/**
	 * {@inheritDoc}
	 */
	public CellProcessor create(ParseEnum annotation, CellProcessor next) {
		return new org.supercsv.cellprocessor.ParseEnum(annotation.enumClass(), annotation.ignoreCase(), next);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	public CellProcessor create(Field forField, CellProcessor next) {
		try {
			return new org.supercsv.cellprocessor.ParseEnum((Class<? extends Enum<?>>) forField.getDeclaringClass(),
				false, next);
		}
		catch(ClassCastException ex) {
			throw new SuperCsvReflectionException(Form.at("Cannot apply ParseEnum-processor to non-enum field ({0})",
				forField.getName()), ex);
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	public Class<ParseEnum> getType() {
		return ParseEnum.class;
	}
	
}

package org.supercsv.io.declarative;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.supercsv.exception.SuperCsvException;
import org.supercsv.util.Form;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Ordering;

public class FieldExtractor {
	/**
	 * Returns all fields of the given class including those of superclasses.
	 * 
	 * @param clazz
	 *            the class to get the fields of
	 * @return all fields of the class and its hierarchy
	 */
	public static List<Field> getFields(Class<?> clazz) {
		List<Field> fields = new ArrayList<Field>();
		extractFields(clazz, fields);
		List<Field> orderedFields = orderFields(fields);
		
		return orderedFields;
	}
	
	private static List<Field> orderFields(List<Field> fields) {
		final BiMap<Field, Integer> order = HashBiMap.create();
		BiMap<Integer, Field> inverse = order.inverse();
		
		List<Field> fieldsWithoutCsvFieldAnnotation = new ArrayList<Field>();
		for( int i = 0; i < fields.size(); i++ ) {
			Field field = fields.get(i);
			int orderVal = i;
			CsvField fieldAnnotation = field.getAnnotation(CsvField.class);
			if( fieldAnnotation != null ) {
				orderVal = fieldAnnotation.order();
			} else {
				fieldsWithoutCsvFieldAnnotation.add(field);
			}
			
			if( inverse.containsKey(orderVal) ) {
				throw new SuperCsvException(Form.at("Explicit order-index {} was declared twice (Field: {}", orderVal,
					field.getName()));
			}
			
			order.put(field, orderVal);
		}
		
		if( !fieldsWithoutCsvFieldAnnotation.isEmpty() && fieldsWithoutCsvFieldAnnotation.size() < fields.size() ) {
			String missingFields = Joiner.on(", ").join(fieldsWithoutCsvFieldAnnotation);
			throw new SuperCsvException(
				Form.at(
					"If you use @CsvField to explicitly define field-order, you have to do it on all fields. Missing on: {}",
					missingFields));
		}
		
		Ordering<Field> ordering = Ordering.natural().onResultOf(new Function<Field, Integer>() {
			
			public Integer apply(Field field) {
				return order.get(field);
			}
		});
		
		return ordering.immutableSortedCopy(fields);
	}
	
	private static void extractFields(Class<?> clazz, List<Field> fields) {
		if( clazz.getSuperclass() != Object.class ) {
			extractFields(clazz.getSuperclass(), fields);
		}
		
		fields.addAll(Arrays.asList(clazz.getDeclaredFields()));
	}
}

package org.supercsv.util;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.supercsv.exception.SuperCsvReflectionException;

public class ReflectionUtilsExt {
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
		
		return fields;
	}
	
	private static void extractFields(Class<?> clazz, List<Field> fields) {
		if( clazz.getSuperclass() != Object.class ) {
			extractFields(clazz.getSuperclass(), fields);
		}
		
		fields.addAll(Arrays.asList(clazz.getDeclaredFields()));
	}
	
	/**
	 * Instantiates the bean (or creates a proxy if it's an interface).
	 * 
	 * @param clazz
	 *            the bean class to instantiate (a proxy will be created if an interface is supplied), using the default
	 *            (no argument) constructor
	 * @return the instantiated bean
	 * @throws SuperCsvReflectionException
	 *             if there was a reflection exception when instantiating the bean
	 */
	public static <T> T instantiateBean(final Class<T> clazz) {
		final T bean;
		if( clazz.isInterface() ) {
			bean = BeanInterfaceProxy.createProxy(clazz);
		} else {
			try {
				bean = clazz.newInstance();
			}
			catch(InstantiationException e) {
				throw new SuperCsvReflectionException(String.format(
					"error instantiating bean, check that %s has a default no-args constructor", clazz.getName()), e);
			}
			catch(IllegalAccessException e) {
				throw new SuperCsvReflectionException("error instantiating bean", e);
			}
		}
		
		return bean;
	}
}

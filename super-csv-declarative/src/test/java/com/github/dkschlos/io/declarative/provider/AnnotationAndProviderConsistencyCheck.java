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
package com.github.dkschlos.io.declarative.provider;

import java.util.Collection;

import com.github.dkschlos.io.declarative.provider.DeclarativeCellProcessorProvider;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.reflections.Reflections;
import com.github.dkschlos.io.declarative.CellProcessorAnnotationDescriptor;

/**
 * Tests all {@link CellProcessorAnnotationDescriptor}-annotations and their providers for consistency since the type system cannot do this
 * for us
 * 
 * @since 2.5
 * @author Dominik Schlosser
 */
@RunWith(Parameterized.class)
public class AnnotationAndProviderConsistencyCheck {
	@Parameters
	public static Collection<Class<?>> getCellProcessorAnnotations() {
		Reflections reflections = new Reflections("org.supercsv");
		return reflections.getTypesAnnotatedWith(CellProcessorAnnotationDescriptor.class);
	}
	
	private Class<?> cellProcessorAnnotationType;
	
	public AnnotationAndProviderConsistencyCheck(Class<?> cellProcessorAnnotationType) {
		this.cellProcessorAnnotationType = cellProcessorAnnotationType;
	}
	
	@SuppressWarnings("rawtypes")
	@Test
	public void test() throws InstantiationException, IllegalAccessException {
		CellProcessorAnnotationDescriptor cellProcessorAnnotation = cellProcessorAnnotationType.getAnnotation(CellProcessorAnnotationDescriptor.class);
		Class<? extends DeclarativeCellProcessorProvider> providerType = cellProcessorAnnotation.provider();
		
		DeclarativeCellProcessorProvider provider = providerType.newInstance();
		
		Assert.assertEquals(cellProcessorAnnotationType, provider.getType());
	}
}

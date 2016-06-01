# super-csv-declarative [![Build Status](https://travis-ci.org/dkschlos/super-csv-declarative.svg?branch=master)](https://travis-ci.org/dkschlos/super-csv-declarative)

Unofficial declarative extension to super-csv, currently supporting version 2.4.0.

It mainly provides two new classes:
- CsvDeclarativeBeanReader
- CsvDeclarativeBeanWriter

Those can be used to read/write CSV-files from/to java beans via conventions and declarative mappings:

```Java
public class Person {
	@Trim
	private String name;
	
	@Optional
	@Trim
	private String middleName;
	
	@Trim
	private String lastName;
	
	private int age;
	private double weight;

	// getters omitted
}
```

This example class leads to an implicit mapping, where csv-file-cells are mapped to properties via implicit order in the class.
CellProcessor-Pipelines are created by annotations or, if absent and applicable, by conventions.
This means, that the above annotated fields lead to a CellProcessor-map like this:

```Java
CellProcessor[] processors = new CellProcessor[] {
			new Trim(),
			new Optional(new Trim()),
			new Trim(),
			new ParseInt(),
			new ParseDouble()	
		};
```

## Explicit field/annotation-order

**Note**: The Java Language Specification doesn't specify the order in which fields of a class or annotations are returned when using reflection. The Oracle JVM does return them in the declared order but others like Dalvik may sort them alphabetically or in any other way.

If your application needs to support such environments you should use the *@CsvField*-annotation for fields and the *order*-fields which is defined in all standard CellProcessor-annotations and can be added to custom ones as well:

```Java
public class Person {
	@Trim
	@CsvField(order = 0)
	private String name;
	
	@Optional(order = 0)
	@Trim(order = 1)
	@CsvField(order = 1)
	private String middleName;
	
	@Trim
	@CsvField(order = 2)
	private String lastName;
	
	@CsvField(order = 3)
	private int age;
	
	@CsvField(order = 4)
	private double weight;

	// getters omitted
}
```

**Note**: Using the *@CsvField*-annotation is all or nothing. You don't use it at all or you need to use it on each field.

## Implementing new Processors

If you want to add a new processor and use it in a declarative way, you need to implement the corresponding *annotation* and a *DeclarativeCellProcessorProvider*-implementation which gets the annotation-instance and creates a *CellProcessorFactory*.

The following example shows how to implement all those necessary parts:

### The annotation

```Java
@CellProcessorAnnotationDescriptor(provider = OptionalCellProcessorProvider.class)
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD })
public @interface Optional {
	int order() default ProcessorOrder.UNDEFINED;
}
```

### The provider

```Java
public class OptionalCellProcessorProvider implements
	DeclarativeCellProcessorProvider<org.supercsv.io.declarative.annotation.Optional> {
	
	public CellProcessorFactory create(final org.supercsv.io.declarative.annotation.Optional annotation) {
		return new CellProcessorFactory() {
			
			public int getOrder() {
				return annotation.order();
			}
			
			public CellProcessor create(CellProcessor next) {
				return new Optional(next);
			}
		};
	}
	
	public Class<org.supercsv.io.declarative.annotation.Optional> getType() {
		return org.supercsv.io.declarative.annotation.Optional.class;
	}
	
}
```

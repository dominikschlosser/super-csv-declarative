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

**Note**: The Java Language Specification doesn't specify the order in which fields of a class or annotations are returned when using reflection. The Oracle JVM does return them in the written order but others like Dalvik may sort them alphabetically or in any other way.
Since super-csv-declarative relies heavily on field/annotation-order, it is right now **only** usable on JVMs which return fields/annotations in the written order.

**If you are certain that your application will only be executed on JVMs which return fields/annotations in the written order, the following is irrelevant to you.**

If your application needs to support such environments you should consider using vanilla-super-csv since the declarative approach won't work as smoothly if you can not rely on field/annotation-ordering.
There is some support for this scenario, though:

```Java
public class Person {
        @Trim
	@CsvField(order = 0)
	private String name;
	
	@CellProcessors({ @CellProcessor(OptionalCellProcessorProvider.class),
		@CellProcessor(TrimCellProcessorProvider.class) })
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

Field ordering can be defined explicitly by the @CsvField-annotation.
**Note**: Using this annotation is all or nothing. You don't use it at all or you need to use it on each field.

Annotation ordering can be defined explicitly by using the @CellProcessors-annotation which gets a list of @CellProcessor-annotations which basically wrap an implementation of the CellProcessorProvider-interface:


```Java
public interface CellProcessorProvider {
	CellProcessor create(Field forField, CellProcessor next);
}
```

There are default implementations for processors which dont need parameters. If you need to pass parameters to a processor you have to provide your own implementation of the above interface.

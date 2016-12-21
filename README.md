# super-csv-declarative [![Build Status](https://travis-ci.org/dmn1k/super-csv-declarative.svg?branch=master)](https://travis-ci.org/dmn1k/super-csv-declarative) [![codecov](https://codecov.io/gh/dmn1k/super-csv-declarative/branch/master/graph/badge.svg)](https://codecov.io/gh/dkschlos/super-csv-declarative)

*New since 3.0.0:*
- *Java 8* is required!
- *repeatable annotations* are supported and many annotations that come with the
  framework are now repeatable!
- you can now mix annotations and programmatic creation of CellProcessors with
  *@CellProcessorFactoryMethod*
- *java.time-types* (LocalDateTime and the like) are supported now
- *java.util.Optional* is supported without use of an annotation (you still might have
  to use com.github.dmn1k.supercsv.io.declarative.annotation.Optional to skip
following processors though)
- the base-namespace and groupId changed from 'com.github.dkschlos' to
  'com.github.dmn1k'


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

	// getters and setters omitted
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

## Field access strategies

By default, super-csv-declarative uses getters and setters to access bean-fields.
To force super-csv-declarative to bypass getters and setters and use the fields directly (for example to build immutable classes), one can annotate a bean with *@CsvAccessorType*:

```Java
@CsvAccessorType(CsvAccessType.FIELD)
public class MyBean {
	// content omitted
}
```

## Ignoring fields

```Java
public class Person {
	private static final int THE_ANSWER = 42;

	@CsvTransient
	private String toIgnore;
}
```

Static fields are ignored as well as all fields annotated with *@CsvTransient*.

## Explicit field index

**Note**: The Java Language Specification doesn't specify the order in which fields of a class or annotations are returned when using reflection. The Oracle JVM does return them in the declared order but others like Dalvik may sort them alphabetically or in any other way.

You can define the target column index of a field manually by using the `index` attribute:

```Java
public class Person {
	@CsvField(index = 0)
	private String name;

	@CsvField(index = 1)
	private String middleName;

	@CsvField(index = 2)
	private String lastName;

	// ...
}
```
## Explicit annotation order

Due to the inability to get the annotation order via reflection you can define the order of the processor annotations manually too:

```Java
public class Person {
	@CsvField(index = 0)
	@Trim
	private String name;

	@CsvField(index = 1)
	@Optional(order = 0)
	@Trim(order = 1)
	private String middleName;

	@CsvField(index = 2)
	@Trim
	private String lastName;

	// ...
}
```

## Mapping modes

The default mapping mode is *STRICT* which means that you have to use *@CsvField* on all fields or on no field at all.
It also means that you have to have a bean-field for each field in the CSV-file when reading.

There is another mapping mode, *LOOSE*, which allows you to partially map fields in a bean by using *@CsvField* only on some fields.
It also allows you to ignore fields in a source CSV-file.

You can change the mapping mode by applying the *@CsvMappingMode*-annotation to beans:

```Java
@CsvMappingMode(CsvMappingModeType.LOOSE)
public class MyBean {
	// content omitted
}
```


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

			public int getIndex() {
				return annotation.index();
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

## Installation

Get it from Maven Central:
```Maven
<dependency>
    <groupId>com.github.dmn1k</groupId>
    <artifactId>super-csv-declarative</artifactId>
    <version>3.0.0</version>
</dependency>
```

# super-csv-declarative

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

A possible future extension is the possibility to provide the order manually (an extra annotation for fields and a new annotation parameter for processor-annotations).

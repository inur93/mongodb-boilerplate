# Getting started #
Add the following to your `pom.xml`:
```xml
<project>
    <dependencies>
        <dependency>
            <groupId>com.vormadal</groupId>
            <artifactId>mongodb-boilerplate</artifactId>
            <version>6.1.0</version>
        </dependency>
    </dependencies>
    <repositories>
        <repository>
            <id>com.vormadal.mongodb</id>
            <name>com.vormadal.mongodb</name>
            <releases>
                <enabled>true</enabled>
            </releases>
            <snapshots>
                <enabled>false</enabled>
            </snapshots>
            <url>https://bitbucket.org/vormadal/mongodb-boilerplate/raw/releases</url>
        </repository>
    </repositories>
</project>
```

# Annotations #
This project comes with 3 annotations intended for use with a Mongo document model.
The annotations are:  
    1.  FieldsClass  
    2. PartialClasses  
    3. PartialClass  
    
## FieldsClass ##
The FieldsClass annotation can be used on a model like this:
```java
@FieldsClass(includeInheritedFields = false)
public class Todo {
    private String id;
    private String description;
}
```
This will generate a new class Fields that will look like this:
```java
public class Fields {
    public static class Todo {
        public static final String id = "id";
        public static final String description = "description";
        public static final String[] values = new String[]{"id", "description"};
    }
}
```
This will let you reference field names when making queries to Mongo while making sure there is no typos and non-existing fields.
If renaming a field you will get a compile error for every line using that constant since the constant no longer exists and you will have to make sure that you use a valid field name.

By setting the FieldsClass property `includeInheritedFields = true` you can get the fields name of the parent class as well.
This can be helpful if the parent class is a compiled class and cannot be annotated itself. 

If multiple models in the same package are annotated with `@FieldsClass` the generated Fields class will contain an innerclass for each of the annotated classes.
However if the annotated classes are in separate packages a new Fields class is generated for each package.

## PartialClasses ##
  
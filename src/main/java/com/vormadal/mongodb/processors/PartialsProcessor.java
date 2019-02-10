package com.vormadal.mongodb.processors;

import com.google.auto.service.AutoService;
import com.vormadal.mongodb.annotations.PartialClass;
import com.vormadal.mongodb.annotations.PartialClasses;

import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.ElementFilter;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;

/**
 * <p>Created: 31-01-2019</p>
 * <p>author: Runi</p>
 */

@AutoService(Processor.class)
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class PartialsProcessor extends BaseProcessor {
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {

        for (TypeElement annotation : annotations) {
            Set<? extends Element> annotatedElements = roundEnv.getElementsAnnotatedWith(annotation);
            Set<TypeElement> types = ElementFilter.typesIn(annotatedElements);

            for (TypeElement classType : types) {

                //the qualified name contains package and class name.
                String qualifiedName = classType.getQualifiedName().toString();
                String packageName = extractPackageName(qualifiedName);
                String className = classType.getSimpleName().toString();

                PartialClasses partialsAnnotation = classType.getAnnotation(PartialClasses.class);

                for (PartialClass partial : partialsAnnotation.value()) {
                    String partialClassName = partial.name();

                    List<Element> fields = new ArrayList<>();
                    Set<? extends Element> classFields = getClassFields(classType, partial.includeInheritedFields(), new HashSet<>());
                    Set<String> includedFields = new HashSet<>(asList(partial.includeFields()));
                    Set<String> excludedFields = new HashSet<>(asList(partial.excludeFields()));

                    //validate
                    validateFields(classType, classFields, includedFields);
                    validateFields(classType, classFields, excludedFields);

                    if (includedFields.size() > 0) {
                        fields = classFields
                                .stream()
                                .filter(f -> includedFields.contains(f.getSimpleName().toString())
                                        && f instanceof VariableElement)
                                .collect(Collectors.toList());

                    } else if (excludedFields.size() > 0) {
                        fields = classFields
                                .stream()
                                .filter(f -> !excludedFields.contains(f.getSimpleName().toString())
                                        && f instanceof VariableElement)
                                .collect(Collectors.toList());

                    }
                    try {
                        writePartialClassesToFile(packageName, partialClassName, fields, className);
                    } catch (IOException e) {
                        error(e.getMessage(), classType);
                    }
                }
            }
        }
        return true;
    }


    private void validateFields(Element element, Set<? extends Element> classFields, Set<String> fields) {
        try {
            for (String field : fields) {
                Optional<? extends Element> first = classFields.stream().filter(f -> f.getSimpleName().toString().equals(field)).findFirst();
                if (!first.isPresent()) {
                    error("Field '" + field + "' does not exist. Check the values provided in MongoDto annotation", element);
                }
                //TODO check if getters and setters are present.
            }
        }catch (Exception e){
            error(e.getMessage(), element);
        }
    }


    private String getterName(Element field){
        String name = field.getSimpleName().toString();
        String upName = name.substring(0, 1).toUpperCase() + name.substring(1);
        return "get" + upName;
    }

    private String setterName(Element field){
        String name = field.getSimpleName().toString();
        String upName = name.substring(0, 1).toUpperCase() + name.substring(1);
        return "set" + upName;
    }

    private void writePartialClassesToFile(String packageName, String className, List<Element> fields, String targetClassName) throws IOException {
        JavaFileObject builderFile = processingEnv.getFiler().createSourceFile(packageName + "." + className);

        try (PrintWriter out = new PrintWriter(builderFile.openWriter())) {
            if (packageName != null) {
                out.print("package ");
                out.print(packageName);
                out.println(";");
                out.println();
            }

            out.print("public class ");
            out.print(className);
            out.println(" {");

            for (Element field : fields) {
                String type = field.asType().toString();
                String name = field.getSimpleName().toString();

                String getterName = getterName(field);
                String setterName = setterName(field);

                //field
                out.print("private ");
                out.print(type);
                out.print(" ");
                out.print(name);
                out.println(";");

                //getter
                out.print("public ");
                out.print(type);
                out.print(" ");
                out.print(getterName);
                out.println("(){");
                out.print("return this.");
                out.print(name);
                out.println(";");
                out.println("}");

                //setter
                out.print("public void ");
                out.print(" ");
                out.print(setterName);
                out.print("(");
                out.print(type);
                out.print(" ");
                out.print(name);
                out.print("){");
                out.print("this.");
                out.print(name);
                out.print(" = ");
                out.print(name);
                out.println(";");
                out.println("}");

            }

            //empty constructor
            out.print("public ");
            out.print(className);
            out.println("(){}");

            //constructor

            //getter for original object
            out.print("public ");
            out.print(targetClassName);
            out.print(" get");
            out.print(targetClassName);
            out.println("(){");
            out.print(targetClassName);
            out.print(" obj = new ");
            out.print(targetClassName);
            out.println("();");
            for(Element field : fields){
                String name = field.getSimpleName().toString();
                String setterName = setterName(field);

                //set field value
                out.print("obj.");
                out.print(setterName);
                out.print("(this.");
                out.print(name);
                out.println(");");
            }
            out.println("return obj;");
            out.println("}");

            out.println("}");
        }
    }


    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return new HashSet<>(Collections.singletonList(PartialClasses.class.getName()));
    }


}

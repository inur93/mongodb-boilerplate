package com.vormadal.mongodb.processors;

import com.google.auto.service.AutoService;
import com.vormadal.mongodb.annotations.MongoDto;
import com.vormadal.mongodb.annotations.PartialDto;

import javax.annotation.processing.AbstractProcessor;
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
import static javax.tools.Diagnostic.Kind.ERROR;
import static javax.tools.Diagnostic.Kind.NOTE;

/**
 * <p>Created: 31-01-2019</p>
 * <p>author: Runi</p>
 */

@AutoService(Processor.class)
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class MongoDtoProcessor extends AbstractProcessor {
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {

        for (TypeElement annotation : annotations) {
            processingEnv.getMessager().printMessage(NOTE, "annotation name: " + annotation.getQualifiedName());
            Set<? extends Element> annotatedElements = roundEnv.getElementsAnnotatedWith(annotation);
            Set<TypeElement> types = ElementFilter.typesIn(annotatedElements);

            String packageName = null;
            int highestOrderPackage = 99;
            Map<String, List<String>> classFields = new HashMap<>();
            for (TypeElement classType : types) {
                /*
                DEBUG
                 */
                String packageClassName = classType.getQualifiedName().toString();
                String className = classType.getSimpleName().toString();
                //try to find the best fit package for the Fields class.
                int rank = packageClassName.length() - packageClassName.replace(".", "").length();
                if (rank < highestOrderPackage) {
                    highestOrderPackage = rank;
                    packageName = extractPackageName(packageClassName);
                }

                processPartialDtos(classType, extractPackageName(packageClassName));

                List<String> fieldNames = new ArrayList<>();
                for (Element field : classType.getEnclosedElements()) {
                    if (field instanceof VariableElement) {
                        fieldNames.add(field.getSimpleName().toString());
                    }
                }
                classFields.put(className, fieldNames);
            }
            try {
                writeFieldClassToFile(packageName, classFields);
            } catch (IOException e) {
                processingEnv.getMessager().printMessage(ERROR, e.getMessage());
            }
        }
        return true;
    }

    public void processPartialDtos(TypeElement classType, String packageName) {
        if (classType.getEnclosedElements().size() > 0) {
            processingEnv.getMessager().printMessage(NOTE, "b1: " + classType.getEnclosedElements().get(0).asType().toString());
            processingEnv.getMessager().printMessage(NOTE, "b2: " + classType.getEnclosedElements().get(0).getEnclosingElement().toString());
        }

        if (classType.getTypeParameters().size() > 0) {
            processingEnv.getMessager().printMessage(NOTE, "v1: " + classType.getTypeParameters().get(0).getSimpleName());
            processingEnv.getMessager().printMessage(NOTE, "v2: " + classType.getTypeParameters().get(0).getKind().toString());
        }

        MongoDto dtoAnnotation = classType.getAnnotation(MongoDto.class);
        for (PartialDto partial : dtoAnnotation.partials()) {
            String partialClassName = partial.name();
            List<Element> includedFields = new ArrayList<>();
            List<? extends Element> classFields = classType.getEnclosedElements();
            Set<String> fields = new HashSet<>(asList(partial.includeFields()));
            Set<String> excludedFields = new HashSet<>(asList(partial.excludeFields()));

            String className = classType.getSimpleName().toString();
            //validate
            validateFields(className, partialClassName, classFields, fields);
            validateFields(className, partialClassName, classFields, excludedFields);


            if (partial.includeFields().length > 0) {

                includedFields = classType.getEnclosedElements()
                        .stream()
                        .filter(f -> fields.contains(f.getSimpleName().toString())
                                && f instanceof VariableElement)
                        .collect(Collectors.toList());

            } else if (partial.excludeFields().length > 0) {

                includedFields = classType.getEnclosedElements()
                        .stream()
                        .filter(f -> !excludedFields.contains(f.getSimpleName().toString())
                                && f instanceof VariableElement)
                        .collect(Collectors.toList());

            }
            try {
                writePartialClassesToFile(packageName, partialClassName, includedFields, className);
            } catch (IOException e) {
                processingEnv.getMessager().printMessage(ERROR, e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private void validateFields(String className, String partialName, List<? extends Element> classFields, Set<String> fields) {
        for (String field : fields) {
            Optional<? extends Element> first = classFields.stream().filter(f -> f.getSimpleName().toString().equals(field)).findFirst();
            if (!first.isPresent()) {
                processingEnv.getMessager().printMessage(ERROR, "Error in '" + partialName + "' field '" + field + "' does not exist in class '" + className + "'");
            }
        }
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
                String upName = name.substring(0, 1).toUpperCase() + name.substring(1);
                String getterName = "get" + upName;
                String setterName = "set" + upName;
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
                String upName = name.substring(0, 1).toUpperCase() + name.substring(1);
                String setterName = "set" + upName;

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
        return new HashSet<>(asList(MongoDto.class.getName()));
    }

    public static void main(String[] args) {
        System.out.println(MongoDto.class.getCanonicalName());
    }

    private String extractPackageName(String fullQualifiedName) {
        return fullQualifiedName.substring(0, fullQualifiedName.lastIndexOf('.'));
    }

    private void writeFieldClassToFile(String packageName, Map<String, List<String>> classFields) throws IOException {
        JavaFileObject builderFile = processingEnv.getFiler().createSourceFile(packageName + ".Fields");

        try (PrintWriter out = new PrintWriter(builderFile.openWriter())) {
            if (packageName != null) {
                out.print("package ");
                out.print(packageName);
                out.println(";");
                out.println();
            }

            out.println("public class Fields {");

            for (Map.Entry<String, List<String>> clazz : classFields.entrySet()) {

                String className = clazz.getKey();

                out.print("public static class ");
                out.print(className);
                out.print(" {");
                out.println();

                for (String field : clazz.getValue()) {
                    out.print("public static final String ");
                    out.print(field);
                    out.print(" = ");
                    out.print("\"");
                    out.print(field);
                    out.println("\";");
                }

                out.println("}");
            }

            out.println("}");
        }

    }
}

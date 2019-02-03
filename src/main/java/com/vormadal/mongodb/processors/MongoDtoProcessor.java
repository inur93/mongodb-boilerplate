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
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
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
            Set<? extends Element> annotatedElements = roundEnv.getElementsAnnotatedWith(annotation);
            Set<TypeElement> types = ElementFilter.typesIn(annotatedElements);

            String packageName = null;
            int highestOrderPackage = 99;
            Map<String, List<String>> classFields = new HashMap<>();
            for (TypeElement classType : types) {

                //the qualified name contains package and class name.
                String qualifiedName = classType.getQualifiedName().toString();
                String className = classType.getSimpleName().toString();
                //try to find the best fit package for the Fields class.
                int rank = qualifiedName.length() - qualifiedName.replace(".", "").length();
                if (rank < highestOrderPackage) {
                    highestOrderPackage = rank;
                    packageName = extractPackageName(qualifiedName);
                }

                processPartialDtos(classType, extractPackageName(qualifiedName));

                List<String> fieldNames = new ArrayList<>();
                List<? extends Element> fields = classType.getEnclosedElements();

                for (Element field : fields) {
                    if (field instanceof VariableElement) {
                        fieldNames.add(field.getSimpleName().toString());
                    }
                }

                classFields.put(className, fieldNames);
            }
            try {
                writeFieldClassToFile(packageName, classFields);
            } catch (IOException e) {
                error(e.getMessage(), null);
            }
        }
        return true;
    }

    private void processPartialDtos(TypeElement classType, String packageName) {

        MongoDto dtoAnnotation = classType.getAnnotation(MongoDto.class);
        String className = classType.getSimpleName().toString();

        for (PartialDto partial : dtoAnnotation.partials()) {
            String partialClassName = partial.name();

            List<Element> fields = new ArrayList<>();
            List<? extends Element> classFields = getClassFields(classType);
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

    private List<? extends Element> getClassFields(TypeElement classType) {

        List<? extends Element> enclosedElements = classType.getEnclosedElements();
        List<Element> list = new ArrayList<>(enclosedElements);

            TypeMirror superclass = classType.getSuperclass();
            if (!superclass.getKind().equals(TypeKind.NONE)) {
                DeclaredType declaredType = (DeclaredType) classType.getSuperclass();
                try{
                    TypeElement typeElement = (TypeElement) declaredType.asElement();
                    List<? extends Element> classFields = getClassFields(typeElement);
                    list.addAll(classFields);
                }catch (Throwable e){
                    error(e.getMessage(), classType);
                }
            }
        return list;
    }

    private void validateFields(Element element, List<? extends Element> classFields, Set<String> fields) {
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

    public String getterName(Element field){
        String name = field.getSimpleName().toString();
        String upName = name.substring(0, 1).toUpperCase() + name.substring(1);
        return "get" + upName;
    }

    public String setterName(Element field){
        String name = field.getSimpleName().toString();
        String upName = name.substring(0, 1).toUpperCase() + name.substring(1);
        return "set" + upName;
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

    private void info(String message, Element element){
        processingEnv.getMessager().printMessage(NOTE, message, element);
    }
    private void error(String message, Element element){
        processingEnv.getMessager().printMessage(ERROR, message, element);
    }
}

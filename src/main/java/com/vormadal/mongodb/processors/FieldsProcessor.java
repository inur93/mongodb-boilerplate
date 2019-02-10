package com.vormadal.mongodb.processors;

import com.google.auto.service.AutoService;
import com.vormadal.mongodb.annotations.FieldsClass;

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

/**
 * <p>Created: 10-02-2019</p>
 * <p>author: Runi</p>
 */
@AutoService(Processor.class)
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class FieldsProcessor extends BaseProcessor {
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {

        for (TypeElement annotation : annotations) {
            Set<? extends Element> annotatedElements = roundEnv.getElementsAnnotatedWith(annotation);
            Set<TypeElement> types = ElementFilter.typesIn(annotatedElements);

           //package, class, fields
            Map<String, Map<String, List<String>>> classFields = new HashMap<>();
            for (TypeElement classType : types) {

                //the qualified name contains package and class name.
                String qualifiedName = classType.getQualifiedName().toString();
                String packageName = extractPackageName(qualifiedName);
                String className = classType.getSimpleName().toString();

                classFields.putIfAbsent(packageName, new HashMap<>());

                FieldsClass dtoAnnotation = classType.getAnnotation(FieldsClass.class);
                List<String> fieldNames = new ArrayList<>();
                Set<? extends Element> fields = getClassFields(classType, dtoAnnotation.includeInheritedFields(), new HashSet<>());
                Set<String> existingFields = new HashSet<>();
                for (Element field : fields) {
                    String name = field.getSimpleName().toString();
                    if ( field instanceof VariableElement) {
                        if(!existingFields.contains(name)) {
                            fieldNames.add(name);
                        }else{
                            info("duplicate field " + name, field);
                        }
                    }
                    existingFields.add(name);
                }

                classFields.get(packageName).put(className, fieldNames);
            }
            try {
                writeFieldClassToFile(classFields);
            } catch (IOException e) {
                error(e.getMessage(), null);
            }
        }
        return true;
    }


    private void writeFieldClassToFile(Map<String, Map<String, List<String>>> classFields) throws IOException {
        for(Map.Entry<String, Map<String, List<String>>> entry : classFields.entrySet()) {
            String packageName = entry.getKey();
            JavaFileObject builderFile = processingEnv.getFiler().createSourceFile(packageName + ".Fields");

            try (PrintWriter out = new PrintWriter(builderFile.openWriter())) {
                if (packageName != null) {
                    out.print("package ");
                    out.print(packageName);
                    out.println(";");
                    out.println();
                }

                out.println("public class Fields {");

                for (Map.Entry<String, List<String>> clazz : entry.getValue().entrySet()) {

                    String className = clazz.getKey();
                    out.print("public static class ");
                    out.print(className);
                    out.print(" {");
                    out.println();

                    //fields
                    List<String> fields = clazz.getValue();
                    for (String field : fields) {
                        out.print("public static final String ");
                        out.print(field);
                        out.print(" = ");
                        out.print("\"");
                        out.print(field);
                        out.println("\";");
                    }

                    //values
                    out.print("public static String[] ");
                    if(fields.contains("values")) out.print("_values");
                    else out.print("values");
                    out.print("= new String[]{");

                    for(String field : fields){
                        out.print("\"");
                        out.print(field);
                        out.print("\"");
                        if(fields.indexOf(field) < fields.size()-1) out.println(",");
                    }
                    out.println("};");

                    out.println("}");
                }

                out.println("}");
            }
        }

    }


    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return new HashSet<>(Collections.singletonList(FieldsClass.class.getName()));
    }
}

package com.vormadal.mongodb.processors;

import javax.annotation.processing.AbstractProcessor;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static javax.tools.Diagnostic.Kind.ERROR;
import static javax.tools.Diagnostic.Kind.NOTE;

/**
 * <p>Created: 10-02-2019</p>
 * <p>author: Runi</p>
 */

abstract class BaseProcessor extends AbstractProcessor {

    Set<? extends Element> getClassFields(TypeElement classType, boolean includeInheritedFields, Set<String> existing) {

        List<? extends Element> enclosedElements = classType.getEnclosedElements();
        //filter any duplicate fields
        Set<Element> list = enclosedElements
                .stream()
                .filter(f -> {
                    String name = f.getSimpleName().toString();
                    boolean filter = !existing.contains(name);
                    existing.add(name);
                    return filter;
                })
                .collect(Collectors.toSet());

        TypeMirror superclass = classType.getSuperclass();
        if (includeInheritedFields && !superclass.getKind().equals(TypeKind.NONE)) {
            DeclaredType declaredType = (DeclaredType) classType.getSuperclass();
            try{
                TypeElement typeElement = (TypeElement) declaredType.asElement();
                Set<? extends Element> classFields = getClassFields(typeElement, true, existing);
                list.addAll(classFields);
            }catch (Throwable e){
                error(e.getMessage(), classType);
            }
        }
        return list;
    }

    String extractPackageName(String fullQualifiedName) {
        return fullQualifiedName.substring(0, fullQualifiedName.lastIndexOf('.'));
    }

    void info(String message, Element element){
        processingEnv.getMessager().printMessage(NOTE, message, element);
    }
    void error(String message, Element element){
        processingEnv.getMessager().printMessage(ERROR, message, element);
    }

}

package com.vormadal.mongodb.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>Created: 01-02-2019</p>
 * <p>author: Runi</p>
 */
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.ANNOTATION_TYPE)
public @interface PartialDto {
    String name();
    String[] includeFields() default {};
    String[] excludeFields() default {};
}

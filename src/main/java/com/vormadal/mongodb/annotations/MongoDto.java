package com.vormadal.mongodb.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>Created: 31-01-2019</p>
 * <p>author: Runi</p>
 */
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.TYPE)
public @interface MongoDto {
    PartialDto[] partials() default {};
}

package com.vormadal.mongodb.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>Created: 31-01-2019</p>
 * <p>author: Runi</p>
 *
 * <p>
 *     When a class is annotated with {@link MongoDto} a static class will be created
 *     inside the Fields class containing the name of all of this classes fields.
 * </p>
 * <p>
 *     This can be useful when querying or updating a mongo collection.
 * </p>
 */
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.TYPE)
public @interface MongoDto {
    /**
     * see {@link PartialDto} for more information.
     */
    PartialDto[] partials() default {};
}

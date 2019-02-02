package com.vormadal.mongodb.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>Created: 01-02-2019</p>
 * <p>author: Runi</p>
 *
 * <p>
 *     Creates a partial class of the annotated class,
 *     containing a getter to get an instance of the annotated class with
 *     the current instance values.
 * </p>
 * <p>
 *  use 'name' to set the name of the partial class.
 *  use 'includeFields' if partial class should only contain a small subset of fields
 *  use 'excludeFields' if partial class should contain most fields with some exceptions.
 * </p>
 * <p>
 *     NOTE: if any of the given fields in 'includeFields' or 'excludeFields'
 *     do not exist in annotated class an error will be thrown.
 *     Or if any of the fields do not have a setter a compile error will occur.
 * </p>
 */
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.ANNOTATION_TYPE)
public @interface PartialDto {
    String name();
    String[] includeFields() default {};
    String[] excludeFields() default {};
}

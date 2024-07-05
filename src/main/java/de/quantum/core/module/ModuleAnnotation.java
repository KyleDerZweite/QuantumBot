package de.quantum.core.module;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ModuleAnnotation {
    String moduleName();
    String moduleDescription();
    String moduleAuthorName() default "Anonymous";
    String moduleAuthorID() default "";
    String moduleVersion() default "v0.0.0";
}

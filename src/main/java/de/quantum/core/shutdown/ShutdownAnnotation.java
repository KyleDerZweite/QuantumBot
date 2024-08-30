package de.quantum.core.shutdown;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ShutdownAnnotation {
    boolean shutdownLast() default false; // add a priority element with a default value
}

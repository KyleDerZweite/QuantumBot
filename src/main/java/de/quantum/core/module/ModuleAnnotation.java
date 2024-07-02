package de.quantum.core.module;

public @interface ModuleAnnotation {
    String moduleName();
    String moduleDescription();
    String moduleAuthorName() default "Anonymous";
    String moduleAuthorID() default "";
    String moduleVersion() default "v0.0.0";
}

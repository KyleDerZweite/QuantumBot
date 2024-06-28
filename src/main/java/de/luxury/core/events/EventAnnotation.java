package de.luxury.core.events;

import net.dv8tion.jda.api.events.GenericEvent;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface EventAnnotation {
    Class<? extends GenericEvent>[] value() default {};
}

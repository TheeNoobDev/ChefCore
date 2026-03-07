package net.chefcraft.core.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Documented
@Target(value = {TYPE, FIELD, METHOD})
@Retention(RUNTIME)
@Inherited
public @interface Clientbound { }

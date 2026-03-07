package net.chefcraft.core.annotation;

import java.lang.annotation.*;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Documented
@Target(value = {TYPE, FIELD, METHOD, ElementType.CONSTRUCTOR})
@Retention(RUNTIME)
@Inherited
public @interface NotSafe {
}

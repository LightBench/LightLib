package com.frahhs.lightlib.feature.annotations;

import com.frahhs.lightlib.feature.FeaturePriority;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Feature {
    FeaturePriority priority() default FeaturePriority.NORMAL;
    String configComment() default "";
}

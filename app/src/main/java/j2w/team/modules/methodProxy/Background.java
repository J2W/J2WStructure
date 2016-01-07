package j2w.team.modules.methodProxy;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import j2w.team.modules.threadpool.BackgroundType;

/**
 * Created by sky on 15/2/20.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Background {

	BackgroundType value() default BackgroundType.HTTP;
}

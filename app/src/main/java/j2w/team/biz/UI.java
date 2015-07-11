package j2w.team.biz;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Created by sky on 15/2/27.
 */
@Documented
@Target(TYPE)
@Retention(RUNTIME)
public @interface UI {
	Class value();
}

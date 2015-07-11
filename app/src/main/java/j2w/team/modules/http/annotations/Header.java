package j2w.team.modules.http.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Created by sky on 15/2/24.
 */
@Documented
@Retention(RUNTIME)
@Target(PARAMETER)
public @interface Header {

	String value();
}

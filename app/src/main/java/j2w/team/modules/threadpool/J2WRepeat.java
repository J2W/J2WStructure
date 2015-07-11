package j2w.team.modules.threadpool;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @创建人 sky
 * @创建时间 15/4/15 下午9:31
 * @类描述 方法是否重复
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface J2WRepeat {

	boolean value() default true;
}

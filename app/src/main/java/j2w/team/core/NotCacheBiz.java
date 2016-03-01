package j2w.team.core;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * @创建人 sky
 * @创建时间 15/4/15 下午9:31
 * @类描述 方法是否重复 - 默认不可重复
 */

@Documented
@Target(TYPE)
@Retention(RUNTIME)
public @interface NotCacheBiz {
	boolean value() default true;
}
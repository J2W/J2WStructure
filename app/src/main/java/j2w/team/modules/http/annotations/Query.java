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
@Target(PARAMETER)
@Retention(RUNTIME)
public @interface Query {

	/**
	 * 查询参数的名称.
	 */
	String value();

	/**
	 * 指定是否链接# value() URL编码
	 */
	boolean encodeName() default false;

	/**
	 * 指定是否要带注释的方法的参数的参数值进行URL编码
	 */
	boolean encodeValue() default true;
}
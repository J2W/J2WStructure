package j2w.team.modules.methodProxy;


import j2w.team.J2WHelper;

/**
 * TODO 测试
 * 
 * @创建人 sky
 * @创建时间 16/2/29
 * @类描述
 */
public class J2WAspectBackground {

	// private static final String POINTCUT_METHOD =
	// "@annotation(j2w.team.modules.methodProxy.Background)";
	//
	// @Pointcut(POINTCUT_METHOD) public void methodAnnotatedWithBackground() {}
	//
	// @Around("methodAnnotatedWithBackground()") public Object
	// weaveJoinPoint(final ProceedingJoinPoint joinPoint) throws Throwable {
	// MethodSignature methodSignature = (MethodSignature)
	// joinPoint.getSignature();
	// StringBuilder key = new StringBuilder();
	// key.append(joinPoint.getTarget());
	// key.append(".");
	// key.append(joinPoint.getSignature().toShortString());
	// J2WMethod j2WMethod =
	// J2WHelper.methodsProxy().createMehtod(key.toString(),
	// methodSignature.getMethod(),
	// joinPoint.getSignature().getDeclaringType());
	// return j2WMethod.invoke(joinPoint);
	// }

}

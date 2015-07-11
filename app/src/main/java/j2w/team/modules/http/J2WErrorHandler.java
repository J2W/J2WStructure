package j2w.team.modules.http;

import j2w.team.common.log.L;

/**
 * Created by sky on 15/2/24.
 */
public interface J2WErrorHandler {

	Throwable handleError(J2WError cause);

	J2WErrorHandler	DEFAULT	= new J2WErrorHandler() {

								@Override public Throwable handleError(J2WError cause) {
									L.e(cause.toString());
									return cause;
								}
							};
}
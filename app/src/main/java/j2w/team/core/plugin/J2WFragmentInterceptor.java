package j2w.team.core.plugin;

import android.os.Bundle;

import j2w.team.view.J2WFragment;

/**
 * @创建人 sky
 * @创建时间 16/1/6
 * @类描述 fragment拦截器
 */
public interface J2WFragmentInterceptor {

	void onFragmentCreated(J2WFragment j2WFragment,Bundle bundle, Bundle savedInstanceState);

	void onFragmentStart(J2WFragment j2WFragment);

	void onFragmentResume(J2WFragment j2WFragment);

	void onFragmentPause(J2WFragment j2WFragment);

	void onFragmentStop(J2WFragment j2WFragment);

	void onFragmentDestroy(J2WFragment j2WFragment);

	J2WFragmentInterceptor NONE = new J2WFragmentInterceptor() {
		@Override
		public void onFragmentCreated(J2WFragment j2WFragment, Bundle bundle, Bundle savedInstanceState) {

		}

		@Override
		public void onFragmentStart(J2WFragment j2WFragment) {

		}

		@Override
		public void onFragmentResume(J2WFragment j2WFragment) {

		}

		@Override
		public void onFragmentPause(J2WFragment j2WFragment) {

		}

		@Override
		public void onFragmentStop(J2WFragment j2WFragment) {

		}

		@Override
		public void onFragmentDestroy(J2WFragment j2WFragment) {

		}
	};
}

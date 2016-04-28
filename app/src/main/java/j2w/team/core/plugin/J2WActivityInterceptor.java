package j2w.team.core.plugin;

import android.os.Bundle;

import j2w.team.view.J2WActivity;
import j2w.team.view.J2WBuilder;

/**
 * @创建人 sky
 * @创建时间 16/1/6
 * @类描述 activity拦截器
 */
public interface J2WActivityInterceptor {

	void build(J2WBuilder initialJ2WBuilder);

	void onCreate(J2WActivity j2WIView, Bundle bundle,Bundle savedInstanceState);

	void onStart(J2WActivity j2WIView);

	void onResume(J2WActivity j2WIView);

	void onPause(J2WActivity j2WIView);

	void onStop(J2WActivity j2WIView);

	void onDestroy(J2WActivity j2WIView);

	void onRestart(J2WActivity j2WIView);

	J2WActivityInterceptor NONE = new J2WActivityInterceptor() {
		@Override
		public void build(J2WBuilder initialJ2WBuilder) {

		}

		@Override
		public void onCreate(J2WActivity j2WIView, Bundle bundle, Bundle savedInstanceState) {

		}

		@Override
		public void onStart(J2WActivity j2WIView) {

		}

		@Override
		public void onResume(J2WActivity j2WIView) {

		}

		@Override
		public void onPause(J2WActivity j2WIView) {

		}

		@Override
		public void onStop(J2WActivity j2WIView) {

		}

		@Override
		public void onDestroy(J2WActivity j2WIView) {

		}

		@Override
		public void onRestart(J2WActivity j2WIView) {

		}
	};

}

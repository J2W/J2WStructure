package j2w.team.core;

/**
 * Created by sky on 15/2/20.
 */
public abstract class J2WRunnable implements Runnable {

	protected final String	name;

	public J2WRunnable(String format, Object... args) {
		this.name = String.format(format, args);
	}

	@Override public final void run() {
		// 获取当前线程名称
		String oldName = Thread.currentThread().getName();
		// 设置新名称
		Thread.currentThread().setName(name);
		try {
			// 执行
			execute();
		} finally {
			// 还原名称
			Thread.currentThread().setName(oldName);
		}
	}

	protected abstract void execute();
}

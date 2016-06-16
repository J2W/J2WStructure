# J2WStructure
接受不完美的自己，享受不断完善的自己 我们的承诺是，每天都要有进步

Gradle 版本
-----------------------------------
1.classpath 'com.android.tools.build:gradle:1.2.3'<br />
2.版本 - gradle-2.4-all.zip<br />

项目引用
-----------------------------------
Project-build.gradle

     buildscript {
         repositories {
             //从中央库里面获取依赖
             jcenter()
         }
         dependencies {
             classpath 'com.android.tools.build:gradle:2.1.2'
         }
     }

     allprojects {
         repositories {
             jcenter()
             //远程仓库
             maven { url "https://github.com/J2W/mvn-repo-j2w/raw/master/repository" }
         }
     }

App-build.gradle:

     android {
        //配置信息
        packagingOptions {
        exclude 'META-INF/services/javax.annotation.processing.Processor'
        exclude 'META-INF/LICENSE.txt'
        exclude 'META-INF/NOTICE.txt'
        }
     }

     dependencies {
        compile fileTree(dir: 'libs', include: ['*.jar'])
        compile 'j2w.team:structure:2.0.6'
     }
###静态代理配置
build.gradle: 

    buildscript {
        repositories {
            //从中央库里面获取依赖
            jcenter()
        }
        dependencies {
            classpath 'org.aspectj:aspectjtools:1.8.1'
        }
    }
    app.gradle
    dependencies {
      compile 'org.aspectj:aspectjrt:1.8.6'
    }
    
    import org.aspectj.bridge.IMessage
    import org.aspectj.bridge.MessageHandler
    import org.aspectj.tools.ajc.Main
    
    //app
    //android.applicationVariants.all { variant ->
    //library
    android.libraryVariants.all { variant ->
        JavaCompile javaCompile = variant.javaCompile
        javaCompile.doLast {
            String[] args = [
                    "-showWeaveInfo",
                    "-1.5",
                    "-inpath", javaCompile.destinationDir.toString(),
                    "-aspectpath", javaCompile.classpath.asPath,
                    "-d", javaCompile.destinationDir.toString(),
                    "-classpath", javaCompile.classpath.asPath,
                    "-bootclasspath", android.bootClasspath.join(File.pathSeparator)
            ]

            MessageHandler handler = new MessageHandler(true);
            new Main().run(args, handler)

            def log = project.logger
            for (IMessage message : handler.getMessages(null, true)) {
                switch (message.getKind()) {
                    case IMessage.ABORT:
                    case IMessage.ERROR:
                    case IMessage.FAIL:
                        log.error message.message, message.thrown
                        break;
                    case IMessage.WARNING:
                    case IMessage.INFO:
                        log.info message.message, message.thrown
                        break;
                    case IMessage.DEBUG:
                        log.debug message.message, message.thrown
                        break;
                }
            }
        }
    }

MVP使用说明帮助
-----------------------------------
## 继承 J2WAppliaction

[J2WRestAdapter生成方法 点击查看说明](https://github.com/J2W/J2WStructure/wiki/J2WRestAdapter)

    public abstract J2WRestAdapter getRestAdapter() //生成方法 点上面连接

    public abstract boolean isLogOpen() //是否打印日志 true 打印 false  不打印

        /**
    	 * 设置全局异常
    	 *
    	 * @return
    	 */
    	@Override public Thread.UncaughtExceptionHandler getExceptionHandler() {
    		return new ExceptionHandler();
    	}

    	/**
    	 * 自定义管理器
    	 *
    	 * @return
    	 */
    	@Override public J2WModulesManage getModulesManage() {
    		return new KMModulesManage(this);
    	}

    	/**
    	 * 自定义帮助类 默认 J2WHelper
    	 *
    	 * @param j2WModulesManage
    	 */
    	@Override public void initHelper(J2WModulesManage j2WModulesManage) {
    		Helper.with(j2WModulesManage);
    	}

    	/**
    	 * 网络适配器
    	 *
    	 * @param builder
    	 * @return
    	 */
    	@Override public J2WRestAdapter getRestAdapter(J2WRestAdapter.Builder builder) {
    		builder.setEndpoint(new KMURLEndpoint());
    		builder.setRequestInterceptor(new KMRequestInterceptor());
    		builder.setResponseInterceptor(new KMResponseInterceptor());
    		builder.setConverter(new KMConverter());
    		builder.setCookieManage(new KMCookieHandler());
    		builder.setTimeOut(60);
    		return builder.build();
    	}

    	/**
    	 * 拦截器配置
    	 *
    	 * @param builder
    	 * @return
    	 */
    	@Override public J2WMethods getMethodInterceptor(J2WMethods.Builder builder) {
    		builder.setActivityInterceptor(new KMActivityInterceptor());
    		builder.setFragmentInterceptor(new KMFragmentInterceptor());
    		builder.addStartInterceptor(new KMMethodStartInterceptor());
    		builder.addEndInterceptor(new KMMethodEndInterceptor());
    		builder.addEndImplInterceptor(new KMDBMethodImplEndInterceptor());
    		builder.addEndImplInterceptor(new KMActionImplEndInterceptor());
    		builder.addErrorInterceptor(new KMMethodErrorInterceptor());
    		builder.addHttpErrorInterceptor(new KMHttpErrorInterceptor());
    		return builder.build();
    	}

    	/** ***** 公共进度布局 *********/
    	@Override public int layoutLoading() {
    		return 0;
    	}

    	@Override public int layoutEmpty() {
    		return 0;
    	}

    	@Override public int layoutBizError() {
    		return 0;
    	}

    	@Override public int layoutHttpError() {
    		return 0;
    	}

## Android studio 模板 Editor->File and Code Templates-> + (添加)

    J2WActivity

    import android.os.Bundle;
    import j2w.team.core.Impl;
    import j2w.team.view.J2WBuilder;
    import j2w.team.view.J2WActivity;

    /**
     * @创建人 ${USER}
     * @创建时间 ${DATE} ${TIME}
     * @类描述 一句话描述 你的UI
     */
    public class ${NAME}Activity extends J2WActivity<I${NAME}Biz> implements I${NAME}Activity {

    	@Override protected J2WBuilder build(J2WBuilder j2WBuilder) {
    		return j2WBuilder;
    	}

    	@Override protected void initData(Bundle bundle) {

    	}

    }
    @Impl(${NAME}Activity.class)
    interface I${NAME}Activity {

    }

    J2WFragment

    import android.os.Bundle;
    import j2w.team.core.Impl;
    import j2w.team.view.J2WBuilder;
    import j2w.team.view.J2WFragment;

    /**
     * @创建人 ${USER}
     * @创建时间 ${DATE} ${TIME}
     * @类描述 一句话描述 你的UI
     */
    public class ${NAME}Fragment extends J2WFragment<I${NAME}Biz> implements I${NAME}Fragment {

        @Override protected J2WBuilder build(J2WBuilder j2WBuilder) {
            return j2WBuilder;
        }

        @Override protected void initData(Bundle bundle) {

        }

    }
    @Impl(${NAME}Fragment.class)
    interface I${NAME}Fragment {

    }

    J2WDialogFragment

    import android.os.Bundle;
    import j2w.team.core.Impl;
    import j2w.team.view.J2WBuilder;
    import j2w.team.view.J2WDialogFragment;

    /**
     * @创建人 ${USER}
     * @创建时间 ${DATE} ${TIME}
     * @类描述 一句话描述 你的UI
     */
    public class ${NAME}DialogFragment extends J2WDialogFragment<I${NAME}Biz> implements I${NAME}DialogFragment {

        @Override protected J2WBuilder build(J2WBuilder j2WBuilder) {
            return j2WBuilder;
        }

        @Override protected void initData(Bundle bundle) {

        }

    }
    @Impl(${NAME}DialogFragment.class)
    interface I${NAME}DialogFragment {

    }

    J2WIBiz - Activity , Fragment , DialogFragment

    Activity

    import j2w.team.core.Impl;
    import j2w.team.core.J2WBiz;
    import j2w.team.core.J2WIBiz;

    /**
     * @创建人 ${USER}
     * @创建时间 ${DATE} ${TIME}
     * @类描述 一句话描述你的业务
     */
    @Impl(${NAME}Biz.class)
    public interface I${NAME}Biz extends J2WIBiz {

    }
    class ${NAME}Biz extends J2WBiz<I${NAME}Activity> implements I${NAME}Biz
    {

    }

    DialogFragment

    import j2w.team.core.Impl;
    import j2w.team.core.J2WBiz;
    import j2w.team.core.J2WIBiz;

    /**
     * @创建人 ${USER}
     * @创建时间 ${DATE} ${TIME}
     * @类描述 一句话描述你的业务
     */
    @Impl(${NAME}Biz.class)
    public interface I${NAME}Biz extends J2WIBiz {

    }
    class ${NAME}Biz extends J2WBiz<I${NAME}DialogFragment> implements I${NAME}Biz
    {

    }


    Fragment

    import j2w.team.core.Impl;
    import j2w.team.core.J2WBiz;
    import j2w.team.core.J2WIBiz;

    /**
     * @创建人 ${USER}
     * @创建时间 ${DATE} ${TIME}
     * @类描述 一句话描述你的业务
     */
    @Impl(${NAME}Biz.class)
    public interface I${NAME}Biz extends J2WIBiz {

    }
    class ${NAME}Biz extends J2WBiz<I${NAME}Fragment> implements I${NAME}Biz
    {

    }



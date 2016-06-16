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


#### Activity 生命周期公共回调方法 说明:可以重写下面方法来做公共的事情

    onSaveInstanceState(J2WActivity j2WIView, Bundle outState)

    onCreate(J2WActivity j2WIView, Bundle bundle)

    onStart(J2WActivity j2WIView)

    onResume(J2WActivity j2WIView)

    onPause(J2WActivity j2WIView)

    onStop(J2WActivity j2WIView)

    onDestroy(J2WActivity j2WIView)

    onRestart(J2WActivity j2WIView)

    //状态布局 - 加载
    @Override public int layoutLoading() {
    	return R.layout.j2w_fragment_loading;
    }
    //状态布局 - 空布局
    @Override public int layoutEmpty() {
    	return R.layout.j2w_fragment_empty;
    }
    //状态布局 - 业务错误布局
    @Override public int layoutBizError() {
    	return R.layout.j2w_fragment_bizerror;
    }
    //状态布局 - 网络错误布局
    @Override public int layoutHttpError() {
    	return R.layout.j2w_fragment_error;
    }

## View : J2WActivity<J2WIDisplay> Biz : J2WBiz<AndroidIDisplay>

#### Display 说明:  Intent跳转,toolbar,DrawerView 统一控制

    接口 : 继承 J2WIDisplay
    实现类: 继承 J2WDisplay
    使用 : super.display()

#### BIZ  业务处理

    接口 : 继承 J2WIBiz 并 注解 @Impl(实现类)   //必须要写
    实现类: 继承 J2WBiz
    使用 : super.biz(MainIBiz.class)   //参数:业务接口Class
    API提供:
        1. 方法 @Background(BackgroundType.HTTP) 注解 子线程执行方法 注: @Background 默认网络线程池
                参数类型     BackgroundType.HTTP        并行 网络线程池
                            BackgroundType.Work        并行 工作线程池
                            BackgroundType.SINGLEWORK  串行 工作线程池
        2. 方法 @J2WRepeat(true) 注解  方法是否可以重复执行  注: 默认可以重复执行
        3. 方法执行完毕后,需要回调View层进行更新UI
               提供方法: super.ui(HomeUI.class) //参数:显示层接口Class

    提供 ：所有方法拦截
    createImpl(Interface.class);
    注解 ：@Interceptor　Interface接口 方法
    J2WBiz 统一回调  interceptorImpl(Class clazz) 方法
#### UI 显示层处理

    接口 : 注解 @Impl(实现类)   //必须要写
    实现类: 继承 J2WActivity 或 J2WFragment
    使用 : super.ui(HomeUI.class) //参数:显示层接口Class
    API提供
        1.需要执行业务处理时，调用业务接口进行处理
            提供方法 : super.biz(MainIBiz.class)   //参数:业务接口Class



## Android studio 模板

    Editor->File and Code Templates-> + (添加)

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
        class ${NAME}Biz extends J2WBiz<I${NAME}DialogFragment> implements I${NAME}Biz
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


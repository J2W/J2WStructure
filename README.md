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
             classpath 'com.android.tools.build:gradle:1.2.3'
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
        compile 'j2w.team:structure:1.0.0'
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

#### UI 显示层处理

    接口 : 注解 @Impl(实现类)   //必须要写
    实现类: 继承 J2WActivity 或 J2WFragment
    使用 : super.ui(HomeUI.class) //参数:显示层接口Class
    API提供
        1.需要执行业务处理时，调用业务接口进行处理
            提供方法 : super.biz(MainIBiz.class)   //参数:业务接口Class



## J2WHelper 帮助API说明

[J2WThreadPoolManager threadPoolHelper() //获取线程池](https://github.com/J2W/J2WStructure/wiki/ThreadPoolHelper)

[SynchronousExecutor mainLooper() //获取主线程](https://github.com/J2W/J2WStructure/wiki/MainLooper)

    J2WApplication getInstance() //获取Application全局上下文

    J2WRestAdapter httpAdapter() //获取网络适配器,创建接口实例

    J2WIScreenManager screenHelper() //获取Activity堆栈管理

    J2WDownloadManager downloader() //获取下载管理器，用于下载文件

    PicassoTools picassoHelper() //获取Picasso图片下载，已经做了很多优化的第三方图片下载

    eventPost(event) //发送Event

## J2WBuilder 使用API说明

### 状态栏颜色

	j2WBuilder.tintColor(R.color.theme_color);


### Toobar


    样式修改 - 重写

        <style name="J2WToolbar.Custom">
           <!-- 设置该属性解决空白部分 默认 16dp-->
           <item name="contentInsetStart">16dp</item>
           <item name="android:layout_width">match_parent</item>
           <item name="android:layout_height">?actionBarSize</item>
           <item name="android:background">?colorPrimary</item>
        </style>

    文字颜色 - 添加
        <style name="MyAppTheme" parent="AppTheme">
            <item name="colorPrimary">@color/default_color</item>
            <item name="colorPrimaryDark">@color/default_color</item>
            <item name="colorAccent">@color/default_color</item>
            <!-- 文字颜色 -->
            <item name="android:textColorPrimary">@android:color/white</item>
            <!-- Menu文字颜色 -->
            <item name="android:actionMenuTextColor">@android:color/black</item>
        </style>

    toolbarDrawerId(int toolbarDrawerId); //设置DrawerLayout ID， 与Toolbar联动

    toolbarMenuListener(Toolbar.OnMenuItemClickListener menuListener); //设置MENU 点击事件

    toolbarIsOpen(boolean isOpenToolbar); //设置是否打开Toolbar

    toolbarMenuId(int toolbarMenuId);//设置MENU布局

### EventBus

    isOpenEventBus(boolean isOpenEventBus) // 设置是否打开EventBus

### ListView

    listHeaderLayoutId(int listHeaderLayoutId); //设置头布局

    listFooterLayoutId(int listFooterLayoutId); //设置尾布局

    listViewOnItemClick(AdapterView.OnItemClickListener itemListener); //设置列表点击事件

    listViewOnItemLongClick(AdapterView.OnItemLongClickListener itemLongListener);//设置列表长按事件

    listViewId(int listId, J2WAdapterItem j2WAdapterItem); //设置列表ID 和 适配器Item

    listViewId(int listId, J2WListViewMultiLayout j2WListViewMultiLayout); //设置列表ID 和 多布局接口

    listSwipRefreshId(int swipRefreshId, J2WRefreshListener j2WRefreshListener); //设置 下拉刷新布局ID  和 事件(包含加载更多)

    listSwipeColorResIds(int... colorResIds); //设置下拉刷新控件颜色

### RecyclerView (替代 ListView GridView)

[J2WRVAdapterItem生成方法 点击查看说明](https://github.com/J2W/J2WStructure/wiki/J2WRVAdapterItem)

    recyclerviewId(int recyclerviewId); //设置View ID

    recyclerviewAdapterItem(J2WRVAdapterItem j2WRVAdapterItem)//设置适配器 ，多布局可在适配器里实现

    //线性布局管理器
    参数:1.列表方向, 2.分割线, 3.动画, 4.列表反转
    recyclerviewLinearLayoutManager(int direction, RecyclerView.ItemDecoration itemDecoration, RecyclerView.ItemAnimator itemAnimator, boolean... reverseLayout)

    //Grid布局管理器
    参数:1.列表方向, 2.多少列  3.分割线, 4.动画, 4.列表反转
    recyclerviewGridLayoutManager(int direction, int spanCount, RecyclerView.ItemDecoration itemDecoration, RecyclerView.ItemAnimator itemAnimator, boolean... reverseLayout)

    //瀑布布局管理器
    参数:1.列表方向, 2.多少列  3.分割线, 4.动画, 4.列表反转
    recyclerviewStaggeredGridyoutManager(int direction, int spanCount, RecyclerView.ItemDecoration itemDecoration, RecyclerView.ItemAnimator itemAnimator, boolean... reverseLayout)

    ecyclerviewSwipRefreshId(int recyclerviewSwipRefreshId, J2WRefreshListener recyclerviewJ2WRefreshListener);//设置 下拉刷新布局ID  和 事件(包含加载更多)

    recyclerviewColorResIds(int... recyclerviewColorResIds)//下拉刷新控件 颜色

### ViewPager

    //ViewPager

    //设置TabId 和 类型
    1、J2WBuilder.TABS_TYPE_CUSTOM - 自定义  2、J2WBuilder.TABS_TYPE_COUNT -  数量
    3、J2WBuilder.TABS_TYPE_ICON   - 图标    4、J2WBuilder.TABS_TYPE_DEFAULT - 默认
    viewPagerTabsId(int tabsId, int tabsType)

    tabsCustomListener(J2WTabsCustomListener j2WTabsCustomListener) // 如果tabsType ＝ J2WBuilder.TABS_TYPE_CUSTOM  必须实现

    viewPagerId(int viewpagerId, FragmentManager fragmentManager); //设置VP 布局ID 和 碎片管理器

    viewPagerChangeListener(J2WViewPagerChangeListener viewPagerChangeListener) //设置滑动事件

    viewPageroffScreenPageLimit(int viewPageroffScreenPageLimit) //设置预加载数量 最小 1

    //Tabs

    tabsShouldExpand(boolean tabsShouldExpand) //设置Tab是自动填充满屏幕的

    tabsDividerColor(int tabsDividerColor) //设置Tab的分割线是透明的

    tabsUnderlineHeight(int tabsUnderlineHeight)//设置Tab底部线的高度

    tabsUnderlineColor(int tabsUnderlineColor) //设置Tab底部线的颜色

    tabsIndicatorHeight(int tabsIndicatorHeight) //设置Tab Indicator 指示灯的高度

    tabsIndicatorColor(int tabsIndicatorColor) //设置Tab Indicator 指示灯的颜色

    tabsTextSize(int tabsTextSize) // 设置Tab 文字大小

    tabsSelectedTextColor(int tabsSelectedTextColor) // 设置选中Tab文字的颜色

    tabsTextColor(int tabsTextColor) // 设置Tab的文字颜色

    tabsBackgroundResource(int tabsBackgroundResource) // 背景颜色

    tabsTabBackground(int tabsTabBackground) //Tabs Item 背景颜色

    tabsTabWidth(int tabsTabWidth) // 设置每个Tab宽度

    tabsIsCurrentItemAnimation(boolean tabsIsCurrentItemAnimation)  // 设置切换是否有动画


整体结构使用项目
-----------------------------------

[完整使用用例](https://github.com/skyJinc/J2WStructureTest)



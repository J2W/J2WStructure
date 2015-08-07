package j2w.team.modules.fresco;

import android.content.res.Resources;
import android.net.Uri;

import com.facebook.drawee.backends.pipeline.PipelineDraweeControllerBuilder;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.request.ImageRequestBuilder;

/**
 * @创建人 sky
 * @创建时间 15/8/5 下午4:19
 * @类描述 接口
 */
public interface IFresco {

	/**
	 * 初始化
	 */
	void initialize();

	/**
	 * 获取 自定义视图
	 * 
	 * @return
	 */
	GenericDraweeHierarchyBuilder hierarchyBuilder(Resources resources);

	/**
	 * 获取 图片请求 地址
	 * 
	 * @return
	 */
	ImageRequestBuilder imageRequestBuilder(Uri uri);

	ImageRequestBuilder imageRequestBuilder(int res);

	/**
	 * 获取 控制器
	 * 
	 * @return
	 */
	PipelineDraweeControllerBuilder controllerBuilder();

	/**
	 * 功能 - 渐进式加载图片 - 影响显示速度
	 *
	 *
	 * @param simpleDraweeView
	 *            控件
	 * @param uri
	 *            地址
	 */
	void intoProgressive(SimpleDraweeView simpleDraweeView, Uri uri);

	/**
	 * 功能 - 多图请求
	 * 
	 * @param simpleDraweeView
	 *            控件
	 * @param lowResUri
	 *            低分辨率图
	 * @param highResUri
	 *            高分辨率图
	 */
	void into(SimpleDraweeView simpleDraweeView, Uri lowResUri, Uri highResUri);

	/**
	 * 功能 - 缩略图 - 仅支持本地 jpeg格式
	 * 
	 * @param simpleDraweeView
	 * @param uri
	 */
	void intoThumbnails(SimpleDraweeView simpleDraweeView, Uri uri);

	/**
	 * 功能 - 动态图
	 * 
	 * @param simpleDraweeView
	 * @param uri
	 */
	void intoGIF(SimpleDraweeView simpleDraweeView, Uri uri);

}
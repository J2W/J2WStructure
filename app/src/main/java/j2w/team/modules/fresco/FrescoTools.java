package j2w.team.modules.fresco;

import android.content.Context;
import android.content.res.Resources;
import android.net.Uri;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.backends.pipeline.PipelineDraweeController;
import com.facebook.drawee.backends.pipeline.PipelineDraweeControllerBuilder;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;

import j2w.team.common.utils.J2WCheckUtils;

/**
 * @创建人 sky
 * @创建时间 15/8/5 下午4:11
 * @类描述 Fresco 图片架构
 */
public class FrescoTools implements IFresco {

	private final ImagePipelineConfig		imagePipelineConfig;

	private final Context					context;

	private GenericDraweeHierarchyBuilder	hierarchyBuilder;		// 自定义视图

	public FrescoTools(Context context, ImagePipelineConfig imagePipelineConfig) {
		this.context = J2WCheckUtils.checkNotNull(context, "Application上下文不能为空");
		this.imagePipelineConfig = J2WCheckUtils.checkNotNull(imagePipelineConfig, "Fresco图片架构配置不能为空");
		initialize();
	}

	@Override public void initialize() {
		Fresco.initialize(context, imagePipelineConfig);
	}

	@Override public GenericDraweeHierarchyBuilder hierarchyBuilder(Resources resources) {
		if (hierarchyBuilder == null) {
			hierarchyBuilder = new GenericDraweeHierarchyBuilder(resources);
		} else {
			hierarchyBuilder.reset();
		}
		return hierarchyBuilder;
	}

	@Override public ImageRequestBuilder imageRequestBuilder(Uri uri) {
		return ImageRequestBuilder.newBuilderWithSource(uri);
	}

	@Override public ImageRequestBuilder imageRequestBuilder(int res) {
		return ImageRequestBuilder.newBuilderWithResourceId(res);
	}

	@Override public PipelineDraweeControllerBuilder controllerBuilder() {
		return Fresco.newDraweeControllerBuilder();
	}

	@Override public void intoProgressive(SimpleDraweeView simpleDraweeView, Uri uri) {
		ImageRequest request = imageRequestBuilder(uri).setProgressiveRenderingEnabled(true).build();
		PipelineDraweeController controller = (PipelineDraweeController) controllerBuilder().setOldController(simpleDraweeView.getController()).setImageRequest(request).build();
		simpleDraweeView.setController(controller);
	}

	@Override public void into(SimpleDraweeView simpleDraweeView, Uri lowResUri, Uri highResUri) {
		PipelineDraweeController controller = (PipelineDraweeController) controllerBuilder().setLowResImageRequest(ImageRequest.fromUri(lowResUri)).setImageRequest(ImageRequest.fromUri(highResUri))
				.setOldController(simpleDraweeView.getController()).build();
		simpleDraweeView.setController(controller);
	}

	@Override public void intoThumbnails(SimpleDraweeView simpleDraweeView, Uri uri) {
		ImageRequest request = imageRequestBuilder(uri).setLocalThumbnailPreviewsEnabled(true).build();
		PipelineDraweeController controller = (PipelineDraweeController) controllerBuilder().setOldController(simpleDraweeView.getController()).setImageRequest(request).build();
		simpleDraweeView.setController(controller);
	}

	@Override public void intoGIF(SimpleDraweeView simpleDraweeView, Uri uri) {
		PipelineDraweeController controller = (PipelineDraweeController) controllerBuilder().setLowResImageRequest(ImageRequest.fromUri(uri)).setAutoPlayAnimations(true)
				.setOldController(simpleDraweeView.getController()).build();
		simpleDraweeView.setController(controller);
	}

}
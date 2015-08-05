package j2w.team.modules.fresco;

import android.content.Context;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.core.ImagePipelineConfig;

import j2w.team.common.utils.J2WCheckUtils;

/**
 * @创建人 sky
 * @创建时间 15/8/5 下午4:11
 * @类描述 Fresco 图片架构
 */
public class FrescoTools implements IFresco {

	private final ImagePipelineConfig	imagePipelineConfig;

	private final Context				context;

	public FrescoTools(Context context, ImagePipelineConfig imagePipelineConfig) {
		this.context = J2WCheckUtils.checkNotNull(context, "Application上下文不能为空");
		this.imagePipelineConfig = J2WCheckUtils.checkNotNull(imagePipelineConfig, "Fresco图片架构配置不能为空");
		initialize();
	}

	@Override public void initialize() {
		Fresco.initialize(context, imagePipelineConfig);
	}
}
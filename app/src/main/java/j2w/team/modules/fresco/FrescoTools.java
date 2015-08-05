package j2w.team.modules.fresco;

import com.facebook.imagepipeline.core.ImagePipelineConfig;

import j2w.team.common.utils.J2WCheckUtils;

/**
 * @创建人 sky
 * @创建时间 15/8/5 下午4:11
 * @类描述 Fresco 图片架构
 */
public class FrescoTools implements IFresco {

	private final ImagePipelineConfig	imagePipelineConfig;

	public FrescoTools(ImagePipelineConfig imagePipelineConfig) {
		this.imagePipelineConfig = J2WCheckUtils.checkNotNull(imagePipelineConfig, "Fresco图片架构配置不能为空");
	}

}
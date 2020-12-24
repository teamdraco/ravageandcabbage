package superlord.ravagecabbage.client.render;

import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.util.ResourceLocation;
import superlord.ravagecabbage.RavageAndCabbage;
import superlord.ravagecabbage.entity.CabbagerEntity;
import superlord.ravagecabbage.client.model.CabbagerModel;

public class CabbagerRenderer extends MobRenderer<CabbagerEntity, CabbagerModel<CabbagerEntity>> {

	private static final ResourceLocation TEXTURE = new ResourceLocation(RavageAndCabbage.MOD_ID, "textures/entity/cabbager.png");
	
	public CabbagerRenderer(EntityRendererManager renderManagerIn) {
		super(renderManagerIn, new CabbagerModel<>(), 0.5F);
	}
	
	public ResourceLocation getEntityTexture(CabbagerEntity entity) {
		return TEXTURE;
	}
	
}

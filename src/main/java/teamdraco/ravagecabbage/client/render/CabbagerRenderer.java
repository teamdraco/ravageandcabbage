package teamdraco.ravagecabbage.client.render;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import teamdraco.ravagecabbage.RavageAndCabbage;
import teamdraco.ravagecabbage.client.ClientEvents;
import teamdraco.ravagecabbage.client.model.CabbagerModel;
import teamdraco.ravagecabbage.common.entities.CabbagerEntity;

@SuppressWarnings("rawtypes")
public class CabbagerRenderer extends MobRenderer<CabbagerEntity, EntityModel<CabbagerEntity>> {

	private static final ResourceLocation TEXTURE = new ResourceLocation(RavageAndCabbage.MOD_ID, "textures/entity/cabbager.png");
	
    @SuppressWarnings({ "unchecked" })
	public CabbagerRenderer(EntityRendererProvider.Context context) {
        super(context, new CabbagerModel(context.bakeLayer(ClientEvents.CABBAGER)), 0.5F);
    }

	@Override
	public ResourceLocation getTextureLocation(CabbagerEntity p_114482_) {
		return TEXTURE;
	}

}
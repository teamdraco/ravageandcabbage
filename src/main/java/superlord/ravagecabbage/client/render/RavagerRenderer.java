package superlord.ravagecabbage.client.render;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.util.ResourceLocation;
import superlord.ravagecabbage.RavageAndCabbage;
import superlord.ravagecabbage.entity.RavageAndCabbageRavagerEntity;
import superlord.ravagecabbage.client.model.RCRavagerModel;

public class RavagerRenderer extends MobRenderer<RavageAndCabbageRavagerEntity, EntityModel<RavageAndCabbageRavagerEntity>> {

	private static final RCRavagerModel RAVAGER_MODEL = new RCRavagerModel();
	public static final ResourceLocation TEXTURE = new ResourceLocation(RavageAndCabbage.MOD_ID, "textures/entity/tamed_ravager.png");
    private static final ResourceLocation SADDLE_TEXTURE = new ResourceLocation(RavageAndCabbage.MOD_ID, "textures/entity/saddle.png");
    private static final ResourceLocation BABY_TEXTURE = new ResourceLocation(RavageAndCabbage.MOD_ID, "textures/entity/ravager_baby.png");

    public RavagerRenderer() {
        super(Minecraft.getInstance().getRenderManager(), RAVAGER_MODEL, 1.1F);
    }

	public void render(RavageAndCabbageRavagerEntity entityIn, float entityYaw, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn) {
		entityModel = RAVAGER_MODEL;
        super.render(entityIn, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
    }
	
	protected void preRenderCallback(RavageAndCabbageRavagerEntity entity, MatrixStack matrixStackIn, float partialTickTime) {
		 if(entity.isChild()) {
			 matrixStackIn.scale(0.5F, 0.5F, 0.5F);
		 }
	 }

    public ResourceLocation getEntityTexture(RavageAndCabbageRavagerEntity entity) {
        if (entity.isChild()) {
            return BABY_TEXTURE;
        } else if (entity.isHorseSaddled()) {
        	return SADDLE_TEXTURE;
        } else {
        	return TEXTURE;
        }
    }

	
}

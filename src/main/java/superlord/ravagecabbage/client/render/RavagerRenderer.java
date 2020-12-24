package superlord.ravagecabbage.client.render;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.util.ResourceLocation;
import superlord.ravagecabbage.RavageAndCabbage;
import superlord.ravagecabbage.entity.RavageAndCabbageRavagerEntity;
import superlord.ravagecabbage.client.model.BabyRavagerModel;
import superlord.ravagecabbage.client.model.RCRavagerModel;

public class RavagerRenderer extends MobRenderer<RavageAndCabbageRavagerEntity, EntityModel<RavageAndCabbageRavagerEntity>> {

	private static final RCRavagerModel RAVAGER_MODEL = new RCRavagerModel();
	@SuppressWarnings("rawtypes")
	private static final BabyRavagerModel CHILD_MODEL = new BabyRavagerModel();
	public static final ResourceLocation TEXTURE = new ResourceLocation(RavageAndCabbage.MOD_ID, "textures/entity/tamed_ravager.png");
    private static final ResourceLocation SADDLE_TEXTURE = new ResourceLocation("textures/entity/illager/ravager.png");
    private static final ResourceLocation BABY_TEXTURE = new ResourceLocation(RavageAndCabbage.MOD_ID, "textures/entity/ravager_baby.png");

    public RavagerRenderer() {
        super(Minecraft.getInstance().getRenderManager(), RAVAGER_MODEL, 2F);
    }

    @SuppressWarnings("unchecked")
	public void render(RavageAndCabbageRavagerEntity entityIn, float entityYaw, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn) {
    	if (entityIn.isChild()) {
            entityModel = CHILD_MODEL;
        } else {
            entityModel = RAVAGER_MODEL;
        }
        super.render(entityIn, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
    }

    public ResourceLocation getEntityTexture(RavageAndCabbageRavagerEntity entity) {
        if (entity.isChild()) {
            return BABY_TEXTURE;
        } else if (entity.isSaddled()) {
        	return SADDLE_TEXTURE;
        } else {
        	return TEXTURE;
        }
    }

	
}

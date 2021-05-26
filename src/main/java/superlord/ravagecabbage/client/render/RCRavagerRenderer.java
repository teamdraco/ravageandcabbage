package superlord.ravagecabbage.client.render;

import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.SaddleLayer;
import net.minecraft.util.ResourceLocation;
import superlord.ravagecabbage.RavageAndCabbage;
import superlord.ravagecabbage.entity.RCRavagerEntity;
import superlord.ravagecabbage.client.model.RCRavagerModel;

public class RCRavagerRenderer extends MobRenderer<RCRavagerEntity, RCRavagerModel<RCRavagerEntity>> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(RavageAndCabbage.MOD_ID, "textures/entity/tamed_ravager.png");
    private static final ResourceLocation SADDLE_TEXTURE = new ResourceLocation(RavageAndCabbage.MOD_ID, "textures/entity/ravager_equipment/saddle.png");
    private static final ResourceLocation BABY_TEXTURE = new ResourceLocation(RavageAndCabbage.MOD_ID, "textures/entity/ravager_baby.png");

    public RCRavagerRenderer(EntityRendererManager renderManagerIn) {
        super(renderManagerIn, new RCRavagerModel<>(), 1.1F);
        this.addLayer(new SaddleLayer<>(this, new RCRavagerModel<>(), SADDLE_TEXTURE));
        this.addLayer(new RCRavagerHornLayer<>(this, new RCRavagerModel<>()));
    }

    @Override
    public ResourceLocation getEntityTexture(RCRavagerEntity entity) {
        if (entity.isChild()) {
            return BABY_TEXTURE;
        }
        else {
        	return TEXTURE;
        }
    }
}

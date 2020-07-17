package coda.ravagecabbage.client.renderer.entity;

import coda.ravagecabbage.client.renderer.entity.model.CabbageRavagerModel;
import coda.ravagecabbage.entity.CabbageRavagerEntity;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class CabbageRavagerRenderer extends MobRenderer<CabbageRavagerEntity, CabbageRavagerModel> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("textures/entity/illager/ravager.png");

    public CabbageRavagerRenderer(EntityRendererManager renderManagerIn) {
        super(renderManagerIn, new CabbageRavagerModel(), 1.1F);
    }

    @Override
    public ResourceLocation getEntityTexture(CabbageRavagerEntity entity) {
        return TEXTURE;
    }
}

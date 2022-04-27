package teamdraco.ravagecabbage.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;
import teamdraco.ravagecabbage.RavageAndCabbage;
import teamdraco.ravagecabbage.client.model.RCRavagerModel;
import teamdraco.ravagecabbage.client.render.layer.RCRavagerHornLayer;
import teamdraco.ravagecabbage.client.render.layer.RCSaddleLayer;
import teamdraco.ravagecabbage.common.entities.RCRavagerEntity;

public class RCRavagerRenderer extends GeoEntityRenderer<RCRavagerEntity> {
    private static final ResourceLocation SADDLE_TEXTURE = new ResourceLocation(RavageAndCabbage.MOD_ID, "textures/entity/ravager_equipment/saddle.png");

    public RCRavagerRenderer(EntityRendererProvider.Context context) {
        super(context, new RCRavagerModel());
        this.addLayer(new RCSaddleLayer<>(this, SADDLE_TEXTURE));
        this.addLayer(new RCRavagerHornLayer(this));
        this.shadowRadius = 1.1F;
    }

    @Override
    public RenderType getRenderType(RCRavagerEntity animatable, float partialTicks, PoseStack stack, @Nullable MultiBufferSource renderTypeBuffer, @Nullable VertexConsumer vertexBuilder, int packedLightIn, ResourceLocation textureLocation) {
        return RenderType.entityTranslucent(textureLocation);
    }
}
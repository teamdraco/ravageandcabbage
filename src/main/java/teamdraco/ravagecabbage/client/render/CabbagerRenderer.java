package teamdraco.ravagecabbage.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;
import teamdraco.ravagecabbage.client.model.CabbagerModel;
import teamdraco.ravagecabbage.common.entities.CabbagerEntity;

public class CabbagerRenderer extends GeoEntityRenderer<CabbagerEntity> {

    public CabbagerRenderer(EntityRendererProvider.Context context) {
        super(context, new CabbagerModel());
        this.shadowRadius = 0.5F;
    }

    @Override
    public RenderType getRenderType(CabbagerEntity animatable, float partialTicks, PoseStack stack, @Nullable MultiBufferSource renderTypeBuffer, @Nullable VertexConsumer vertexBuilder, int packedLightIn, ResourceLocation textureLocation) {
        return RenderType.entityTranslucent(textureLocation);
    }
}
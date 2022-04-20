package teamdraco.ravagecabbage.client.render.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import software.bernie.geckolib3.renderers.geo.GeoLayerRenderer;
import software.bernie.geckolib3.renderers.geo.IGeoRenderer;
import teamdraco.ravagecabbage.RavageAndCabbage;
import teamdraco.ravagecabbage.common.entity.RCRavagerEntity;

@OnlyIn(Dist.CLIENT)
public class RCSaddleLayer<T extends RCRavagerEntity> extends GeoLayerRenderer<T> {
   private final ResourceLocation textureLocation;

   public RCSaddleLayer(IGeoRenderer<T> p_117390_, ResourceLocation p_117392_) {
      super(p_117390_);
      this.textureLocation = p_117392_;
   }

   public void render(PoseStack p_117394_, MultiBufferSource p_117395_, int p_117396_, T p_117397_, float p_117398_, float p_117399_, float p_117400_, float p_117401_, float p_117402_, float p_117403_) {
      if (p_117397_.isSaddled()) {
         VertexConsumer vertexconsumer = p_117395_.getBuffer(RenderType.entityCutoutNoCull(this.textureLocation));

         this.getRenderer().render(this.getEntityModel().getModel(this.getEntityModel().getModelLocation(p_117397_)), p_117397_, p_117398_, RenderType.entityCutoutNoCull(this.textureLocation), p_117394_, p_117395_,
                 vertexconsumer, p_117396_, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0f);
      }
   }
}
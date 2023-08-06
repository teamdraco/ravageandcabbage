package codyhuh.ravagecabbage.client.render.layer;

import codyhuh.ravagecabbage.client.model.RCRavagerModel;
import codyhuh.ravagecabbage.common.entities.RCRavagerEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Overlay;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BannerRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.BannerItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.BannerBlockEntity;

public class RCRavagerBannerLayer extends RenderLayer<RCRavagerEntity, RCRavagerModel> {
    private final ModelPart flag;

    public RCRavagerBannerLayer(RenderLayerParent<RCRavagerEntity, RCRavagerModel> entityRendererIn) {
        super(entityRendererIn);
        this.flag = Minecraft.getInstance().getEntityModels().bakeLayer(ModelLayers.BANNER).getChild("flag");
    }

    @Override
    public void render(PoseStack stack, MultiBufferSource bufferIn, int packedLightIn, RCRavagerEntity entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        if (entity.getOwner() != null) {

            ItemStack itemstack = entity.getOwner().getItemInHand(InteractionHand.MAIN_HAND);

            if (itemstack.is(ItemTags.BANNERS)) {
                stack.pushPose();
                ModelPart modelpart = this.getParentModel().root;
                modelpart.translateAndRotate(stack);
                stack.translate(-1.1875F, 1.0F, -0.9375F);
                stack.translate(0.125F, -1.5F, 0.9F);
                stack.scale(0.5F, 0.5F, 0.5F);
                stack.mulPose(Axis.YP.rotationDegrees(-90.0F));
                stack.translate(-0.5F, -0.5F, -0.5F);
                BannerRenderer.renderPatterns(stack, bufferIn, packedLightIn, OverlayTexture.NO_OVERLAY, flag, ModelBakery.BANNER_BASE, true, BannerBlockEntity.createPatterns(((BannerItem)itemstack.getItem()).getColor(), BannerBlockEntity.getItemPatterns(itemstack)));

                stack.pushPose();
                modelpart.translateAndRotate(stack);
                stack.translate(0.0F, 0.0F, -3.06F);
                BannerRenderer.renderPatterns(stack, bufferIn, packedLightIn, OverlayTexture.NO_OVERLAY, flag, ModelBakery.BANNER_BASE, true, BannerBlockEntity.createPatterns(((BannerItem)itemstack.getItem()).getColor(), BannerBlockEntity.getItemPatterns(itemstack)));
                stack.popPose();

                stack.popPose();
            }
        }
    }
}
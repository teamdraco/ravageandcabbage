package teamdraco.ravagecabbage.client.render.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import software.bernie.geckolib3.renderers.geo.GeoLayerRenderer;
import software.bernie.geckolib3.renderers.geo.IGeoRenderer;
import teamdraco.ravagecabbage.RavageAndCabbage;
import teamdraco.ravagecabbage.common.entities.RCRavagerEntity;
import teamdraco.ravagecabbage.common.items.DyeableRavagerHornArmorItem;
import teamdraco.ravagecabbage.common.items.RavagerHornArmorItem;

public class RCRavagerHornLayer extends GeoLayerRenderer<RCRavagerEntity> {
    private static final ResourceLocation MODEL = new ResourceLocation(RavageAndCabbage.MOD_ID, "geo/ravager.geo.json");

    public RCRavagerHornLayer(IGeoRenderer<RCRavagerEntity> entityRendererIn) {
        super(entityRendererIn);
    }

    @Override
    public RenderType getRenderType(ResourceLocation textureLocation) {
        return super.getRenderType(textureLocation);
    }

    @Override
    public void render(PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn, RCRavagerEntity entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        ItemStack itemstack = entity.getItemBySlot(EquipmentSlot.HEAD);
        if (itemstack.getItem() instanceof RavagerHornArmorItem) {
            RavagerHornArmorItem armor = (RavagerHornArmorItem)itemstack.getItem();
            float f;
            float f1;
            float f2;
            if (armor instanceof DyeableRavagerHornArmorItem) {
                int i = ((DyeableRavagerHornArmorItem)armor).getColor(itemstack);
                f = (float)(i >> 16 & 255) / 255.0F;
                f1 = (float)(i >> 8 & 255) / 255.0F;
                f2 = (float)(i & 255) / 255.0F;
            } else {
                f = 1.0F;
                f1 = 1.0F;
                f2 = 1.0F;
            }

            this.getRenderer().render(this.getEntityModel().getModel(this.getEntityModel().getModelLocation(entity)), entity, partialTicks, RenderType.entityCutoutNoCull(armor.getArmorTexture()), matrixStackIn, bufferIn,
                    bufferIn.getBuffer(RenderType.entityCutoutNoCull(armor.getArmorTexture())), packedLightIn, OverlayTexture.NO_OVERLAY, f, f1, f2, 1.0f);
        }
    }
}

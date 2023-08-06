package codyhuh.ravagecabbage.client.render.layer;

import codyhuh.ravagecabbage.client.model.RCRavagerModel;
import codyhuh.ravagecabbage.common.entities.RCRavagerEntity;
import codyhuh.ravagecabbage.common.items.DyeableRavagerHornArmorItem;
import codyhuh.ravagecabbage.common.items.RavagerHornArmorItem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RCRavagerHornLayer<T extends RCRavagerEntity, M extends EntityModel<T>> extends RenderLayer<T, M> {
    private RCRavagerModel model;

    public RCRavagerHornLayer(RenderLayerParent<T, M> entityRendererIn, RCRavagerModel model) {
        super(entityRendererIn);
        this.model = model;
    }

    @SuppressWarnings("unchecked")
	@Override
    public void render(PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn, T entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        ItemStack itemstack = entity.getItemBySlot(EquipmentSlot.HEAD);
        if (itemstack.getItem() instanceof RavagerHornArmorItem) {
            RavagerHornArmorItem armor = (RavagerHornArmorItem)itemstack.getItem();
            this.getParentModel().copyPropertiesTo((EntityModel<T>) this.model);
            this.model.prepareMobModel(entity, limbSwing, limbSwingAmount, partialTicks);
            this.model.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
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

            VertexConsumer ivertexbuilder = bufferIn.getBuffer(RenderType.entityCutoutNoCull(armor.getArmorTexture()));
            this.model.renderToBuffer(matrixStackIn, ivertexbuilder, packedLightIn, OverlayTexture.NO_OVERLAY, f, f1, f2, 1.0F);
        }
    }
}
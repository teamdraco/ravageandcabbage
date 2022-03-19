package teamdraco.ravagecabbage.client.old.render.layer;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import teamdraco.ravagecabbage.client.old.model.RCRavagerModel;
import teamdraco.ravagecabbage.common.entity.RCRavagerEntity;
import teamdraco.ravagecabbage.common.items.DyeableRavagerHornArmorItem;
import teamdraco.ravagecabbage.common.items.RavagerHornArmorItem;

@OnlyIn(Dist.CLIENT)
public class RCRavagerHornLayer<T extends RCRavagerEntity, M extends EntityModel<T>> extends LayerRenderer<T, M> {
    private RCRavagerModel<T> model;

    public RCRavagerHornLayer(IEntityRenderer<T, M> entityRendererIn, RCRavagerModel<T> model) {
        super(entityRendererIn);
        this.model = model;
    }

    @Override
    public void render(MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn, T entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        ItemStack itemstack = entity.getItemBySlot(EquipmentSlotType.HEAD);
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

            IVertexBuilder ivertexbuilder = bufferIn.getBuffer(RenderType.entityCutoutNoCull(armor.getArmorTexture()));
            this.model.renderToBuffer(matrixStackIn, ivertexbuilder, packedLightIn, OverlayTexture.NO_OVERLAY, f, f1, f2, 1.0F);
        }
    }
}
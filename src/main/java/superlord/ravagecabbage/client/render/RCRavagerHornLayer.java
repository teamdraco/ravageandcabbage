package superlord.ravagecabbage.client.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import superlord.ravagecabbage.RavageAndCabbage;
import superlord.ravagecabbage.entity.RCRavagerEntity;

@OnlyIn(Dist.CLIENT)
public class RCRavagerHornLayer<T extends RCRavagerEntity, M extends EntityModel<T>> extends LayerRenderer<T, M> {
    private static final ResourceLocation DIAMOND_HORNS = new ResourceLocation(RavageAndCabbage.MOD_ID, "textures/entity/ravager_equipment/diamond_horns.png");
    private final M model;

    public RCRavagerHornLayer(IEntityRenderer<T, M> entityRendererIn, M model) {
        super(entityRendererIn);
        this.model = model;
    }

    @Override
    public void render(MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn, T entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        if (entity.hasHornArmor() && entity.getItemStackFromSlot(EquipmentSlotType.HEAD).getDamage() < entity.getItemStackFromSlot(EquipmentSlotType.HEAD).getMaxDamage()) {
            this.getEntityModel().copyModelAttributesTo(model);
            this.model.setLivingAnimations(entity, limbSwing, limbSwingAmount, partialTicks);
            this.model.setRotationAngles(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
            IVertexBuilder ivertexbuilder = bufferIn.getBuffer(RenderType.getEntityCutoutNoCull(DIAMOND_HORNS));
            this.model.render(matrixStackIn, ivertexbuilder, packedLightIn, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
        }
    }
}
package superlord.ravagecabbage.client.model;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import superlord.ravagecabbage.entity.CabbagerEntity;

@OnlyIn(Dist.CLIENT)
public class CabbagerModel<T extends Entity> extends EntityModel<CabbagerEntity> {
    public ModelRenderer field_217143_g;
    public ModelRenderer field_191217_a;
    public ModelRenderer field_191223_g;
    public ModelRenderer field_217144_h;
    public ModelRenderer field_191224_h;
    public ModelRenderer bodyOverlay;
    public ModelRenderer body;
    public ModelRenderer field_217151_b;
    public ModelRenderer field_191217_a_1;
    public ModelRenderer field_217152_f;

    public CabbagerModel() {
        this.textureWidth = 128;
        this.textureHeight = 64;
        this.bodyOverlay = new ModelRenderer(this, 0, 38);
        this.bodyOverlay.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.bodyOverlay.addBox(-4.0F, 0.0F, -3.0F, 8.0F, 18.0F, 6.0F, 0.5F, 0.5F, 0.5F);
        this.body = new ModelRenderer(this, 16, 20);
        this.body.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.body.addBox(-4.0F, 0.0F, -3.0F, 8.0F, 12.0F, 6.0F, 0.0F, 0.0F, 0.0F);
        this.field_191224_h = new ModelRenderer(this, 40, 46);
        this.field_191224_h.mirror = true;
        this.field_191224_h.setRotationPoint(5.0F, 2.0F, 0.0F);
        this.field_191224_h.addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, 0.0F, 0.0F, 0.0F);
        this.field_191217_a_1 = new ModelRenderer(this, 24, 0);
        this.field_191217_a_1.setRotationPoint(0.0F, -2.0F, 0.0F);
        this.field_191217_a_1.addBox(-1.0F, -1.0F, -6.0F, 2.0F, 4.0F, 2.0F, 0.0F, 0.0F, 0.0F);
        this.field_191223_g = new ModelRenderer(this, 40, 46);
        this.field_191223_g.setRotationPoint(-5.0F, 2.0F, 0.0F);
        this.field_191223_g.addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, 0.0F, 0.0F, 0.0F);
        this.field_191217_a = new ModelRenderer(this, 0, 0);
        this.field_191217_a.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.field_191217_a.addBox(-4.0F, -10.0F, -4.0F, 8.0F, 10.0F, 8.0F, 0.0F, 0.0F, 0.0F);
        this.field_217151_b = new ModelRenderer(this, 65, 0);
        this.field_217151_b.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.field_217151_b.addBox(-4.0F, -10.0F, -4.0F, 8.0F, 10.0F, 8.0F, 0.5F, 0.5F, 0.5F);
        this.field_217143_g = new ModelRenderer(this, 0, 22);
        this.field_217143_g.setRotationPoint(-2.0F, 12.0F, 0.0F);
        this.field_217143_g.addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, 0.0F, 0.0F, 0.0F);
        this.field_217144_h = new ModelRenderer(this, 0, 22);
        this.field_217144_h.mirror = true;
        this.field_217144_h.setRotationPoint(2.0F, 12.0F, 0.0F);
        this.field_217144_h.addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, 0.0F, 0.0F, 0.0F);
        this.field_217152_f = new ModelRenderer(this, 59, 47);
        this.field_217152_f.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.field_217152_f.addBox(-8.0F, -8.0F, -6.0F, 16.0F, 16.0F, 1.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(field_217152_f, -1.5707963267948966F, 0.0F, 0.0F);
        this.field_191217_a.addChild(this.field_191217_a_1);
        this.field_217151_b.addChild(this.field_217152_f);
        this.field_191217_a.addChild(this.field_217151_b);
    }

    @Override
    public void setRotationAngles(CabbagerEntity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.field_191217_a.rotateAngleY = netHeadYaw * ((float) Math.PI / 180F);
        this.field_191217_a.rotateAngleX = headPitch * ((float) Math.PI / 180F);
//        this.arms.rotationPointY = 3.0F;
//        this.arms.rotationPointZ = -1.0F;
//        this.arms.rotateAngleX = -0.75F;
        if (this.isSitting) {
            this.field_191223_g.rotateAngleX = (-(float) Math.PI / 5F);
            this.field_191223_g.rotateAngleY = 0.0F;
            this.field_191223_g.rotateAngleZ = 0.0F;
            this.field_191224_h.rotateAngleX = (-(float) Math.PI / 5F);
            this.field_191224_h.rotateAngleY = 0.0F;
            this.field_191224_h.rotateAngleZ = 0.0F;
            this.field_217143_g.rotateAngleX = -1.4137167F;
            this.field_217143_g.rotateAngleY = ((float) Math.PI / 10F);
            this.field_217143_g.rotateAngleZ = 0.07853982F;
            this.field_217144_h.rotateAngleX = -1.4137167F;
            this.field_217144_h.rotateAngleY = (-(float) Math.PI / 10F);
            this.field_217144_h.rotateAngleZ = -0.07853982F;
        } else {
            this.field_191223_g.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F + (float) Math.PI) * 2.0F * limbSwingAmount * 0.5F;
            this.field_191223_g.rotateAngleY = 0.0F;
            this.field_191223_g.rotateAngleZ = 0.0F;
            this.field_191224_h.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F) * 2.0F * limbSwingAmount * 0.5F;
            this.field_191224_h.rotateAngleY = 0.0F;
            this.field_191224_h.rotateAngleZ = 0.0F;
            this.field_217143_g.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount * 0.5F;
            this.field_217143_g.rotateAngleY = 0.0F;
            this.field_217143_g.rotateAngleZ = 0.0F;
            this.field_217144_h.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F + (float) Math.PI) * 1.4F * limbSwingAmount * 0.5F;
            this.field_217144_h.rotateAngleY = 0.0F;
            this.field_217144_h.rotateAngleZ = 0.0F;
        }
    }

    @Override
    public void render(MatrixStack matrixStackIn, IVertexBuilder bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) { 
        ImmutableList.of(this.body, this.bodyOverlay, this.field_191224_h, this.field_191223_g, this.field_191217_a, this.field_217143_g, this.field_217144_h).forEach((modelRenderer) -> {
            modelRenderer.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
        });
    }

    public void setRotateAngle(ModelRenderer modelRenderer, float x, float y, float z) {
        modelRenderer.rotateAngleX = x;
        modelRenderer.rotateAngleY = y;
        modelRenderer.rotateAngleZ = z;
    }
}

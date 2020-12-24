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
import superlord.ravagecabbage.entity.RavageAndCabbageRavagerEntity;

/**
 * BabyRavager - Coda1552
 * Created using Tabula 8.0.0
 */
@OnlyIn(Dist.CLIENT)
public class BabyRavagerModel<T extends Entity> extends EntityModel<RavageAndCabbageRavagerEntity> {
    public ModelRenderer chest;
    public ModelRenderer hips;
    public ModelRenderer armLeft;
    public ModelRenderer armRight;
    public ModelRenderer neck;
    public ModelRenderer legRight;
    public ModelRenderer legLeft;
    public ModelRenderer head;
    public ModelRenderer jaw;
    public ModelRenderer hornLeft;
    public ModelRenderer hornRight;
    public ModelRenderer head_1;

    public BabyRavagerModel() {
        this.textureWidth = 96;
        this.textureHeight = 64;
        this.hornRight = new ModelRenderer(this, 62, 26);
        this.hornRight.setRotationPoint(-5.0F, -8.5F, -6.0F);
        this.hornRight.addBox(-2.0F, -1.5F, -6.0F, 2.0F, 3.0F, 6.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(hornRight, -0.5235987755982988F, 0.0F, 0.0F);
        this.head = new ModelRenderer(this, 24, 26);
        this.head.setRotationPoint(0.0F, 8.0F, -8.0F);
        this.head.addBox(-5.0F, -13.0F, -10.0F, 10.0F, 12.0F, 10.0F, 0.0F, 0.0F, 0.0F);
        this.legLeft = new ModelRenderer(this, 0, 26);
        this.legLeft.mirror = true;
        this.legLeft.setRotationPoint(5.0F, -8.5F, 6.0F);
        this.legLeft.addBox(-3.0F, 0.0F, -3.0F, 6.0F, 25.0F, 6.0F, 0.0F, 0.0F, 0.0F);
        this.head_1 = new ModelRenderer(this, 0, 0);
        this.head_1.setRotationPoint(0.0F, -4.0F, -10.0F);
        this.head_1.addBox(-1.5F, -1.6F, -3.0F, 3.0F, 6.0F, 3.0F, 0.0F, 0.0F, 0.0F);
        this.chest = new ModelRenderer(this, 0, 0);
        this.chest.setRotationPoint(0.0F, 7.0F, -3.0F);
        this.chest.addBox(-5.5F, -7.0F, -6.0F, 11.0F, 14.0F, 12.0F, 0.0F, 0.0F, 0.0F);
        this.jaw = new ModelRenderer(this, 14, 52);
        this.jaw.setRotationPoint(0.0F, -1.0F, 0.0F);
        this.jaw.addBox(-5.0F, -1.0F, -10.0F, 10.0F, 2.0F, 10.0F, 0.0F, 0.0F, 0.0F);
        this.armLeft = new ModelRenderer(this, 0, 26);
        this.armLeft.mirror = true;
        this.armLeft.setRotationPoint(5.0F, -8.0F, -4.0F);
        this.armLeft.addBox(-3.0F, 0.0F, -3.0F, 6.0F, 25.0F, 6.0F, 0.0F, 0.0F, 0.0F);
        this.neck = new ModelRenderer(this, 54, 43);
        this.neck.setRotationPoint(0.0F, -1.0F, 0.0F);
        this.neck.addBox(-3.5F, -1.5F, -14.0F, 7.0F, 7.0F, 14.0F, 0.0F, 0.0F, 0.0F);
        this.armRight = new ModelRenderer(this, 0, 26);
        this.armRight.mirror = true;
        this.armRight.setRotationPoint(-5.0F, -8.0F, -4.0F);
        this.armRight.addBox(-3.0F, 0.0F, -3.0F, 6.0F, 25.0F, 6.0F, 0.0F, 0.0F, 0.0F);
        this.hips = new ModelRenderer(this, 46, 0);
        this.hips.setRotationPoint(0.0F, 0.5F, 6.0F);
        this.hips.addBox(-4.0F, -5.5F, 0.0F, 8.0F, 12.0F, 10.0F, 0.0F, 0.0F, 0.0F);
        this.legRight = new ModelRenderer(this, 0, 26);
        this.legRight.mirror = true;
        this.legRight.setRotationPoint(-5.0F, -8.5F, 6.0F);
        this.legRight.addBox(-3.0F, 0.0F, -3.0F, 6.0F, 25.0F, 6.0F, 0.0F, 0.0F, 0.0F);
        this.hornLeft = new ModelRenderer(this, 62, 26);
        this.hornLeft.setRotationPoint(5.0F, -8.5F, -6.0F);
        this.hornLeft.addBox(0.0F, -1.5F, -6.0F, 2.0F, 3.0F, 6.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(hornLeft, -0.5235987755982988F, 0.0F, 0.0F);
        this.head.addChild(this.hornRight);
        this.neck.addChild(this.head);
        this.hips.addChild(this.legLeft);
        this.head.addChild(this.head_1);
        this.head.addChild(this.jaw);
        this.chest.addChild(this.armLeft);
        this.chest.addChild(this.neck);
        this.chest.addChild(this.armRight);
        this.chest.addChild(this.hips);
        this.hips.addChild(this.legRight);
        this.head.addChild(this.hornLeft);
    }

    @Override
    public void render(MatrixStack matrixStackIn, IVertexBuilder bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) { 
        ImmutableList.of(this.chest).forEach((modelRenderer) -> { 
            modelRenderer.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
        });
    }

    @Override
    public void setRotationAngles(RavageAndCabbageRavagerEntity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
    	this.neck.rotateAngleX = headPitch * ((float)Math.PI / 180F);
		this.neck.rotateAngleY = netHeadYaw * ((float)Math.PI / 180F);
		float f = 0.4F * limbSwingAmount;
		this.legRight.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F) * f;
		this.legLeft.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F + (float)Math.PI) * f;
		this.armRight.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F + (float)Math.PI) * f;
		this.armLeft.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F) * f;
    }

    /**
     * This is a helper function from Tabula to set the rotation of model parts
     */
    public void setRotateAngle(ModelRenderer modelRenderer, float x, float y, float z) {
        modelRenderer.rotateAngleX = x;
        modelRenderer.rotateAngleY = y;
        modelRenderer.rotateAngleZ = z;
    }
}

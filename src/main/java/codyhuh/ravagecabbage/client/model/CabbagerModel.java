package codyhuh.ravagecabbage.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;

// Made with Blockbench 4.2.5
// Exported for Minecraft version 1.17 - 1.18 with Mojang mappings
// Paste this class into your mod and generate all required imports


public class CabbagerModel<T extends Entity> extends EntityModel<T> {
	// This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(new ResourceLocation("modid", "cabbagermodel"), "main");
	private final ModelPart field_217143_g;
	private final ModelPart field_191217_a;
	private final ModelPart field_191223_g;
	private final ModelPart field_217144_h;
	private final ModelPart field_191224_h;
	private final ModelPart bodyOverlay;
	private final ModelPart body;

	public CabbagerModel(ModelPart root) {
		this.field_217143_g = root.getChild("field_217143_g");
		this.field_191217_a = root.getChild("field_191217_a");
		this.field_191223_g = root.getChild("field_191223_g");
		this.field_217144_h = root.getChild("field_217144_h");
		this.field_191224_h = root.getChild("field_191224_h");
		this.bodyOverlay = root.getChild("bodyOverlay");
		this.body = root.getChild("body");
	}

	@SuppressWarnings("unused")
	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition field_217143_g = partdefinition.addOrReplaceChild("field_217143_g", CubeListBuilder.create().texOffs(0, 22).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(-2.0F, 12.0F, 0.0F));

		PartDefinition field_191217_a = partdefinition.addOrReplaceChild("field_191217_a", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -10.0F, -4.0F, 8.0F, 10.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition field_191217_a_1 = field_191217_a.addOrReplaceChild("field_191217_a_1", CubeListBuilder.create().texOffs(24, 0).addBox(-1.0F, -1.0F, -6.0F, 2.0F, 4.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -2.0F, 0.0F));

		PartDefinition field_217151_b = field_191217_a.addOrReplaceChild("field_217151_b", CubeListBuilder.create().texOffs(65, 0).addBox(-4.0F, -10.0F, -4.0F, 8.0F, 10.0F, 8.0F, new CubeDeformation(0.5F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition field_217152_f = field_217151_b.addOrReplaceChild("field_217152_f", CubeListBuilder.create().texOffs(59, 47).addBox(-8.0F, -8.0F, -6.0F, 16.0F, 16.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -1.5708F, 0.0F, 0.0F));

		PartDefinition field_191223_g = partdefinition.addOrReplaceChild("field_191223_g", CubeListBuilder.create().texOffs(40, 46).addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(-5.0F, 2.0F, 0.0F));

		PartDefinition field_217144_h = partdefinition.addOrReplaceChild("field_217144_h", CubeListBuilder.create().texOffs(0, 22).mirror().addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(2.0F, 12.0F, 0.0F));

		PartDefinition field_191224_h = partdefinition.addOrReplaceChild("field_191224_h", CubeListBuilder.create().texOffs(40, 46).mirror().addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(5.0F, 2.0F, 0.0F));

		PartDefinition bodyOverlay = partdefinition.addOrReplaceChild("bodyOverlay", CubeListBuilder.create().texOffs(0, 38).addBox(-4.0F, 0.0F, -3.0F, 8.0F, 18.0F, 6.0F, new CubeDeformation(0.5F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(16, 20).addBox(-4.0F, 0.0F, -3.0F, 8.0F, 12.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		return LayerDefinition.create(meshdefinition, 128, 64);
	}

	@Override
	public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		 this.field_191217_a.yRot = netHeadYaw * ((float) Math.PI / 180F);
	        this.field_191217_a.xRot = headPitch * ((float) Math.PI / 180F);
//	        this.arms.rotationPointY = 3.0F;
//	        this.arms.rotationPointZ = -1.0F;
//	        this.arms.rotateAngleX = -0.75F;
	        if (this.riding) {
	            this.field_191223_g.xRot = (-(float) Math.PI / 5F);
	            this.field_191223_g.yRot = 0.0F;
	            this.field_191223_g.zRot = 0.0F;
	            this.field_191224_h.xRot = (-(float) Math.PI / 5F);
	            this.field_191224_h.yRot = 0.0F;
	            this.field_191224_h.zRot = 0.0F;
	            this.field_217143_g.xRot = -1.4137167F;
	            this.field_217143_g.yRot = ((float) Math.PI / 10F);
	            this.field_217143_g.zRot = 0.07853982F;
	            this.field_217144_h.xRot = -1.4137167F;
	            this.field_217144_h.yRot = (-(float) Math.PI / 10F);
	            this.field_217144_h.zRot = -0.07853982F;
	        } else {
	            this.field_191223_g.xRot = Mth.cos(limbSwing * 0.6662F + (float) Math.PI) * 2.0F * limbSwingAmount * 0.5F;
	            this.field_191223_g.yRot = 0.0F;
	            this.field_191223_g.zRot = 0.0F;
	            this.field_191224_h.xRot = Mth.cos(limbSwing * 0.6662F) * 2.0F * limbSwingAmount * 0.5F;
	            this.field_191224_h.yRot = 0.0F;
	            this.field_191224_h.zRot = 0.0F;
	            this.field_217143_g.xRot = Mth.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount * 0.5F;
	            this.field_217143_g.yRot = 0.0F;
	            this.field_217143_g.zRot = 0.0F;
	            this.field_217144_h.xRot = Mth.cos(limbSwing * 0.6662F + (float) Math.PI) * 1.4F * limbSwingAmount * 0.5F;
	            this.field_217144_h.yRot = 0.0F;
	            this.field_217144_h.zRot = 0.0F;
	        }
	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		field_217143_g.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
		field_191217_a.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
		field_191223_g.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
		field_217144_h.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
		field_191224_h.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
		bodyOverlay.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
		body.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
	}
}
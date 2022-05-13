package teamdraco.ravagecabbage.client.model;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.processor.IBone;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.model.provider.data.EntityModelData;
import teamdraco.ravagecabbage.RavageAndCabbage;
import teamdraco.ravagecabbage.common.entities.RCRavagerEntity;

public class RCRavagerModel extends AnimatedGeoModel<RCRavagerEntity> {

    @Override
    public ResourceLocation getModelLocation(RCRavagerEntity entity) {
        return new ResourceLocation(RavageAndCabbage.MOD_ID, "geo/ravager.geo.json");
    }

    @Override
    public ResourceLocation getTextureLocation(RCRavagerEntity entity) {
        return entity.isBaby() ? new ResourceLocation(RavageAndCabbage.MOD_ID, "textures/entity/ravager_baby.png") : new ResourceLocation(RavageAndCabbage.MOD_ID, "textures/entity/tamed_ravager.png");
    }

    @Override
    public ResourceLocation getAnimationFileLocation(RCRavagerEntity entity) {
        return new ResourceLocation(RavageAndCabbage.MOD_ID, "animations/ravager.animation.json");
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
    public void setLivingAnimations(RCRavagerEntity entity, Integer uniqueID, AnimationEvent customPredicate) {
        super.setLivingAnimations(entity, uniqueID, customPredicate);
        IBone neck = this.getAnimationProcessor().getBone("neck");
        IBone jaw = this.getAnimationProcessor().getBone("mouth");
        EntityModelData extraData = (EntityModelData) customPredicate.getExtraDataOfType(EntityModelData.class).get(0);

        neck.setRotationY(extraData.netHeadYaw * ((float) Math.PI / 180F));
        neck.setRotationX(extraData.headPitch * ((float) Math.PI / 180F));

        int i = entity.getAttackTick();
        int j = entity.getAttackTick();
        int l = entity.getRoarTick();
        if (l > 0) {
            float f = Mth.triangleWave((float)l - customPredicate.getPartialTick(), 10.0F);
            float f1 = (1.0F + f) * 0.5F;
            float f2 = f1 * f1 * f1 * 12.0F;
            float f3 = f2 * Mth.sin(neck.getRotationX());
            neck.setPositionZ(-6.5F + f2);
            neck.setPositionY(-7.0F - f3);
            float f4 = Mth.sin(((float)l - customPredicate.getPartialTick()) / 10.0F * (float)Math.PI * 0.25F);
            jaw.setRotationX(((float)Math.PI / 2F) * f4);
            if (l > 5) {
                jaw.setRotationX(Mth.sin(((float)(-4 + l) - customPredicate.getPartialTick()) / 4.0F) * (float)Math.PI * 0.4F);
            } else {
                jaw.setRotationX(0.15707964F * Mth.sin((float)Math.PI * ((float)l - customPredicate.getPartialTick()) / 10.0F));
            }
        } else {
            float f6 = -1.0F * Mth.sin(neck.getRotationX());
            neck.setPositionX(0.0F);
            neck.setPositionY(-7.0F - f6);
            neck.setPositionZ(5.5F);
            boolean flag = i > 0;
            neck.setRotationX(flag ? 0.21991149F : 0.0F);
            jaw.setRotationX((float)Math.PI * (flag ? 0.05F : 0.01F));
            if (flag) {
                double d0 = (double)i / 40.0D;
                neck.setRotationX((float)Math.sin(d0 * 10.0D) * 3.0F);
            } else if (j > 0) {
                float f7 = Mth.sin(((float) (20 - j) - customPredicate.getPartialTick()) / 20.0F * (float) Math.PI * 0.25F);
                jaw.setRotationX(((float) Math.PI / 2F) * f7);
            }
        }
    }
}

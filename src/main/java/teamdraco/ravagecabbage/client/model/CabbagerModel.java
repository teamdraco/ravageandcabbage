package teamdraco.ravagecabbage.client.model;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.processor.IBone;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.model.provider.data.EntityModelData;
import teamdraco.ravagecabbage.RavageAndCabbage;
import teamdraco.ravagecabbage.common.entity.CabbagerEntity;

public class CabbagerModel extends AnimatedGeoModel<CabbagerEntity> {

    @Override
    public ResourceLocation getModelLocation(CabbagerEntity entity) {
        return new ResourceLocation(RavageAndCabbage.MOD_ID, "geo/entity/cabbager.geo.json");
    }

    @Override
    public ResourceLocation getTextureLocation(CabbagerEntity entity) {
        return new ResourceLocation(RavageAndCabbage.MOD_ID, "textures/entity/cabbager.png");
    }

    @Override
    public ResourceLocation getAnimationFileLocation(CabbagerEntity entity) {
        return new ResourceLocation(RavageAndCabbage.MOD_ID, "animations/entity/cabbager.animations.json");
    }

    @Override
    public void setLivingAnimations(CabbagerEntity entity, Integer uniqueID, AnimationEvent customPredicate) {
        super.setLivingAnimations(entity, uniqueID, customPredicate);
        IBone body = this.getAnimationProcessor().getBone("body");
        EntityModelData extraData = (EntityModelData) customPredicate.getExtraDataOfType(EntityModelData.class).get(0);

        if (!entity.isInWater()) {
            body.setRotationZ(1.5708f);
        }
        else {
            body.setRotationX(extraData.netHeadYaw * (float)Math.PI / 180F);
            body.setRotationY(extraData.headPitch * (float)Math.PI / 180F);
        }
    }
}

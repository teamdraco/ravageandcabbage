package teamdraco.ravagecabbage.client.render;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.layers.SaddleLayer;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;
import teamdraco.ravagecabbage.RavageAndCabbage;
import teamdraco.ravagecabbage.client.model.CabbagerModel;
import teamdraco.ravagecabbage.client.model.RCRavagerModel;
import teamdraco.ravagecabbage.client.old.render.layer.RCRavagerHornLayer;
import teamdraco.ravagecabbage.common.entity.CabbagerEntity;
import teamdraco.ravagecabbage.common.entity.RCRavagerEntity;

public class RCRavagerRenderer extends GeoEntityRenderer<RCRavagerEntity> {
    private static final ResourceLocation SADDLE_TEXTURE = new ResourceLocation(RavageAndCabbage.MOD_ID, "textures/entity/ravager_equipment/saddle.png");

    public RCRavagerRenderer(EntityRendererProvider.Context context) {
        super(context, new RCRavagerModel());
        // todo - make these GeoLayers
        //this.addLayer(new SaddleLayer<>(this, new RCRavagerModel<>(), SADDLE_TEXTURE));
        //this.addLayer(new RCRavagerHornLayer<>(this, new RCRavagerModel<>()));
        this.shadowRadius = 1.1F;
    }
}
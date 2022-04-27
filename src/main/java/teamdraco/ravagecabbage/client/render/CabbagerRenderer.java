package teamdraco.ravagecabbage.client.render;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;
import teamdraco.ravagecabbage.client.model.CabbagerModel;
import teamdraco.ravagecabbage.common.entities.CabbagerEntity;

public class CabbagerRenderer extends GeoEntityRenderer<CabbagerEntity> {

    public CabbagerRenderer(EntityRendererProvider.Context context) {
        super(context, new CabbagerModel());
        this.shadowRadius = 0.5F;
    }
}
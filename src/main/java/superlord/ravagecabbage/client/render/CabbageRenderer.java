package superlord.ravagecabbage.client.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.SpriteRenderer;
import superlord.ravagecabbage.entity.item.CabbageItemEntity;

public class CabbageRenderer extends SpriteRenderer<CabbageItemEntity> {

    public CabbageRenderer(EntityRendererManager renderManagerIn) {
        super(renderManagerIn, Minecraft.getInstance().getItemRenderer());
    }
}

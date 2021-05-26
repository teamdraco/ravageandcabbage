package superlord.ravagecabbage.items;

import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import superlord.ravagecabbage.RavageAndCabbage;

public class RavagerHornArmorItem extends Item {
    private final int armorValue;
    private final ResourceLocation tex;

    public RavagerHornArmorItem(int armorValue, String tierArmor, Item.Properties builder) {
        this(armorValue, new ResourceLocation(RavageAndCabbage.MOD_ID, "textures/entity/ravager_equipment/" + tierArmor + "_horns.png"), builder);
    }

    public RavagerHornArmorItem(int armorValue, ResourceLocation texture, Item.Properties builder) {
        super(builder);
        this.armorValue = armorValue;
        this.tex = texture;
    }

    @OnlyIn(Dist.CLIENT)
    public ResourceLocation getArmorTexture() {
        return tex;
    }

    public int getArmorValue() {
        return this.armorValue;
    }
}

package codyhuh.ravagecabbage.common.items;

import codyhuh.ravagecabbage.RavageAndCabbage;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Item;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class RavagerHornArmorItem extends Item implements IRavagerHornArmorItem {
    private final int armorValue;
    private final ResourceLocation tex;
    private final ArmorMaterial armorMaterial;

    public RavagerHornArmorItem(int armorValue, String tierArmor, Item.Properties builder, ArmorMaterial armorMaterial) {
        this(armorValue, new ResourceLocation(RavageAndCabbage.MOD_ID, "textures/entity/ravager_equipment/" + tierArmor + "_horns.png"), builder, armorMaterial);
    }

    public RavagerHornArmorItem(int armorValue, ResourceLocation texture, Item.Properties builder, ArmorMaterial armorMaterial) {
        super(builder);
        this.armorMaterial = armorMaterial;
        this.armorValue = armorValue;
        this.tex = texture;
    }

    @OnlyIn(Dist.CLIENT)
    public ResourceLocation getArmorTexture() {
        return tex;
    }

    public ArmorMaterial getArmorMaterial() {
        return this.armorMaterial;
    }

    public int getArmorValue() {
        return this.armorValue;
    }
}

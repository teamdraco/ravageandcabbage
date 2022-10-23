package teamdraco.ravagecabbage.common.items;

import net.minecraft.world.item.*;

public class DyeableRavagerHornArmorItem extends RavagerHornArmorItem implements DyeableLeatherItem {

    public DyeableRavagerHornArmorItem(int armorValue, String p_i50047_2_, Item.Properties builder) {
        super(armorValue, p_i50047_2_, builder, ArmorMaterials.LEATHER);
    }
}

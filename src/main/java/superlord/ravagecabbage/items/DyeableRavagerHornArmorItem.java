package superlord.ravagecabbage.items;

import net.minecraft.item.IDyeableArmorItem;
import net.minecraft.item.Item;

public class DyeableRavagerHornArmorItem extends RavagerHornArmorItem implements IDyeableArmorItem {
    public DyeableRavagerHornArmorItem(int armorValue, String p_i50047_2_, Item.Properties builder) {
        super(armorValue, p_i50047_2_, builder);
    }
    public DyeableRavagerHornArmorItem(int armorValue, net.minecraft.util.ResourceLocation texture, Item.Properties builder) {
        super(armorValue, texture, builder);
    }
}

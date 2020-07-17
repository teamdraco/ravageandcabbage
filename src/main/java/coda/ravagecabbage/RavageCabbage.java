package coda.ravagecabbage;

import coda.ravagecabbage.init.ItemInit;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Mod;

@Mod(RavageCabbage.MOD_ID)
public class RavageCabbage {

	public static final String MOD_ID = "ravagecabbage";

	public RavageCabbage() {
	}

	public static class RCItemGroup extends ItemGroup {
		public static final RCItemGroup INSTANCE = new RCItemGroup("ravagecabbage_tab");

		private RCItemGroup(String label) {
			super(label);
		}
		@Override
		public ItemStack createIcon() {
            return new ItemStack(ItemInit.CABBAGE);
		}
	}
}

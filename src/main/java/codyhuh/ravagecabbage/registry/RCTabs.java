package codyhuh.ravagecabbage.registry;

import codyhuh.ravagecabbage.RavageAndCabbage;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class RCTabs {
    public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, RavageAndCabbage.MOD_ID);

    public static final RegistryObject<CreativeModeTab> ITEM_GROUP = TABS.register("rc_tab", () -> CreativeModeTab.builder().icon(() -> new ItemStack(RCItems.CABBAGE.get()))
            .title(Component.translatable("itemGroup.ravageandcabbage"))
            .displayItems((pParameters, pOutput) -> {
                for (var item : RCItems.ITEMS.getEntries()) {
                    pOutput.accept(item.get());
                }

            }).build());
}

package superlord.ravagecabbage;

import net.minecraft.entity.ai.attributes.GlobalEntityTypeAttributes;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;
import superlord.ravagecabbage.entity.CabbagerEntity;
import superlord.ravagecabbage.entity.RavageAndCabbageRavagerEntity;
import superlord.ravagecabbage.init.BlockInit;
import superlord.ravagecabbage.init.EntityInit;
import superlord.ravagecabbage.init.ItemInit;

@Mod(RavageAndCabbage.MOD_ID)
@Mod.EventBusSubscriber(modid = RavageAndCabbage.MOD_ID)
public class RavageAndCabbage {
	
    public static final String MOD_ID = "ravageandcabbage";
    
    public RavageAndCabbage() {
    	IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        bus.addListener(this::registerCommon);
        EntityInit.REGISTER.register(bus);
        ItemInit.REGISTER.register(bus);
        BlockInit.REGISTER.register(bus);
        bus.addListener(this::doClientStuff);

    }
    
    private void registerCommon(FMLCommonSetupEvent event) {
        registerEntityAttributes();
    }
    
    private void doClientStuff(final FMLClientSetupEvent event) {
    	
    }
    
    private void registerEntityAttributes() {
        GlobalEntityTypeAttributes.put(EntityInit.CABBAGER.get(), CabbagerEntity.createAttributes().create());
        GlobalEntityTypeAttributes.put(EntityInit.RAVAGER.get(), RavageAndCabbageRavagerEntity.func_234233_eS_().create());
    }
    
    
    public final static ItemGroup GROUP = new ItemGroup("ravageandcabbage_item_group") {
        @Override
        public ItemStack createIcon() {
            return new ItemStack(ItemInit.CABBAGE.get());
        }
    };
    
    
    public static <T extends IForgeRegistryEntry<T>> T register(IForgeRegistry<T> registry, T entry, String registryKey) {
        entry.setRegistryName(new ResourceLocation(RavageAndCabbage.MOD_ID, registryKey));
        registry.register(entry);
        return entry;
    }
    
}

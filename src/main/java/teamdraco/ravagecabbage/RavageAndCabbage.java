package teamdraco.ravagecabbage;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import software.bernie.geckolib3.GeckoLib;
import teamdraco.ravagecabbage.common.entity.CabbagerEntity;
import teamdraco.ravagecabbage.common.entity.RCRavagerEntity;
import teamdraco.ravagecabbage.registry.RCBlocks;
import teamdraco.ravagecabbage.registry.RCEntities;
import teamdraco.ravagecabbage.registry.RCItems;
import teamdraco.ravagecabbage.network.RCNetwork;

@Mod(RavageAndCabbage.MOD_ID)
public class RavageAndCabbage {
	
    public static final String MOD_ID = "ravageandcabbage";
    public static final Logger LOGGER = LogManager.getLogger();

    public RavageAndCabbage() {
    	IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();

        bus.addListener(this::registerCommon);
        bus.addListener(this::registerEntityAttributes);

        RCEntities.REGISTER.register(bus);
        RCItems.REGISTER.register(bus);
        RCBlocks.REGISTER.register(bus);

        GeckoLib.initialize();
    }

    private void registerCommon(final FMLCommonSetupEvent event) {
        RCNetwork.init();
    }

    private void registerEntityAttributes(EntityAttributeCreationEvent event) {
        event.put(RCEntities.CABBAGER.get(), CabbagerEntity.createAttributes().build());
        event.put(RCEntities.RAVAGER.get(), RCRavagerEntity.createAttributes().build());
    }

    public final static CreativeModeTab GROUP = new CreativeModeTab(MOD_ID) {
        @Override
        public ItemStack makeIcon() {
            return new ItemStack(RCItems.CABBAGE.get());
        }
    };
}

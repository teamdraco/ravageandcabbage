package codyhuh.ravagecabbage;

import codyhuh.ravagecabbage.common.entities.CabbagerEntity;
import codyhuh.ravagecabbage.registry.RCTabs;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.world.entity.raid.Raid;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import codyhuh.ravagecabbage.common.entities.CorruptedVillager;
import codyhuh.ravagecabbage.common.entities.RCRavagerEntity;
import codyhuh.ravagecabbage.network.RCNetwork;
import codyhuh.ravagecabbage.registry.RCBlocks;
import codyhuh.ravagecabbage.registry.RCEntities;
import codyhuh.ravagecabbage.registry.RCItems;

@Mod(RavageAndCabbage.MOD_ID)
public class RavageAndCabbage {
	public static final String MOD_ID = "ravageandcabbage";
	public static final Logger LOGGER = LogManager.getLogger();

	public RavageAndCabbage() {
		IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();

		bus.addListener(this::registerCommon);
		bus.addListener(this::registerEntityAttributes);
		bus.addListener(this::addRaider);

		RCEntities.ENTITIES.register(bus);
		RCItems.ITEMS.register(bus);
		RCBlocks.BLOCKS.register(bus);
		RCTabs.TABS.register(bus);
	}

	private void registerCommon(final FMLCommonSetupEvent event) {
		RCNetwork.init();
	}

	private void addRaider(FMLCommonSetupEvent event) {
		event.enqueueWork(() -> {
			Raid.RaiderType.create("cabbager", RCEntities.CABBAGER.get(), new int[] {0, 1, 2, 2, 1, 2, 2, 3 });
		});
	}

	private void registerEntityAttributes(EntityAttributeCreationEvent event) {
		event.put(RCEntities.CABBAGER.get(), CabbagerEntity.createAttributes().build());
		event.put(RCEntities.RAVAGER.get(), RCRavagerEntity.createAttributes().build());
		event.put(RCEntities.CORRUPTED_VILLAGER.get(), CorruptedVillager.createAttributes().build());
	}

}

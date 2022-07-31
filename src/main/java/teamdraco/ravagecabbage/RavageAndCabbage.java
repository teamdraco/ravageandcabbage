package teamdraco.ravagecabbage;

import net.minecraft.world.level.block.ComposterBlock;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.world.entity.raid.Raid;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import teamdraco.ravagecabbage.common.entities.CabbagerEntity;
import teamdraco.ravagecabbage.common.entities.RCRavagerEntity;
import teamdraco.ravagecabbage.network.RCNetwork;
import teamdraco.ravagecabbage.registry.RCBlocks;
import teamdraco.ravagecabbage.registry.RCEntities;
import teamdraco.ravagecabbage.registry.RCItems;

@Mod(RavageAndCabbage.MOD_ID)
public class RavageAndCabbage {
	public static final String MOD_ID = "ravageandcabbage";
	public static final Logger LOGGER = LogManager.getLogger();

	public RavageAndCabbage() {
		IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();

		bus.addListener(this::registerCommon);
		bus.addListener(this::registerEntityAttributes);
		bus.addListener(this::setup);

		RCEntities.REGISTER.register(bus);
		RCItems.REGISTER.register(bus);
		RCBlocks.REGISTER.register(bus);
	}

	private void registerCommon(final FMLCommonSetupEvent event) {
		RCNetwork.init();
	}

	private void setup(FMLCommonSetupEvent event) {
		event.enqueueWork(() -> {
			Raid.RaiderType.create("cabbager", RCEntities.CABBAGER.get(), new int[] {0, 1, 2, 2, 1, 2, 2, 3 });
			ComposterBlock.COMPOSTABLES.put(RCItems.CABBAGE.get().asItem(), 0.65F);
			ComposterBlock.COMPOSTABLES.put(RCItems.CABBAGE_SEEDS.get().asItem(), 0.3F);
		});
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

	// todo - add loot to the empty chest in the stable
}

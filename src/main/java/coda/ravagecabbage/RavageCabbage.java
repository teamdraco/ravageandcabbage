package coda.ravagecabbage;

import coda.ravagecabbage.init.EntityInit;
import coda.ravagecabbage.init.ItemInit;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntitySpawnPlacementRegistry;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.fish.AbstractFishEntity;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.gen.Heightmap;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(RavageCabbage.MOD_ID)
@Mod.EventBusSubscriber(modid = RavageCabbage.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class RavageCabbage {

	public static final String MOD_ID = "ravagecabbage";
	public static RavageCabbage instance;

	public RavageCabbage() {
		IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
		modEventBus.addListener(this::setup);
		modEventBus.addListener(this::doClientStuff);

		ItemInit.ITEMS.register(modEventBus);
		EntityInit.ENTITIES.register(modEventBus);

		instance = this;
		MinecraftForge.EVENT_BUS.register(this);
	}

	private void setup(final FMLCommonSetupEvent event) {

	}

	private void doClientStuff(final FMLCommonSetupEvent event) {
	}

	public static class RCItemGroup extends ItemGroup {
		public static final RCItemGroup INSTANCE = new RCItemGroup("ravagecabbage_tab");

		private RCItemGroup(String label) {
			super(label);
		}
		@Override
		public ItemStack createIcon() {
            return new ItemStack(ItemInit.CABBAGE.get());
		}
	}
}

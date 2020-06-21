package coda.ravagecabbage;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import coda.ravagecabbage.init.ItemInit;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(RavageCabbage.modid)
public class RavageCabbage {

	private static final Logger LOGGER = LogManager.getLogger();
	public static final String modid = "ravagecabbage";
	public static RavageCabbage instance;
	
	public RavageCabbage() {
		final IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
		modEventBus.addListener(this::setup);
		modEventBus.addListener(this::doClientStuff);
		instance = this;
		MinecraftForge.EVENT_BUS.register(this);
	}
	
	private void setup(final FMLCommonSetupEvent event) {
		
	}
	
	private void doClientStuff(final FMLClientSetupEvent event) {
		
	}
	
	@SubscribeEvent
	public void onServerStarting(FMLServerStartingEvent event) {
		
	}
	
	public static class RCItemGroup extends ItemGroup {
		public static final RCItemGroup instance = new RCItemGroup(ItemGroup.GROUPS.length, "ravagecabbage_tab");
		
		private RCItemGroup(int index, String label) {
			super(index, label);
		}
		@Override
		public ItemStack createIcon() {
            return new ItemStack(ItemInit.CABBAGE);
		}
	}
	
}

package superlord.ravagecabbage;

import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.Codec;
import net.minecraft.entity.ai.attributes.GlobalEntityTypeAttributes;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.WorldGenRegistries;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.FlatChunkGenerator;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.settings.DimensionStructuresSettings;
import net.minecraft.world.gen.settings.StructureSeparationSettings;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import superlord.ravagecabbage.entity.CabbagerEntity;
import superlord.ravagecabbage.entity.RavageAndCabbageRavagerEntity;
import superlord.ravagecabbage.init.*;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

@Mod(RavageAndCabbage.MOD_ID)
@Mod.EventBusSubscriber(modid = RavageAndCabbage.MOD_ID)
public class RavageAndCabbage {
	
    public static final String MOD_ID = "ravageandcabbage";
    public static final Logger LOGGER = LogManager.getLogger();

    public RavageAndCabbage() {
    	IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        bus.addListener(this::registerCommon);
        bus.addListener(this::doClientStuff);
        bus.addListener(this::setup);

        RCEntities.REGISTER.register(bus);
        RCItems.REGISTER.register(bus);
        RCBlocks.REGISTER.register(bus);
        RCStructures.REGISTER.register(bus);

        IEventBus forgeBus = MinecraftForge.EVENT_BUS;
        forgeBus.addListener(EventPriority.NORMAL, this::addDimensionalSpacing);
        forgeBus.addListener(EventPriority.HIGH, this::biomeModification);
    }
    
    private void registerCommon(FMLCommonSetupEvent event) {
        registerEntityAttributes();
    }

    public void setup(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            RCStructures.setupStructures();
            RCConfiguredStructures.registerConfiguredStructures();

            WorldGenRegistries.NOISE_SETTINGS.getEntries().forEach(settings -> {
                Map<Structure<?>, StructureSeparationSettings> structureMap = settings.getValue().getStructures().func_236195_a_();

                if( structureMap instanceof ImmutableMap){
                    Map<Structure<?>, StructureSeparationSettings> tempMap = new HashMap<>(structureMap);
                    tempMap.put(RCStructures.STABLE.get(), DimensionStructuresSettings.field_236191_b_.get(RCStructures.STABLE.get()));
                    settings.getValue().getStructures().field_236193_d_ = tempMap;
                }
                else{
                    structureMap.put(RCStructures.STABLE.get(), DimensionStructuresSettings.field_236191_b_.get(RCStructures.STABLE.get()));
                }
            });
        });
    }

    private void doClientStuff(final FMLClientSetupEvent event) {
    }
    
    private void registerEntityAttributes() {
        GlobalEntityTypeAttributes.put(RCEntities.CABBAGER.get(), CabbagerEntity.createAttributes().create());
        GlobalEntityTypeAttributes.put(RCEntities.RAVAGER.get(), RavageAndCabbageRavagerEntity.func_234233_eS_().create());
    }

    public final static ItemGroup GROUP = new ItemGroup("ravageandcabbage_item_group") {
        @Override
        public ItemStack createIcon() {
            return new ItemStack(RCItems.CABBAGE.get());
        }
    };
    
    public static <T extends IForgeRegistryEntry<T>> T register(IForgeRegistry<T> registry, T entry, String registryKey) {
        entry.setRegistryName(new ResourceLocation(RavageAndCabbage.MOD_ID, registryKey));
        registry.register(entry);
        return entry;
    }

    public void biomeModification(final BiomeLoadingEvent event) {
        if (event.getCategory() == Biome.Category.PLAINS) {
            event.getGeneration().getStructures().add(() -> RCConfiguredStructures.CONFIGURED_STABLE);
        }
    }

    private static Method GETCODEC_METHOD;
    public void addDimensionalSpacing(final WorldEvent.Load event) {
        if(event.getWorld() instanceof ServerWorld){
            ServerWorld serverWorld = (ServerWorld)event.getWorld();

            try {
                if(GETCODEC_METHOD == null) GETCODEC_METHOD = ObfuscationReflectionHelper.findMethod(ChunkGenerator.class, "getCodec");
                ResourceLocation cgRL = Registry.CHUNK_GENERATOR_CODEC.getKey((Codec<? extends ChunkGenerator>) GETCODEC_METHOD.invoke(serverWorld.getChunkProvider().generator));
                if(cgRL != null && cgRL.getNamespace().equals("terraforged")) return;
            }
            catch(Exception e){
                RavageAndCabbage.LOGGER.error("Was unable to check if " + serverWorld.getDimensionKey().getLocation() + " is using Terraforged's ChunkGenerator.");
            }

            if(serverWorld.getChunkProvider().getChunkGenerator() instanceof FlatChunkGenerator &&
                    serverWorld.getDimensionKey().equals(World.OVERWORLD)){
                return;
            }

            Map<Structure<?>, StructureSeparationSettings> tempMap = new HashMap<>(serverWorld.getChunkProvider().generator.func_235957_b_().func_236195_a_());
            tempMap.putIfAbsent(RCStructures.STABLE.get(), DimensionStructuresSettings.field_236191_b_.get(RCStructures.STABLE.get()));
            serverWorld.getChunkProvider().generator.func_235957_b_().field_236193_d_ = tempMap;
        }
    }
}

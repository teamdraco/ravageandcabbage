package superlord.ravagecabbage.init;

import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import superlord.ravagecabbage.RavageAndCabbage;
import superlord.ravagecabbage.entity.CabbagerEntity;
import superlord.ravagecabbage.entity.RCRavagerEntity;
import superlord.ravagecabbage.entity.item.CabbageItemEntity;

public class RCEntities {
	
    public static final DeferredRegister<EntityType<?>> REGISTER = DeferredRegister.create(ForgeRegistries.ENTITIES, RavageAndCabbage.MOD_ID);

    public static final RegistryObject<EntityType<CabbageItemEntity>> CABBAGE = REGISTER.register("cabbage", () -> EntityType.Builder.<CabbageItemEntity>create(CabbageItemEntity::new, EntityClassification.MISC).size(1.0f, 1.0f).build(new ResourceLocation(RavageAndCabbage.MOD_ID, "cabbage").toString()));
    public static final RegistryObject<EntityType<CabbagerEntity>> CABBAGER = REGISTER.register("cabbager", () -> EntityType.Builder.<CabbagerEntity>create(CabbagerEntity::new, EntityClassification.MONSTER).size(0.6F, 1.95F).trackingRange(8).build(new ResourceLocation(RavageAndCabbage.MOD_ID, "cabbager").toString()));
    public static final RegistryObject<EntityType<RCRavagerEntity>> RAVAGER = REGISTER.register("ravager", () -> EntityType.Builder.<RCRavagerEntity>create(RCRavagerEntity::new, EntityClassification.CREATURE).size(1.95F, 2.2F).trackingRange(10).build(new ResourceLocation(RavageAndCabbage.MOD_ID, "ravager").toString()));
}

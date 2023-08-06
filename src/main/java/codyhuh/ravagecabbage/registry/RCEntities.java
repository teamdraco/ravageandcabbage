package codyhuh.ravagecabbage.registry;

import codyhuh.ravagecabbage.RavageAndCabbage;
import codyhuh.ravagecabbage.common.entities.CabbagerEntity;
import codyhuh.ravagecabbage.common.entities.RCRavagerEntity;
import codyhuh.ravagecabbage.common.entities.item.CabbageItemEntity;
import codyhuh.ravagecabbage.common.entities.item.CorruptedCabbageItemEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import codyhuh.ravagecabbage.common.entities.CorruptedVillager;

public class RCEntities {
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, RavageAndCabbage.MOD_ID);

    public static final RegistryObject<EntityType<CabbageItemEntity>> CABBAGE = create("cabbage", EntityType.Builder.<CabbageItemEntity>of(CabbageItemEntity::new, MobCategory.MISC).sized(1.0f, 1.0f));
    public static final RegistryObject<EntityType<CabbagerEntity>> CABBAGER = create("cabbager", EntityType.Builder.of(CabbagerEntity::new, MobCategory.MONSTER).sized(0.6F, 1.95F).clientTrackingRange(8));
    public static final RegistryObject<EntityType<RCRavagerEntity>> RAVAGER = create("ravager", EntityType.Builder.of(RCRavagerEntity::new, MobCategory.CREATURE).sized(1.95F, 2.2F).clientTrackingRange(10));
    public static final RegistryObject<EntityType<CorruptedCabbageItemEntity>> CORRUPTED_CABBAGE = create("corrupted_cabbage", EntityType.Builder.<CorruptedCabbageItemEntity>of(CorruptedCabbageItemEntity::new, MobCategory.MISC).sized(1.0f, 1.0f));
    public static final RegistryObject<EntityType<CorruptedVillager>> CORRUPTED_VILLAGER = create("corrupted_villager", EntityType.Builder.of(CorruptedVillager::new, MobCategory.CREATURE).sized(0.6F, 1.95F));


    private static <T extends Entity> RegistryObject<EntityType<T>> create(String name, EntityType.Builder<T> builder) {
        return ENTITIES.register(name, () -> builder.build(RavageAndCabbage.MOD_ID + "." + name));
    }
}

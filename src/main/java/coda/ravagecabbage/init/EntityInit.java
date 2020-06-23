package coda.ravagecabbage.init;

import coda.ravagecabbage.RavageCabbage;
import coda.ravagecabbage.entity.CabbageRavagerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = RavageCabbage.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class EntityInit {
    public static final EntityType<CabbageRavagerEntity> RAVAGER = create("ravager", EntityType.Builder.create(CabbageRavagerEntity::new, EntityClassification.CREATURE).size(1.95F, 2.2F));

    private static <T extends Entity> EntityType<T> create(String name, EntityType.Builder<T> builder) {
        EntityType<T> type = builder.build(name);
        type.setRegistryName(name);
        return type;
    }

    @SubscribeEvent
    public static void registerEntities(final RegistryEvent.Register<EntityType<?>> event) {
        event.getRegistry().register(RAVAGER);
    }
}

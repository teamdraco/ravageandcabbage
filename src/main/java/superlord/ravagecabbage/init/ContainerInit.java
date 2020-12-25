package superlord.ravagecabbage.init;

import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import superlord.ravagecabbage.RavageAndCabbage;
import superlord.ravagecabbage.common.inventory.RavagerContainer;

public class ContainerInit {
	
    public static final DeferredRegister<ContainerType<?>> REGISTER = DeferredRegister.create(ForgeRegistries.CONTAINERS, RavageAndCabbage.MOD_ID);

    public static final RegistryObject<ContainerType<Container>> RAVAGER = REGISTER.register("ravager_container", () -> IForgeContainerType.create(RavagerContainer::new));
    

}

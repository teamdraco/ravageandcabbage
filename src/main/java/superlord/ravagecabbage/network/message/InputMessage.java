package superlord.ravagecabbage.network.message;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvents;
import net.minecraftforge.fml.network.NetworkEvent;
import superlord.ravagecabbage.entity.RCRavagerEntity;

import java.util.function.Supplier;

public class InputMessage {
    public int key;

    public InputMessage(int key) {
        this.key = key;
    }

    public static void encode(InputMessage message, PacketBuffer buffer) {
        buffer.writeInt(message.key);
    }

    public static InputMessage decode(PacketBuffer buffer) {
        return new InputMessage(buffer.readInt());
    }

    public static void handle(InputMessage message, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            PlayerEntity player = context.getSender();
            Entity vehicle = player.getRidingEntity();
            if (vehicle instanceof RCRavagerEntity) {
                RCRavagerEntity ravager = ((RCRavagerEntity) vehicle);
                if (ravager.hasHornArmor() && ravager.getItemStackFromSlot(EquipmentSlotType.HEAD).getDamage() < ravager.getItemStackFromSlot(EquipmentSlotType.HEAD).getMaxDamage() && ravager.isTamed() && ravager.getControllingPassenger() == player) {
                    if (ravager.attackTick == 0) {
                        ravager.attackTick = 30;
                        ravager.world.setEntityState(ravager, (byte)4);
                        ravager.playSound(SoundEvents.ENTITY_RAVAGER_ATTACK, 1.0F, 1.0F);
                        for (Entity entity : ravager.world.getEntitiesWithinAABB(LivingEntity.class, ravager.getBoundingBox().grow(4.0D))) {
                            if (!(entity instanceof RCRavagerEntity) && !(entity instanceof PlayerEntity)) {
                                entity.attackEntityFrom(DamageSource.causeMobDamage(ravager), 4.0F);
                                ravager.getItemStackFromSlot(EquipmentSlotType.HEAD).damageItem(1, ravager, (p_213613_1_) -> {
                                    p_213613_1_.sendBreakAnimation(EquipmentSlotType.HEAD);
                                });
                            }
                        }
                    }
                }
            }
        });
        context.setPacketHandled(true);
    }
}

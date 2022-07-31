package teamdraco.ravagecabbage.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkEvent;
import teamdraco.ravagecabbage.common.entities.RCRavagerEntity;

import java.util.function.Supplier;

public class KeyInputMessage {
    public int key;

    public KeyInputMessage(int key) {
        this.key = key;
    }

    public static void encode(KeyInputMessage message, FriendlyByteBuf buffer) {
        buffer.writeInt(message.key);
    }

    public static KeyInputMessage decode(FriendlyByteBuf buffer) {
        return new KeyInputMessage(buffer.readInt());
    }

    public static void handle(KeyInputMessage message, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            Player player = context.getSender();
            Entity vehicle = player.getVehicle();
            if (vehicle instanceof RCRavagerEntity ravager) {
                if (ravager.hasHornArmor() && ravager.getItemBySlot(EquipmentSlot.HEAD).getDamageValue() <= ravager.getItemBySlot(EquipmentSlot.HEAD).getMaxDamage() && ravager.isTame() && ravager.getControllingPassenger() == player) {
                    if (ravager.attackTick == 0) {
                        ravager.attackTick = 30;
                        ravager.level.broadcastEntityEvent(ravager, (byte)4);
                        ravager.playSound(SoundEvents.RAVAGER_ATTACK, 1.0F, 1.0F);
                        for (Entity entity : ravager.level.getEntitiesOfClass(LivingEntity.class, ravager.getBoundingBox().inflate(4.0D))) {
                            if (!(entity instanceof RCRavagerEntity) && !(entity instanceof Player)) {
                                entity.hurt(DamageSource.mobAttack(ravager), 4.0F);
                                ravager.getItemBySlot(EquipmentSlot.HEAD).hurtAndBreak(1, ravager, (p_213613_1_) -> p_213613_1_.broadcastBreakEvent(EquipmentSlot.HEAD));
                            }
                        }
                    }
                }
            }
        });
        context.setPacketHandled(true);
    }
}

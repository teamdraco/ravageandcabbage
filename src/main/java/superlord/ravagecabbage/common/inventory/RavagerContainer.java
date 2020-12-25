package superlord.ravagecabbage.common.inventory;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.PacketBuffer;
import superlord.ravagecabbage.entity.RavageAndCabbageRavagerEntity;
import superlord.ravagecabbage.init.ContainerInit;

public class RavagerContainer extends Container {

	public RavageAndCabbageRavagerEntity ravager;

	public RavagerContainer(int id, PlayerInventory inv) {
		super(ContainerInit.RAVAGER.get(), id);
	}

    public RavagerContainer(int id, RavageAndCabbageRavagerEntity ravager, PlayerEntity player) {
        this(id, player.inventory);
		this.ravager = ravager;
		this.addSlot(new Slot(ravager.inventory, 0, 8, 18) {
			public boolean isItemValid(ItemStack stack) {
				return stack.getItem() == Items.SADDLE && ravager.inventory.getStackInSlot(0).isEmpty() && ravager.isTamed();
			}

			public int getSlotStackLimit() {
				return 1;
			}
		});

		for (int i1 = 0; i1 < 3; ++i1)
			for (int k1 = 0; k1 < 9; ++k1)
				this.addSlot(new Slot(player.inventory, k1 + i1 * 9 + 9, 8 + k1 * 18, 102 + i1 * 18 + -18));
		for (int j1 = 0; j1 < 9; ++j1) this.addSlot(new Slot(player.inventory, j1, 8 + j1 * 18, 142));
		if(ravager.hasChest()) for (int k = 0; k < 3; ++k)
			for (int l = 0; l < 5; ++l)
				this.addSlot(new Slot(ravager.inventory, 2 + l + k * 5, 80 + l * 18, 18 + k * 18));
	}
    
    public RavagerContainer(final int windowID, final PlayerInventory playerInv, final PacketBuffer data) {
		this(windowID, playerInv);
	}
    
	public ItemStack transferStackInSlot(PlayerEntity playerIn, int index) {
		return ItemStack.EMPTY;
	}

	@Override
	public boolean canInteractWith(PlayerEntity playerIn) {
		return ravager.isTamed() && ravager.isOwner(playerIn);
	}

	public void onContainerClosed(PlayerEntity playerIn) {
		super.onContainerClosed(playerIn);
		this.ravager.inventory.closeInventory(playerIn);
	}

}

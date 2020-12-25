package superlord.ravagecabbage.client.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import superlord.ravagecabbage.RavageAndCabbage;
import superlord.ravagecabbage.common.inventory.RavagerContainer;
import superlord.ravagecabbage.entity.RavageAndCabbageRavagerEntity;

public class RavagerContainerGUI extends ContainerScreen<RavagerContainer> {
	
	private static final ResourceLocation GUI = new ResourceLocation(RavageAndCabbage.MOD_ID, "textures/gui/container/ravager.png");
    private static final ResourceLocation CHEST = new ResourceLocation(RavageAndCabbage.MOD_ID, "textures/gui/container/ravager_chest.png");
    private final RavageAndCabbageRavagerEntity ravager;

    public RavagerContainerGUI(RavagerContainer screenContainer, PlayerInventory inv, ITextComponent titleIn) {
        super(screenContainer, inv, titleIn);
        this.ravager = screenContainer.ravager;
    }



	@Override
	protected void drawGuiContainerForegroundLayer(MatrixStack stack, int mouseX, int mouseY) {
		super.drawGuiContainerForegroundLayer(stack, mouseX, mouseY);
		String s = this.title.getString();
		this.font.drawString(stack, s, (float)(this.xSize / 2 - this.font.getStringWidth(s) / 2), 6.0F, 4210752);
		this.font.drawString(stack, this.playerInventory.getDisplayName().getString(), 8.0f, 69.0f, 0x404040);
	}

	@Override
	public void render(MatrixStack stack, int mouseX, int mouseY, float partialTicks) {
		this.renderBackground(stack);
		super.render(stack, mouseX, mouseY, partialTicks);
		this.renderHoveredTooltip(stack, mouseX, mouseY);
	}

	@SuppressWarnings("deprecation")
	@Override
	protected void drawGuiContainerBackgroundLayer(MatrixStack matrixStack, float partialTicks, int x, int y) {
		RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
		if(ravager.hasChest()) {
			this.minecraft.getTextureManager().bindTexture(CHEST);
		} else {
			this.minecraft.getTextureManager().bindTexture(GUI);
		}
		this.blit(matrixStack, this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);

		this.blit(matrixStack, this.guiLeft + 79, this.guiTop + 35, 176, 0, 0, 16);		
	}

}

package net.dries007.tfc.client.screen;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.dries007.tfc.common.capabilities.heat.Heat;
import net.dries007.tfc.common.container.PotContainer;
import net.dries007.tfc.common.tileentity.FirepitTileEntity;
import net.dries007.tfc.common.tileentity.PotTileEntity;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;

public class PotScreen extends TFCContainerScreen<PotContainer>
{
    private static final ResourceLocation BACKGROUND = new ResourceLocation(MOD_ID, "textures/gui/fire_pit_cooking_pot.png");
    private final PotTileEntity tile;

    public PotScreen(PotContainer container, PlayerInventory playerInventory, ITextComponent name)
    {
        super(container, playerInventory, name, BACKGROUND);
        this.tile = container.getTileEntity();
    }

    @Override
    protected void renderBg(MatrixStack matrixStack, float partialTicks, int mouseX, int mouseY)
    {
        super.renderBg(matrixStack, partialTicks, mouseX, mouseY);
        int temp = (int) (51 * tile.getFields()[FirepitTileEntity.FIELD_TEMP] / Heat.maxVisibleTemperature());
        if (temp > 0)
            blit(matrixStack, leftPos + 30, topPos + 66 - Math.min(51, temp), 176, 0, 15, 5);
        //todo: cooking pot stuff
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks)
    {
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        //todo: cooking pot stage of boiling
    }

    @Override
    protected void renderLabels(MatrixStack matrixStack, int x, int y)
    {

    }
}

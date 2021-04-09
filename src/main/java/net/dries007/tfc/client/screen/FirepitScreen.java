package net.dries007.tfc.client.screen;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.dries007.tfc.common.capabilities.heat.Heat;
import net.dries007.tfc.common.container.FirepitContainer;
import net.dries007.tfc.common.tileentity.FirepitTileEntity;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;

public class FirepitScreen extends TileEntityScreen<FirepitTileEntity, FirepitContainer>
{
    private static final ResourceLocation FIREPIT = new ResourceLocation(MOD_ID, "textures/gui/fire_pit.png");

    public FirepitScreen(FirepitContainer container, PlayerInventory playerInventory, ITextComponent name)
    {
        super(container, playerInventory, name, FIREPIT);
        inventoryLabelY += 20;
        imageHeight += 20;
    }

    @Override
    protected void renderBg(MatrixStack matrixStack, float partialTicks, int mouseX, int mouseY)
    {
        super.renderBg(matrixStack, partialTicks, mouseX, mouseY);
        int temp = (int) (51 * ((FirepitTileEntity) tile).getSyncableData().get(FirepitTileEntity.DATA_SLOT_TEMPERATURE) / Heat.maxVisibleTemperature());
        if (temp > 0)
            blit(matrixStack, leftPos + 30, topPos + 76 - Math.min(51, temp), 176, 0, 15, 5);
    }
}

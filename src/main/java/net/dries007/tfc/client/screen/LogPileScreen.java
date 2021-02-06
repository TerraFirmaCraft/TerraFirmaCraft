package net.dries007.tfc.client.screen;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;

import net.dries007.tfc.common.container.LogPileContainer;

public class LogPileScreen extends TFCContainerScreen<LogPileContainer>
{
    public LogPileScreen(LogPileContainer container, PlayerInventory playerInventory, ITextComponent name)
    {
        super(container, playerInventory, name, SMALL_INV);
    }
}

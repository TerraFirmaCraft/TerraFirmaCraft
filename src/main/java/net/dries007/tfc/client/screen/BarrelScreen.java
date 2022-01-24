package net.dries007.tfc.client.screen;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

import net.dries007.tfc.common.blockentities.BarrelBlockEntity;
import net.dries007.tfc.common.container.BarrelContainer;
import net.dries007.tfc.util.Helpers;

public class BarrelScreen extends BlockEntityScreen<BarrelBlockEntity, BarrelContainer>
{
    private static final ResourceLocation BACKGROUND = Helpers.identifier("textures/gui/barrel.png");

    public BarrelScreen(BarrelContainer container, Inventory playerInventory, Component name)
    {
        super(container, playerInventory, name, BACKGROUND);
    }

    // todo: all custom functionality
}

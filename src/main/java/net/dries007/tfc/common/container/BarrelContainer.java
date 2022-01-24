package net.dries007.tfc.common.container;

import net.minecraft.world.entity.player.Inventory;

import net.dries007.tfc.common.blockentities.BarrelBlockEntity;

public class BarrelContainer extends BlockEntityContainer<BarrelBlockEntity>
{
    public static BarrelContainer create(BarrelBlockEntity barrel, Inventory playerInv, int windowId)
    {
        return new BarrelContainer(windowId, barrel).init(playerInv);
    }

    private BarrelContainer(int windowId, BarrelBlockEntity barrel)
    {
        super(TFCContainerTypes.BARREL.get(), windowId, barrel);
    }

    @Override
    protected void addContainerSlots()
    {
        // todo: slots
    }
}

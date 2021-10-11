/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.container;

import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.items.CapabilityItemHandler;

import net.dries007.tfc.common.blockentities.CrucibleBlockEntity;

public class CrucibleContainer extends BlockEntityContainer<CrucibleBlockEntity>
{
    public static CrucibleContainer create(CrucibleBlockEntity crucible, Inventory playerInv, int windowId)
    {
        return new CrucibleContainer(windowId, crucible).init(playerInv, 55);
    }

    private CrucibleContainer(int windowId, CrucibleBlockEntity crucible)
    {
        super(TFCContainerTypes.CRUCIBLE.get(), windowId, crucible);

        addDataSlots(crucible.getSyncableData());
    }

    @Override
    protected void addContainerSlots()
    {
        blockEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(inventory -> {
            for (int slot = CrucibleBlockEntity.SLOT_INPUT_START; slot <= CrucibleBlockEntity.SLOT_INPUT_END; slot++)
            {
                final int line = slot / 3, column = slot % 3;
                addSlot(new CallbackSlot(blockEntity, inventory, slot, 26 + column * 18, 82 + line * 18));
            }

            addSlot(new CallbackSlot(blockEntity, inventory, CrucibleBlockEntity.SLOT_OUTPUT, 152, 100));
        });
    }
}

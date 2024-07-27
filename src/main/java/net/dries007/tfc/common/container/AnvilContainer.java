/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.container;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.blockentities.AnvilBlockEntity;
import net.dries007.tfc.common.component.forge.ForgeStep;
import net.dries007.tfc.common.component.forge.Forging;
import net.dries007.tfc.common.component.forge.ForgingCapability;
import net.dries007.tfc.common.container.slot.CallbackSlot;
import net.dries007.tfc.common.recipes.AnvilRecipe;
import net.dries007.tfc.util.Helpers;

public class AnvilContainer extends BlockEntityContainer<AnvilBlockEntity> implements ButtonHandlerContainer
{
    // IDs [0, 7] indicate step buttons
    public static final int PLAN_ID = 8;

    public static AnvilContainer create(AnvilBlockEntity anvil, Inventory playerInv, int windowId)
    {
        return new AnvilContainer(windowId, anvil).init(playerInv, 41);
    }

    protected AnvilContainer(int windowId, AnvilBlockEntity anvil)
    {
        super(TFCContainerTypes.ANVIL.get(), windowId, anvil);
    }

    @Override
    public void onButtonPress(int buttonID, @Nullable CompoundTag extraNBT)
    {
        if (buttonID == PLAN_ID)
        {
            final Level level = blockEntity.getLevel();
            if (level != null)
            {
                final ItemStack stack = getSlot(AnvilBlockEntity.SLOT_INPUT_MAIN).getItem();
                if (AnvilRecipe.hasAny(level, stack, blockEntity.getTier()) && player instanceof ServerPlayer serverPlayer)
                {
                    Helpers.openScreen(serverPlayer, blockEntity.planProvider(), blockEntity.getBlockPos());
                }
            }
        }
        else
        {
            final ForgeStep step = ForgeStep.valueOf(buttonID);
            if (player instanceof ServerPlayer serverPlayer && step != null)
            {
                blockEntity.work(serverPlayer, step);
            }
        }
    }

    @Override
    protected void addContainerSlots()
    {
        addSlot(new CallbackSlot(blockEntity, AnvilBlockEntity.SLOT_INPUT_MAIN, 31, 68));
        addSlot(new CallbackSlot(blockEntity, AnvilBlockEntity.SLOT_INPUT_SECOND, 13, 68));
        addSlot(new CallbackSlot(blockEntity, AnvilBlockEntity.SLOT_HAMMER, 129, 68));
        addSlot(new CallbackSlot(blockEntity, AnvilBlockEntity.SLOT_CATALYST, 147, 68));
    }

    @Override
    protected boolean moveStack(ItemStack stack, int slotIndex)
    {
        return switch (typeOf(slotIndex))
            {
                case MAIN_INVENTORY, HOTBAR -> !moveItemStackTo(stack, AnvilBlockEntity.SLOT_HAMMER, AnvilBlockEntity.SLOT_CATALYST + 1, false)
                    && !moveItemStackTo(stack, AnvilBlockEntity.SLOT_INPUT_MAIN, AnvilBlockEntity.SLOT_INPUT_SECOND + 1, false);
                case CONTAINER -> {
                    final Level level = blockEntity.getLevel();
                    final Forging forge = ForgingCapability.get(stack);

                    // Shift clicking needs to attempt to clear the recipe on the stack, then restore it if we fail to transfer out
                    AnvilRecipe recipe = null;
                    int target = -1;

                    if (level != null)
                    {
                        recipe = forge.view().recipe();
                        target = forge.view().target();
                        forge.clearRecipeIfNotWorked();
                    }

                    // Do the stack movement
                    final boolean result = !moveItemStackTo(stack, containerSlots, slots.size(), false);

                    // And then restore the stack
                    if (!stack.isEmpty() && recipe != null)
                    {
                        forge.setRecipe(recipe, target);
                    }
                    yield result;
                }
            };
    }
}

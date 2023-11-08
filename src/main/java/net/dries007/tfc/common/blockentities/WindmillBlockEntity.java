/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blockentities;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.items.ItemStackHandler;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blocks.mechanical.WindmillBlock;
import net.dries007.tfc.util.Helpers;

import static net.dries007.tfc.TerraFirmaCraft.*;

public class WindmillBlockEntity extends TickableInventoryBlockEntity<ItemStackHandler>
{
    public static void serverTick(Level level, BlockPos pos, BlockState state, WindmillBlockEntity mill)
    {
        mill.checkForLastTickSync();
        if (mill.needsStateUpdate)
        {
            mill.updateBlockState();
        }
        if (level.getGameTime() % 20 == 0)
        {
            mill.checkPowered();
        }
    }

    public static void clientTick(Level level, BlockPos pos, BlockState state, WindmillBlockEntity mill)
    {
        if (mill.powered)
        {
            mill.ticks++;
        }
        else
        {
            mill.ticks = 0;
        }
    }

    public static final int SLOTS = 5;
    private static final Component NAME = Component.translatable(MOD_ID + ".block_entity.windmill");
    private boolean needsStateUpdate = false;
    private boolean powered = false;
    private int ticks = 0;

    public WindmillBlockEntity(BlockPos pos, BlockState state)
    {
        super(TFCBlockEntities.WINDMILL.get(), pos, state, defaultInventory(SLOTS), NAME);
    }

    @Override
    public void loadAdditional(CompoundTag nbt)
    {
        super.loadAdditional(nbt);
        needsStateUpdate = true;
        powered = nbt.getBoolean("powered");
    }

    @Override
    public void saveAdditional(CompoundTag nbt)
    {
        super.saveAdditional(nbt);
        nbt.putBoolean("powered", powered);
    }

    public boolean isPowered()
    {
        return powered;
    }

    public int getTicks()
    {
        return ticks;
    }

    public void checkPowered()
    {
        assert level != null;
        final boolean wasPower = powered;

        if (getBlockState().getValue(WindmillBlock.STAGE) == 0)
        {
            powered = false;
            if (wasPower)
            {
                markForSync();
            }
            return;
        }

        powered = true;
        if (!wasPower)
        {
            markForSync();
        }
    }

    public void updateBlockState()
    {
        assert level != null;
        final int current = getBlockState().getValue(WindmillBlock.STAGE);
        int now = 0;
        for (ItemStack stack : Helpers.iterate(inventory))
        {
            if (!stack.isEmpty())
            {
                now++;
            }
        }
        if (now != current)
        {
            level.setBlockAndUpdate(worldPosition, getBlockState().setValue(WindmillBlock.STAGE, Mth.clamp(now, 0, SLOTS)));
        }
    }

    @Override
    public void setAndUpdateSlots(int slot)
    {
        super.setAndUpdateSlots(slot);
        needsStateUpdate = true;
    }

    @Override
    public int getSlotStackLimit(int slot)
    {
        return 1;
    }

    @Override
    public boolean isItemValid(int slot, ItemStack stack)
    {
        return Helpers.isItem(stack, TFCTags.Items.WINDMILL_BLADES);
    }
}

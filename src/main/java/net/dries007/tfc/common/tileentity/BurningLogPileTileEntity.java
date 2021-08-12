/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.tileentity;

import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;

import net.dries007.tfc.common.blocks.CharcoalPileBlock;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.config.TFCConfig;

public class BurningLogPileTileEntity extends TickCounterTileEntity
{
    public static void serverTick(Level level, BlockPos pos, BlockState state, BurningLogPileTileEntity entity)
    {
        if (entity.lastUpdateTick > 0 && entity.getTicksSinceUpdate() > TFCConfig.SERVER.charcoalTicks.get())
        {
            entity.createCharcoal();
        }
    }

    private int logs;

    public BurningLogPileTileEntity(BlockPos pos, BlockState state)
    {
        super(TFCTileEntities.BURNING_LOG_PILE.get(), pos, state);
    }

    @Override
    public void load(CompoundTag nbt)
    {
        logs = nbt.getInt("logs");
        super.load(nbt);
    }

    @Override
    public CompoundTag save(CompoundTag nbt)
    {
        nbt.putInt("logs", logs);
        return super.save(nbt);
    }

    public void light(int logs)
    {
        this.logs = logs;
        resetCounter();
        markForSync();
    }

    /**
     * This function does some magic **** to not create floating charcoal.
     */
    private void createCharcoal()
    {
        if (level == null) return;
        final BlockState pile = TFCBlocks.CHARCOAL_PILE.get().defaultBlockState();
        final BlockPos.MutableBlockPos mutablePos = worldPosition.mutable();

        int j = 0;
        Block block;
        do
        {
            j++;
            mutablePos.move(Direction.DOWN);
            block = level.getBlockState(mutablePos).getBlock();
            // This is here so that the charcoal pile will collapse Bottom > Top
            // Because the pile scans Top > Bottom this is necessary to avoid floating blocks
            if (block == TFCBlocks.LOG_PILE.get())
            {
                return;
            }
        } while (level.isEmptyBlock(worldPosition) || block == TFCBlocks.CHARCOAL_PILE.get() || block == TFCBlocks.BURNING_LOG_PILE.get());

        double logs = this.logs * (0.25 + 0.25 * level.getRandom().nextFloat());
        int charcoal = (int) Mth.clamp(logs, 0, 8);
        if (charcoal == 0)
        {
            level.setBlockAndUpdate(worldPosition, Blocks.AIR.defaultBlockState());
            return;
        }
        if (j == 1)
        {
            // This log pile is at the bottom of the charcoal pit
            level.setBlockAndUpdate(worldPosition, pile.setValue(CharcoalPileBlock.LAYERS, charcoal));
            return;
        }
        mutablePos.setWithOffset(worldPosition, 0, j - 1, 0);
        for (int k = j - 1; k >= 0; k--)
        {
            // Climb back up from the bottom
            mutablePos.move(Direction.DOWN);
            BlockState state = level.getBlockState(mutablePos);
            if (level.isEmptyBlock(mutablePos))
            {
                // If it hits air, place the remaining pile in that block
                level.setBlockAndUpdate(mutablePos, pile.setValue(CharcoalPileBlock.LAYERS, charcoal));
                level.setBlockAndUpdate(worldPosition, Blocks.AIR.defaultBlockState());
                return;
            }

            if (state.is(TFCBlocks.CHARCOAL_PILE.get()))
            {
                // Place what it can in the existing charcoal pit, then continue climbing
                charcoal += state.getValue(CharcoalPileBlock.LAYERS);
                int toCreate = Math.min(charcoal, 8);
                level.setBlockAndUpdate(mutablePos, pile.setValue(CharcoalPileBlock.LAYERS, toCreate));
                charcoal -= toCreate;
            }

            if (charcoal <= 0)
            {
                level.setBlockAndUpdate(worldPosition, Blocks.AIR.defaultBlockState());
                return;
            }
        }
        // If you exit the loop, its arrived back at the original position OR needs to rest the original position, and needs to replace that block
        level.setBlockAndUpdate(worldPosition, pile.setValue(CharcoalPileBlock.LAYERS, charcoal));
    }
}

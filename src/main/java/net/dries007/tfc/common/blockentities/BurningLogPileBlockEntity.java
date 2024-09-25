/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blockentities;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import net.dries007.tfc.common.blocks.CharcoalPileBlock;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.config.TFCConfig;
import net.dries007.tfc.util.Helpers;

public class BurningLogPileBlockEntity extends TickCounterBlockEntity
{
    public static void serverTick(Level level, BlockPos pos, BlockState state, BurningLogPileBlockEntity entity)
    {
        if (entity.lastUpdateTick > 0 && entity.getTicksSinceUpdate() > TFCConfig.SERVER.charcoalTicks.get())
        {
            entity.createCharcoal();
        }
    }

    private int logs;

    public BurningLogPileBlockEntity(BlockPos pos, BlockState state)
    {
        super(TFCBlockEntities.BURNING_LOG_PILE.get(), pos, state);
    }

    @Override
    public void loadAdditional(CompoundTag nbt, HolderLookup.Provider provider)
    {
        logs = nbt.getInt("logs");
        super.loadAdditional(nbt, provider);
    }

    @Override
    public void saveAdditional(CompoundTag nbt, HolderLookup.Provider provider)
    {
        nbt.putInt("logs", logs);
        super.saveAdditional(nbt, provider);
    }

    public void light(int logs)
    {
        this.logs = logs;
        resetCounter();
        markForSync();
    }

    public int getLogs()
    {
        return logs;
    }

    private void createCharcoal()
    {
        if (level == null) return;
        if (isPileBlock(level.getBlockState(worldPosition.above()))) return;
        int charcoal = getCharcoalAmount(level, this.logs);
        int height = 1;

        BlockPos.MutableBlockPos currentPos = worldPosition.mutable().move(Direction.DOWN);
        BlockState currentState = level.getBlockState(currentPos);
        while (currentState.is(TFCBlocks.BURNING_LOG_PILE.get()))
        {
            height += 1;
            int logs = level.getBlockEntity(currentPos, TFCBlockEntities.BURNING_LOG_PILE.get()).map(BurningLogPileBlockEntity::getLogs).orElse(0);
            charcoal += getCharcoalAmount(level, logs);
            currentPos.move(Direction.DOWN);
            currentState = level.getBlockState(currentPos);
        }

        // Set the current position to the bottom of the pile
        currentPos.set(worldPosition).move(0, 1 - height, 0);

        // If the block below is charcoal stack that first
        BlockState belowState = level.getBlockState(currentPos.below());
        if (belowState.is(TFCBlocks.CHARCOAL_PILE.get()))
        {
            int currentAmount = belowState.getValue(CharcoalPileBlock.LAYERS);
            int amount = Mth.clamp(charcoal, 0, 8 - currentAmount);
            if (amount > 0)
            {
                charcoal -= amount;
                level.setBlockAndUpdate(currentPos.below(), belowState.setValue(CharcoalPileBlock.LAYERS, currentAmount + amount));
            }
        }

        for (int i = 0; i < height; i++)
        {
            if (charcoal > 0)
            {
                int amount = Mth.clamp(charcoal, 0, 8);
                charcoal -= amount;
                level.setBlockAndUpdate(currentPos, TFCBlocks.CHARCOAL_PILE.get().defaultBlockState().setValue(CharcoalPileBlock.LAYERS, amount));
            }
            else
            {
                level.setBlockAndUpdate(currentPos, Blocks.AIR.defaultBlockState());
            }
            currentPos.move(Direction.UP);
        }

    }

    private static int getCharcoalAmount(Level level, int logs)
    {
        return (int) Mth.clamp(logs * (0.25 + 0.25 * level.getRandom().nextFloat()), 0, 8);
    }

    private static boolean isPileBlock(BlockState state)
    {
        return Helpers.isBlock(state, TFCBlocks.CHARCOAL_PILE.get()) || Helpers.isBlock(state, TFCBlocks.BURNING_LOG_PILE.get());
    }
}

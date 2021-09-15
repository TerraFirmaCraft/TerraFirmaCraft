/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks.wood;

import java.util.Random;
import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SaplingBlock;
import net.minecraft.world.level.block.state.BlockState;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blockentities.TickCounterBlockEntity;
import net.dries007.tfc.common.blocks.EntityBlockExtension;
import net.dries007.tfc.common.blocks.ExtendedProperties;
import net.dries007.tfc.common.blocks.IForgeBlockExtension;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.calendar.ICalendar;
import net.dries007.tfc.world.feature.tree.TFCTreeGrower;

public class TFCSaplingBlock extends SaplingBlock implements IForgeBlockExtension, EntityBlockExtension
{
    private final ExtendedProperties properties;
    private final int daysToGrow;

    public TFCSaplingBlock(TFCTreeGrower tree, ExtendedProperties properties, int days)
    {
        super(tree, properties.properties());
        this.properties = properties;
        this.daysToGrow = days;
    }

    @Override
    public ExtendedProperties getExtendedProperties()
    {
        return properties;
    }

    @Override
    public void randomTick(BlockState state, ServerLevel worldIn, BlockPos pos, Random random)
    {
        if (worldIn.getMaxLocalRawBrightness(pos.above()) >= 9 && random.nextInt(7) == 0)
        {
            if (!worldIn.isAreaLoaded(pos, 1))
                return; // Forge: prevent loading unloaded chunks when checking neighbor's light
            TickCounterBlockEntity te = Helpers.getBlockEntity(worldIn, pos, TickCounterBlockEntity.class);
            if (te != null)
            {
                long days = te.getTicksSinceUpdate() / ICalendar.TICKS_IN_DAY;
                if (days > daysToGrow)
                {
                    this.advanceTree(worldIn, pos, state.setValue(STAGE, 1), random);
                }
            }
        }
    }

    @Override
    public boolean isValidBonemealTarget(BlockGetter worldIn, BlockPos pos, BlockState state, boolean isClient)
    {
        return false;
    }

    @Override
    public void setPlacedBy(Level worldIn, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack)
    {
        TickCounterBlockEntity te = Helpers.getBlockEntity(worldIn, pos, TickCounterBlockEntity.class);
        if (te != null)
        {
            te.resetCounter();
        }
        super.setPlacedBy(worldIn, pos, state, placer, stack);
    }

    @Override
    protected boolean mayPlaceOn(BlockState state, BlockGetter worldIn, BlockPos pos)
    {
        return super.mayPlaceOn(state, worldIn, pos) || TFCTags.Blocks.BUSH_PLANTABLE_ON.contains(state.getBlock());
    }
}
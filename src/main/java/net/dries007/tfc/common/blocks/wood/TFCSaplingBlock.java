/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks.wood;

import java.util.Random;
import java.util.function.Supplier;

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
import net.dries007.tfc.config.TFCConfig;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.calendar.ICalendar;
import net.dries007.tfc.world.feature.tree.TFCTreeGrower;
import org.jetbrains.annotations.Nullable;

public class TFCSaplingBlock extends SaplingBlock implements IForgeBlockExtension, EntityBlockExtension
{
    private final ExtendedProperties properties;
    private final Supplier<Integer> daysToGrow;

    public TFCSaplingBlock(TFCTreeGrower tree, ExtendedProperties properties, int days)
    {
        this(tree, properties, () -> days);
    }

    public TFCSaplingBlock(TFCTreeGrower tree, ExtendedProperties properties, Supplier<Integer> days)
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
    @SuppressWarnings("deprecation")
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, Random random)
    {
        if (level.getMaxLocalRawBrightness(pos.above()) >= 9 && random.nextInt(7) == 0)
        {
            if (!level.isAreaLoaded(pos, 1))
            {
                return;
            }
            if (level.getBlockEntity(pos) instanceof TickCounterBlockEntity counter)
            {
                if (counter.getTicksSinceUpdate() > ICalendar.TICKS_IN_DAY *  getDaysToGrow() * TFCConfig.SERVER.globalSaplingGrowthModifier.get())
                {
                    this.advanceTree(level, pos, state.setValue(STAGE, 1), random);
                }
            }
        }
    }

    @Override
    public boolean isValidBonemealTarget(BlockGetter level, BlockPos pos, BlockState state, boolean isClient)
    {
        return false;
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack)
    {
        TickCounterBlockEntity.reset(level, pos);
        super.setPlacedBy(level, pos, state, placer, stack);
    }

    @Override
    protected boolean mayPlaceOn(BlockState state, BlockGetter level, BlockPos pos)
    {
        return super.mayPlaceOn(state, level, pos) || Helpers.isBlock(state.getBlock(), TFCTags.Blocks.BUSH_PLANTABLE_ON);
    }

    public int getDaysToGrow()
    {
        return daysToGrow.get();
    }

}
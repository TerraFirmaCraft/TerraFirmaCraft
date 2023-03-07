/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks;

import java.util.Random;
import java.util.function.ToIntFunction;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.CandleBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

import net.dries007.tfc.common.blockentities.TickCounterBlockEntity;
import net.dries007.tfc.common.items.TFCItems;
import net.dries007.tfc.config.TFCConfig;
import net.dries007.tfc.util.Helpers;

public class TFCCandleBlock extends CandleBlock implements IForgeBlockExtension, EntityBlockExtension
{
    public static void onRandomTick(BlockState state, ServerLevel level, BlockPos pos)
    {
        if (level.getBlockEntity(pos) instanceof TickCounterBlockEntity candle)
        {
            final int candleTicks = TFCConfig.SERVER.candleTicks.get();
            if (candle.getTicksSinceUpdate() > candleTicks && candleTicks > 0)
            {
                level.setBlockAndUpdate(pos, state.setValue(LIT, false));
            }
        }
    }

    public static final ToIntFunction<BlockState> LIGHTING_SCALE = (state) -> state.getValue(LIT) ? 3 * state.getValue(CANDLES) + 2 : 0;

    private final ExtendedProperties properties;

    public TFCCandleBlock(ExtendedProperties properties)
    {
        super(properties.properties());
        this.properties = properties;
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit)
    {
        // This is a QoL fix for candle-firestarter interactions. Candles extinguish on right click, so firestarter + NOT SHIFT = immediately extinguished, after you light it.
        // Firestarter can only be used in the main hand, so if we detect that specific case, we don't allow the candle to be extinguished.
        if (Helpers.isItem(player.getMainHandItem(), TFCItems.FIRESTARTER.get()))
        {
            return InteractionResult.PASS;
        }
        return super.use(state, level, pos, player, hand, hit);
    }

    @Override
    public ExtendedProperties getExtendedProperties()
    {
        return this.properties;
    }

    @Override
    @SuppressWarnings("deprecation")
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, Random rand)
    {
        onRandomTick(state, level, pos);
    }

}

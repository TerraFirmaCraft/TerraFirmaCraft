/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.mixin;

import java.util.Random;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SnowLayerBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blocks.SnowPileBlock;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.blocks.wood.ILeavesBlock;
import net.dries007.tfc.config.TFCConfig;
import net.dries007.tfc.util.EnvironmentHelpers;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.climate.Climate;
import net.dries007.tfc.util.climate.OverworldClimateModel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SnowLayerBlock.class)
public abstract class SnowLayerBlockMixin extends Block
{
    private SnowLayerBlockMixin(Properties properties)
    {
        super(properties);
    }

    @Override
    public float getSpeedFactor()
    {
        return TFCConfig.SERVER.enableSnowSlowEntities.get() ? 0.6f : 1.0f;
    }

    /**
     * Add behavior to snow blocks - when they are destroyed, they should only destroy one layer.
     */
    @Override
    public boolean onDestroyedByPlayer(BlockState state, Level level, BlockPos pos, Player player, boolean willHarvest, FluidState fluid)
    {
        playerWillDestroy(level, pos, state, player);
        final int prevLayers = state.getValue(SnowLayerBlock.LAYERS);
        if (prevLayers > 1 && !player.isCreative())
        {
            return level.setBlock(pos, state.setValue(SnowLayerBlock.LAYERS, prevLayers - 1), level.isClientSide ? 11 : 3);
        }
        return level.setBlock(pos, fluid.createLegacyBlock(), level.isClientSide ? 11 : 3);
    }

    @Inject(method = "canSurvive", at = @At(value = "RETURN"), cancellable = true)
    private void canSurviveAddIceAndLeavesConditions(BlockState state, LevelReader level, BlockPos pos, CallbackInfoReturnable<Boolean> cir)
    {
        // Allow tfc leaves to accumulate a single layer of snow on them, despite not having a solid collision face
        // This condition cannot be properly added via the tag, because we only want this to pass for single-layer snow
        if (!cir.getReturnValueZ() && level.getBlockState(pos.below()).getBlock() instanceof ILeavesBlock && state.getValue(SnowLayerBlock.LAYERS) == 1)
        {
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "updateShape", at = @At(value = "RETURN"), cancellable = true)
    private void updateShapeSurviveOnLeavesWithSingleLayer(BlockState stateIn, Direction facing, BlockState facingState, LevelAccessor level, BlockPos currentPos, BlockPos facingPos, CallbackInfoReturnable<BlockState> cir)
    {
        // If we can't survive, see if we can survive with only one layer, to allow the above leaves check to pass instead
        if (Helpers.isBlock(cir.getReturnValue(), Blocks.AIR) && stateIn.getValue(SnowLayerBlock.LAYERS) > 1)
        {
            final BlockState state = stateIn.setValue(SnowLayerBlock.LAYERS, 1);
            if (state.canSurvive(level, currentPos))
            {
                cir.setReturnValue(state);
            }
        }
    }

    @Inject(method = "getStateForPlacement", at = @At(value = "HEAD"), cancellable = true)
    private void getStateForPlacementOnSnowPile(BlockPlaceContext context, CallbackInfoReturnable<BlockState> cir)
    {
        final BlockState state = context.getLevel().getBlockState(context.getClickedPos());
        if (Helpers.isBlock(state, TFCBlocks.SNOW_PILE.get()))
        {
            // Similar to how snow layers modifies their placement state when targeting other snow layers, we do the same for snow piles
            cir.setReturnValue(state.setValue(SnowLayerBlock.LAYERS, Math.min(8, state.getValue(SnowLayerBlock.LAYERS) + 1)));
        }
    }

    @Inject(method = "randomTick", at = @At(value = "HEAD"), cancellable = true)
    private void meltRarelyDueToTemperature(BlockState state, ServerLevel level, BlockPos pos, RandomSource random, CallbackInfo ci)
    {
        // Heavily reduced chance, as most snow melting happens through EnvironmentHelpers, this is only really to account for overhangs and hidden snow
        if (random.nextInt(EnvironmentHelpers.SNOW_MELT_RANDOM_TICK_CHANCE) == 0 && Climate.getTemperature(level, pos) > OverworldClimateModel.SNOW_MELT_TEMPERATURE)
        {
            SnowPileBlock.removePileOrSnow(level, pos, state);
            ci.cancel();
        }
    }
}

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
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SnowLayerBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;

import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.blocks.wood.ILeavesBlock;
import net.dries007.tfc.config.TFCConfig;
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
    public boolean onDestroyedByPlayer(BlockState state, Level world, BlockPos pos, Player player, boolean willHarvest, FluidState fluid)
    {
        playerWillDestroy(world, pos, state, player);
        final int prevLayers = state.getValue(SnowLayerBlock.LAYERS);
        if (prevLayers > 1)
        {
            return world.setBlock(pos, state.setValue(SnowLayerBlock.LAYERS, prevLayers - 1), world.isClientSide ? 11 : 3);
        }
        return world.setBlock(pos, fluid.createLegacyBlock(), world.isClientSide ? 11 : 3);
    }

    @Inject(method = "canSurvive", at = @At(value = "RETURN"), cancellable = true)
    private void inject$canSurvive(BlockState state, LevelReader worldIn, BlockPos pos, CallbackInfoReturnable<Boolean> cir)
    {
        if (cir.getReturnValueZ())
        {
            // Snow should not survive on ice (this adds to the big existing conditional
            BlockState belowState = worldIn.getBlockState(pos.below());
            if (belowState.is(TFCBlocks.SEA_ICE.get()))
            {
                cir.setReturnValue(false);
            }
        }
        else
        {
            // Allow tfc leaves to accumulate a single layer of snow on them, despite not having a solid collision face
            if (state.getValue(SnowLayerBlock.LAYERS) == 1)
            {
                BlockState stateDown = worldIn.getBlockState(pos.below());
                if (stateDown.getBlock() instanceof ILeavesBlock)
                {
                    cir.setReturnValue(true);
                }
            }
        }
    }

    @Inject(method = "updateShape", at = @At(value = "RETURN"), cancellable = true)
    private void inject$updateShape(BlockState stateIn, Direction facing, BlockState facingState, LevelAccessor worldIn, BlockPos currentPos, BlockPos facingPos, CallbackInfoReturnable<BlockState> cir)
    {
        // If we can't survive, see if we can survive with only one layer, to allow the above leaves check to pass instead
        if (cir.getReturnValue().is(Blocks.AIR) && stateIn.getValue(SnowLayerBlock.LAYERS) > 1)
        {
            BlockState state = stateIn.setValue(SnowLayerBlock.LAYERS, 1);
            if (state.canSurvive(worldIn, currentPos))
            {
                cir.setReturnValue(state);
            }
        }
    }

    @Inject(method = "randomTick", at = @At(value = "RETURN"))
    private void inject$randomTick(BlockState state, ServerLevel level, BlockPos pos, Random random, CallbackInfo ci)
    {
        if (TFCConfig.SERVER.enableSnowAffectedByTemperature.get())
        {
            // Only run this if the default logic hasn't already set the block to air
            BlockState prevState = level.getBlockState(pos);
            if (prevState == state && Climate.getTemperature(level, pos) > OverworldClimateModel.SNOW_MELT_TEMPERATURE)
            {
                int layers = state.getValue(SnowLayerBlock.LAYERS);
                if (layers != 8 || !level.getBlockState(pos.above()).is(this)) // If the above block is also layers, that should decay first
                {
                    if (layers > 1)
                    {
                        level.setBlockAndUpdate(pos, state.setValue(SnowLayerBlock.LAYERS, layers - 1));
                    }
                    else
                    {
                        dropResources(state, level, pos);
                        level.removeBlock(pos, false);
                    }
                }
            }
        }
    }
}

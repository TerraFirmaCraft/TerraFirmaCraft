package net.dries007.tfc.mixin.block;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SnowBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.blocks.wood.ILeavesBlock;
import net.dries007.tfc.config.TFCConfig;
import net.dries007.tfc.util.Climate;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * A series of modifications to snow blocks:
 * 1. they can melt based on the TFC climate
 * 2. allow placement on top of TFC leaves (which are non solid)
 * 3. Slow entities passing through them
 * 4. When broken, only break one layer of snow
 */
@Mixin(SnowBlock.class)
public abstract class SnowBlockMixin extends Block
{
    private SnowBlockMixin(Properties properties)
    {
        super(properties);
    }

    @Inject(method = "canSurvive", at = @At(value = "RETURN"), cancellable = true)
    private void inject$canSurvive(BlockState state, IWorldReader worldIn, BlockPos pos, CallbackInfoReturnable<Boolean> cir)
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
            if (state.getValue(SnowBlock.LAYERS) == 1)
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
    private void inject$updateShape(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos, CallbackInfoReturnable<BlockState> cir)
    {
        // If we can't survive, see if we can survive with only one layer, to allow the above leaves check to pass instead
        if (cir.getReturnValue().is(Blocks.AIR) && stateIn.getValue(SnowBlock.LAYERS) > 1)
        {
            BlockState state = stateIn.setValue(SnowBlock.LAYERS, 1);
            if (state.canSurvive(worldIn, currentPos))
            {
                cir.setReturnValue(state);
            }
        }
    }

    @Inject(method = "randomTick", at = @At(value = "RETURN"))
    private void inject$randomTick(BlockState state, ServerWorld worldIn, BlockPos pos, Random random, CallbackInfo ci)
    {
        if (TFCConfig.SERVER.enableSnowAffectedByTemperature.get())
        {
            // Only run this if the default logic hasn't already set the block to air
            BlockState prevState = worldIn.getBlockState(pos);
            if (prevState == state && Climate.getTemperature(worldIn, pos) > Climate.SNOW_MELT_TEMPERATURE)
            {
                int layers = state.getValue(SnowBlock.LAYERS);
                if (layers != 8 || !worldIn.getBlockState(pos.above()).is(this)) // If the above block is also layers, that should decay first
                {
                    if (layers > 1)
                    {
                        worldIn.setBlockAndUpdate(pos, state.setValue(SnowBlock.LAYERS, layers - 1));
                    }
                    else
                    {
                        dropResources(state, worldIn, pos);
                        worldIn.removeBlock(pos, false);
                    }
                }
            }
        }
    }

    @Override
    public float getSpeedFactor()
    {
        if (TFCConfig.SERVER.enableSnowSlowEntities.get())
        {
            return 0.6f;
        }
        return 1.0f;
    }

    /**
     * Add behavior to snow blocks - when they are destroyed, they should only destroy one layer.
     */
    @Override
    public boolean removedByPlayer(BlockState state, World world, BlockPos pos, PlayerEntity player, boolean willHarvest, FluidState fluid)
    {
        playerWillDestroy(world, pos, state, player);
        final int prevLayers = state.getValue(SnowBlock.LAYERS);
        if (prevLayers > 1)
        {
            return world.setBlock(pos, state.setValue(SnowBlock.LAYERS, prevLayers - 1), world.isClientSide ? 11 : 3);
        }
        return world.setBlock(pos, fluid.createLegacyBlock(), world.isClientSide ? 11 : 3);
    }
}

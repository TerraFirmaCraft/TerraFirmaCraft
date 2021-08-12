/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.mixin.block;

import java.util.Random;

import net.minecraft.block.BlockState;
import net.minecraft.block.BreakableBlock;
import net.minecraft.block.IceBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import net.dries007.tfc.config.TFCConfig;
import net.dries007.tfc.util.Climate;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Modifies ice blocks such that they melt based on the TFC climate
 */
@Mixin(IceBlock.class)
public abstract class IceBlockMixin extends BreakableBlock
{
    private IceBlockMixin(Properties properties)
    {
        super(properties);
    }

    @Shadow
    protected abstract void melt(BlockState state, World worldIn, BlockPos pos);

    @Inject(method = "randomTick", at = @At(value = "RETURN"))
    private void inject$randomTick(BlockState state, ServerWorld worldIn, BlockPos pos, Random random, CallbackInfo ci)
    {
        if (TFCConfig.SERVER.enableIceAffectedByTemperature.get())
        {
            // Only run this if the default logic hasn't already set the block to air
            BlockState prevState = worldIn.getBlockState(pos);
            if (prevState == state && Climate.getTemperature(worldIn, pos) > Climate.ICE_MELT_TEMPERATURE)
            {
                melt(state, worldIn, pos);
            }
        }
    }
}

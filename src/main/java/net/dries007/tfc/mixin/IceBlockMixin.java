/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.mixin;

import java.util.Random;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.IceBlock;
import net.minecraft.world.level.block.state.BlockState;

import net.dries007.tfc.common.blocks.IcePileBlock;
import net.dries007.tfc.util.EnvironmentHelpers;
import net.dries007.tfc.util.climate.Climate;
import net.dries007.tfc.util.climate.OverworldClimateModel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(IceBlock.class)
public abstract class IceBlockMixin extends Block
{
    private IceBlockMixin(Properties properties)
    {
        super(properties);
    }

    @Inject(method = "randomTick", at = @At("HEAD"), cancellable = true)
    private void meltRarelyDueToTemperature(BlockState state, ServerLevel level, BlockPos pos, Random random, CallbackInfo ci)
    {
        // Heavily reduced chance, as most snow melting happens through EnvironmentHelpers, this is only really to account for overhangs and hidden snow
        if (random.nextInt(EnvironmentHelpers.ICE_MELT_RANDOM_TICK_CHANCE) == 0 && Climate.getTemperature(level, pos) > OverworldClimateModel.ICE_MELT_TEMPERATURE)
        {
            IcePileBlock.removeIcePileOrIce(level, pos, state);
            ci.cancel();
        }
    }
}

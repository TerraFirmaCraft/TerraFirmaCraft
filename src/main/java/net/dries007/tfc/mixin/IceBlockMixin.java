package net.dries007.tfc.mixin;

import java.util.Random;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.IceBlock;
import net.minecraft.world.level.block.state.BlockState;

import net.dries007.tfc.config.TFCConfig;
import net.dries007.tfc.util.climate.Climate;
import net.dries007.tfc.util.climate.OverworldClimateModel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(IceBlock.class)
public abstract class IceBlockMixin
{
    @Shadow
    protected abstract void melt(BlockState state, Level worldIn, BlockPos pos);

    @Inject(method = "randomTick", at = @At(value = "RETURN"))
    private void inject$randomTick(BlockState state, ServerLevel level, BlockPos pos, Random random, CallbackInfo ci)
    {
        if (TFCConfig.SERVER.enableIceAffectedByTemperature.get())
        {
            // Only run this if the default logic hasn't already set the block to air
            BlockState prevState = level.getBlockState(pos);
            if (prevState == state && Climate.getTemperature(level, pos) > OverworldClimateModel.ICE_MELT_TEMPERATURE)
            {
                melt(state, level, pos);
            }
        }
    }
}

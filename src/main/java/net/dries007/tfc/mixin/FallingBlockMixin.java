package net.dries007.tfc.mixin;

import java.util.Random;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.FallingBlock;
import net.minecraft.world.level.block.state.BlockState;

import net.dries007.tfc.common.recipes.LandslideRecipe;
import net.dries007.tfc.common.recipes.inventory.BlockRecipeWrapper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FallingBlock.class)
public abstract class FallingBlockMixin
{
    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    private void preventVanillaFallingBlockBehavior(BlockState state, ServerLevel level, BlockPos pos, Random random, CallbackInfo ci)
    {
        if (LandslideRecipe.getRecipe(level, new BlockRecipeWrapper(pos, state)) != null)
        {
            ci.cancel();
        }
    }
}

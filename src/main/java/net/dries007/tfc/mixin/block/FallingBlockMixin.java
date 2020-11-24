package net.dries007.tfc.mixin.block;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.FallingBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;

import net.dries007.tfc.common.recipes.BlockRecipeWrapper;
import net.dries007.tfc.common.recipes.LandslideRecipe;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Nullify vanilla falling block behavior when we have alternate handling via landslide recipes
 */
@Mixin(FallingBlock.class)
public abstract class FallingBlockMixin extends Block
{
    private FallingBlockMixin(Properties properties)
    {
        super(properties);
    }

    /**
     * This it responsible for actually causing vanilla falling blocks to fall.
     * Ticks are scheduled elsewhere but we do not bother catching them as they will just no-op here.
     */
    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    private void inject$onPlace(BlockState state, ServerWorld worldIn, BlockPos pos, Random rand, CallbackInfo ci)
    {
        if (LandslideRecipe.getRecipe(worldIn, new BlockRecipeWrapper(pos, state)) != null)
        {
            ci.cancel();
        }
    }
}

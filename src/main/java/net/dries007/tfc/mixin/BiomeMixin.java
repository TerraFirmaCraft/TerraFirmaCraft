package net.dries007.tfc.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.biome.Biome;

import net.dries007.tfc.util.Climate;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Biome.class)
public abstract class BiomeMixin
{
    /**
     * Redirect a call to {@link Biome#getTemperature(BlockPos)} with one that has a world and position context
     *
     * In vanilla this is either called from ServerWorld, or from world generation with ISeedReader - both of which are able to cast up to IWorld.
     * For cases where this cast is not valid we just default to the vanilla temperature.
     */
    @Redirect(method = "shouldSnow", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/biome/Biome;isColdEnoughToSnow(Lnet/minecraft/core/BlockPos;)Z"))
    private boolean shouldSnowWithClimate(Biome biome, BlockPos pos, LevelReader worldIn)
    {
        return Climate.getVanillaBiomeTemperature(biome, worldIn instanceof LevelAccessor level ? level : null, pos) < 0.15f;
    }

    /**
     * Redirect a call to {@link Biome#getTemperature(BlockPos)} with one that has a world and position context
     *
     * In vanilla this is either called from ServerWorld, or from world generation with ISeedReader - both of which are able to cast up to IWorld.
     * FFor cases where this cast is not valid we just default to the vanilla temperature.
     */
    @Redirect(method = "shouldFreeze(Lnet/minecraft/world/level/LevelReader;Lnet/minecraft/core/BlockPos;Z)Z", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/biome/Biome;getTemperature(Lnet/minecraft/core/BlockPos;)F"))
    private float shouldFreezeWithClimate(Biome biome, BlockPos pos, LevelReader worldIn)
    {
        return Climate.getVanillaBiomeTemperature(biome, worldIn instanceof LevelAccessor level ? level : null, pos);
    }
}

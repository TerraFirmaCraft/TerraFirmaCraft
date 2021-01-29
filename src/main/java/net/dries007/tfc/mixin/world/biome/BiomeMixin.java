/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.mixin.world.biome;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.biome.Biome;

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
    @Redirect(method = "shouldSnow", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/biome/Biome;getTemperature(Lnet/minecraft/util/math/BlockPos;)F"))
    private float redirect$shouldSnow$getTemperature(Biome biome, BlockPos pos, IWorldReader worldIn)
    {
        return Climate.getVanillaBiomeTemperature(biome, worldIn instanceof IWorld ? (IWorld) worldIn : null, pos);
    }

    /**
     * Redirect a call to {@link Biome#getTemperature(BlockPos)} with one that has a world and position context
     *
     * In vanilla this is either called from ServerWorld, or from world generation with ISeedReader - both of which are able to cast up to IWorld.
     * FFor cases where this cast is not valid we just default to the vanilla temperature.
     */
    @Redirect(method = "shouldFreeze(Lnet/minecraft/world/IWorldReader;Lnet/minecraft/util/math/BlockPos;Z)Z", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/biome/Biome;getTemperature(Lnet/minecraft/util/math/BlockPos;)F"))
    private float redirect$shouldFreeze$getTemperature(Biome biome, BlockPos pos, IWorldReader worldIn)
    {
        return Climate.getVanillaBiomeTemperature(biome, worldIn instanceof IWorld ? (IWorld) worldIn : null, pos);
    }
}

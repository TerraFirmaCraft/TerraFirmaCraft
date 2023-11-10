/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.mixin.client;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.biome.Biome;

import net.dries007.tfc.common.blocks.IBlockRain;
import net.dries007.tfc.util.climate.Climate;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(LevelRenderer.class)
public abstract class LevelRendererMixin
{
    @Shadow private ClientLevel level;

    /**
     * Redirect the call to {@link Biome#getPrecipitationAt(BlockPos)}.
     */
    @Redirect(method = "renderSnowAndRain", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/biome/Biome;getPrecipitationAt(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/biome/Biome$Precipitation;"))
    private Biome.Precipitation renderSnowAndRainUseClimate(Biome biome, BlockPos pos)
    {
        return Climate.getPrecipitation(level, pos);
    }

    /**
     * Redirect the call to {@link Biome#getPrecipitationAt(BlockPos)}.
     */
    @Redirect(method = "tickRain", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/biome/Biome;getPrecipitationAt(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/biome/Biome$Precipitation;"))
    private Biome.Precipitation tickRainUseClimate(Biome biome, BlockPos pos)
    {
        return Climate.getPrecipitation(level, pos);
    }

    @Redirect(method = "tickRain", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;getCollisionShape(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/phys/shapes/VoxelShape;"))
    private VoxelShape getCollisionShapeIgnoreLeaves(BlockState state, BlockGetter level, BlockPos pos)
    {
        return state.getBlock() instanceof IBlockRain ? Shapes.block() : state.getCollisionShape(level, pos);
    }

}

/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.client.extensions;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.particle.TerrainParticle;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.neoforged.neoforge.client.extensions.common.IClientBlockExtensions;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.client.RenderHelpers;
import net.dries007.tfc.common.blockentities.SheetPileBlockEntity;
import net.dries007.tfc.common.blockentities.TFCBlockEntities;
import net.dries007.tfc.common.blocks.devices.SheetPileBlock;
import net.dries007.tfc.util.data.FluidHeat;

public record SheetPileExtension(Block block) implements IClientBlockExtensions
{
    /**
     * Prevent vanilla particles, and render our own. This fixes two issues:
     * <ul>
     *     <li>Particles render based on the combined bounds of the block, not on the targeted face</li>
     *     <li>Particles render using the first-available texture by face, not the targeted face</li>
     * </ul>
     * Both of these are derived from behavior in {@link ParticleEngine#crack(BlockPos, Direction)}, and what this method
     * is based on, with modifications.
     *
     * @return {@code true} to prevent vanilla particles from rendering.
     */
    @Override
    public boolean addHitEffects(BlockState state, Level level, HitResult target, ParticleEngine manager)
    {
        // All vanilla call paths provide `BlockHitResult`, why doesn't this API?
        // There's no other way to access the position here, this is terrible...
        // Also, particle calls require a client level...
        if (state.getBlock() == block &&
            !SheetPileBlock.isEmptyContents(state) &&
            target instanceof BlockHitResult blockHit &&
            level instanceof ClientLevel clientLevel)
        {
            final BlockPos pos = blockHit.getBlockPos();
            final @Nullable SheetPileBlockEntity pile = level.getBlockEntity(pos, TFCBlockEntities.SHEET_PILE.get()).orElse(null);
            if (pile != null)
            {
                final @Nullable Direction targetedFace = SheetPileBlock.getTargetedFace(state, blockHit);
                if (targetedFace != null)
                {
                    addHitEffects(clientLevel, pos, state, targetedFace, pile.getOrCacheMetal(targetedFace));
                }
            }
        }
        return true;
    }

    private void addHitEffects(ClientLevel level, BlockPos pos, BlockState state, Direction face, FluidHeat metal)
    {
        double x = level.random.nextDouble() * 0.8 + 0.1;
        double y = level.random.nextDouble() * 0.8 + 0.1;
        double z = level.random.nextDouble() * 0.8 + 0.1;
        switch (face)
        {
            case DOWN -> y = 0.1;
            case UP -> y = 0.9;
            case NORTH -> z = 0.1;
            case SOUTH -> z = 0.9;
            case WEST -> x = 0.1;
            case EAST -> x = 0.9;
        }

        // Set the sprite directly rather than calling `updateSprite()`, so we pick up the correct particle for the face being hit
        final TextureAtlasSprite metalSprite = RenderHelpers.blockTexture(metal.textureId());
        final Particle particle = new TerrainParticle(level, pos.getX() + x, pos.getY() + y, pos.getZ() + z, 0, 0, 0, state, pos)
        {{
            sprite = metalSprite;
        }}
            .setPower(0.2f)
            .scale(0.6f);

        Minecraft.getInstance().particleEngine.add(particle);
    }

    /**
     * Prevent vanilla destruction particles from rendering when a sheet pile is broken. As far as I can tell, there is no practical way, from this
     * method, that we can identify what sheet is being broken. And without that, we can't ever ensure we render the correct particles outside of
     * weird edge cases, so I'd rather err on the side of correctness and say "these don't generate particles on break"
     */
    @Override
    public boolean addDestroyEffects(BlockState state, Level level, BlockPos pos, ParticleEngine manager)
    {
        return true;
    }
}

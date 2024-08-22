/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.CherryParticle;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec2;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.client.ClimateRenderCache;
import net.dries007.tfc.client.TFCColors;
import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blocks.plant.fruit.FruitTreeLeavesBlock;
import net.dries007.tfc.common.blocks.wood.TFCLeavesBlock;
import net.dries007.tfc.util.Helpers;

public class FallingLeafParticle extends CherryParticle
{
    private final float windMoveX;
    private final float windMoveZ;

    public FallingLeafParticle(ClientLevel level, double x, double y, double z, SpriteSet set, boolean tinted, @Nullable BlockState state)
    {
        super(level, x, y, z, set);

        final BlockPos pos = BlockPos.containing(x, y, z);

        if (state != null)
        {
            int color = -1;
            final Block block = state.getBlock();

            if (block instanceof FruitTreeLeavesBlock fruit)
            {
                color = fruit.getFlowerColor();
            }
            else if (block instanceof TFCLeavesBlock leaves && Helpers.isBlock(state, TFCTags.Blocks.SEASONAL_LEAVES))
            {
                color = TFCColors.getSeasonalFoliageColor(pos, 0, leaves.getAutumnIndex());
            }
            else if (tinted)
            {
                color = TFCColors.getFoliageColor(pos, 0);
            }
            if (color != -1)
            {
                setColor(((color >> 16) & 0xFF) / 255F, ((color >> 8) & 0xFF) / 255F, (color & 0xFF) / 255F);
            }
        }

        final Vec2 wind = ClimateRenderCache.INSTANCE.getWind();
        final float windStrength = wind.length();
        windMoveX = wind.x * windStrength * 0.6f;
        windMoveZ = wind.y * windStrength * 0.6f;
    }

    @Override
    public void move(double dx, double dy, double dz)
    {
        super.move(dx + windMoveX, dy, dz + windMoveZ);
    }

    public record Provider(SpriteSet set, boolean tinted) implements ParticleProvider<BlockParticleOption>
    {
        @Override
        public Particle createParticle(BlockParticleOption type, ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed)
        {
            return new FallingLeafParticle(level, x, y, z, set, tinted, type.getState());
        }
    }

    public record SimpleProvider(SpriteSet set) implements ParticleProvider<SimpleParticleType>
    {
        @Override
        public Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed)
        {
            return new FallingLeafParticle(level, x, y, z, set, false, null);
        }
    }
}

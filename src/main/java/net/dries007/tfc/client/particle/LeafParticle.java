/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.level.block.state.BlockState;

import net.dries007.tfc.client.ClimateRenderCache;
import net.dries007.tfc.client.TFCColors;
import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blocks.wood.TFCLeavesBlock;
import net.dries007.tfc.common.blocks.wood.Wood;
import net.dries007.tfc.util.Helpers;

public class LeafParticle extends TextureSheetParticle
{
    private final float windMoveX, windMoveZ;
    private final int xSignModifier = random.nextBoolean() ? 1 : -1;
    private final int zSignModifier = random.nextBoolean() ? 1 : -1;
    private final double xMod = (random.nextFloat() - 0.5f) / 7;
    private final double zMod = (random.nextFloat() - 0.5f) / 7;

    public LeafParticle(ClientLevel level, double x, double y, double z, double motionX, double motionY, double motionZ, boolean tinted)
    {
        super(level, x, y, z);
        lifetime = 60 + random.nextInt(20);
        xd = motionX;
        yd = motionY;
        zd = motionZ;
        scale(Mth.nextFloat(random, 1.8f, 2.6f));
        final BlockPos pos = BlockPos.containing(x, y, z);
        if (tinted)
        {
            final BlockState state = level.getBlockState(pos);
            final int color = Helpers.isBlock(state, TFCTags.Blocks.SEASONAL_LEAVES) ? TFCColors.getSeasonalFoliageColor(pos, 0, state.getBlock() instanceof TFCLeavesBlock ? ((TFCLeavesBlock) state.getBlock()).getAutumnIndex() : 0) : TFCColors.getFoliageColor(pos, 0);
            setColor(((color >> 16) & 0xFF) / 255F, ((color >> 8) & 0xFF) / 255F, (color & 0xFF) / 255F);
        }

        final Vec2 wind = ClimateRenderCache.INSTANCE.getWind();
        final float windStrength = wind.length();
        windMoveX = wind.x * windStrength * 0.2f;
        windMoveZ = wind.y * windStrength * 0.2f;
    }

    @Override
    public void tick()
    {
        xo = x;
        yo = y;
        zo = z;

        if (age++ >= lifetime) { remove(); }

        final float swayWave = 0.03f * Mth.sin(age / 12.5f);
        final float rollWave = 0.1f * Mth.sin(age / 12.5f);

        roll = rollWave * xSignModifier;

        yd -= 0.001D;
        move(xd, yd, zd);

        xd = swayWave * xSignModifier + xMod + windMoveX;
        yd *= 0.96D;
        zd = swayWave * zSignModifier + zMod + windMoveZ;

        if (onGround)
        {
            alpha += 0.04;
            if (alpha >= 1)
            {
                remove();
            }
        }
    }

    @Override
    public ParticleRenderType getRenderType()
    {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    public record Provider(SpriteSet set, boolean tinted) implements ParticleProvider<SimpleParticleType>
    {
        @Override
        public Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed)
        {
            LeafParticle particle = new LeafParticle(level, x, y, z, xSpeed, ySpeed, zSpeed, tinted);
            particle.pickSprite(set);
            return particle;
        }
    }
}

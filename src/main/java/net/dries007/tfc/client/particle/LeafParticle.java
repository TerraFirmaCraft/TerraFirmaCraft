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

import net.dries007.tfc.client.TFCColors;
import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.util.Helpers;

public class LeafParticle extends TextureSheetParticle
{
    private final int xSignModifier = random.nextBoolean() ? 1 : -1;
    private final int zSignModifier = random.nextBoolean() ? 1 : -1;
    private final double xMod = (random.nextFloat() - 0.5f) / 7;
    private final double zMod = (random.nextFloat() - 0.5f) / 7;

    private LeafParticle(ClientLevel level, double x, double y, double z, double motionX, double motionY, double motionZ, boolean tinted)
    {
        super(level, x, y, z);
        lifetime = 60 + random.nextInt(20);
        xd = motionX;
        yd = motionY;
        zd = motionZ;
        scale(Mth.nextFloat(random, 1.8f, 2.6f));
        final BlockPos pos = new BlockPos(x, y, z);
        if (tinted)
        {
            final int color = Helpers.isBlock(level.getBlockState(pos), TFCTags.Blocks.SEASONAL_LEAVES) ? TFCColors.getSeasonalFoliageColor(pos, 0) : TFCColors.getFoliageColor(pos, 0);
            setColor(((color >> 16) & 0xFF) / 255F, ((color >> 8) & 0xFF) / 255F, (color & 0xFF) / 255F);
        }
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

        xd = swayWave * xSignModifier + xMod;
        yd *= 0.96D;
        zd = swayWave * zSignModifier + zMod;

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

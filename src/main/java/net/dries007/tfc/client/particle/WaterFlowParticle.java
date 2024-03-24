/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.minecraft.world.level.material.FluidState;

import net.dries007.tfc.common.blocks.RiverWaterBlock;
import net.dries007.tfc.world.river.Flow;

public class WaterFlowParticle extends TextureSheetParticle
{
    private final float particleRandom;

    public WaterFlowParticle(ClientLevel level, double x, double y, double z)
    {
        super(level, x, y, z);
        this.setSize(0.02F, 0.02F);
        this.quadSize *= random.nextFloat() * 0.6F + 0.2F;
        final BlockPos pos = BlockPos.containing(x, y, z);
        final FluidState state = level.getFluidState(pos);
        setDirectionFromFlow(state);
        lifetime = 80 + random.nextInt(20);
        particleRandom = random.nextFloat() - 0.5f;
    }

    private void setDirectionFromFlow(FluidState state)
    {
        if (state.hasProperty(RiverWaterBlock.FLOW))
        {
            final Flow flow = state.getValue(RiverWaterBlock.FLOW);
            final var vec = flow.getVector();
            xd = (vec.x * 0.18) + (xd * 0.02);
            zd = (vec.z * 0.18) + (zd * 0.02);
            oRoll = roll;
            roll += particleRandom * 0.01;
        }
        else
        {
            xd *= 0.98;
            zd *= 0.98;
        }
    }

    @Override
    public void tick()
    {
        xo = x;
        yo = y;
        zo = z;
        final BlockPos pos = BlockPos.containing(x, y, z);
        final FluidState state = level.getFluidState(pos);
        yo = y = pos.getY() + state.getHeight(level, pos) + (particleRandom * 0.05f);
        setDirectionFromFlow(state);
        if (!state.hasProperty(RiverWaterBlock.FLOW) || !level.getBlockState(pos.above()).isAir())
        {
            lifetime = Math.min(lifetime, 10);
        }
        if (lifetime-- <= 0)
        {
            remove();
            level.addParticle(ParticleTypes.BUBBLE_POP, x, y, z, 0, 0, 0);
        }
        else
        {
            move(xd, yd, zd);
        }
    }

    @Override
    public ParticleRenderType getRenderType()
    {
        return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
    }

    public record Provider(SpriteSet sprite) implements ParticleProvider<SimpleParticleType>
    {
        @Override
        public Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed)
        {
            var particle = new WaterFlowParticle(level, x, y, z);
            particle.pickSprite(sprite);
            return particle;
        }
    }
}

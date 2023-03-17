/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.DripParticle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.util.Mth;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;

import net.dries007.tfc.client.RenderHelpers;

/**
 * Generic version of {@link DripParticle}
 */
public class FluidDripParticle extends TextureSheetParticle
{
    private final Fluid type;
    protected boolean isGlowing;

    public FluidDripParticle(ClientLevel level, double x, double y, double z, Fluid fluid)
    {
        super(level, x, y, z);
        setSize(0.01F, 0.01F);
        gravity = 0.06F;
        type = fluid;

        final int color = RenderHelpers.getFluidColor(type);
        rCol = ((color >> 16) & 0xFF) / 255f;
        gCol = ((color >> 8) & 0xFF) / 255f;
        bCol = ((color) & 0xFF) / 255f;
    }

    protected Fluid getFluid()
    {
        return type;
    }

    @Override
    public ParticleRenderType getRenderType()
    {
        return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
    }

    @Override
    public int getLightColor(float partialTick)
    {
        return isGlowing ? 240 : super.getLightColor(partialTick);
    }

    @Override
    public void tick()
    {
        xo = x;
        yo = y;
        zo = z;
        preMoveUpdate();
        if (!removed)
        {
            yd -= gravity;
            move(xd, yd, zd);
            postMoveUpdate();
            if (!removed)
            {
                xd *= 0.98F;
                yd *= 0.98F;
                zd *= 0.98F;
                final BlockPos blockpos = new BlockPos(x, y, z);
                final FluidState fluidstate = level.getFluidState(blockpos);
                if (fluidstate.getType() == type && y < blockpos.getY() + fluidstate.getHeight(level, blockpos))
                {
                    remove();
                }
            }
        }
    }

    protected void preMoveUpdate()
    {
        if (lifetime-- <= 0) remove();
    }

    protected void postMoveUpdate() {}

    public static class FluidHangParticle extends FluidDripParticle
    {
        private final ParticleOptions fallingParticle;

        public FluidHangParticle(ClientLevel level, double x, double y, double z, Fluid fluid)
        {
            this(level, x, y, z, fluid, new FluidParticleOption(TFCParticles.FLUID_FALL.get(), fluid));
        }

        public FluidHangParticle(ClientLevel level, double x, double y, double z, Fluid fluid, ParticleOptions fallParticle)
        {
            super(level, x, y, z, fluid);
            fallingParticle = fallParticle;
            lifetime = 40;
            gravity *= 0.02f;
        }


        @Override
        protected void preMoveUpdate()
        {
            if (lifetime-- <= 0)
            {
                remove();
                level.addParticle(fallingParticle, x, y, z, xd, yd, zd);
            }
        }

        @Override
        protected void postMoveUpdate()
        {
            xd *= 0.02D;
            yd *= 0.02D;
            zd *= 0.02D;
        }
    }

    public static class FluidFallAndLandParticle extends FluidDripParticle
    {
        private final ParticleOptions landingParticle;

        public FluidFallAndLandParticle(ClientLevel level, double x, double y, double z, Fluid fluid)
        {
            this(level, x, y, z, fluid, new FluidParticleOption(TFCParticles.FLUID_LAND.get(), fluid));
        }

        public FluidFallAndLandParticle(ClientLevel level, double x, double y, double z, Fluid fluid, ParticleOptions landParticle)
        {
            super(level, x, y, z, fluid);
            landingParticle = landParticle;
            lifetime = 40;
            gravity *= 0.02f;
        }

        @Override
        protected void postMoveUpdate()
        {
            if (this.onGround)
            {
                this.remove();
                this.level.addParticle(this.landingParticle, this.x, this.y, this.z, 0.0D, 0.0D, 0.0D);
            }
        }
    }

    public static class FluidLandParticle extends FluidFallAndLandParticle
    {
        public FluidLandParticle(ClientLevel level, double x, double y, double z, Fluid fluid)
        {
            super(level, x, y, z, fluid);
            lifetime = (int) (16.0D / (Math.random() * 0.8D + 0.2D));
        }
    }

    /**
     * This exists to take the place of a custom packet that would be used to track all manner of unnecessary parameters.
     */
    public static class BarrelDripParticle extends FluidFallAndLandParticle
    {
        public BarrelDripParticle(ClientLevel level, double x, double y, double z, Fluid fluid)
        {
            super(level, x, y, z, fluid);
            // heuristic: if we are at ~ the middle, we know this to be the non-motion direction
            // therefore we add speed in the other direction
            final double dx = x - Mth.floor(x);
            final double dz = z - Mth.floor(z);
            if (dx < 0.5)
            {
                xd = 0.01;
            }
            else if (dx > 0.5)
            {
                xd = -0.01;
            }
            else if (dz < 0.5)
            {
                zd = 0.01;
            }
            else
            {
                zd = -0.01;
            }
        }
    }

    public interface FluidParticleFactory
    {
        FluidDripParticle create(ClientLevel level, double x, double y, double z, Fluid fluid);
    }

    public static ParticleProvider<FluidParticleOption> provider(SpriteSet set, FluidParticleFactory factory)
    {
        return (type, level, x, y, z, dx, dy, dz) -> {
            final FluidDripParticle particle = factory.create(level, x, y, z, type.getFluid());
            particle.pickSprite(set);
            return particle;
        };
    }

}

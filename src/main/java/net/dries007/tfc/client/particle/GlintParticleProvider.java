/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.client.particle;

import net.minecraft.ChatFormatting;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.SuspendedTownParticle;
import net.minecraft.core.particles.SimpleParticleType;

public class GlintParticleProvider implements ParticleProvider<SimpleParticleType>
{
    private final SpriteSet sprite;
    private final float red;
    private final float green;
    private final float blue;

    public GlintParticleProvider(SpriteSet sprite, ChatFormatting format)
    {
        assert format.getColor() != null;
        final int color = format.getColor();

        this.sprite = sprite;
        this.red = ((color >> 16) & 0xFF) / 255F;
        this.green = ((color >> 8) & 0xFF) / 255F;
        this.blue = (color & 0xFF) / 255F;
    }

    @Override
    public Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed)
    {
        final SuspendedTownParticle particle = new SuspendedTownParticle(level, x, y, z, xSpeed, ySpeed, zSpeed){};
        particle.pickSprite(sprite);
        particle.setColor(red, green, blue);
        return particle;
    }
}

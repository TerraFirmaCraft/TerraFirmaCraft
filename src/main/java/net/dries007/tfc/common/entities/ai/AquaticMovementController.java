/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.entities.ai;

import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.controller.MovementController;
import net.minecraft.entity.passive.fish.AbstractFishEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.MathHelper;

public class AquaticMovementController extends MovementController
{
    private final AbstractFishEntity fish;
    private final boolean xz;
    private final int inertia;

    public AquaticMovementController(AbstractFishEntity fish, boolean xz, int inertia)
    {
        super(fish);
        this.fish = fish;
        this.xz = xz;
        this.inertia = inertia;
    }

    @Override
    public void tick()
    {
        if (fish.isEyeInFluid(FluidTags.WATER))
        {
            fish.setDeltaMovement(fish.getDeltaMovement().add(0.0D, 0.005D, 0.0D));
        }

        if (operation == MovementController.Action.MOVE_TO && !fish.getNavigation().isDone())
        {
            float speed = (float) (speedModifier * fish.getAttributeValue(Attributes.MOVEMENT_SPEED));
            fish.setSpeed(MathHelper.lerp(0.125F, fish.getSpeed(), speed));
            double dx = (wantedX - fish.getX()) * inertia;
            double dy = (wantedY - fish.getY()) * inertia;
            double dz = (wantedZ - fish.getZ()) * inertia;
            if (dy != 0.0D)
            {
                double length = MathHelper.sqrt(dx * dx + dy * dy + dz * dz);
                // tfc: this is normally only set for the Y direction, preventing fish from moving laterally in any meaningful way
                if (xz)
                {
                    fish.setDeltaMovement(fish.getDeltaMovement().add(fish.getSpeed() * (dx / length) * 0.03D, fish.getSpeed() * (dy / length) * 0.03D, fish.getSpeed() * (dz / length) * 0.03D));
                }
                else
                {
                    fish.setDeltaMovement(fish.getDeltaMovement().add(0, fish.getSpeed() * (dy / length) * 0.03D, 0));
                }
            }

            if (dx != 0.0D || dz != 0.0D)
            {
                float targetAngle = (float) (MathHelper.atan2(dz, dx) * (180F / Math.PI)) - 90.0F;
                fish.yRot = rotlerp(fish.yRot, targetAngle, 90.0F);
                fish.yBodyRot = fish.yRot;
            }

        }
        else
        {
            if (inertia > 1)
            {
                fish.setSpeed(MathHelper.lerp(0.7F, fish.getSpeed(), 0.0F));
            }
            else
            {
                fish.setSpeed(0.0F);
            }
        }
    }
}

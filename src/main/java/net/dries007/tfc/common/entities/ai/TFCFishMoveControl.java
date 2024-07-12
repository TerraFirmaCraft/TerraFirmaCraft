/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.entities.ai;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.neoforged.neoforge.common.NeoForgeMod;

public class TFCFishMoveControl extends MoveControl
{
    private final PathfinderMob fish;

    public TFCFishMoveControl(PathfinderMob fish)
    {
        super(fish);
        this.fish = fish;
    }

    /**
     * Copy of AbstractFish.FishMoveControl#tick
     */
    @Override
    public void tick()
    {
        if (fish.isEyeInFluidType(NeoForgeMod.WATER_TYPE.value()))
        {
            fish.setDeltaMovement(fish.getDeltaMovement().add(0.0D, 0.005D, 0.0D));
        }

        if (operation == MoveControl.Operation.MOVE_TO && !fish.getNavigation().isDone())
        {
            float f = (float) (speedModifier * fish.getAttributeValue(Attributes.MOVEMENT_SPEED));
            fish.setSpeed(Mth.lerp(0.125F, fish.getSpeed(), f));
            double dx = wantedX - fish.getX();
            double dy = wantedY - fish.getY();
            double dz = wantedZ - fish.getZ();
            if (dy != 0.0D)
            {
                double length = Math.sqrt(dx * dx + dy * dy + dz * dz);
                //tfc: fish are actually able to swim in the X and Z directions instead of just drifting
                fish.setDeltaMovement(fish.getDeltaMovement().add((double) fish.getSpeed() * (dx / length) * 0.03D, (double) fish.getSpeed() * (dy / length) * 0.1D, (double) fish.getSpeed() * (dz / length) * 0.1D));
            }

            if (dx != 0.0D || dz != 0.0D)
            {
                float f1 = (float) (Mth.atan2(dz, dx) * (double) (180F / (float) Math.PI)) - 90.0F;
                fish.setYRot(rotlerp(fish.getYRot(), f1, 90.0F));
                fish.yBodyRot = fish.getYRot();
            }

        }
        else
        {
            fish.setSpeed(0.0F);
        }
    }
}

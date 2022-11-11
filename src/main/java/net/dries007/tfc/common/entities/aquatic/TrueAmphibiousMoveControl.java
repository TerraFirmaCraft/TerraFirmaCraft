/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.entities.aquatic;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.control.SmoothSwimmingMoveControl;

public class TrueAmphibiousMoveControl extends SmoothSwimmingMoveControl
{
    private final MoveControl walkingMoveControl;

    public TrueAmphibiousMoveControl(Mob mob, int maxTurnX, int maxTurnY, float inWaterSpeed, float outOfWaterSpeed, boolean gravity)
    {
        super(mob, maxTurnX, maxTurnY, inWaterSpeed, outOfWaterSpeed, gravity);
        this.walkingMoveControl = new MoveControl(mob);
    }

    @Override
    public void setWantedPosition(double x, double y, double z, double speed)
    {
        super.setWantedPosition(x, y, z, speed);
        walkingMoveControl.setWantedPosition(x, y, z, speed);
    }

    @Override
    public void strafe(float forward, float strafe)
    {
        super.strafe(forward, strafe);
        walkingMoveControl.strafe(forward, strafe);
    }

    @Override
    public void tick()
    {
        if (mob.isInWater())
        {
            super.tick();
        }
        else
        {
            walkingMoveControl.tick();
        }
    }
}

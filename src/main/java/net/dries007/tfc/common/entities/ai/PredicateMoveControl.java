/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.entities.ai;

import java.util.function.Predicate;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.control.MoveControl;

public class PredicateMoveControl<T extends Mob> extends MoveControl
{
    private final T mob;
    private final Predicate<T> predicate;

    public PredicateMoveControl(T mob, Predicate<T> predicate)
    {
        super(mob);
        this.mob = mob;
        this.predicate = predicate;
    }

    @Override
    public void tick()
    {
        if (predicate.test(mob))
        {
            super.tick();
        }
    }
}

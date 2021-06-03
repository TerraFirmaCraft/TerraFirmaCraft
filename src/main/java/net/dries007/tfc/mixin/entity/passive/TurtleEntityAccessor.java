/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.mixin.entity.passive;

import net.minecraft.entity.passive.TurtleEntity;
import net.minecraft.util.math.BlockPos;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(TurtleEntity.class)
public interface TurtleEntityAccessor
{
    @Invoker("setHasEgg")
    void invoke$setHasEgg(boolean hasEgg);

    @Invoker("setLayingEgg")
    void invoke$setLayingEgg(boolean layingEgg);

    @Invoker("getHomePos")
    BlockPos invoke$getHomePos();

    @Invoker("isTravelling")
    boolean invoke$isTravelling();

    @Accessor("layEggCounter")
    int getEggCounter();

    @Accessor("layEggCounter")
    void setEggCounter(int counter);
}

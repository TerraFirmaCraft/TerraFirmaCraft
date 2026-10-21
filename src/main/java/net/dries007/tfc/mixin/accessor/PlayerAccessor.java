/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.mixin.accessor;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Player.class)
public interface PlayerAccessor
{
    @Invoker("setShoulderEntityLeft")
    void invoke$setShoulderEntityLeft(CompoundTag tag);

    @Invoker("setShoulderEntityRight")
    void invoke$setShoulderEntityRight(CompoundTag tag);
}
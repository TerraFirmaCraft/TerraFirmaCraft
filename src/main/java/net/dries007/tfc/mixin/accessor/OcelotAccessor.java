/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.mixin.accessor;

import net.minecraft.world.entity.animal.Ocelot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Ocelot.class)
public interface OcelotAccessor
{
    @Invoker("setTrusting")
    void invoke$setTrusting(boolean trusting);

    @Invoker("isTrusting")
    boolean invoke$isTrusting();
}

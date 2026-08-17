/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.mixin.accessor;

import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.animal.camel.Camel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Camel.class)
public interface CamelAccessor
{
    @Invoker("getBodyAnchorAnimationYOffset")
    double invoke$getBodyAnchorAnimationYOffset(boolean firstPassenger, float partialTick, EntityDimensions dimensions, float scale);
}

/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.mixin.client.accessor;

import net.minecraft.client.model.ParrotModel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ParrotModel.class)
public interface ParrotModelAccessor
{
    @Invoker("prepare")
    void invoke$prepare(ParrotModel.State state);

    @Invoker("setupAnim")
    void invoke$setupAnim(ParrotModel.State state, int tickCount, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch);
}
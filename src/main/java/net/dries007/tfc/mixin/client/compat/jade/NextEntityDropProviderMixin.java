/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.mixin.client.compat.jade;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import snownee.jade.addon.vanilla.NextEntityDropProvider;
import snownee.jade.api.Accessor;
import snownee.jade.api.EntityAccessor;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;

import net.dries007.tfc.common.entities.prey.TFCArmadillo;

@Mixin(NextEntityDropProvider.class)
public abstract class NextEntityDropProviderMixin
{
    @Inject(method = "appendTooltip(Lsnownee/jade/api/ITooltip;Lsnownee/jade/api/Accessor;Lsnownee/jade/api/config/IPluginConfig;)V", at = @At("HEAD"), cancellable = true)
    public void tfc$appendTooltip(ITooltip tooltip, Accessor accessor, IPluginConfig config, CallbackInfo ci)
    {
        if (accessor instanceof EntityAccessor entityAccessor && entityAccessor.getEntity() instanceof TFCArmadillo)
        {
            ci.cancel();
        }
    }
}

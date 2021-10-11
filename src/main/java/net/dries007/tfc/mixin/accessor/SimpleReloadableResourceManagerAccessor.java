/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.mixin.accessor;

import java.util.List;

import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.SimpleReloadableResourceManager;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(SimpleReloadableResourceManager.class)
public interface SimpleReloadableResourceManagerAccessor
{
    @Accessor("listeners")
    List<PreparableReloadListener> accessor$getListeners();
}

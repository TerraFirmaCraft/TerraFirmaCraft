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

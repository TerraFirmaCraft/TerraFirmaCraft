/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.mixin.plugin;

import net.neoforged.fml.loading.LoadingModList;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import com.google.common.collect.ImmutableMap;

/**
 * Mixin plugin to conditionally apply mixins only when specific mods are present.
 * This is better than using require = 0 when mixing into an optional mod's class,
 * because this avoids class not found errors in console when the target mod is
 * not present.
 */
public class TFCMixinPlugin implements IMixinConfigPlugin
{
    public static final Supplier<Boolean> TRUE = () -> true;

    public static final Map<String, Supplier<Boolean>> CONDITIONS = ImmutableMap.of(
        "net.dries007.tfc.mixin.client.compat.sodium.DefaultFluidRendererMixin", () -> LoadingModList.get().getModFileById("sodium") != null,
        "net.dries007.tfc.mixin.client.compat.jade.NextEntityDropProviderMixin", () -> LoadingModList.get().getModFileById("jade") != null
    );

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName)
    {
        return CONDITIONS.getOrDefault(mixinClassName, TRUE).get();
    }

    // No-ops
    @Override
    public void onLoad(String mixinPackage)
    {

    }

    @Override
    public String getRefMapperConfig()
    {
        return null;
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets)
    {

    }

    @Override
    public List<String> getMixins()
    {
        return null;
    }

    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo)
    {

    }

    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo)
    {

    }
}

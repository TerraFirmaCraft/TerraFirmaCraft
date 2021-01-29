/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.mixin.loot;

import java.util.Collections;
import java.util.Set;

import com.google.common.collect.Sets;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootParameter;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * This would be preferably resolved by https://github.com/MinecraftForge/MinecraftForge/pull/7515
 * For now it just allows us to use custom loot parameters
 */
@Mixin(LootContext.Builder.class)
public abstract class LootContextBuilderMixin
{
    private static final Sets.SetView<LootParameter<?>> EMPTY = Sets.union(Collections.emptySet(), Collections.emptySet());

    /**
     * This specifically ignores the first empty set check, by just not computing the extra parameters
     */
    @Redirect(method = "create", at = @At(value = "INVOKE", target = "Lcom/google/common/collect/Sets;difference(Ljava/util/Set;Ljava/util/Set;)Lcom/google/common/collect/Sets$SetView;", remap = false, ordinal = 0))
    private Sets.SetView<LootParameter<?>> redirect$create$difference(Set<?> set1, Set<?> set2)
    {
        return EMPTY;
    }
}

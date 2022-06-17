/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.commands;

import java.util.concurrent.CompletableFuture;

import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;

public class VeinFeatureArgument implements ArgumentType<ResourceLocation>
{
    @Override
    public ResourceLocation parse(StringReader reader) throws CommandSyntaxException
    {
        return ResourceLocation.read(reader);
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder)
    {
        final S source = context.getSource();
        if (source instanceof SharedSuggestionProvider provider)
        {
            return provider.suggestRegistryElements(Registry.CONFIGURED_FEATURE_REGISTRY, SharedSuggestionProvider.ElementSuggestionType.ALL, builder, context);
        }
        return builder.buildFuture();
    }
}

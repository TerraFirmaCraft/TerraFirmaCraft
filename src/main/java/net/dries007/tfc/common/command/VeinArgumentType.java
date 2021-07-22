/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.command;

import java.util.concurrent.CompletableFuture;

import net.minecraft.command.ISuggestionProvider;
import net.minecraft.util.ResourceLocation;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;

public class VeinArgumentType implements ArgumentType<ResourceLocation>
{
    @Override
    public ResourceLocation parse(StringReader reader) throws CommandSyntaxException
    {
        final ResourceLocation id = ResourceLocation.read(reader);
        if (!LocateVeinCommand.getVeins().containsKey(id))
        {
            throw LocateVeinCommand.ERROR_UNKNOWN_VEIN.createWithContext(reader, id);
        }
        return id;
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder)
    {
        ISuggestionProvider.suggest(LocateVeinCommand.getVeins().keySet().stream().map(ResourceLocation::toString), builder);
        return builder.buildFuture();
    }
}

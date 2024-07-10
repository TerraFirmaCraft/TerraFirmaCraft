/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.container.AnvilContainer;
import net.dries007.tfc.common.recipes.AnvilRecipe;
import net.dries007.tfc.common.recipes.TFCRecipeTypes;
import net.dries007.tfc.util.Helpers;

public record SelectAnvilPlanPacket(ResourceLocation recipeId) implements CustomPacketPayload
{
    public static final CustomPacketPayload.Type<SelectAnvilPlanPacket> TYPE = PacketHandler.type("select_anvil_plan");
    public static final StreamCodec<ByteBuf, SelectAnvilPlanPacket> STREAM = ResourceLocation.STREAM_CODEC.map(SelectAnvilPlanPacket::new, c -> c.recipeId);

    public SelectAnvilPlanPacket(AnvilRecipe recipe)
    {
        this(recipe.getId());
    }

    @Override
    public Type<? extends CustomPacketPayload> type()
    {
        return TYPE;
    }

    void handle(@Nullable ServerPlayer player)
    {
        if (player != null && player.containerMenu instanceof AnvilContainer anvilContainer)
        {
            final @Nullable AnvilRecipe recipe = Helpers.getRecipes(player.level(), TFCRecipeTypes.ANVIL).get(recipeId);
            anvilContainer.getBlockEntity().chooseRecipe(recipe);
        }
    }
}

/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.container;

import java.util.function.Consumer;

import net.dries007.tfc.util.Helpers;
import org.jetbrains.annotations.Nullable;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkHooks;

/**
 * Handling logic for containers that are opened via item stacks.
 * This represents a factory of {@link MenuProvider}s, which are created on demand for an individual {@link ItemStack}.
 * One piece of extra data is written, via the {@link Helpers#openScreen(ServerPlayer, MenuProvider, Consumer)} call, which contains the hand that this was opened from.
 */
public class ItemStackContainerProvider
{
    public static InteractionHand read(FriendlyByteBuf buffer)
    {
        return buffer.readBoolean() ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND;
    }

    public static Consumer<FriendlyByteBuf> write(InteractionHand hand)
    {
        return buffer -> buffer.writeBoolean(hand == InteractionHand.MAIN_HAND);
    }

    private final ItemStackContainer.Factory<? extends ItemStackContainer> factory;
    @Nullable private final Component name;

    public ItemStackContainerProvider(ItemStackContainer.Factory<? extends ItemStackContainer> factory)
    {
        this(factory, null);
    }

    public ItemStackContainerProvider(ItemStackContainer.Factory<? extends ItemStackContainer> factory, @Nullable Component name)
    {
        this.factory = factory;
        this.name = name;
    }

    public MenuProvider of(ItemStack stack, InteractionHand hand)
    {
        return new SimpleMenuProvider((windowId, playerInventory, player) -> factory.create(stack, hand, playerInventory, windowId), name == null ? stack.getHoverName() : name);
    }
}

/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.container;

import java.util.function.Consumer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

/**
 * Handling logic for containers that are opened via item stacks.
 * This represents a factory of {@link MenuProvider}s, which are created on demand for an individual {@link ItemStack}.
 * <p>
 * When opening a container from an item unsealedStack, we have to encode the slot which the container was originally opened with. {@code [0, 9]} indicate a hotbar slot index, and {@code -1} indicates it was opened with the offhand.
 */
public class ItemStackContainerProvider
{
    public static Consumer<FriendlyByteBuf> write(InteractionHand hand)
    {
        return buffer -> buffer.writeBoolean(hand == InteractionHand.MAIN_HAND);
    }

    public static Info read(FriendlyByteBuf buffer, Inventory playerInventory)
    {
        final byte slot = buffer.readByte();
        final InteractionHand hand = slot == -1 ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND;
        final ItemStack stack;
        if (slot == -1)
        {
            stack = playerInventory.player.getOffhandItem();
        }
        else
        {
            final int prevSelected = playerInventory.selected;
            playerInventory.selected = slot;
            stack = playerInventory.getSelected();
            playerInventory.selected = prevSelected;
        }
        return new Info(stack, hand, slot);
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

    public void openScreen(ServerPlayer player, InteractionHand hand)
    {
        openScreen(player, hand, buffer -> {});
    }

    public void openScreen(ServerPlayer player, InteractionHand hand, Consumer<FriendlyByteBuf> additionalData)
    {
        final ItemStack stack = player.getItemInHand(hand);
        final int encodedSlot = hand == InteractionHand.OFF_HAND ? -1 : player.getInventory().selected;
        final MenuProvider provider = new SimpleMenuProvider((windowId, playerInventory, playerIn) -> factory.create(stack, hand, encodedSlot, playerInventory, windowId), name == null ? stack.getHoverName() : name);

        player.openMenu(provider, buffer -> {
            additionalData.accept(buffer);
            buffer.writeByte(encodedSlot);
        });
    }

    public record Info(ItemStack stack, InteractionHand hand, int slot) {}
}

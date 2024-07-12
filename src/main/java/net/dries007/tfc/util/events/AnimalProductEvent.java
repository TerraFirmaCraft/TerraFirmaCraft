/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.util.events;

import com.mojang.datafixers.util.Either;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.ICancellableEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.entities.livestock.TFCAnimalProperties;
import net.dries007.tfc.util.Helpers;

/**
 * This event fires when a sheep is sheared or a cow is milked.
 * Cancelling this event prevents the default behavior, which is controlled by each entity's implementation.
 * <p>
 * While the 'tool' stack (bucket, shears) is provided, expect that entities can operate it outside this event. You should almost always copy it before modifying it.
 * This event does *not* control if an animal can give products, it is for the sole purpose of modifying / blocking what happens when products are made.
 * If you wish to control that, use {@link net.neoforged.neoforge.event.entity.player.PlayerInteractEvent.EntityInteract}
 * <p>
 * The 'uses' parameter indicates how much wear the animal will take from this happening.
 * <p>
 * This event has a {@code product}, which may either be an {@link ItemStack} or {@link FluidStack}, not both. Only the type of product which is non-empty will retain modifications - you cannot make an event fired due to milking a cow drop an item, for example. Doing so will void whatever product this event originally had.
 */
public final class AnimalProductEvent extends Event implements ICancellableEvent
{
    public static boolean produce(Level level, BlockPos pos, TFCAnimalProperties entity, ItemStack product, ItemStack tool, int uses)
    {
        return produce(level, pos, null, entity, product, tool, uses);
    }

    /**
     * @return {@code true} if the event was handled using the default behavior (add uses, spawn the product)
     */
    public static boolean produce(Level level, BlockPos pos, @Nullable Player player, TFCAnimalProperties entity, ItemStack product, ItemStack tool, int uses)
    {
        final AnimalProductEvent event = new AnimalProductEvent(level, pos, player, entity, product, tool, uses);
        if (!NeoForge.EVENT_BUS.post(event).isCanceled())
        {
            // spawning items is server only
            if (!level.isClientSide)
            {
                if (player != null)
                {
                    ItemHandlerHelper.giveItemToPlayer(player, event.getProduct());
                }
                else
                {
                    Helpers.spawnItem(level, pos, event.getProduct());
                }
            }
            entity.addUses(event.getUses());
            return true;
        }
        return false;
    }

    private final Level level;
    private final BlockPos pos;
    private final TFCAnimalProperties animalProperties;
    @Nullable
    private final Player player;
    private final ItemStack tool;
    private Either<ItemStack, FluidStack> product;
    private int uses;

    public AnimalProductEvent(Level level, BlockPos pos, @Nullable Player player, TFCAnimalProperties entity, ItemStack product, ItemStack tool, int uses)
    {
        this(level, pos, player, entity, Either.left(product), tool, uses);
    }

    public AnimalProductEvent(Level level, BlockPos pos, @Nullable Player player, TFCAnimalProperties entity, FluidStack fluidProduct, ItemStack tool, int uses)
    {
        this(level, pos, player, entity, Either.right(fluidProduct), tool, uses);
    }

    private AnimalProductEvent(Level level, BlockPos pos, @Nullable Player player, TFCAnimalProperties entity, Either<ItemStack, FluidStack> product, ItemStack tool, int uses)
    {
        this.level = level;
        this.pos = pos;
        this.animalProperties = entity;
        this.player = player;
        this.tool = tool;
        this.product = product;
        this.uses = uses;
    }

    public Level getLevel()
    {
        return level;
    }

    public BlockPos getPos()
    {
        return pos;
    }

    public TFCAnimalProperties getAnimalProperties()
    {
        return animalProperties;
    }

    public Entity getEntity()
    {
        return getAnimalProperties().getEntity();
    }

    @Nullable
    public Player getPlayer()
    {
        return player;
    }

    public ItemStack getTool()
    {
        return tool;
    }

    public ItemStack getProduct()
    {
        return product.left().orElse(ItemStack.EMPTY);
    }

    public FluidStack getFluidProduct()
    {
        return product.right().orElse(FluidStack.EMPTY);
    }

    public void setProduct(ItemStack product)
    {
        this.product = Either.left(product);
    }

    public void setProduct(FluidStack product)
    {
        this.product = Either.right(product);
    }

    public int getUses()
    {
        return uses;
    }

    public void setUses(int uses)
    {
        this.uses = uses;
    }
}

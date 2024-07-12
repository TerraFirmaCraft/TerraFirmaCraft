/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.items;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.items.ItemHandlerHelper;

import net.dries007.tfc.common.blockentities.JarsBlockEntity;
import net.dries007.tfc.common.blocks.JarShelfBlock;
import net.dries007.tfc.common.blocks.JarsBlock;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.blocks.devices.BottomSupportedDeviceBlock;
import net.dries007.tfc.util.Helpers;

public class JarItem extends Item
{
    private final ResourceLocation model;
    private final boolean hasContainerItem;

    public JarItem(Properties properties, String fruit, boolean hasContainerItem)
    {
        this(properties, Helpers.identifier("block/jar/" + fruit), hasContainerItem);
    }

    public JarItem(Properties properties, ResourceLocation model, boolean hasContainerItem)
    {
        super(properties);
        this.model = model;
        this.hasContainerItem = hasContainerItem;
    }

    @Override
    public InteractionResult useOn(UseOnContext context)
    {
        final Level level = context.getLevel();
        final ItemStack held = context.getItemInHand();
        final BlockPos pos = context.getClickedPos();
        final BlockPos above = pos.above();
        final Direction dir = context.getClickedFace();
        final BlockState stateUp = level.getBlockState(above);
        final Player player = context.getPlayer();
        if (dir == Direction.UP && BottomSupportedDeviceBlock.canSurvive(level, above) && stateUp.isAir())
        {
            level.setBlockAndUpdate(above, TFCBlocks.JARS.get().defaultBlockState());
            if (level.getBlockEntity(above) instanceof JarsBlockEntity jars)
            {
                final var inv = Helpers.getCapability(jars, Capabilities.ITEM);
                if (inv != null)
                {
                    final int slot = getSlot(context);
                    final ItemStack leftover = inv.insertItem(slot, held.split(1), false);
                    if (player != null)
                    {
                        ItemHandlerHelper.giveItemToPlayer(player, leftover);
                        level.setBlockAndUpdate(above, JarsBlock.updateStateValues(level, above, level.getBlockState(above)));
                    }
                }
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }
        if (level.getBlockEntity(above) instanceof JarsBlockEntity jars)
        {
            final int slot = getSlot(context);
            final var inv = Helpers.getCapability(jars, Capabilities.ITEM);
            if (inv != null)
            {
                final ItemStack leftover = inv.insertItem(slot, held.split(1), false);
                if (player != null)
                    ItemHandlerHelper.giveItemToPlayer(player, leftover);
                level.setBlockAndUpdate(above, JarsBlock.updateStateValues(level, above, level.getBlockState(above)));
                return InteractionResult.sidedSuccess(level.isClientSide);
            }
        }
        if (dir == Direction.UP && stateUp.getBlock() instanceof JarShelfBlock block && player != null)
        {
            return block.use(stateUp, level, above, player, context.getHand(), new BlockHitResult(context.getClickLocation(), Direction.UP, above, true));
        }
        return InteractionResult.PASS;
    }

    private static int getSlot(UseOnContext context)
    {
        final Vec3 location = context.getClickLocation();
        final boolean x = Math.round(location.x) < location.x;
        final boolean z = Math.round(location.z) < location.z;
        final int slot = (x ? 1 : 0) + (z ? 2 : 0);
        return slot;
    }

    @Override
    public boolean hasCraftingRemainingItem(ItemStack stack)
    {
        return hasContainerItem;
    }

    @Override
    public ItemStack getCraftingRemainingItem(ItemStack itemStack)
    {
        return new ItemStack(TFCItems.EMPTY_JAR.get());
    }

    public ResourceLocation getModel()
    {
        return model;
    }
}

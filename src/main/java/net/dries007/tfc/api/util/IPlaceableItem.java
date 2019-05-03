/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.api.util;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;
import javax.annotation.Nullable;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import net.dries007.tfc.client.TFCGuiHandler;
import net.dries007.tfc.objects.blocks.BlocksTFC;
import net.dries007.tfc.objects.te.TELogPile;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.OreDictionaryHelper;

import static net.dries007.tfc.objects.blocks.BlockCharcoalPile.LAYERS;

@FunctionalInterface
public interface IPlaceableItem
{
    /**
     * When an item that implements IPlaceableItem is right clicked, this will be called to invoke special placement logic
     * Implement this if you want a custom item block that might need to subclass another item class
     *
     * @param world  The world
     * @param pos    The position that was clicked
     * @param stack  The player's held item stack
     * @param player The current player
     * @param facing The face of the block that was clicked
     * @param hitVec The hit vector
     * @return if the block was placed (will consume one item from the player's item)
     */
    boolean placeItemInWorld(World world, BlockPos pos, ItemStack stack, EntityPlayer player, @Nullable EnumFacing facing, @Nullable Vec3d hitVec);

    /**
     * This will be called after a sucessful placement. If this is nonzero, the player will consume that amount from their held item.
     *
     * @return the amount to consume
     */
    default int consumeAmount()
    {
        return 1;
    }


    class Impl
    {
        // Add to this map for things that fire when you right click a block
        private static final Map<Predicate<ItemStack>, IPlaceableItem> placeableInstances = new HashMap<>();
        // Add to this map for things that fire when you right click in the air OR when you right click a block
        private static final Map<Predicate<ItemStack>, IPlaceableItem> usableInstances = new HashMap<>();

        static
        {
            // Charcoal -> charcoal piles
            // This is also where charcoal piles grow
            placeableInstances.put(stack -> stack.getItem() == Items.COAL && stack.getMetadata() == 1, (world, pos, stack, player, facing, hitVec) ->
            {
                if (facing == null) return false;

                IBlockState state = world.getBlockState(pos);

                if (facing == EnumFacing.UP && state.getBlock() == BlocksTFC.CHARCOAL_PILE)
                {
                    if (state.getValue(LAYERS) < 8)
                    {
                        world.setBlockState(pos, state.withProperty(LAYERS, state.getValue(LAYERS) + 1));
                        world.playSound(null, pos, SoundEvents.BLOCK_GRAVEL_PLACE, SoundCategory.BLOCKS, 1.0f, 0.5f);

                        return true;
                    }
                }

                if (world.getBlockState(pos.down().offset(facing)).isNormalCube() && world.getBlockState(pos.offset(facing)).getBlock().isReplaceable(world, pos.offset(facing)))
                {
                    // Create a new charcoal pile
                    if (!world.isRemote)
                    {
                        world.setBlockState(pos.offset(facing), BlocksTFC.CHARCOAL_PILE.getDefaultState());

                        world.playSound(null, pos.offset(facing), SoundEvents.BLOCK_GRAVEL_PLACE, SoundCategory.BLOCKS, 1.0f, 0.5f);

                        return true;
                    }
                }

                return false;
            });

            // Logs -> Log Piles (placement + insertion)
            placeableInstances.put(stack -> OreDictionaryHelper.doesStackMatchOre(stack, "logWood"), (world, pos, stack, player, facing, hitVec) -> {
                if (player.isSneaking())
                {
                    if (facing != null)
                    {
                        if (world.getBlockState(pos).getBlock() == BlocksTFC.LOG_PILE)
                        {
                            if (!world.isRemote)
                            {
                                TELogPile te = Helpers.getTE(world, pos, TELogPile.class);
                                if (te != null)
                                {
                                    if (te.insertLog(stack.copy()))
                                    {
                                        world.playSound(null, pos.offset(facing), SoundEvents.BLOCK_WOOD_PLACE, SoundCategory.BLOCKS, 1.0F, 1.0F);
                                        return true;
                                    }
                                    else
                                    {
                                        // Insert log didn't work, see if trying to place another log pile
                                        if (facing == EnumFacing.UP && te.countLogs() == 16 || (facing != EnumFacing.UP && world.getBlockState(pos.down().offset(facing)).isNormalCube()
                                            && world.getBlockState(pos.offset(facing)).getBlock().isReplaceable(world, pos.offset(facing))))
                                        {
                                            world.setBlockState(pos.offset(facing), BlocksTFC.LOG_PILE.getStateForPlacement(world, pos, facing, 0, 0, 0, 0, player));

                                            TELogPile te2 = Helpers.getTE(world, pos.offset(facing), TELogPile.class);
                                            if (te2 != null)
                                            {
                                                te2.insertLog(stack.copy());
                                            }

                                            world.playSound(null, pos.offset(facing), SoundEvents.BLOCK_WOOD_PLACE, SoundCategory.BLOCKS, 1.0F, 1.0F);
                                            return true;
                                        }
                                    }
                                }
                            }
                        }
                        else
                        {
                            if (world.getBlockState(pos.down().offset(facing)).isNormalCube()
                                && world.getBlockState(pos.offset(facing)).getBlock().isReplaceable(world, pos.offset(facing)) &&
                                player.isSneaking())
                            {
                                // Place log pile
                                if (!world.isRemote)
                                {
                                    world.setBlockState(pos.offset(facing), BlocksTFC.LOG_PILE.getStateForPlacement(world, pos, facing, 0, 0, 0, 0, player));

                                    TELogPile te = Helpers.getTE(world, pos.offset(facing), TELogPile.class);
                                    if (te != null)
                                    {
                                        te.insertLog(stack.copy());
                                    }

                                    world.playSound(null, pos.offset(facing), SoundEvents.BLOCK_WOOD_PLACE, SoundCategory.BLOCKS, 1.0F, 1.0F);
                                    return true;
                                }
                            }
                        }
                    }
                    return true;
                }
                return false;
            });

            // Clay -> Knapping
            putBoth(stack -> stack.getItem() == Items.CLAY_BALL && stack.getCount() >= 5, (world, pos, stack, player, facing, hitVec) -> {
                TFCGuiHandler.openGui(world, pos, player, TFCGuiHandler.Type.KNAPPING_CLAY);
                return false;
            });
        }

        public static boolean isPlaceable(ItemStack stack)
        {
            if (stack.getItem() instanceof IPlaceableItem)
            {
                return true;
            }
            return placeableInstances.keySet().stream().anyMatch(x -> x.test(stack));
        }

        public static IPlaceableItem getPlaceable(ItemStack stack)
        {
            if (stack.getItem() instanceof IPlaceableItem)
            {
                return (IPlaceableItem) stack.getItem();
            }
            return placeableInstances.entrySet().stream().filter(x -> x.getKey().test(stack)).map(Map.Entry::getValue).findFirst().orElse(null);
        }

        public static boolean isUsable(ItemStack stack)
        {
            return usableInstances.keySet().stream().anyMatch(x -> x.test(stack));
        }

        public static IPlaceableItem getUsable(ItemStack stack)
        {
            return usableInstances.entrySet().stream().filter(x -> x.getKey().test(stack)).map(Map.Entry::getValue).findFirst().orElse(null);
        }

        private static void putBoth(Predicate<ItemStack> predicate, IPlaceableItem placeable)
        {
            placeableInstances.put(predicate, placeable);
            usableInstances.put(predicate, placeable);
        }

        private Impl() {}
    }
}

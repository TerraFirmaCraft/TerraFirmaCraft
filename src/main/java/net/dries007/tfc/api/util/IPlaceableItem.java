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
import net.dries007.tfc.client.TFCSounds;
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
     * This will be called after a successful placement. If this is nonzero, the player will consume that amount from their held item.
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
                if (facing != null)
                {
                    IBlockState state = world.getBlockState(pos);
                    if (state.getBlock() == BlocksTFC.CHARCOAL_PILE && state.getValue(LAYERS) < 8)
                    {
                        // Check the player isn't standing inside the placement area for the next layer
                        IBlockState stateToPlace = state.withProperty(LAYERS, state.getValue(LAYERS) + 1);
                        if (world.checkNoEntityCollision(stateToPlace.getBoundingBox(world, pos).offset(pos)))
                        {
                            if (!world.isRemote)
                            {
                                world.setBlockState(pos, state.withProperty(LAYERS, state.getValue(LAYERS) + 1));
                                world.playSound(null, pos, TFCSounds.CHARCOAL_PILE.getPlaceSound(), SoundCategory.BLOCKS, 1.0f, 1.0f);
                            }
                            return true;
                        }
                    }
                    BlockPos posAt = pos;
                    if (!state.getBlock().isReplaceable(world, pos))
                    {
                        posAt = posAt.offset(facing);
                    }
                    if (world.getBlockState(posAt.down()).isSideSolid(world, posAt.down(), EnumFacing.UP) && world.getBlockState(posAt).getBlock().isReplaceable(world, pos))
                    {
                        IBlockState stateToPlace = BlocksTFC.CHARCOAL_PILE.getDefaultState().withProperty(LAYERS, 1);
                        if (world.checkNoEntityCollision(stateToPlace.getBoundingBox(world, posAt).offset(posAt)))
                        {
                            // Create a new charcoal pile
                            if (!world.isRemote)
                            {
                                world.setBlockState(posAt, stateToPlace);
                                world.playSound(null, posAt, TFCSounds.CHARCOAL_PILE.getPlaceSound(), SoundCategory.BLOCKS, 1.0f, 1.0f);
                            }
                            return true;
                        }
                    }
                }
                return false;
            });

            // Logs -> Log Piles (placement + insertion)
            placeableInstances.put(stack -> OreDictionaryHelper.doesStackMatchOre(stack, "logWood"), (world, pos, stack, player, facing, hitVec) -> {
                if (facing != null)
                {
                    IBlockState stateAt = world.getBlockState(pos);
                    if (stateAt.getBlock() == BlocksTFC.LOG_PILE)
                    {
                        // Clicked on a log pile, so try to insert into the original
                        TELogPile te = Helpers.getTE(world, pos, TELogPile.class);
                        if (te != null)
                        {
                            if (te.insertLog(stack.copy()))
                            {
                                if (!world.isRemote)
                                {
                                    world.playSound(null, pos.offset(facing), SoundEvents.BLOCK_WOOD_PLACE, SoundCategory.BLOCKS, 1.0F, 1.0F);
                                }
                                return true;
                            }
                        }
                    }

                    // Try and place a log pile - if you were sneaking or you clicked on a log pile
                    if (stateAt.getBlock() == BlocksTFC.LOG_PILE || player.isSneaking())
                    {
                        BlockPos posAt = pos;
                        if (!stateAt.getBlock().isReplaceable(world, pos))
                        {
                            posAt = posAt.offset(facing);
                        }
                        if (world.getBlockState(posAt.down()).isNormalCube() && world.mayPlace(BlocksTFC.LOG_PILE, posAt, false, facing, null))
                        {
                            // Place log pile
                            if (!world.isRemote)
                            {
                                world.setBlockState(posAt, BlocksTFC.LOG_PILE.getStateForPlacement(world, posAt, facing, 0, 0, 0, 0, player));

                                TELogPile te = Helpers.getTE(world, posAt, TELogPile.class);
                                if (te != null)
                                {
                                    te.insertLog(stack.copy());
                                }

                                world.playSound(null, posAt, SoundEvents.BLOCK_WOOD_PLACE, SoundCategory.BLOCKS, 1.0F, 1.0F);
                            }
                            return true;
                        }
                    }
                }
                return false;
            });

            // Clay -> Knapping
            putBoth(stack -> stack.getItem() == Items.CLAY_BALL && stack.getCount() >= 5, new IPlaceableItem()
            {
                @Override
                public boolean placeItemInWorld(World world, BlockPos pos, ItemStack stack, EntityPlayer player, @Nullable EnumFacing facing, @Nullable Vec3d hitVec)
                {
                    if (!world.isRemote)
                    {
                        TFCGuiHandler.openGui(world, pos, player, TFCGuiHandler.Type.KNAPPING_CLAY);
                    }
                    return true;
                }

                @Override
                public int consumeAmount()
                {
                    return 0;
                }
            });

            // Leather -> Knapping
            putBoth(stack -> OreDictionaryHelper.doesStackMatchOre(stack, "leather"), new IPlaceableItem()
            {
                @Override
                public boolean placeItemInWorld(World world, BlockPos pos, ItemStack stack, EntityPlayer player, @Nullable EnumFacing facing, @Nullable Vec3d hitVec)
                {
                    if (Helpers.playerHasItemMatchingOre(player.inventory, "knife"))
                    {
                        if (!world.isRemote)
                        {
                            TFCGuiHandler.openGui(world, pos, player, TFCGuiHandler.Type.KNAPPING_LEATHER);
                        }
                        return true;
                    }
                    return false;
                }

                @Override
                public int consumeAmount()
                {
                    return 0;
                }
            });
        }

        @Nullable
        public static IPlaceableItem getPlaceable(ItemStack stack)
        {
            if (stack.getItem() instanceof IPlaceableItem)
            {
                return (IPlaceableItem) stack.getItem();
            }
            return placeableInstances.entrySet().stream().filter(x -> x.getKey().test(stack)).map(Map.Entry::getValue).findFirst().orElse(null);
        }

        @Nullable
        public static IPlaceableItem getUsable(ItemStack stack)
        {
            return usableInstances.entrySet().stream().filter(x -> x.getKey().test(stack)).findFirst().map(Map.Entry::getValue).orElse(null);
        }

        private static void putBoth(Predicate<ItemStack> predicate, IPlaceableItem placeable)
        {
            placeableInstances.put(predicate, placeable);
            usableInstances.put(predicate, placeable);
        }

        private Impl() {}
    }
}
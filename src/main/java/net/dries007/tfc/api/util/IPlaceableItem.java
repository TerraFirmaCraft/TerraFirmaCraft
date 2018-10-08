/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.api.util;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;
import javax.annotation.Nullable;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.client.TFCGuiHandler;
import net.dries007.tfc.objects.blocks.BlockCharcoalPile;
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
    boolean placeItemInWorld(World world, BlockPos pos, ItemStack stack, EntityPlayer player, @Nullable EnumFacing facing, Vec3d hitVec);


    class Impl
    {
        private static final Map<Predicate<ItemStack>, IPlaceableItem> placeables = new HashMap<>();

        static
        {
            // Charcoal -> charcoal piles
            placeables.put(stack -> stack.getItem() == Items.COAL && stack.getMetadata() == 1, (world, pos, stack, player, facing, hitVec) ->
            {
                if (facing != null)
                {
                    if (world.getBlockState(pos.down().offset(facing)).isNormalCube()
                        && world.getBlockState(pos.offset(facing)).getBlock().isReplaceable(world, pos.offset(facing)))
                    {

                        if (world.getBlockState(pos).getBlock() instanceof BlockCharcoalPile)
                        {
                            if (world.getBlockState(pos).getValue(LAYERS) != 8)
                            {
                                // Adding layers is handled in BlockCharcoalPile
                                return false;
                            }
                        }
                        if (!world.isRemote)
                        {
                            // noinspection ConstantConditions
                            world.setBlockState(pos.offset(facing), BlocksTFC.CHARCOAL_PILE.getDefaultState());
                            world.playSound(null, pos.offset(facing), SoundEvents.BLOCK_GRAVEL_PLACE, SoundCategory.BLOCKS, 1.0F, 0.5F);
                            return true;
                        }

                    }
                }
                return false;
            });

            // Logs -> Log Piles (placement + insertion)
            placeables.put(stack -> OreDictionaryHelper.doesStackMatchOre(stack, "logWood"), (world, pos, stack, player, facing, hitVec) -> {
                if (player.isSneaking())
                {
                    if (facing != null)
                    {
                        //noinspection ConstantConditions
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
                                            // noinspection ConstantConditions
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
                                    // noinspection ConstantConditions
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
            placeables.put(stack -> stack.getItem() == Items.CLAY_BALL, (world, pos, stack, player, facing, hitVec) -> {
                TFCGuiHandler.openGui(world, pos, player, TFCGuiHandler.Type.KNAPPING_CLAY);
                TerraFirmaCraft.getLog().debug("Opening Knapping Clay Gui");
                return false;
            });

            // todo: TFC leather knapping
            // todo: fire clay knapping
        }

        public static boolean isPlaceable(ItemStack stack)
        {
            if (stack.getItem() instanceof IPlaceableItem)
            {
                return true;
            }
            return placeables.keySet().stream().anyMatch(x -> x.test(stack));
        }

        public static IPlaceableItem getPlaceable(ItemStack stack)
        {
            if (stack.getItem() instanceof IPlaceableItem)
            {
                return (IPlaceableItem) stack.getItem();
            }
            return placeables.entrySet().stream().filter(x -> x.getKey().test(stack)).map(Map.Entry::getValue).findFirst().orElse(null);
        }

        private Impl() {}
    }
}

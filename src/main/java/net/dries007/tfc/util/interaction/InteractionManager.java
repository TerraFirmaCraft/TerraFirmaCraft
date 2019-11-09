/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.util.interaction;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;
import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

import net.dries007.tfc.client.TFCGuiHandler;
import net.dries007.tfc.client.TFCSounds;
import net.dries007.tfc.objects.blocks.BlocksTFC;
import net.dries007.tfc.objects.fluids.FluidsTFC;
import net.dries007.tfc.objects.te.TELogPile;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.OreDictionaryHelper;

import static net.dries007.tfc.api.util.TFCConstants.MOD_ID;
import static net.dries007.tfc.objects.blocks.BlockCharcoalPile.LAYERS;

@Mod.EventBusSubscriber(modid = MOD_ID)
public final class InteractionManager
{
    private static final Map<Predicate<ItemStack>, IRightClickBlockAction> USE_ACTIONS = new HashMap<>();
    private static final Map<Predicate<ItemStack>, IRightClickItemAction> RIGHT_CLICK_ACTIONS = new HashMap<>();

    static
    {
        // Clay knapping
        putBoth(stack -> OreDictionaryHelper.doesStackMatchOre(stack, "clay") && stack.getCount() >= 5, (worldIn, playerIn, handIn) -> {
            if (!worldIn.isRemote)
            {
                TFCGuiHandler.openGui(worldIn, playerIn, TFCGuiHandler.Type.KNAPPING_CLAY);
            }
            return EnumActionResult.SUCCESS;
        });

        // Fire clay knapping
        putBoth(stack -> OreDictionaryHelper.doesStackMatchOre(stack, "fireClay") && stack.getCount() >= 5, ((worldIn, playerIn, handIn) -> {
            if (!worldIn.isRemote)
            {
                TFCGuiHandler.openGui(worldIn, playerIn, TFCGuiHandler.Type.KNAPPING_FIRE_CLAY);
            }
            return EnumActionResult.SUCCESS;
        }));

        // Leather knapping
        putBoth(stack -> OreDictionaryHelper.doesStackMatchOre(stack, "leather"), ((worldIn, playerIn, handIn) -> {
            if (Helpers.playerHasItemMatchingOre(playerIn.inventory, "knife"))
            {
                if (!worldIn.isRemote)
                {
                    TFCGuiHandler.openGui(worldIn, playerIn, TFCGuiHandler.Type.KNAPPING_LEATHER);
                }
                return EnumActionResult.SUCCESS;
            }
            return EnumActionResult.FAIL;
        }));

        // Logs -> Log Piles (placement + insertion)
        USE_ACTIONS.put(stack -> OreDictionaryHelper.doesStackMatchOre(stack, "logWood"), (stack, player, worldIn, pos, hand, direction, hitX, hitY, hitZ) -> {
            if (direction != null)
            {
                IBlockState stateAt = worldIn.getBlockState(pos);
                if (stateAt.getBlock() == BlocksTFC.LOG_PILE)
                {
                    // Clicked on a log pile, so try to insert into the original
                    TELogPile te = Helpers.getTE(worldIn, pos, TELogPile.class);
                    if (te != null)
                    {
                        if (te.insertLog(stack.copy()))
                        {
                            if (!worldIn.isRemote)
                            {
                                worldIn.playSound(null, pos.offset(direction), SoundEvents.BLOCK_WOOD_PLACE, SoundCategory.BLOCKS, 1.0F, 1.0F);
                                stack.shrink(1);
                                player.setHeldItem(hand, stack);
                            }
                            return EnumActionResult.SUCCESS;
                        }
                    }
                }

                // Try and place a log pile - if you were sneaking or you clicked on a log pile
                if (stateAt.getBlock() == BlocksTFC.LOG_PILE || player.isSneaking())
                {
                    BlockPos posAt = pos;
                    if (!stateAt.getBlock().isReplaceable(worldIn, pos))
                    {
                        posAt = posAt.offset(direction);
                    }
                    if (worldIn.getBlockState(posAt.down()).isNormalCube() && worldIn.mayPlace(BlocksTFC.LOG_PILE, posAt, false, direction, null))
                    {
                        // Place log pile
                        if (!worldIn.isRemote)
                        {
                            worldIn.setBlockState(posAt, BlocksTFC.LOG_PILE.getStateForPlacement(worldIn, posAt, direction, 0, 0, 0, 0, player));

                            TELogPile te = Helpers.getTE(worldIn, posAt, TELogPile.class);
                            if (te != null)
                            {
                                te.insertLog(stack.copy());
                            }

                            worldIn.playSound(null, posAt, SoundEvents.BLOCK_WOOD_PLACE, SoundCategory.BLOCKS, 1.0F, 1.0F);

                            stack.shrink(1);
                            player.setHeldItem(hand, stack);
                        }
                        return EnumActionResult.SUCCESS;
                    }
                }
            }
            // Pass to allow the normal `onItemUse` to get called, which handles item block placement
            return EnumActionResult.PASS;
        });

        // Charcoal -> charcoal piles
        // This is also where charcoal piles grow
        USE_ACTIONS.put(stack -> OreDictionaryHelper.doesStackMatchOre(stack, "charcoal"), (stack, player, worldIn, pos, hand, direction, hitX, hitY, hitZ) -> {
            if (direction != null)
            {
                IBlockState state = worldIn.getBlockState(pos);
                if (state.getBlock() == BlocksTFC.CHARCOAL_PILE && state.getValue(LAYERS) < 8)
                {
                    // Check the player isn't standing inside the placement area for the next layer
                    IBlockState stateToPlace = state.withProperty(LAYERS, state.getValue(LAYERS) + 1);
                    if (worldIn.checkNoEntityCollision(stateToPlace.getBoundingBox(worldIn, pos).offset(pos)))
                    {
                        if (!worldIn.isRemote)
                        {
                            worldIn.setBlockState(pos, state.withProperty(LAYERS, state.getValue(LAYERS) + 1));
                            worldIn.playSound(null, pos, TFCSounds.CHARCOAL_PILE.getPlaceSound(), SoundCategory.BLOCKS, 1.0f, 1.0f);
                            stack.shrink(1);
                            player.setHeldItem(hand, stack);
                        }
                        return EnumActionResult.SUCCESS;
                    }
                }
                BlockPos posAt = pos;
                if (!state.getBlock().isReplaceable(worldIn, pos))
                {
                    posAt = posAt.offset(direction);
                }
                if (worldIn.getBlockState(posAt.down()).isSideSolid(worldIn, posAt.down(), EnumFacing.UP) && worldIn.getBlockState(posAt).getBlock().isReplaceable(worldIn, pos))
                {
                    IBlockState stateToPlace = BlocksTFC.CHARCOAL_PILE.getDefaultState().withProperty(LAYERS, 1);
                    if (worldIn.checkNoEntityCollision(stateToPlace.getBoundingBox(worldIn, posAt).offset(posAt)))
                    {
                        // Create a new charcoal pile
                        if (!worldIn.isRemote)
                        {
                            worldIn.setBlockState(posAt, stateToPlace);
                            worldIn.playSound(null, posAt, TFCSounds.CHARCOAL_PILE.getPlaceSound(), SoundCategory.BLOCKS, 1.0f, 1.0f);
                            stack.shrink(1);
                            player.setHeldItem(hand, stack);
                        }
                        return EnumActionResult.SUCCESS;
                    }
                }
            }
            return EnumActionResult.FAIL;
        });

        RIGHT_CLICK_ACTIONS.put(stack -> Items.GLASS_BOTTLE.equals(stack.getItem()), (worldIn, playerIn, handIn) -> {
            RayTraceResult traceResult = Helpers.rayTrace(worldIn, playerIn, true);
            if (traceResult == null)
            {
                return EnumActionResult.PASS;
            }

            Block rayTracedBlock = worldIn.getBlockState(traceResult.getBlockPos()).getBlock();
            boolean isTfcFluid = FluidsTFC.getAllWrappers().stream()
                .anyMatch(fluidWrapper -> fluidWrapper.get().getBlock().equals(rayTracedBlock));

            return isTfcFluid ? EnumActionResult.FAIL : EnumActionResult.PASS;
        });
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event)
    {
        IRightClickBlockAction action = InteractionManager.findItemUseAction(event.getItemStack());
        if (action != null)
        {
            // Use alternative handling
            EnumActionResult result;
            if (event.getSide() == Side.CLIENT)
            {
                result = ClientInteractionManager.processRightClickBlock(event, action);
            }
            else
            {
                result = ServerInteractionManager.processRightClickBlock(event, action);
            }
            event.setCancellationResult(result);
            event.setCanceled(true);
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onRightClickItem(PlayerInteractEvent.RightClickItem event)
    {
        IRightClickItemAction action = InteractionManager.findItemRightClickAction(event.getItemStack());
        if (action != null)
        {
            // Use alternative handling
            EnumActionResult result;
            if (event.getSide() == Side.CLIENT)
            {
                result = ClientInteractionManager.processRightClickItem(event, action);
            }
            else
            {
                result = ServerInteractionManager.processRightClickItem(event, action);
            }
            if (result != EnumActionResult.PASS)
            {
                event.setCancellationResult(result);
                event.setCanceled(true);
            }
        }
    }

    @Nullable
    private static IRightClickBlockAction findItemUseAction(ItemStack stack)
    {
        return USE_ACTIONS.entrySet().stream().filter(e -> e.getKey().test(stack)).findFirst().map(Map.Entry::getValue).orElse(null);
    }

    @Nullable
    private static IRightClickItemAction findItemRightClickAction(ItemStack stack)
    {
        return RIGHT_CLICK_ACTIONS.entrySet().stream().filter(e -> e.getKey().test(stack)).findFirst().map(Map.Entry::getValue).orElse(null);
    }

    private static void putBoth(Predicate<ItemStack> predicate, IRightClickItemAction minorAction)
    {
        USE_ACTIONS.put(predicate, minorAction);
        RIGHT_CLICK_ACTIONS.put(predicate, minorAction);
    }
}

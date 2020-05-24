/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.util.interaction;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;
import javax.annotation.Nullable;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

import net.dries007.tfc.ConfigTFC;
import net.dries007.tfc.api.recipes.knapping.KnappingType;
import net.dries007.tfc.client.TFCGuiHandler;
import net.dries007.tfc.client.TFCSounds;
import net.dries007.tfc.objects.blocks.BlocksTFC;
import net.dries007.tfc.objects.te.TELogPile;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.OreDictionaryHelper;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;
import static net.dries007.tfc.objects.blocks.BlockCharcoalPile.LAYERS;

@Mod.EventBusSubscriber(modid = MOD_ID)
public final class InteractionManager
{
    private static final Map<Predicate<ItemStack>, IRightClickBlockAction> USE_ACTIONS = new HashMap<>();
    private static final Map<Predicate<ItemStack>, IRightClickItemAction> RIGHT_CLICK_ACTIONS = new HashMap<>();
    private static final ThreadLocal<Boolean> PROCESSING_INTERACTION = ThreadLocal.withInitial(() -> false); // avoids stack overflows where some mods post interaction events during onBlockActivated (see #1321)

    static
    {
        // Clay knapping
        putBoth(stack -> OreDictionaryHelper.doesStackMatchOre(stack, "clay") && stack.getCount() >= KnappingType.CLAY.getAmountToConsume(), (worldIn, playerIn, handIn) -> {
            if (!worldIn.isRemote)
            {
                TFCGuiHandler.openGui(worldIn, playerIn, TFCGuiHandler.Type.KNAPPING_CLAY);
            }
            return EnumActionResult.SUCCESS;
        });

        // Fire clay knapping
        putBoth(stack -> OreDictionaryHelper.doesStackMatchOre(stack, "fireClay") && stack.getCount() >= KnappingType.FIRE_CLAY.getAmountToConsume(), ((worldIn, playerIn, handIn) -> {
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

        putBoth(stack -> OreDictionaryHelper.doesStackMatchOre(stack, "bowl"), ((worldIn, playerIn, handIn) -> {
            // Offhand must contain a knife - avoids opening the salad gui whenever you empty a bowl form eating
            if (OreDictionaryHelper.doesStackMatchOre(playerIn.getHeldItem(handIn == EnumHand.MAIN_HAND ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND), "knife"))
            {
                if (!worldIn.isRemote)
                {
                    TFCGuiHandler.openGui(worldIn, playerIn, TFCGuiHandler.Type.SALAD);
                }
                return EnumActionResult.SUCCESS;
            }
            return EnumActionResult.PASS;
        }));

        // Logs -> Log Piles (placement + insertion)
        USE_ACTIONS.put(stack -> OreDictionaryHelper.doesStackMatchOre(stack, "logWood"), (stack, player, worldIn, pos, hand, direction, hitX, hitY, hitZ) -> {
            if (direction != null)
            {
                IBlockState stateAt = worldIn.getBlockState(pos);
                if (stateAt.getBlock() == BlocksTFC.LOG_PILE)
                {
                    // Clicked on a log pile, so try to insert into the original
                    // This is called first when player is sneaking, otherwise the call chain is passed to the BlockLogPile#onBlockActivated
                    TELogPile te = Helpers.getTE(worldIn, pos, TELogPile.class);
                    if (te != null)
                    {
                        if (!player.isSneaking())
                        {
                            if (te.insertLog(stack))
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
                        else
                        {
                            int inserted = te.insertLogs(stack.copy());
                            if (inserted > 0)
                            {
                                if (!worldIn.isRemote)
                                {
                                    worldIn.playSound(null, pos.offset(direction), SoundEvents.BLOCK_WOOD_PLACE, SoundCategory.BLOCKS, 1.0F, 1.0F);
                                    stack.shrink(inserted);
                                    player.setHeldItem(hand, stack);
                                }
                                return EnumActionResult.SUCCESS;
                            }
                        }
                    }
                }
                // Try and place a log pile - if you were sneaking
                if (ConfigTFC.General.OVERRIDES.enableLogPiles && player.isSneaking())
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
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event)
    {
        if (!PROCESSING_INTERACTION.get())
        {
            PROCESSING_INTERACTION.set(true);
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
            PROCESSING_INTERACTION.set(false);
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
            event.setCancellationResult(result);
            event.setCanceled(true);
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

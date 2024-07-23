/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.items;

import java.util.function.IntSupplier;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.common.ModConfigSpec.IntValue;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.items.ItemHandlerHelper;

import net.dries007.tfc.client.TFCSounds;
import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.component.TFCComponents;
import net.dries007.tfc.common.component.fluid.FluidComponent;
import net.dries007.tfc.common.component.fluid.FluidContainerInfo;
import net.dries007.tfc.common.component.heat.HeatCapability;
import net.dries007.tfc.common.component.heat.HeatComponent;
import net.dries007.tfc.common.component.mold.IMold;
import net.dries007.tfc.common.container.TFCContainerProviders;
import net.dries007.tfc.common.fluids.FluidHelpers;
import net.dries007.tfc.common.recipes.CastingRecipe;
import net.dries007.tfc.config.TFCConfig;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.Metal;
import net.dries007.tfc.util.data.FluidHeat;

public class MoldItem extends Item
{
    private static IntSupplier mapItemTypeToConfigValue(Metal.ItemType type)
    {
        final IntValue intValue = switch (type)
            {
                case INGOT -> TFCConfig.SERVER.moldIngotCapacity;
                case PICKAXE_HEAD -> TFCConfig.SERVER.moldPickaxeHeadCapacity;
                case PROPICK_HEAD -> TFCConfig.SERVER.moldPropickHeadCapacity;
                case AXE_HEAD -> TFCConfig.SERVER.moldAxeHeadCapacity;
                case SHOVEL_HEAD -> TFCConfig.SERVER.moldShovelHeadCapacity;
                case HOE_HEAD -> TFCConfig.SERVER.moldHoeHeadCapacity;
                case CHISEL_HEAD -> TFCConfig.SERVER.moldChiselHeadCapacity;
                case HAMMER_HEAD -> TFCConfig.SERVER.moldHammerHeadCapacity;
                case SAW_BLADE -> TFCConfig.SERVER.moldSawBladeCapacity;
                case JAVELIN_HEAD -> TFCConfig.SERVER.moldJavelinHeadCapacity;
                case SWORD_BLADE -> TFCConfig.SERVER.moldSwordBladeCapacity;
                case MACE_HEAD -> TFCConfig.SERVER.moldMaceHeadCapacity;
                case KNIFE_BLADE -> TFCConfig.SERVER.moldKnifeBladeCapacity;
                case SCYTHE_BLADE -> TFCConfig.SERVER.moldScytheBladeCapacity;
                default -> throw new AssertionError("No config value for type: " + type.name());
            };
        return () -> Helpers.getValueOrDefault(intValue);
    }

    private final FluidContainerInfo containerInfo;

    public MoldItem(Metal.ItemType type, Properties properties)
    {
        this(mapItemTypeToConfigValue(type), type == Metal.ItemType.INGOT ? TFCTags.Fluids.USABLE_IN_INGOT_MOLD : TFCTags.Fluids.USABLE_IN_TOOL_HEAD_MOLD, properties);
        assert type.hasMold(); // Easy sanity check
    }

    public MoldItem(IntValue capacity, TagKey<Fluid> fluidTag, Properties properties)
    {
        this(() -> Helpers.getValueOrDefault(capacity), fluidTag, properties);
    }

    public MoldItem(IntSupplier capacity, TagKey<Fluid> fluidTag, Properties properties)
    {
        super(properties
            .component(TFCComponents.HEAT, HeatComponent.of(HeatCapability.POTTERY_HEAT_CAPACITY))
            .component(TFCComponents.FLUID, FluidComponent.EMPTY));

        this.containerInfo = new FluidContainerInfo() {
            @Override
            public boolean canContainFluid(Fluid input)
            {
                return Helpers.isFluid(input, fluidTag) && FluidHeat.get(input) != null;
            }

            @Override
            public int fluidCapacity()
            {
                return capacity.getAsInt();
            }
        };
    }

    public FluidContainerInfo containerInfo()
    {
        return containerInfo;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand)
    {
        final ItemStack stack = player.getItemInHand(hand);
        final IMold mold = IMold.get(stack);
        if (mold != null)
        {
            if (player.isShiftKeyDown())
            {
                // Try and un-mold
                final CastingRecipe recipe = CastingRecipe.get(mold);
                if (recipe != null)
                {
                    if (mold.isMolten())
                    {
                        player.displayClientMessage(Component.translatable("tfc.tooltip.small_vessel.alloy_molten"), true);
                        return InteractionResultHolder.consume(stack);
                    }
                    else
                    {
                        final ItemStack result = recipe.assemble(mold);

                        // Draining directly from the mold is denied, as the mold is not molten
                        // So, we need to clear the mold specially
                        mold.drainIgnoringTemperature(Integer.MAX_VALUE, IFluidHandler.FluidAction.EXECUTE);

                        // Give them the result of the casting
                        ItemHandlerHelper.giveItemToPlayer(player, result);
                        if (player.getRandom().nextFloat() < recipe.getBreakChance())
                        {
                            stack.shrink(1);
                            level.playSound(null, player.blockPosition(), TFCSounds.CERAMIC_BREAK.get(), SoundSource.PLAYERS, 1.0f, 1.0f);
                        }
                        return InteractionResultHolder.pass(stack);
                    }
                }
            }
            else
            {
                if (mold.isMolten())
                {
                    if (player instanceof ServerPlayer serverPlayer)
                    {
                        TFCContainerProviders.MOLD_LIKE_ALLOY.openScreen(serverPlayer, hand);
                    }
                }
                else if (!mold.getFluidInTank(0).isEmpty())
                {
                    player.displayClientMessage(Component.translatable("tfc.tooltip.small_vessel.alloy_solid"), true);
                }
                return InteractionResultHolder.success(stack);
            }
        }
        return InteractionResultHolder.pass(stack);
    }

    @Override
    public boolean overrideOtherStackedOnMe(ItemStack stack, ItemStack carried, Slot slot, ClickAction action, Player player, SlotAccess carriedSlot)
    {
        if (action == ClickAction.SECONDARY && !player.isCreative() && slot.allowModification(player))
        {
            final IMold mold = IMold.get(stack);
            if (mold != null && !mold.isMolten())
            {
                final CastingRecipe recipe = CastingRecipe.get(mold);
                if (recipe != null)
                {
                    final ItemStack result = recipe.assemble(mold);

                    final boolean noCarry = carried.isEmpty();
                    final boolean stackable = ItemStack.isSameItemSameComponents(result, carried) && result.getCount() + carried.getCount() <= carried.getMaxStackSize();

                    if (!noCarry && !stackable)
                    {
                        return false;
                    }

                    // Draining directly from the mold is denied, as the mold is not molten
                    // So, we need to clear the mold specially
                    mold.drainIgnoringTemperature(Integer.MAX_VALUE, IFluidHandler.FluidAction.EXECUTE);

                    // Give them the result of the casting
                    if (noCarry)
                    {
                        carriedSlot.set(result);
                    }
                    else
                    {
                        carried.grow(result.getCount());
                    }
                    if (player.getRandom().nextFloat() < recipe.getBreakChance())
                    {
                        stack.shrink(1);
                        player.level().playSound(null, player.blockPosition(), TFCSounds.CERAMIC_BREAK.get(), SoundSource.PLAYERS, 1.0f, 1.0f);
                    }

                    // Update slots, if we're in a crafting menu, to update output slots. See #2378
                    player.containerMenu.slotsChanged(slot.container);
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public int getMaxStackSize(ItemStack stack)
    {
        // We cannot just query the stack size to see if it has a contained fluid, as that would be self-referential
        // So we have to query a handler that *would* return a capability here, which means copying with stack size = 1
        return FluidHelpers.getContainedFluid(stack.copyWithCount(1)).isEmpty() ? super.getMaxStackSize(stack) : 1;
    }
}

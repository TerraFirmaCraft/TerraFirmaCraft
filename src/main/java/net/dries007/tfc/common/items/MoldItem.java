package net.dries007.tfc.common.items;

import java.util.List;
import java.util.function.IntSupplier;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.ChatFormatting;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.fmllegacy.network.NetworkHooks;
import net.minecraftforge.items.ItemHandlerHelper;

import net.dries007.tfc.client.TFCSounds;
import net.dries007.tfc.common.capabilities.DelegateFluidHandler;
import net.dries007.tfc.common.capabilities.DelegateHeatHandler;
import net.dries007.tfc.common.capabilities.MoldLike;
import net.dries007.tfc.common.capabilities.heat.HeatCapability;
import net.dries007.tfc.common.capabilities.heat.HeatHandler;
import net.dries007.tfc.common.capabilities.heat.IHeat;
import net.dries007.tfc.common.container.ItemStackContainerProvider;
import net.dries007.tfc.common.container.TFCContainerProviders;
import net.dries007.tfc.common.recipes.CastingRecipe;
import net.dries007.tfc.config.TFCConfig;
import net.dries007.tfc.util.Metal;

public class MoldItem extends Item
{
    private static IntSupplier mapItemTypeToConfigValue(Metal.ItemType type)
    {
        return switch (type)
            {
                case INGOT -> TFCConfig.SERVER.moldIngotCapacity::get;
                case PICKAXE_HEAD -> TFCConfig.SERVER.moldPickaxeHeadCapacity::get;
                case PROPICK_HEAD -> TFCConfig.SERVER.moldPropickHeadCapacity::get;
                case AXE_HEAD -> TFCConfig.SERVER.moldAxeHeadCapacity::get;
                case SHOVEL_HEAD -> TFCConfig.SERVER.moldShovelHeadCapacity::get;
                case HOE_HEAD -> TFCConfig.SERVER.moldHoeHeadCapacity::get;
                case CHISEL_HEAD -> TFCConfig.SERVER.moldChiselHeadCapacity::get;
                case HAMMER_HEAD -> TFCConfig.SERVER.moldHammerHeadCapacity::get;
                case SAW_BLADE -> TFCConfig.SERVER.moldSawBladeCapacity::get;
                case JAVELIN_HEAD -> TFCConfig.SERVER.moldJavelinHeadCapacity::get;
                case SWORD_BLADE -> TFCConfig.SERVER.moldSwordBladeCapacity::get;
                case MACE_HEAD -> TFCConfig.SERVER.moldMaceHeadCapacity::get;
                case KNIFE_BLADE -> TFCConfig.SERVER.moldKnifeBladeCapacity::get;
                case SCYTHE_BLADE -> TFCConfig.SERVER.moldScytheBladeCapacity::get;
                default -> throw new AssertionError("No config value for type: " + type.name());
            };
    }

    private final IntSupplier capacity;

    public MoldItem(Metal.ItemType type, Properties properties)
    {
        this(mapItemTypeToConfigValue(type), properties);
        assert type.hasMold(); // Easy sanity check
    }

    public MoldItem(IntSupplier capacity, Properties properties)
    {
        super(properties);

        this.capacity = capacity;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand)
    {
        final ItemStack stack = player.getItemInHand(hand);
        if (!level.isClientSide() && player instanceof ServerPlayer serverPlayer)
        {
            final MoldLike mold = MoldLike.get(stack);
            if (mold != null)
            {
                if (player.isShiftKeyDown())
                {
                    // Try and un-mold
                    final CastingRecipe recipe = CastingRecipe.get(mold);
                    if (recipe != null)
                    {
                        final ItemStack result = recipe.assemble(mold);
                        mold.drain(Integer.MAX_VALUE, IFluidHandler.FluidAction.EXECUTE); // Clear mold contents
                        ItemHandlerHelper.giveItemToPlayer(player, result); // Give them the result of the casting
                        if (player.getRandom().nextFloat() < recipe.getBreakChance())
                        {
                            stack.shrink(1);
                            level.playSound(player, player.blockPosition(), TFCSounds.CERAMIC_BREAK.get(), SoundSource.PLAYERS, 1.0f, 1.0f);
                        }
                    }
                }
                else
                {
                    if (mold.isMolten())
                    {
                        NetworkHooks.openGui(serverPlayer, TFCContainerProviders.MOLD_LIKE_ALLOY.of(stack, hand), ItemStackContainerProvider.write(hand));
                    }
                    else if (!mold.getFluidInTank(0).isEmpty())
                    {
                        player.displayClientMessage(new TranslatableComponent("tfc.tooltip.small_vessel.alloy_solid"), true);
                    }
                }
            }
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt)
    {
        return new MoldCapability(stack, capacity.getAsInt());
    }

    static class MoldCapability implements MoldLike, ICapabilityProvider, DelegateHeatHandler, DelegateFluidHandler
    {
        private final ItemStack stack;
        private final LazyOptional<MoldCapability> capability;

        private final HeatHandler heat;
        private final FluidTank tank;

        private boolean initialized; // If the internal capability objects have loaded their data.

        MoldCapability(ItemStack stack, int capacity)
        {
            this.stack = stack;
            this.capability = LazyOptional.of(() -> this);

            this.heat = new HeatHandler(1, 0, 0);
            this.tank = new FluidTank(capacity, fluid -> Metal.MANAGER.getMetal(fluid.getFluid()) != null); // Must be a metal
        }

        @Override
        public void setTemperature(float temperature)
        {
            heat.setTemperature(temperature);
            save();
        }

        @Override
        public void addTooltipInfo(ItemStack stack, List<Component> text)
        {
            heat.addTooltipInfo(stack, text);
            final FluidStack fluid = tank.getFluid();
            if (!fluid.isEmpty())
            {
                final Metal metal = Metal.MANAGER.getMetal(fluid.getFluid());
                if (metal != null)
                {
                    text.add(new TranslatableComponent("tfc.tooltip.small_vessel.contents").withStyle(ChatFormatting.DARK_GREEN));
                    text.add(metal.getDisplayName()
                        .append(" ")
                        .append(new TranslatableComponent("tfc.tooltip.small_vessel.alloy_units", fluid.getAmount()))
                        .append(" ")
                        .append(new TranslatableComponent(isMolten() ? "tfc.tooltip.small_vessel.molten" : "tfc.tooltip.small_vessel.solid")));
                }
            }
        }

        @Nonnull
        @Override
        public ItemStack getContainer()
        {
            return stack;
        }

        @Nonnull
        @Override
        public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side)
        {
            if (cap == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY || cap == CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY || cap == HeatCapability.CAPABILITY)
            {
                load();
                return capability.cast();
            }
            return LazyOptional.empty();
        }

        @Override
        public int fill(FluidStack resource, FluidAction action)
        {
            // Can always fill into a mold, despite the solid-ness
            final int amount = tank.fill(resource, action);
            if (amount > 0)
            {
                updateHeatCapacity();
                save();
            }
            return amount;
        }

        @Nonnull
        @Override
        public FluidStack drain(FluidStack resource, FluidAction action)
        {
            if (resource.getFluid() == tank.getFluid().getFluid())
            {
                final FluidStack result = drain(resource.getAmount(), action);
                if (!result.isEmpty())
                {
                    updateHeatCapacity();
                    save();
                }
                return result;
            }
            return FluidStack.EMPTY;
        }

        @Nonnull
        @Override
        public FluidStack drain(int maxDrain, FluidAction action)
        {
            final FluidStack result = tank.drain(maxDrain, action);
            if (!result.isEmpty())
            {
                updateHeatCapacity();
                save();
            }
            return result;
        }

        @Override
        public IFluidHandler getFluidHandler()
        {
            return tank;
        }

        @Override
        public IHeat getHeatHandler()
        {
            return heat;
        }

        @Override
        public boolean isMolten()
        {
            final Metal metal = getContainedMetal();
            if (metal != null)
            {
                return getTemperature() >= metal.getMeltTemperature();
            }
            return false;
        }

        /**
         * @see VesselItem.VesselCapability#load()
         */
        private void load()
        {
            if (!initialized)
            {
                initialized = true;

                final CompoundTag tag = stack.getOrCreateTag();
                tank.readFromNBT(tag.getCompound("tank"));
                heat.deserializeNBT(tag.getCompound("heat"));

                updateHeatCapacity();
            }
        }

        private void save()
        {
            final CompoundTag tag = stack.getOrCreateTag();
            tag.put("tank", tank.writeToNBT(new CompoundTag()));
            tag.put("heat", heat.serializeNBT());
        }

        @Nullable
        private Metal getContainedMetal()
        {
            return Metal.MANAGER.getMetal(tank.getFluid().getFluid());
        }

        private void updateHeatCapacity()
        {
            final Metal metal = getContainedMetal();
            heat.setHeatCapacity(metal != null ? metal.getHeatCapacity() : 1); // If fluid is not empty, should not be null, but we don't check for that case here
        }
    }
}

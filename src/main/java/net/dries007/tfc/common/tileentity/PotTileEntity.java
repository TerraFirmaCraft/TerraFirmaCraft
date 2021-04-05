package net.dries007.tfc.common.tileentity;

import javax.annotation.Nullable;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.items.ItemStackHandler;

import net.dries007.tfc.common.container.PotContainer;
import net.dries007.tfc.common.recipes.FluidInventoryRecipeWrapper;
import net.dries007.tfc.common.recipes.IPotRecipe;
import net.dries007.tfc.common.recipes.TFCRecipeTypes;
import net.dries007.tfc.common.types.FuelManager;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;

public class PotTileEntity extends FirepitTileEntity
{
    private static final ITextComponent NAME = new TranslationTextComponent(MOD_ID + ".tile_entity.pot");

    public static final int SLOT_EXTRA_INPUT_START = 4;
    public static final int SLOT_EXTRA_INPUT_END = 8;

    private IPotRecipe.Output output;
    private IPotRecipe cachedPotRecipe;
    private int boilingTicks;
    protected FluidTank tank;
    protected final LazyOptional<IFluidHandler> fluidCapability;

    public PotTileEntity()
    {
        this(TFCTileEntities.POT.get(), 9, NAME);
    }

    public PotTileEntity(TileEntityType<?> type, int inventorySlots, ITextComponent defaultName)
    {
        super(type, inventorySlots, defaultName);
        output = null;
        cachedPotRecipe = null;
        boilingTicks = 0;
        tank = new FluidTank(1000); //todo: predicate whitelist? custom tank impl?
        fluidCapability = LazyOptional.of(() -> tank);
    }

    @Override
    public void load(BlockState state, CompoundNBT nbt)
    {
        tank.readFromNBT(nbt.getCompound("tank"));
        if (nbt.hasUUID("output"))
            output.deserializeNBT(nbt);
        boilingTicks = nbt.getInt("boilingTicks");
        super.load(state, nbt);
    }

    @Override
    public CompoundNBT save(CompoundNBT nbt)
    {
        nbt.put("tank", tank.writeToNBT(new CompoundNBT()));
        if (output != null)
            nbt.put("output", output.serializeNBT());
        nbt.putInt("boilingTicks", boilingTicks);
        return super.save(nbt);
    }

    @Override
    protected void handleCooking()
    {
        IPotRecipe recipe = cachedPotRecipe;
        if (isBoiling())
        {
            if (boilingTicks > recipe.getDuration())
            {
                ItemStackHandler inv = convertInventory();
                FluidStack fluidStack = tank.drain(1000, IFluidHandler.FluidAction.SIMULATE);
                if (!recipe.isValid(inv, fluidStack))
                {
                    quit();
                    return;
                }
                output = recipe.getOutput(inv, fluidStack);
                for (int i = SLOT_EXTRA_INPUT_START; i <= SLOT_EXTRA_INPUT_END; i++)
                    inventory.setStackInSlot(i, ItemStack.EMPTY);

                tank.setFluid(FluidStack.EMPTY);
                tank.fill(recipe.getOutputFluid(), IFluidHandler.FluidAction.EXECUTE);

                quit();
                return;
            }
            boilingTicks++;
        }
        else quit();
    }

    private void quit()
    {
        boilingTicks = 0;
        markForSync();
    }

    @Override
    protected void handleQuenching()
    {
        quit();
    }

    public boolean isBoiling() // if we have a recipe, there is no output, and we're hot enough, we boil
    {
        return cachedPotRecipe != null && output == null && cachedPotRecipe.isValidTemperature(temperature);
    }

    public void setFinished()
    {
        output = null;
        markForSync();
    }

    /**
     * Distinct from getOutput() in {@link IPotRecipe#getOutput(ItemStackHandler, FluidStack)}
     * This is a copy of the internal data from calling that function
     */
    public IPotRecipe.Output getOutput()
    {
        return output;
    }

    public boolean hasOutput()
    {
        return output != null;
    }

    @Override
    public void updateCache()
    {
        if (level == null) return;
        FluidInventoryRecipeWrapper wrapper = new FluidInventoryRecipeWrapper(convertInventory(), tank.drain(1000, IFluidHandler.FluidAction.SIMULATE));
        cachedPotRecipe = level.getRecipeManager().getRecipeFor(TFCRecipeTypes.POT, wrapper, level).orElse(null);
    }

    private ItemStackHandler convertInventory()
    {
        ItemStackHandler inv = new ItemStackHandler(5);
        for (int i = 0; i < 5; i++)
            inv.setStackInSlot(i, inventory.getStackInSlot(i + SLOT_EXTRA_INPUT_START).copy());
        return inv;
    }

    public FluidStack getFluidContained()
    {
        return tank.getFluid();
    }

    @Override
    public int getSlotStackLimit(int slot)
    {
        return 1;
    }

    @Override
    public boolean isItemValid(int slot, ItemStack stack)
    {
        if (slot == SLOT_FUEL_INPUT)
        {
            return FuelManager.get(stack).isPresent();
        }
        return slot >= SLOT_EXTRA_INPUT_START && slot <= SLOT_EXTRA_INPUT_END;
    }

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, @Nullable Direction side)
    {
        if (cap == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY && side == null)
        {
            return fluidCapability.cast();
        }
        return super.getCapability(cap, side);
    }

    @Nullable
    @Override
    public Container createMenu(int windowID, PlayerInventory playerInv, PlayerEntity player)
    {
        return new PotContainer(this, playerInv, windowID);
    }
}

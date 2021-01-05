package net.dries007.tfc.common.tileentity;

import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;

import net.minecraft.block.BlockState;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.IIntArray;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;

import net.dries007.tfc.common.blocks.devices.FirepitBlock;
import net.dries007.tfc.common.capabilities.heat.HeatCapability;
import net.dries007.tfc.common.capabilities.heat.IHeat;
import net.dries007.tfc.common.container.FirepitContainer;
import net.dries007.tfc.common.recipes.HeatingRecipe;
import net.dries007.tfc.common.recipes.ItemStackRecipeWrapper;
import net.dries007.tfc.common.types.Fuel;
import net.dries007.tfc.common.types.FuelManager;
import net.dries007.tfc.config.TFCConfig;
import net.dries007.tfc.util.IntArrayBuilder;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;

public class FirepitTileEntity extends TickableInventoryTileEntity
{
    private static final ITextComponent NAME = new TranslationTextComponent(MOD_ID + ".tile_entity.firepit");

    //todo: ICalendarTickable, hopper compat without IInventory

    public static final int SLOT_FUEL_CONSUME = 0; // where fuel is taken by the firepit
    public static final int SLOT_FUEL_INPUT = 3; // where fuel is inserted into the firepit (0-3 are all fuel slots)
    public static final int SLOT_ITEM_INPUT = 4; // item to be cooked
    public static final int SLOT_OUTPUT_1 = 5; // generic output slot
    public static final int SLOT_OUTPUT_2 = 6; // extra output slot

    protected boolean needsSlotUpdate = false; // sets when fuel needs to be cascaded
    protected int burnTicks; // ticks remaining for the burning of the fuel item
    protected int airTicks; // ticks remaining for bellows provided air
    protected float burnTemperature; // burn temperature of the current fuel item
    protected float temperature; // current actual temperature
    protected final IIntArray syncableData;
    protected HeatingRecipe cachedRecipe;

    public static final int FIELD_TEMP = 3;

    public FirepitTileEntity()
    {
        this(TFCTileEntities.FIREPIT.get(), 7, NAME);
    }

    public FirepitTileEntity(TileEntityType<?> type, int inventorySlots, ITextComponent defaultName)
    {
        super(type, inventorySlots, defaultName);

        burnTicks = 0;
        temperature = 0;
        burnTemperature = 0;

        syncableData = new IntArrayBuilder().add(() -> (int) temperature, value -> temperature = value);
        cachedRecipe = null;
    }

    @Override
    public void load(BlockState state, CompoundNBT nbt)
    {
        needsSlotUpdate = nbt.getBoolean("needsSlotUpdate");
        temperature = nbt.getFloat("temperature");
        burnTicks = nbt.getInt("burnTicks");
        airTicks = nbt.getInt("airTicks");
        burnTemperature = nbt.getFloat("burnTemperature");
        super.load(state, nbt);
    }

    @Override
    public CompoundNBT save(CompoundNBT nbt)
    {
        nbt.putBoolean("needsSlotUpdate", needsSlotUpdate);
        nbt.putFloat("temperature", temperature);
        nbt.putInt("burnTicks", burnTicks);
        nbt.putInt("airTicks", airTicks);
        nbt.putFloat("burnTemperature", burnTemperature);
        return super.save(nbt);
    }

    @Override
    public void tick()
    {
        super.tick();
        //todo:  check for calendar update
        if (level != null && !level.isClientSide)
        {
            BlockState state = level.getBlockState(worldPosition);
            if (state.getValue(FirepitBlock.LIT))
            {
                // Update fuel
                if (burnTicks > 0)
                    burnTicks -= airTicks > 0 ? 2 : 1; // burn fuel faster when under the bellows effect
                if (burnTicks <= 0) // consume fuel when we're out of burn ticks
                {
                    ItemStack stack = inventory.getStackInSlot(SLOT_FUEL_CONSUME);
                    if (stack.isEmpty()) // there's no fuel, so the fire goes out
                    {
                        extinguish(state);
                    }
                    else
                    {
                        inventory.setStackInSlot(SLOT_FUEL_CONSUME, ItemStack.EMPTY);
                        needsSlotUpdate = true;
                        Fuel fuel = FuelManager.get(stack);
                        if (fuel != null)
                        {
                            burnTicks += fuel.getAmount();
                            burnTemperature = fuel.getTemperature();
                        }
                    }
                }
            }
        }
        airTicks = airTicks > 0 ? airTicks - 1 : 0; // decrement the bellows time (or just keep it at zero)
        if (temperature > 0 || burnTemperature > 0)
        {
            // Update temperature
            float targetTemperature = burnTemperature + airTicks; // bellows raises the max temperature we can reach
            if (temperature != targetTemperature)
            {
                double delta = TFCConfig.SERVER.itemHeatingModifier.get();
                temperature = HeatCapability.adjustTempTowards(temperature, targetTemperature, (float) delta * (airTicks > 0 ? 2 : 1), (float) delta * (airTicks > 0 ? 0.5f : 1));
            }
        }
        handleCooking();
        if (needsSlotUpdate)
            cascadeFuelSlots();
    }

    /**
     * Superclasses should override this so as to still be able to call super.tick() without acting like a blank firepit does
     */
    protected void handleCooking()
    {
        if (temperature > 0)
        {
            // The fire pit is nice: it will automatically move input to output for you, saving the trouble of losing the input due to melting / burning
            ItemStack inputStack = inventory.getStackInSlot(SLOT_ITEM_INPUT);
            IHeat cap = inputStack.getCapability(HeatCapability.CAPABILITY, null).resolve().orElse(null);
            if (cap != null)
            {
                float itemTemp = cap.getTemperature();
                if (temperature > itemTemp)
                    HeatCapability.addTemp(cap); // heat up the item no matter what

                //todo: handle possible fluid melting
                if (cachedRecipe != null && cachedRecipe.isValidTemperature(itemTemp))
                {
                    HeatingRecipe recipe = cachedRecipe;
                    ItemStack outputStack = recipe.assemble(new ItemStackRecipeWrapper(inputStack));
                    inputStack.shrink(1);
                    if (!outputStack.isEmpty())
                    {
                        //todo: leftovers
                        inventory.insertItem(SLOT_OUTPUT_1, outputStack, false);
                    }
                }
            }
        }
    }

    private void cascadeFuelSlots()
    {
        int lowestOpenSlot = SLOT_FUEL_CONSUME;
        for (int i = SLOT_FUEL_CONSUME; i <= SLOT_FUEL_INPUT; i++)
        {
            ItemStack stack = inventory.getStackInSlot(i);
            if (!stack.isEmpty())
            {
                if (i > lowestOpenSlot)
                {
                    inventory.setStackInSlot(lowestOpenSlot, stack.copy());
                    inventory.setStackInSlot(i, ItemStack.EMPTY);
                }
                lowestOpenSlot++;
            }
        }
        needsSlotUpdate = false;
    }

    @Override
    public void setAndUpdateSlots(int slot)
    {
        markDirtyFast();
        needsSlotUpdate = true;
        if (level != null)
            cachedRecipe = HeatingRecipe.getRecipe(level, new ItemStackRecipeWrapper(inventory.getStackInSlot(SLOT_ITEM_INPUT)));
    }

    public void extinguish(BlockState state)
    {
        if (level != null)
        {
            level.setBlockAndUpdate(worldPosition, state.setValue(FirepitBlock.LIT, false));
            burnTicks = 0;
            airTicks = 0;
            burnTemperature = 0;
        }
    }

    public void onAddAttachment()
    {
        BlockPos pos = worldPosition;
        if (level == null) return;
        for (int i = SLOT_ITEM_INPUT; i <= SLOT_OUTPUT_2; i++)
        {
            level.addFreshEntity(new ItemEntity(level, pos.getX() + 0.5D, pos.getY() + 0.7D, pos.getZ() + 0.5D, inventory.getStackInSlot(i)));
        }
    }

    public List<ItemStack> getLogs()
    {
        List<ItemStack> logs = NonNullList.withSize(4, ItemStack.EMPTY);
        for (int i = SLOT_FUEL_CONSUME; i <= SLOT_FUEL_INPUT; i++)
        {
            logs.set(i, inventory.getStackInSlot(i));
        }
        return logs;
    }

    public float[] getFields()
    {
        return new float[] {(float) burnTicks, (float) airTicks, burnTemperature, temperature};
    }

    public void acceptData(List<ItemStack> logs, float[] fields)
    {
        for (int i = SLOT_FUEL_CONSUME; i <= SLOT_FUEL_INPUT; i++)
        {
            inventory.setStackInSlot(i, logs.get(i));
        }
        burnTicks = (int) fields[0];
        airTicks = (int) fields[1];
        burnTemperature = fields[2];
        temperature = fields[3];

        needsSlotUpdate = true;
    }

    @Override
    public int getSlotStackLimit(int slot)
    {
        // Output slots can carry full stacks, the rest is individual
        return (slot == SLOT_OUTPUT_1 || slot == SLOT_OUTPUT_2) ? 64 : 1;
    }

    @Override
    public boolean isItemValid(int slot, ItemStack stack)
    {
        switch (slot)
        {
            case SLOT_FUEL_INPUT:
                return FuelManager.isFirepitFuel(stack);
            case SLOT_ITEM_INPUT:
                return stack.getCapability(HeatCapability.CAPABILITY).isPresent();
            case SLOT_OUTPUT_1:
            case SLOT_OUTPUT_2:
                return stack.getCapability(HeatCapability.CAPABILITY).isPresent() && stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY).isPresent();
            default:
                return false;
        }
    }

    @Override
    public void clearContent()
    {
        for (int i = SLOT_FUEL_CONSUME; i <= SLOT_OUTPUT_2; i++)
        {
            inventory.setStackInSlot(i, ItemStack.EMPTY);
        }
    }

    @Nullable
    @Override
    public Container createMenu(int p_createMenu_1_, PlayerInventory p_createMenu_2_, PlayerEntity p_createMenu_3_)
    {
        return new FirepitContainer(this, p_createMenu_2_, p_createMenu_1_);
    }

    public IIntArray getSyncableData()
    {
        return syncableData;
    }
}

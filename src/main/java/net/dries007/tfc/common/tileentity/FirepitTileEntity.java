package net.dries007.tfc.common.tileentity;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Queue;
import javax.annotation.Nullable;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.IIntArray;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.util.Constants;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blocks.devices.FirepitBlock;
import net.dries007.tfc.common.capabilities.heat.HeatCapability;
import net.dries007.tfc.common.container.FirepitContainer;
import net.dries007.tfc.common.recipes.HeatingRecipe;
import net.dries007.tfc.common.recipes.ItemStackRecipeWrapper;
import net.dries007.tfc.common.types.Fuel;
import net.dries007.tfc.common.types.FuelManager;
import net.dries007.tfc.config.TFCConfig;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.IntArrayBuilder;
import net.dries007.tfc.util.calendar.Calendars;
import net.dries007.tfc.util.calendar.ICalendarTickable;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;

public class FirepitTileEntity extends TickableInventoryTileEntity implements ICalendarTickable
{
    private static final ITextComponent NAME = new TranslationTextComponent(MOD_ID + ".tile_entity.firepit");

    //todo: hopper compat without IInventory, canInsert/canExtract functions are missing

    public static final int SLOT_FUEL_CONSUME = 0; // where fuel is taken by the firepit
    public static final int SLOT_FUEL_INPUT = 3; // where fuel is inserted into the firepit (0-3 are all fuel slots)
    public static final int SLOT_ITEM_INPUT = 4; // item to be cooked
    public static final int SLOT_OUTPUT_1 = 5; // generic output slot
    public static final int SLOT_OUTPUT_2 = 6; // extra output slot
    public static final int DATA_SLOT_TEMPERATURE = 0;

    protected boolean needsSlotUpdate = false; // sets when fuel needs to be cascaded
    protected int burnTicks; // ticks remaining for the burning of the fuel item
    protected int airTicks; // ticks remaining for bellows provided air
    protected float burnTemperature; // burn temperature of the current fuel item
    protected float temperature; // current actual temperature
    private long lastPlayerTick;
    protected final IIntArray syncableData;
    protected HeatingRecipe cachedRecipe;
    private final Queue<ItemStack> leftover = new LinkedList<>(); // Leftover items when we can't merge output into any output slot.

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
        lastPlayerTick = Calendars.SERVER.getTicks();

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
        lastPlayerTick = nbt.getLong("lastPlayerTick");
        updateCache();
        if (nbt.hasUUID("leftover"))
        {
            ListNBT surplusItems = nbt.getList("leftover", Constants.NBT.TAG_COMPOUND);
            for (int i = 0; i < surplusItems.size(); i++)
            {
                leftover.add(ItemStack.of(surplusItems.getCompound(i)));
            }
        }
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
        nbt.putLong("lastPlayerTick", lastPlayerTick);
        if (!leftover.isEmpty())
        {
            ListNBT surplusList = new ListNBT();
            for (ItemStack stack : leftover)
            {
                surplusList.add(stack.serializeNBT());
            }
            nbt.put("leftover", surplusList);
        }
        return super.save(nbt);
    }

    @Override
    public void tick()
    {
        super.tick();
        checkForCalendarUpdate();
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
                        Optional<Fuel> optionalFuel = FuelManager.get(stack);
                        if (optionalFuel.isPresent())
                        {
                            Fuel fuel = optionalFuel.get();
                            burnTicks += fuel.getDuration();
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
            float targetTemperature = burnTemperature + (airTicks > 0 ? MathHelper.clamp(burnTemperature, 0, 300) : 0);
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

    @Override
    public void onCalendarUpdate(long deltaPlayerTicks)
    {
        if (level == null || !level.getBlockState(worldPosition).getValue(FirepitBlock.LIT)) return;
        // Consume fuel as dictated by the delta player ticks (don't simulate any input changes), and then extinguish
        if (burnTicks > deltaPlayerTicks)
        {
            burnTicks -= deltaPlayerTicks;
            return;
        }
        else
        {
            deltaPlayerTicks -= burnTicks;
            burnTicks = 0;
        }
        needsSlotUpdate = true; // Need to consume fuel
        for (int i = SLOT_FUEL_CONSUME; i <= SLOT_FUEL_INPUT; i++)
        {
            ItemStack fuelStack = inventory.getStackInSlot(i);
            Optional<Fuel> optionalFuel = FuelManager.get(fuelStack);
            if (optionalFuel.isPresent())
            {
                Fuel fuel = optionalFuel.get();
                inventory.setStackInSlot(i, ItemStack.EMPTY);
                if (fuel.getDuration() > deltaPlayerTicks)
                {
                    burnTicks = (int) (fuel.getDuration() - deltaPlayerTicks);
                    burnTemperature = fuel.getTemperature();
                    return;
                }
                else
                {
                    deltaPlayerTicks -= fuel.getDuration();
                    burnTicks = 0;
                }
            }
        }
        if (deltaPlayerTicks > 0) // Consumed all fuel, so extinguish and cool instantly
        {
            extinguish(level.getBlockState(worldPosition));
            handleQuenching();
        }
    }

    protected void handleQuenching()
    {
        inventory.getStackInSlot(SLOT_ITEM_INPUT).getCapability(HeatCapability.CAPABILITY, null).ifPresent(cap -> cap.setTemperature(0f));
    }

    @Override
    public long getLastUpdateTick()
    {
        return lastPlayerTick;
    }

    @Override
    public void setLastUpdateTick(long tick)
    {
        lastPlayerTick = tick;
    }

    /**
     * Superclasses should override this so as to still be able to call super.tick() without acting like a blank firepit does
     */
    protected void handleCooking()
    {
        if (temperature > 0)
        {
            ItemStack inputStack = inventory.getStackInSlot(SLOT_ITEM_INPUT);
            //IHeat cap = inputStack.getCapability(HeatCapability.CAPABILITY, null).resolve().orElse(null);
            inputStack.getCapability(HeatCapability.CAPABILITY).ifPresent(cap -> {
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
                        outputStack = mergeOutputStack(outputStack);
                        if (!outputStack.isEmpty())
                            leftover.add(outputStack);
                    }
                }
            });
        }
        if (!leftover.isEmpty())
        {
            ItemStack outputStack = leftover.peek();
            outputStack = mergeOutputStack(outputStack); // grab the front of the queue and try to merge right away
            if (outputStack.isEmpty())
                leftover.poll(); // if we merged successfully, let's remove it
        }
    }

    private ItemStack mergeOutputStack(ItemStack outputStack) //todo: CapabilityFood.mergeItemStacksIgnoreCreationDate
    {
        outputStack = inventory.insertItem(SLOT_OUTPUT_1, outputStack, false); // insertItem returns what's left over
        outputStack = inventory.insertItem(SLOT_OUTPUT_2, outputStack, false);
        setAndUpdateSlots(SLOT_ITEM_INPUT); // unfortunately this is needed
        return outputStack; // put into the leftover queue after
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
        super.setAndUpdateSlots(slot);
        needsSlotUpdate = true;
        updateCache();
    }

    public void updateCache()
    {
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

    public void dump()
    {
        if (level == null) return;
        for (int i = SLOT_ITEM_INPUT; i < inventory.getSlots(); i++)
        {
            Helpers.spawnItem(level, worldPosition, inventory.getStackInSlot(i), 0.7D);
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

    public void onRainDrop()
    {
        if (level == null) return;
        burnTicks -= TFCConfig.SERVER.rainTicks.get();
        Helpers.playSound(level, worldPosition, SoundEvents.LAVA_EXTINGUISH);
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
                return FuelManager.get(stack).isPresent() && stack.getItem().is(TFCTags.Items.FIREPIT_FUEL);
            case SLOT_ITEM_INPUT:
                return stack.getCapability(HeatCapability.CAPABILITY).isPresent();
            case SLOT_OUTPUT_1:
            case SLOT_OUTPUT_2:
                return true; // todo: need canInsert/canExtract stuff for this to work properly
            //return stack.getCapability(HeatCapability.CAPABILITY).isPresent() && stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY).isPresent();
            default:
                return false;
        }
    }

    @Nullable
    @Override
    public Container createMenu(int windowID, PlayerInventory playerInv, PlayerEntity player)
    {
        return new FirepitContainer(this, playerInv, windowID);
    }

    public IIntArray getSyncableData()
    {
        return syncableData;
    }
}

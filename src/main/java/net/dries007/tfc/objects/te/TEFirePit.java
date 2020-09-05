/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.te;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;

import net.dries007.tfc.ConfigTFC;
import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.api.capability.food.*;
import net.dries007.tfc.api.capability.heat.CapabilityItemHeat;
import net.dries007.tfc.api.capability.heat.Heat;
import net.dries007.tfc.api.capability.heat.IItemHeat;
import net.dries007.tfc.api.recipes.heat.HeatRecipe;
import net.dries007.tfc.objects.blocks.devices.BlockFirePit;
import net.dries007.tfc.objects.inventory.capability.IItemHandlerSidedCallback;
import net.dries007.tfc.objects.inventory.capability.ItemHandlerSidedWrapper;
import net.dries007.tfc.objects.items.food.ItemDynamicBowlFood;
import net.dries007.tfc.objects.items.food.ItemFoodTFC;
import net.dries007.tfc.util.agriculture.Food;
import net.dries007.tfc.util.calendar.CalendarTFC;
import net.dries007.tfc.util.calendar.ICalendarTickable;
import net.dries007.tfc.util.fuel.Fuel;
import net.dries007.tfc.util.fuel.FuelManager;

import static net.dries007.tfc.objects.blocks.devices.BlockFirePit.ATTACHMENT;
import static net.dries007.tfc.objects.blocks.devices.BlockFirePit.LIT;

@ParametersAreNonnullByDefault
public class TEFirePit extends TETickableInventory implements ICalendarTickable, ITileFields, IItemHandlerSidedCallback
{
    // Slot 0 - 3 = fuel slots with 3 being input, 4 = normal input slot, 5 and 6 are output slots 1 + 2
    public static final int SLOT_FUEL_CONSUME = 0;
    public static final int SLOT_FUEL_INPUT = 3;
    public static final int SLOT_ITEM_INPUT = 4; // Only used by the regular fire pit
    public static final int SLOT_OUTPUT_1 = 5;
    public static final int SLOT_OUTPUT_2 = 6;
    public static final int SLOT_EXTRA_INPUT_START = 7; // Used by the grill / cooking pot
    public static final int SLOT_EXTRA_INPUT_END = 11;

    public static final int FIELD_TEMPERATURE = 0;
    public static final int FIELD_COOKING_POT_STAGE = 1;
    public static final int FIELD_COOKING_POT_SERVINGS = 2;

    public static final float COOKING_POT_BOILING_TEMPERATURE = Heat.VERY_HOT.getMin();

    private final IItemHandler[] inventoryWrappers;
    private final Queue<ItemStack> leftover = new LinkedList<>(); // Leftover items when we can't merge output into any output slot.
    // Grill
    private final HeatRecipe[] cachedGrillRecipes;
    private HeatRecipe cachedRecipe;
    private boolean requiresSlotUpdate = false;
    private float temperature; // Current Temperature
    private int burnTicks; // Ticks remaining on the current item of fuel
    private int airTicks; // Ticks of bellows provided air remaining
    private float burnTemperature; // Temperature provided from the current item of fuel
    private long lastPlayerTick; // Last player tick this forge was ticked (for purposes of catching up)
    // Attachments
    private ItemStack attachedItemStack;
    // Cooking Pot
    private CookingPotStage cookingPotStage;
    private int boilingTicks;
    private FoodData soupContents;
    private int soupServings;
    private Nutrient soupNutrient;
    private long soupCreationDate;

    public TEFirePit()
    {
        super(12);

        temperature = 0;
        burnTemperature = 0;
        burnTicks = 0;
        cachedRecipe = null;
        lastPlayerTick = CalendarTFC.PLAYER_TIME.getTicks();

        attachedItemStack = ItemStack.EMPTY;

        cookingPotStage = CookingPotStage.EMPTY;
        boilingTicks = 0;
        soupContents = null;
        soupServings = 0;
        soupNutrient = null;
        soupCreationDate = 0;

        cachedGrillRecipes = new HeatRecipe[5];

        inventoryWrappers = Arrays.stream(EnumFacing.values()).map(e -> new ItemHandlerSidedWrapper(this, inventory, e)).toArray(IItemHandler[]::new);
    }

    /**
     * Consume more fuel on rain
     */
    public void onRainDrop()
    {
        burnTicks -= ConfigTFC.Devices.FIRE_PIT.rainTicks;
        // Play the "tsssss" sound
        world.playSound(null, pos, SoundEvents.BLOCK_LAVA_EXTINGUISH, SoundCategory.BLOCKS, 0.8f, 0.8f + net.dries007.tfc.Constants.RNG.nextFloat() * 0.4f);
    }

    @Override
    public void update()
    {
        super.update();
        checkForCalendarUpdate();
        if (!world.isRemote)
        {
            IBlockState state = world.getBlockState(pos);
            if (state.getValue(LIT))
            {
                // Update fuel
                if (burnTicks > 0)
                {
                    burnTicks -= airTicks > 0 ? 2 : 1;
                }
                if (burnTicks <= 0)
                {
                    // Consume fuel
                    ItemStack stack = inventory.getStackInSlot(SLOT_FUEL_CONSUME);
                    if (stack.isEmpty())
                    {
                        world.setBlockState(pos, state.withProperty(LIT, false));
                        burnTicks = 0;
                        burnTemperature = 0;
                    }
                    else
                    {
                        inventory.setStackInSlot(SLOT_FUEL_CONSUME, ItemStack.EMPTY);
                        requiresSlotUpdate = true;
                        Fuel fuel = FuelManager.getFuel(stack);
                        burnTicks += fuel.getAmount();
                        burnTemperature = fuel.getTemperature();
                    }
                }
            }

            // Update air ticks
            if (airTicks > 0)
            {
                airTicks--;
            }
            else
            {
                airTicks = 0;
            }

            // Always update temperature / cooking, until the fire pit is not hot anymore
            if (temperature > 0 || burnTemperature > 0)
            {
                // Update temperature
                float targetTemperature = burnTemperature + airTicks;
                if (temperature != targetTemperature)
                {
                    float delta = (float) ConfigTFC.Devices.TEMPERATURE.heatingModifier;
                    temperature = CapabilityItemHeat.adjustTempTowards(temperature, targetTemperature, delta * (airTicks > 0 ? 2 : 1), delta * (airTicks > 0 ? 0.5f : 1));
                }
            }

            BlockFirePit.FirePitAttachment attachment = state.getValue(ATTACHMENT);
            if (attachment == BlockFirePit.FirePitAttachment.NONE)
            {
                markForSync();
                if (temperature > 0)
                {
                    // The fire pit is nice: it will automatically move input to output for you, saving the trouble of losing the input due to melting / burning
                    ItemStack stack = inventory.getStackInSlot(SLOT_ITEM_INPUT);
                    IItemHeat cap = stack.getCapability(CapabilityItemHeat.ITEM_HEAT_CAPABILITY, null);
                    if (cap != null)
                    {
                        float itemTemp = cap.getTemperature();
                        if (temperature > itemTemp)
                        {
                            CapabilityItemHeat.addTemp(cap);
                        }

                        handleInputMelting(stack);
                    }
                }

                // Leftover handling
                if (!leftover.isEmpty())
                {
                    ItemStack outputStack = leftover.peek();

                    // Try inserting in any slot
                    outputStack = mergeOutputStack(outputStack);
                    if (outputStack.isEmpty())
                    {
                        leftover.poll();
                    }
                }
            }
            else if (attachment == BlockFirePit.FirePitAttachment.COOKING_POT)
            {
                if (cookingPotStage == CookingPotStage.WAITING)
                {
                    markForSync();
                    if (temperature > COOKING_POT_BOILING_TEMPERATURE)
                    {
                        // Begin boiling
                        cookingPotStage = CookingPotStage.BOILING;
                        boilingTicks = 0;
                    }
                }
                else if (cookingPotStage == CookingPotStage.BOILING)
                {
                    if (temperature < COOKING_POT_BOILING_TEMPERATURE)
                    {
                        // Stop boiling
                        cookingPotStage = CookingPotStage.WAITING;
                        boilingTicks = 0;
                    }
                    else
                    {
                        boilingTicks++;
                        if (boilingTicks > ConfigTFC.Devices.FIRE_PIT.ticks)
                        {
                            // Convert output
                            float water = 20, saturation = 2; // soups have base 20 water + 2 saturation
                            float[] nutrition = new float[Nutrient.TOTAL];
                            int ingredientCount = 0;
                            for (int i = SLOT_EXTRA_INPUT_START; i <= SLOT_EXTRA_INPUT_END; i++)
                            {
                                ItemStack ingredient = inventory.getStackInSlot(i);
                                IFood food = ingredient.getCapability(CapabilityFood.CAPABILITY, null);
                                if (food != null)
                                {
                                    if (food.isRotten())
                                    {
                                        ingredientCount = 0;
                                        break;
                                    }
                                    water += food.getData().getWater();
                                    saturation += food.getData().getSaturation();
                                    float[] ingredientNutrition = food.getData().getNutrients();
                                    for (Nutrient nutrient : Nutrient.values())
                                    {
                                        nutrition[nutrient.ordinal()] += ingredientNutrition[nutrient.ordinal()];
                                    }
                                    ingredientCount++;
                                }
                                inventory.setStackInSlot(i, ItemStack.EMPTY);
                            }
                            if (ingredientCount > 0)
                            {
                                float multiplier = 1 - (0.05f * ingredientCount); // per-serving multiplier of nutrition
                                water *= multiplier;
                                saturation *= multiplier;
                                Nutrient maxNutrient = Nutrient.GRAIN; // default nutrient in case there is no nutrition
                                float maxNutrientValue = 0;
                                for (Nutrient nutrient : Nutrient.values())
                                {
                                    nutrition[nutrient.ordinal()] *= multiplier;
                                    if (nutrition[nutrient.ordinal()] > maxNutrientValue)
                                    {
                                        maxNutrientValue = nutrition[nutrient.ordinal()];
                                        maxNutrient = nutrient;
                                    }
                                }

                                soupContents = new FoodData(4, water, saturation, nutrition, Food.SOUP_GRAIN.getData().getDecayModifier());
                                soupServings = (int) (ingredientCount / 2f) + 1;
                                soupNutrient = maxNutrient; // the max nutrient determines the item you get
                                soupCreationDate = CapabilityFood.getRoundedCreationDate();

                                cookingPotStage = CookingPotStage.FINISHED;
                            }
                            else
                            {
                                cookingPotStage = CookingPotStage.EMPTY;
                            }
                        }
                    }
                }
            }
            else if (attachment == BlockFirePit.FirePitAttachment.GRILL)
            {
                // Only difference is we do the same heating recipe manipulations, just with five extra slots instead.
                for (int i = SLOT_EXTRA_INPUT_START; i <= SLOT_EXTRA_INPUT_END; i++)
                {
                    ItemStack stack = inventory.getStackInSlot(i);
                    IItemHeat cap = stack.getCapability(CapabilityItemHeat.ITEM_HEAT_CAPABILITY, null);
                    if (cap != null)
                    {
                        float itemTemp = cap.getTemperature();
                        if (temperature > itemTemp)
                        {
                            CapabilityItemHeat.addTemp(cap);
                        }
                        handleGrillCooking(i, stack, cap);
                    }
                }
            }

            // This is here to avoid duplication glitches
            if (requiresSlotUpdate)
            {
                cascadeFuelSlots();
            }
            markDirty();
        }
    }

    @Override
    public void onCalendarUpdate(long deltaPlayerTicks)
    {
        IBlockState state = world.getBlockState(pos);
        if (!state.getValue(LIT))
        {
            return;
        }
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
        // Need to consume fuel
        requiresSlotUpdate = true;
        for (int i = SLOT_FUEL_CONSUME; i <= SLOT_FUEL_INPUT; i++)
        {
            ItemStack fuelStack = inventory.getStackInSlot(i);
            Fuel fuel = FuelManager.getFuel(fuelStack);
            inventory.setStackInSlot(i, ItemStack.EMPTY);
            if (fuel.getAmount() > deltaPlayerTicks)
            {
                burnTicks = (int) (fuel.getAmount() - deltaPlayerTicks);
                burnTemperature = fuel.getTemperature();
                return;
            }
            else
            {
                deltaPlayerTicks -= fuel.getAmount();
                burnTicks = 0;
            }
        }
        if (deltaPlayerTicks > 0)
        {
            // Consumed all fuel, so extinguish and cool instantly
            burnTemperature = 0;
            temperature = 0;
            ItemStack stack = inventory.getStackInSlot(SLOT_ITEM_INPUT);
            IItemHeat cap = stack.getCapability(CapabilityItemHeat.ITEM_HEAT_CAPABILITY, null);
            if (cap != null)
            {
                cap.setTemperature(0f);
            }
            world.setBlockState(pos, state.withProperty(LIT, false));
        }
    }

    @Override
    public long getLastUpdateTick()
    {
        return lastPlayerTick;
    }

    @Override
    public void setLastUpdateTick(long tick)
    {
        this.lastPlayerTick = tick;
    }

    @Override
    public void setAndUpdateSlots(int slot)
    {
        this.markDirty();
        requiresSlotUpdate = true;

        // Update cached recipe
        cachedRecipe = HeatRecipe.get(inventory.getStackInSlot(SLOT_ITEM_INPUT));

        // If grill, update other heat recipes
        if (world.getBlockState(pos).getValue(ATTACHMENT) == BlockFirePit.FirePitAttachment.GRILL)
        {
            updateCachedGrillRecipes();
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt)
    {
        temperature = nbt.getFloat("temperature");
        burnTicks = nbt.getInteger("burnTicks");
        airTicks = nbt.getInteger("airTicks");
        burnTemperature = nbt.getFloat("burnTemperature");
        lastPlayerTick = nbt.getLong("lastPlayerTick");
        if (nbt.hasKey("leftover"))
        {
            NBTTagList surplusItems = nbt.getTagList("leftover", Constants.NBT.TAG_COMPOUND);
            for (int i = 0; i < surplusItems.tagCount(); i++)
            {
                leftover.add(new ItemStack(surplusItems.getCompoundTagAt(i)));
            }
        }

        attachedItemStack = new ItemStack(nbt.getCompoundTag("attachedItemStack"));

        BlockFirePit.FirePitAttachment attachment = BlockFirePit.FirePitAttachment.valueOf(nbt.getInteger("attachment"));
        if (attachment == BlockFirePit.FirePitAttachment.COOKING_POT)
        {
            cookingPotStage = CookingPotStage.valueOf(nbt.getInteger("cookingPotStage"));
            if (cookingPotStage == CookingPotStage.FINISHED)
            {
                soupServings = nbt.getInteger("soupServings");
                soupNutrient = Nutrient.valueOf(nbt.getInteger("soupNutrient"));
                soupContents = new FoodData();
                soupContents.deserializeNBT(nbt.getCompoundTag("soupContents"));
                soupCreationDate = nbt.getLong("soupCreationDate");
            }
            else if (cookingPotStage == CookingPotStage.BOILING)
            {
                boilingTicks = nbt.getInteger("boilingTicks");
            }
        }

        super.readFromNBT(nbt);

        // Update recipe cache
        cachedRecipe = HeatRecipe.get(inventory.getStackInSlot(SLOT_ITEM_INPUT));
        if (attachment == BlockFirePit.FirePitAttachment.GRILL)
        {
            updateCachedGrillRecipes();
        }
    }

    @Override
    @Nonnull
    public NBTTagCompound writeToNBT(NBTTagCompound nbt)
    {
        nbt.setFloat("temperature", temperature);
        nbt.setInteger("burnTicks", burnTicks);
        nbt.setFloat("burnTemperature", burnTemperature);
        nbt.setLong("lastPlayerTick", lastPlayerTick);
        if (!leftover.isEmpty())
        {
            NBTTagList surplusList = new NBTTagList();
            for (ItemStack stack : leftover)
            {
                surplusList.appendTag(stack.serializeNBT());
            }
            nbt.setTag("leftover", surplusList);
        }
        nbt.setTag("attachedItemStack", attachedItemStack.serializeNBT());

        BlockFirePit.FirePitAttachment attachment = world.getBlockState(pos).getValue(ATTACHMENT);
        nbt.setInteger("attachment", attachment.ordinal()); // to de-serialize the correct stuff
        if (attachment == BlockFirePit.FirePitAttachment.COOKING_POT)
        {
            nbt.setInteger("cookingPotStage", cookingPotStage.ordinal());
            if (cookingPotStage == CookingPotStage.BOILING)
            {
                nbt.setInteger("boilingTicks", boilingTicks);
            }
            else if (cookingPotStage == CookingPotStage.FINISHED)
            {
                nbt.setTag("soupContents", soupContents.serializeNBT());
                nbt.setInteger("soupNutrient", soupNutrient.ordinal());
                nbt.setInteger("soupServings", soupServings);
                nbt.setLong("soupCreationDate", soupCreationDate);
            }
        }
        return super.writeToNBT(nbt);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing)
    {
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
        {
            return (T) new ItemHandlerSidedWrapper(this, inventory, facing);
        }
        return super.getCapability(capability, facing);
    }

    @Override
    public void onBreakBlock(World world, BlockPos pos, IBlockState state)
    {
        if (!attachedItemStack.isEmpty())
        {
            InventoryHelper.spawnItemStack(world, pos.getX(), pos.getY(), pos.getZ(), attachedItemStack);
        }
        super.onBreakBlock(world, pos, state);
    }

    @Override
    public int getSlotLimit(int slot)
    {
        // Output slots can have anything, everything else is 1 max
        return slot == SLOT_OUTPUT_1 || slot == SLOT_OUTPUT_2 ? 64 : 1;
    }

    @Override
    public boolean isItemValid(int slot, ItemStack stack)
    {
        switch (slot)
        {
            case SLOT_FUEL_INPUT: // Valid fuel if it is registered correctly
                return FuelManager.isItemFuel(stack) && !FuelManager.isItemForgeFuel(stack);
            case SLOT_ITEM_INPUT: // Valid input as long as it can be heated
                return stack.hasCapability(CapabilityItemHeat.ITEM_HEAT_CAPABILITY, null);
            case SLOT_OUTPUT_1:
            case SLOT_OUTPUT_2: // Valid insert into output as long as it can hold fluids and is heat-able
                return stack.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null) && stack.hasCapability(CapabilityItemHeat.ITEM_HEAT_CAPABILITY, null);
            case SLOT_EXTRA_INPUT_START:
            case SLOT_EXTRA_INPUT_START + 1:
            case SLOT_EXTRA_INPUT_START + 2:
            case SLOT_EXTRA_INPUT_START + 3:
            case SLOT_EXTRA_INPUT_START + 4:
                BlockFirePit.FirePitAttachment attachment = world.getBlockState(pos).getValue(ATTACHMENT);
                if (attachment == BlockFirePit.FirePitAttachment.COOKING_POT)
                {
                    // Cooking pot inputs must be food & category of veg, cooked or uncooked meat
                    return stack.hasCapability(CapabilityFood.CAPABILITY, null) && Food.Category.doesStackMatchCategories(stack, Food.Category.FRUIT, Food.Category.VEGETABLE, Food.Category.COOKED_MEAT, Food.Category.MEAT);
                }
                else if (attachment == BlockFirePit.FirePitAttachment.GRILL)
                {
                    // Grill can only do food + heatable
                    return stack.hasCapability(CapabilityFood.CAPABILITY, null) && stack.hasCapability(CapabilityItemHeat.ITEM_HEAT_CAPABILITY, null);
                }
            default: // Other fuel slots + output slots
                return false;
        }
    }

    public void onCreate(ItemStack log)
    {
        Fuel fuel = FuelManager.getFuel(log);
        burnTicks = fuel.getAmount();
        burnTemperature = fuel.getTemperature();
    }

    public int getSoupServings()
    {
        return soupServings;
    }

    public void onConvertToCookingPot(EntityPlayer player, ItemStack stack)
    {
        dumpNonExtraItems(player);
        cookingPotStage = CookingPotStage.EMPTY;
        attachedItemStack = stack.splitStack(1);
    }

    public void onConvertToGrill(EntityPlayer player, ItemStack stack)
    {
        dumpNonExtraItems(player);
        attachedItemStack = stack.splitStack(1);
    }

    public void onRemoveAttachment(EntityPlayer player, ItemStack stack)
    {
        ItemHandlerHelper.giveItemToPlayer(player, attachedItemStack);
        attachedItemStack = ItemStack.EMPTY;
    }

    public void addWaterToCookingPot()
    {
        // Advance the stage
        cookingPotStage = CookingPotStage.WAITING;
        // And also reset the temperature
        temperature = 0;
    }

    @Nonnull
    public CookingPotStage getCookingPotStage()
    {
        return cookingPotStage;
    }

    @Override
    public int getFieldCount()
    {
        return 3;
    }

    @Override
    public void setField(int index, int value)
    {
        switch (index)
        {
            case FIELD_TEMPERATURE:
                this.temperature = (float) value;
                break;
            case FIELD_COOKING_POT_STAGE:
                this.cookingPotStage = CookingPotStage.valueOf(value);
                break;
            case FIELD_COOKING_POT_SERVINGS:
                this.soupServings = value;
                break;
            default:
                TerraFirmaCraft.getLog().warn("Invalid Field ID {} in TEFirePit#setField", index);
        }
    }

    @Override
    public int getField(int index)
    {
        switch (index)
        {
            case FIELD_TEMPERATURE:
                return (int) temperature;
            case FIELD_COOKING_POT_STAGE:
                return cookingPotStage.ordinal();
            case FIELD_COOKING_POT_SERVINGS:
                return soupServings;
            default:
                TerraFirmaCraft.getLog().warn("Invalid Field ID {} in TEFirePit#getField", index);
                return 0;
        }
    }

    public void onAirIntake(int amount)
    {
        airTicks += amount;
        if (airTicks > 600)
        {
            airTicks = 600;
        }
    }

    public void onUseBowlOnCookingPot(EntityPlayer player, ItemStack stack)
    {
        if (soupServings > 0)
        {
            soupServings--;

            ItemStack soupStack = new ItemStack(getSoupItem());
            IFood soupFood = soupStack.getCapability(CapabilityFood.CAPABILITY, null);
            if (soupFood instanceof ItemDynamicBowlFood.DynamicFoodHandler)
            {
                soupFood.setCreationDate(soupCreationDate);
                ((ItemDynamicBowlFood.DynamicFoodHandler) soupFood).initCreationDataAndBowl(stack.splitStack(1), soupContents);
            }
            ItemHandlerHelper.giveItemToPlayer(player, soupStack);
            if (soupServings == 0)
            {
                cookingPotStage = CookingPotStage.EMPTY;
            }
        }
    }

    @Override
    public boolean canInsert(int slot, ItemStack stack, EnumFacing side)
    {
        return slot == SLOT_FUEL_INPUT || (cookingPotStage != CookingPotStage.BOILING && cookingPotStage != CookingPotStage.FINISHED);
    }

    @Override
    public boolean canExtract(int slot, EnumFacing side)
    {
        return slot == SLOT_FUEL_INPUT || (cookingPotStage != CookingPotStage.BOILING && cookingPotStage != CookingPotStage.FINISHED);
    }

    private void updateCachedGrillRecipes()
    {
        for (int i = SLOT_EXTRA_INPUT_START; i <= SLOT_EXTRA_INPUT_END; i++)
        {
            cachedGrillRecipes[i - SLOT_EXTRA_INPUT_START] = HeatRecipe.get(inventory.getStackInSlot(i));
        }
    }

    private void dumpNonExtraItems(EntityPlayer player)
    {
        for (int i = SLOT_ITEM_INPUT; i < SLOT_OUTPUT_2; i++)
        {
            ItemStack stack = inventory.getStackInSlot(i);
            if (!stack.isEmpty())
            {
                ItemHandlerHelper.giveItemToPlayer(player, stack);
            }
            inventory.setStackInSlot(i, ItemStack.EMPTY);
        }
    }

    private void cascadeFuelSlots()
    {
        // This will cascade all fuel down to the lowest available slot
        int lowestAvailSlot = 0;
        for (int i = 0; i < 4; i++)
        {
            ItemStack stack = inventory.getStackInSlot(i);
            if (!stack.isEmpty())
            {
                // Move to lowest avail slot
                if (i > lowestAvailSlot)
                {
                    inventory.setStackInSlot(lowestAvailSlot, stack.copy());
                    inventory.setStackInSlot(i, ItemStack.EMPTY);
                }
                lowestAvailSlot++;
            }
        }
        requiresSlotUpdate = false;
    }

    private void handleInputMelting(ItemStack stack)
    {
        IItemHeat cap = stack.getCapability(CapabilityItemHeat.ITEM_HEAT_CAPABILITY, null);

        if (cachedRecipe != null && cap != null && cachedRecipe.isValidTemperature(cap.getTemperature()))
        {
            HeatRecipe recipe = cachedRecipe; // Avoids NPE on slot changes
            // Handle possible metal output
            FluidStack fluidStack = recipe.getOutputFluid(stack);
            float itemTemperature = cap.getTemperature();
            if (fluidStack != null)
            {
                ItemStack output = inventory.getStackInSlot(SLOT_OUTPUT_1);
                IFluidHandler fluidHandler = output.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null);
                if (fluidHandler != null)
                {
                    int amountFilled = fluidHandler.fill(fluidStack.copy(), true);
                    fluidStack.amount -= amountFilled;

                    // If the fluid was filled, make sure to make it the same temperature
                    IItemHeat heatHandler = output.getCapability(CapabilityItemHeat.ITEM_HEAT_CAPABILITY, null);
                    if (heatHandler != null)
                    {
                        heatHandler.setTemperature(itemTemperature);
                    }
                }
                if (fluidStack.amount > 0)
                {
                    output = inventory.getStackInSlot(SLOT_OUTPUT_2);
                    fluidHandler = output.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null);

                    if (fluidHandler != null)
                    {
                        int amountFilled = fluidHandler.fill(fluidStack, true);

                        if (amountFilled > 0)
                        {
                            // If the fluid was filled, make sure to make it the same temperature
                            IItemHeat heatHandler = output.getCapability(CapabilityItemHeat.ITEM_HEAT_CAPABILITY, null);
                            if (heatHandler != null)
                            {
                                heatHandler.setTemperature(itemTemperature);
                            }
                        }
                    }
                }
            }

            // Handle removal of input
            ItemStack inputStack = inventory.getStackInSlot(SLOT_ITEM_INPUT);
            ItemStack outputStack = recipe.getOutputStack(inputStack);

            inputStack.shrink(1);
            if (!outputStack.isEmpty())
            {
                outputStack = mergeOutputStack(outputStack);
                if (!outputStack.isEmpty())
                {
                    leftover.add(outputStack);
                }
            }
        }
    }

    private void handleGrillCooking(int slot, ItemStack stack, IItemHeat heat)
    {
        HeatRecipe recipe = cachedGrillRecipes[slot - SLOT_EXTRA_INPUT_START];
        if (recipe != null && recipe.isValidTemperature(heat.getTemperature()))
        {
            ItemStack output = recipe.getOutputStack(stack);
            CapabilityFood.applyTrait(output, FoodTrait.WOOD_GRILLED);
            inventory.setStackInSlot(slot, output);
            markForSync();
        }
    }

    /**
     * Merges a stack into the two output slots
     *
     * @param outputStack the stack to merge.
     * @return the leftover outputStack that wasn't merged
     */
    private ItemStack mergeOutputStack(ItemStack outputStack)
    {
        outputStack = inventory.insertItem(SLOT_OUTPUT_1, outputStack, false);
        inventory.setStackInSlot(SLOT_OUTPUT_1, CapabilityFood.mergeItemStacksIgnoreCreationDate(inventory.getStackInSlot(SLOT_OUTPUT_1), outputStack));
        outputStack = inventory.insertItem(SLOT_OUTPUT_2, outputStack, false);
        inventory.setStackInSlot(SLOT_OUTPUT_2, CapabilityFood.mergeItemStacksIgnoreCreationDate(inventory.getStackInSlot(SLOT_OUTPUT_2), outputStack));
        return outputStack;
    }

    private Item getSoupItem()
    {
        switch (soupNutrient)
        {
            case GRAIN:
                return ItemFoodTFC.get(Food.SOUP_GRAIN);
            case VEGETABLES:
                return ItemFoodTFC.get(Food.SOUP_VEGETABLE);
            case FRUIT:
                return ItemFoodTFC.get(Food.SOUP_FRUIT);
            case PROTEIN:
                return ItemFoodTFC.get(Food.SOUP_MEAT);
            default:
                return ItemFoodTFC.get(Food.SOUP_DAIRY);
        }
    }

    public enum CookingPotStage
    {
        EMPTY, WAITING, BOILING, FINISHED;

        private static final CookingPotStage[] VALUES = values();

        public static CookingPotStage valueOf(int id)
        {
            return id >= 0 && id < VALUES.length ? VALUES[id] : EMPTY;
        }
    }
}
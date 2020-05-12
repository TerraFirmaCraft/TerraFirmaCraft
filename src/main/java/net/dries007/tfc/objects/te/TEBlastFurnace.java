/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.te;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import com.google.common.collect.ImmutableList;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fluids.FluidStack;

import net.dries007.tfc.ConfigTFC;
import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.api.capability.heat.CapabilityItemHeat;
import net.dries007.tfc.api.capability.heat.IItemHeat;
import net.dries007.tfc.api.capability.metal.CapabilityMetalItem;
import net.dries007.tfc.api.capability.metal.IMetalItem;
import net.dries007.tfc.api.recipes.BlastFurnaceRecipe;
import net.dries007.tfc.api.util.IHeatConsumerBlock;
import net.dries007.tfc.objects.blocks.BlockMolten;
import net.dries007.tfc.objects.blocks.BlocksTFC;
import net.dries007.tfc.objects.blocks.devices.BlockBlastFurnace;
import net.dries007.tfc.util.Alloy;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.OreDictionaryHelper;
import net.dries007.tfc.util.fuel.Fuel;
import net.dries007.tfc.util.fuel.FuelManager;

import static net.dries007.tfc.objects.blocks.property.ILightableBlock.LIT;

@ParametersAreNonnullByDefault
public class TEBlastFurnace extends TETickableInventory implements ITickable, ITileFields
{
    public static final int SLOT_TUYERE = 0;
    public static final int FIELD_TEMPERATURE = 0, FIELD_ORE = 1, FIELD_FUEL = 2, FIELD_MELT = 3, FIELD_ORE_UNITS = 4, CHIMNEY_LEVELS = 5;

    private final List<ItemStack> oreStacks = new ArrayList<>();
    private final List<ItemStack> fuelStacks = new ArrayList<>();
    private final Alloy alloy;
    private int maxFuel = 0, maxOre = 0, delayTimer = 0, meltAmount = 0, chimney = 0;
    private long burnTicksLeft = 0, airTicks = 0;
    private int fuelCount = 0, oreCount = 0, oreUnits; // Used to show on client's GUI how much ore/fuel TE has
    private int temperature = 0;
    private float burnTemperature = 0;

    public TEBlastFurnace()
    {
        super(1);
        // Blast furnaces hold the same amount of crucibles, should it matter to be different?
        this.alloy = new Alloy(ConfigTFC.Devices.CRUCIBLE.tank);
    }

    @Override
    public int getSlotLimit(int slot)
    {
        return 1;
    }

    @Override
    public boolean isItemValid(int slot, ItemStack stack)
    {
        return OreDictionaryHelper.doesStackMatchOre(stack, "tuyere");
    }

    @SuppressWarnings("unused")
    public long getBurnTicksLeft()
    {
        return burnTicksLeft;
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt)
    {
        oreStacks.clear();
        NBTTagList ores = nbt.getTagList("ores", Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < ores.tagCount(); i++)
        {
            oreStacks.add(new ItemStack(ores.getCompoundTagAt(i)));
        }

        fuelStacks.clear();
        NBTTagList fuels = nbt.getTagList("fuels", Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < fuels.tagCount(); i++)
        {
            fuelStacks.add(new ItemStack(fuels.getCompoundTagAt(i)));
        }
        burnTicksLeft = nbt.getLong("burnTicksLeft");
        airTicks = nbt.getLong("airTicks");
        burnTemperature = nbt.getFloat("burnTemperature");
        temperature = nbt.getInteger("temperature");
        alloy.deserializeNBT(nbt.getCompoundTag("alloy"));
        super.readFromNBT(nbt);
    }

    @Override
    @Nonnull
    public NBTTagCompound writeToNBT(NBTTagCompound nbt)
    {
        NBTTagList ores = new NBTTagList();
        for (ItemStack stack : oreStacks)
        {
            ores.appendTag(stack.serializeNBT());
        }
        nbt.setTag("ores", ores);
        NBTTagList fuels = new NBTTagList();
        for (ItemStack stack : fuelStacks)
        {
            fuels.appendTag(stack.serializeNBT());
        }
        nbt.setTag("fuels", fuels);
        nbt.setLong("burnTicksLeft", burnTicksLeft);
        nbt.setLong("airTicks", airTicks);
        nbt.setFloat("burnTemperature", burnTemperature);
        nbt.setInteger("temperature", temperature);
        nbt.setTag("alloy", alloy.serializeNBT());
        return super.writeToNBT(nbt);
    }

    @Override
    public void onBreakBlock(World worldIn, BlockPos pos, IBlockState state)
    {
        // Dump everything in world
        for (int i = 1; i < 6; i++)
        {
            if (world.getBlockState(pos.up(i)).getBlock() == BlocksTFC.MOLTEN)
            {
                world.setBlockToAir(pos.up(i));
            }
        }
        for (ItemStack stack : oreStacks)
        {
            InventoryHelper.spawnItemStack(world, pos.north().getX(), pos.getY(), pos.north().getZ(), stack);
        }
        for (ItemStack stack : fuelStacks)
        {
            InventoryHelper.spawnItemStack(world, pos.north().getX(), pos.getY(), pos.north().getZ(), stack);
        }
        super.onBreakBlock(world, pos, state);
    }

    public ImmutableList<ItemStack> getFuelStacks()
    {
        return ImmutableList.copyOf(fuelStacks);
    }

    public ImmutableList<ItemStack> getOreStacks()
    {
        return ImmutableList.copyOf(oreStacks);
    }

    public boolean canIgnite()
    {
        if (!world.isRemote)
        {
            return !fuelStacks.isEmpty() && !oreStacks.isEmpty();
        }
        return false;
    }

    @Override
    public int getFieldCount()
    {
        return 6;
    }

    @Override
    public void setField(int index, int value)
    {
        switch (index)
        {
            case FIELD_TEMPERATURE:
                temperature = value;
                return;
            case FIELD_ORE:
                oreCount = value;
                return;
            case FIELD_FUEL:
                fuelCount = value;
                return;
            case FIELD_MELT:
                meltAmount = value;
                return;
            case FIELD_ORE_UNITS:
                oreUnits = value;
                return;
            case CHIMNEY_LEVELS:
                chimney = value;
                return;
        }
        TerraFirmaCraft.getLog().warn("Illegal field id {} in TEBlastFurnace#setField", index);
    }

    @Override
    public int getField(int index)
    {
        switch (index)
        {
            case FIELD_TEMPERATURE:
                return temperature;
            case FIELD_ORE:
                return oreCount;
            case FIELD_FUEL:
                return fuelCount;
            case FIELD_MELT:
                return meltAmount;
            case FIELD_ORE_UNITS:
                return oreUnits;
            case CHIMNEY_LEVELS:
                return chimney;
        }
        TerraFirmaCraft.getLog().warn("Illegal field id {} in TEBlastFurnace#getField", index);
        return 0;
    }

    @Override
    public void update()
    {
        super.update();
        if (!world.isRemote)
        {
            IBlockState state = world.getBlockState(pos);
            if (state.getValue(LIT))
            {
                // Update bellows air
                if (--airTicks <= 0)
                {
                    airTicks = 0;
                }

                if (--burnTicksLeft <= 0)
                {
                    if (!fuelStacks.isEmpty())
                    {
                        ItemStack fuelStack = fuelStacks.get(0);
                        fuelStacks.remove(0);
                        Fuel fuel = FuelManager.getFuel(fuelStack);
                        burnTicksLeft = (int) (Math.ceil(fuel.getAmount() / ConfigTFC.Devices.BLAST_FURNACE.consumption));
                        burnTemperature = fuel.getTemperature();
                    }
                    else
                    {
                        burnTemperature = 0;
                    }
                    markForSync();
                }

                if (temperature > 0 || burnTemperature > 0)
                {
                    float targetTemperature = burnTemperature + airTicks;
                    if (temperature < targetTemperature)
                    {
                        // Modifier for heating = 2x for bellows
                        temperature += (airTicks > 0 ? 2 : 1) * ConfigTFC.Devices.TEMPERATURE.heatingModifier;
                    }
                    else if (temperature > targetTemperature)
                    {
                        // Modifier for cooling = 0.5x for bellows
                        temperature -= (airTicks > 0 ? 0.5 : 1) * ConfigTFC.Devices.TEMPERATURE.heatingModifier;
                    }
                    // Provide heat to blocks that are one block bellow AKA crucible
                    Block blockCrucible = world.getBlockState(pos.down()).getBlock();
                    if (blockCrucible instanceof IHeatConsumerBlock)
                    {
                        ((IHeatConsumerBlock) blockCrucible).acceptHeat(world, pos.down(), temperature);
                    }
                    if (!world.isRemote)
                    {
                        oreStacks.removeIf(stack ->
                        {
                            IItemHeat cap = stack.getCapability(CapabilityItemHeat.ITEM_HEAT_CAPABILITY, null);
                            if (cap != null)
                            {
                                // Update temperature of item
                                float itemTemp = cap.getTemperature();
                                if (temperature > itemTemp)
                                {
                                    CapabilityItemHeat.addTemp(cap);
                                }
                                if (cap.isMolten())
                                {
                                    convertToMolten(stack);
                                    ItemStack tuyereStack = inventory.getStackInSlot(0);
                                    if (!tuyereStack.isEmpty())
                                    {
                                        Helpers.damageItem(tuyereStack);
                                    }
                                    return true;
                                }
                            }
                            return false;
                        });
                    }
                    if (temperature <= 0 && burnTemperature <= 0)
                    {
                        temperature = 0;
                        world.setBlockState(pos, state.withProperty(LIT, false));
                    }
                }
            }

            meltAmount = alloy.getAmount(); //update for client GUI
            if (--delayTimer <= 0)
            {
                delayTimer = 20;
                // Update multiblock status

                // Detect client changes
                int oldChimney = chimney;
                int oldOre = oreCount;
                int oldFuel = fuelCount;

                chimney = BlockBlastFurnace.getChimneyLevels(world, pos);
                int newMaxItems = chimney * 4;
                maxFuel = newMaxItems;
                maxOre = newMaxItems;
                while (maxOre < oreStacks.size())
                {
                    //Structure lost one or more chimney levels
                    InventoryHelper.spawnItemStack(world, pos.north().getX(), pos.getY(), pos.north().getZ(), oreStacks.get(0));
                    oreStacks.remove(0);
                }
                while (maxFuel < fuelStacks.size())
                {
                    InventoryHelper.spawnItemStack(world, pos.north().getX(), pos.north().getY(), pos.north().getZ(), fuelStacks.get(0));
                    fuelStacks.remove(0);
                }
                addItemsFromWorld();
                updateSlagBlock(state.getValue(LIT));

                oreCount = oreStacks.size();
                oreUnits = oreStacks.stream().mapToInt(stack -> {
                    IMetalItem metalObject = CapabilityMetalItem.getMetalItem(stack);
                    if (metalObject != null)
                    {
                        return metalObject.getSmeltAmount(stack);
                    }
                    return 1;
                }).sum();
                fuelCount = fuelStacks.size();

                if (oldChimney != chimney || oldOre != oreCount || oldFuel != fuelCount)
                {
                    markForSync();
                }
            }
            if (alloy.removeAlloy(1, true) > 0)
            {
                // Move already molten liquid metal to the crucible.
                // This makes the effect of slowly filling up the crucible.
                // Take into account full or non-existent (removed) crucibles
                TECrucible te = Helpers.getTE(world, pos.down(), TECrucible.class);
                if (te != null && te.addMetal(alloy.getResult(), 1) <= 0)
                {
                    alloy.removeAlloy(1, false);
                }
            }
        }
    }


    public void debug()
    {
        TerraFirmaCraft.getLog().debug("Debugging Blast Furnace:");
        TerraFirmaCraft.getLog().debug("Temp {} | Burn Temp {} | Fuel Ticks {}", temperature, burnTemperature, burnTicksLeft);
        TerraFirmaCraft.getLog().debug("Burning? {}", world.getBlockState(pos).getValue(LIT));
        int i = 0;
        for (ItemStack item : oreStacks)
        {
            TerraFirmaCraft.getLog().debug("Slot: {} - NBT: {}", i, item.serializeNBT().toString());
        }
    }

    /**
     * Passed from BlockBlastFurnace's IBellowsConsumerBlock
     *
     * @param airAmount the air amount
     */
    public void onAirIntake(int airAmount)
    {
        ItemStack stack = inventory.getStackInSlot(SLOT_TUYERE);
        if (!stack.isEmpty() && burnTicksLeft > 0)
        {
            airTicks += airAmount;
            if (airTicks > 600)
            {
                airTicks = 600;
            }
        }
    }

    /**
     * Melts stacks
     */
    private void convertToMolten(ItemStack stack)
    {
        BlastFurnaceRecipe recipe = BlastFurnaceRecipe.get(stack);
        if (recipe != null)
        {
            FluidStack output = recipe.getOutput(stack);
            if (output != null)
            {
                alloy.add(output);
            }
        }
    }

    private void addItemsFromWorld()
    {
        EntityItem fluxEntity = null, oreEntity = null;
        List<EntityItem> items = world.getEntitiesWithinAABB(EntityItem.class, new AxisAlignedBB(pos.up(), pos.up().add(1, 5, 1)), EntitySelectors.IS_ALIVE);
        for (EntityItem entityItem : items)
        {
            ItemStack stack = entityItem.getItem();
            BlastFurnaceRecipe recipe = BlastFurnaceRecipe.get(stack);
            if (recipe != null)
            {
                oreEntity = entityItem;
                // Try searching for the additive (flux for pig iron)
                for (EntityItem item : items)
                {
                    if (recipe.isValidAdditive(item.getItem()))
                    {
                        fluxEntity = item;
                        break;
                    }
                }
                if (fluxEntity != null)
                {
                    // We have both additives + ores for the found recipe
                    break;
                }
                else
                {
                    // Didn't found the correct additive, abort adding the ore to the input
                    oreEntity = null;
                }
            }
            if (FuelManager.isItemBloomeryFuel(stack))
            {
                // Add fuel
                while (maxFuel > fuelStacks.size())
                {
                    markDirty();
                    fuelStacks.add(stack.splitStack(1));
                    if (stack.getCount() <= 0)
                    {
                        entityItem.setDead();
                        break;
                    }
                }
            }
        }

        // Add each ore consuming flux
        while (maxOre > oreStacks.size())
        {
            if (fluxEntity == null || oreEntity == null)
            {
                break;
            }
            markDirty();

            ItemStack flux = fluxEntity.getItem();
            flux.shrink(1);

            ItemStack ore = oreEntity.getItem();
            oreStacks.add(ore.splitStack(1));

            if (flux.getCount() <= 0)
            {
                fluxEntity.setDead();
                fluxEntity = null;
            }

            if (ore.getCount() <= 0)
            {
                oreEntity.setDead();
                oreEntity = null;
            }
        }
    }

    private void updateSlagBlock(boolean cooking)
    {
        int slag = fuelStacks.size() + oreStacks.size();
        //If there's at least one item, show one layer so player knows that it is holding stacks
        int slagLayers = slag == 1 ? 1 : slag / 2;
        for (int i = 1; i < 6; i++)
        {
            if (slagLayers > 0)
            {
                if (slagLayers >= 4)
                {
                    slagLayers -= 4;
                    world.setBlockState(pos.up(i), BlocksTFC.MOLTEN.getDefaultState().withProperty(LIT, cooking).withProperty(BlockMolten.LAYERS, 4));
                }
                else
                {
                    world.setBlockState(pos.up(i), BlocksTFC.MOLTEN.getDefaultState().withProperty(LIT, cooking).withProperty(BlockMolten.LAYERS, slagLayers));
                    slagLayers = 0;
                }
            }
            else
            {
                //Remove any surplus slag(ie: after cooking/structure became compromised)
                if (world.getBlockState(pos.up(i)).getBlock() == BlocksTFC.MOLTEN)
                {
                    world.setBlockToAir(pos.up(i));
                }
            }
        }
    }
}

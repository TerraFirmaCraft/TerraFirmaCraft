/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.te;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

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

import net.dries007.tfc.ConfigTFC;
import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.api.capability.heat.CapabilityItemHeat;
import net.dries007.tfc.api.capability.heat.IItemHeat;
import net.dries007.tfc.api.types.Metal;
import net.dries007.tfc.api.util.IHeatConsumerBlock;
import net.dries007.tfc.api.util.IMetalObject;
import net.dries007.tfc.objects.blocks.BlockMolten;
import net.dries007.tfc.objects.blocks.BlocksTFC;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.OreDictionaryHelper;
import net.dries007.tfc.util.fuel.Fuel;
import net.dries007.tfc.util.fuel.FuelManager;

import static net.dries007.tfc.api.capability.heat.CapabilityItemHeat.MAX_TEMPERATURE;
import static net.dries007.tfc.objects.blocks.property.ILightableBlock.LIT;

@ParametersAreNonnullByDefault
public class TEBlastFurnace extends TEInventory implements ITickable, ITileFields
{
    public static final int SLOT_TUYERE = 0;
    public static final int FIELD_TEMPERATURE = 0, FIELD_ORE = 1, FIELD_FUEL = 2, FIELD_MELT = 3, FIELD_ORE_UNITS = 4;

    private List<ItemStack> oreStacks = new ArrayList<>();
    private List<ItemStack> fuelStacks = new ArrayList<>();

    private int maxFuel = 0, maxOre = 0, delayTimer = 0, meltAmount = 0;
    private long burnTicksLeft = 0, airTicks = 0;
    private int fuelCount = 0, oreCount = 0, oreUnits; // Used to show on client's GUI how much ore/fuel TE has

    private int temperature = 0;
    private float burnTemperature = 0;

    public TEBlastFurnace()
    {
        super(1);
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
        for (int i = 0; i < ores.tagCount(); i++)
        {
            fuelStacks.add(new ItemStack(fuels.getCompoundTagAt(i)));
        }
        burnTicksLeft = nbt.getLong("burnTicksLeft");
        airTicks = nbt.getLong("airTicks");
        burnTemperature = nbt.getFloat("burnTemperature");
        temperature = nbt.getInteger("temperature");
        meltAmount = nbt.getInteger("meltAmount");
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
        nbt.setTag("fuels", ores);
        nbt.setLong("burnTicksLeft", burnTicksLeft);
        nbt.setLong("airTicks", airTicks);
        nbt.setFloat("burnTemperature", burnTemperature);
        nbt.setInteger("temperature", temperature);
        nbt.setInteger("meltAmount", meltAmount);
        return super.writeToNBT(nbt);
    }

    @Override
    public void onBreakBlock(World worldIn, BlockPos pos)
    {
        //Dump everything in world
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
        super.onBreakBlock(world, pos);
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
        return 5;
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
        }
        TerraFirmaCraft.getLog().warn("Illegal field id {} in TEBlastFurnace#getField", index);
        return 0;
    }

    @Override
    public void update()
    {
        if (!world.isRemote)
        {
            IBlockState state = world.getBlockState(pos);

            if (--delayTimer <= 0)
            {
                delayTimer = 20;
                // Update multiblock status
                int newMaxItems = BlocksTFC.BLAST_FURNACE.getChimneyLevels(world, pos) * 4;
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
                if (newMaxItems <= 0)
                {
                    //Structure became compromised
                    world.destroyBlock(pos, true);
                    return;
                }
                addItemsFromWorld();
                updateSlagBlock(state.getValue(LIT));

                oreCount = oreStacks.size();
                oreUnits = oreStacks.stream().mapToInt(stack -> {
                    if (stack.getItem() instanceof IMetalObject)
                    {
                        return ((IMetalObject) stack.getItem()).getSmeltAmount(stack);
                    }
                    return 1;
                }).sum();
                fuelCount = fuelStacks.size();
            }
            if (meltAmount > 0)
            {
                //Move already molten liquid metal to the crucible.
                //This makes the effect of slow(not so much) filling up the crucible.
                TECrucible te = Helpers.getTE(world, pos.down(), TECrucible.class);
                if (te != null)
                {
                    te.addMetal(Metal.PIG_IRON, 1);
                    meltAmount -= 1;
                }
            }
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
                        burnTicksLeft = fuel.getAmount();
                        burnTemperature = fuel.getTemperature();
                        this.markDirty();
                    }
                    else
                    {
                        burnTemperature = 0;
                    }
                }

                if (temperature > 0 || burnTemperature > 0)
                {
                    float targetTemperature = Math.min(MAX_TEMPERATURE, burnTemperature + airTicks);
                    if (temperature < targetTemperature)
                    {
                        // Modifier for heating = 2x for bellows
                        temperature += (airTicks > 0 ? 2 : 1) * ConfigTFC.GENERAL.temperatureModifierHeating;
                    }
                    else if (temperature > targetTemperature)
                    {
                        // Modifier for cooling = 0.5x for bellows
                        temperature -= (airTicks > 0 ? 0.5 : 1) * ConfigTFC.GENERAL.temperatureModifierHeating;
                    }
                    // Provide heat to blocks that are one block bellow AKA crucible
                    Block blockCrucible = world.getBlockState(pos.down()).getBlock();
                    if (blockCrucible instanceof IHeatConsumerBlock)
                    {
                        ((IHeatConsumerBlock) blockCrucible).acceptHeat(world, pos.down(), temperature);
                    }
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
        if (!stack.isEmpty() && stack.getItem() instanceof IMetalObject)
        {
            IMetalObject metal = (IMetalObject) stack.getItem();
            meltAmount += metal.getSmeltAmount(stack);
        }
    }

    private void addItemsFromWorld()
    {
        EntityItem fluxEntity = null, oreEntity = null;
        for (EntityItem entityItem : world.getEntitiesWithinAABB(EntityItem.class, new AxisAlignedBB(pos.up(), pos.up().add(1, 5, 1)), EntitySelectors.IS_ALIVE))
        {
            ItemStack stack = entityItem.getItem();
            if (FuelManager.isItemFuel(stack))
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
            else if (stack.getItem() instanceof IMetalObject)
            {
                IMetalObject metalItem = (IMetalObject) stack.getItem();
                Metal metal = metalItem.getMetal(stack);
                if (metal == Metal.WROUGHT_IRON || metal == Metal.PIG_IRON)
                {
                    oreEntity = entityItem;
                }
            }
            else if (OreDictionaryHelper.doesStackMatchOre(stack, "dustFlux"))
            {
                fluxEntity = entityItem;
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

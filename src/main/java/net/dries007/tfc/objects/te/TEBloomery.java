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
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;

import net.dries007.tfc.ConfigTFC;
import net.dries007.tfc.api.recipes.BloomeryRecipe;
import net.dries007.tfc.objects.blocks.BlockCharcoalPile;
import net.dries007.tfc.objects.blocks.BlockMolten;
import net.dries007.tfc.objects.blocks.BlocksTFC;
import net.dries007.tfc.objects.blocks.devices.BlockBloomery;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.calendar.CalendarTFC;

import static net.dries007.tfc.objects.blocks.property.ILightableBlock.LIT;
import static net.minecraft.block.BlockHorizontal.FACING;

@SuppressWarnings("WeakerAccess")
@ParametersAreNonnullByDefault
public class TEBloomery extends TETickableInventory implements ITickable
{
    // Gets the internal block, should be charcoal pile/bloom
    private static final Vec3i OFFSET_INTERNAL = new Vec3i(1, 0, 0);
    // Gets the external block, the front of the facing to dump contents in world.
    private static final Vec3i OFFSET_EXTERNAL = new Vec3i(-1, 0, 0);
    protected final List<ItemStack> oreStacks = new ArrayList<>();
    protected final List<ItemStack> fuelStacks = new ArrayList<>();

    protected int maxFuel = 0, maxOre = 0; // Helper variables, not necessary to serialize
    protected long litTick; // Tick that started the process

    protected BlockPos internalBlock = null, externalBlock = null;
    protected BloomeryRecipe cachedRecipe = null;

    public TEBloomery()
    {
        super(0);
    }

    @Override
    public void readFromNBT(NBTTagCompound tag)
    {
        oreStacks.clear();
        NBTTagList ores = tag.getTagList("ores", Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < ores.tagCount(); i++)
        {
            oreStacks.add(new ItemStack(ores.getCompoundTagAt(i)));
        }

        fuelStacks.clear();
        NBTTagList fuels = tag.getTagList("fuels", Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < fuels.tagCount(); i++)
        {
            fuelStacks.add(new ItemStack(fuels.getCompoundTagAt(i)));
        }
        litTick = tag.getLong("litTick");
        super.readFromNBT(tag);
    }

    @Override
    @Nonnull
    public NBTTagCompound writeToNBT(NBTTagCompound tag)
    {
        NBTTagList ores = new NBTTagList();
        for (ItemStack stack : oreStacks)
        {
            ores.appendTag(stack.serializeNBT());
        }
        tag.setTag("ores", ores);
        NBTTagList fuels = new NBTTagList();
        for (ItemStack stack : fuelStacks)
        {
            fuels.appendTag(stack.serializeNBT());
        }
        tag.setTag("fuels", fuels);
        tag.setLong("litTick", litTick);
        return super.writeToNBT(tag);
    }

    @Override
    public void onBreakBlock(World worldIn, BlockPos pos, IBlockState state)
    {
        dumpItems();
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

    @Override
    public void update()
    {
        super.update();
        if (!world.isRemote && world.getTotalWorldTime() % 20 == 0)
        {
            IBlockState state = world.getBlockState(pos);
            if (state.getValue(LIT))
            {
                if (this.getRemainingTicks() <= 0)
                {
                    if (cachedRecipe == null && !oreStacks.isEmpty())
                    {
                        cachedRecipe = BloomeryRecipe.get(oreStacks.get(0));
                        if (cachedRecipe == null)
                        {
                            dumpItems();
                        }
                    }
                    if (cachedRecipe != null)
                    {
                        world.setBlockState(getInternalBlock(), BlocksTFC.BLOOM.getDefaultState());
                        TEBloom te = Helpers.getTE(world, getInternalBlock(), TEBloom.class);
                        if (te != null)
                        {
                            te.setBloom(cachedRecipe.getOutput(oreStacks));
                        }
                    }

                    oreStacks.clear();
                    fuelStacks.clear();
                    cachedRecipe = null; // Clear recipe

                    updateSlagBlock(false);
                    state = state.withProperty(LIT, false);
                    world.setBlockState(pos, state);
                    markDirty();
                }
            }

            // Update multiblock status
            int newMaxItems = BlockBloomery.getChimneyLevels(world, getInternalBlock()) * 8;
            EnumFacing direction = world.getBlockState(pos).getValue(FACING);
            if (!BlocksTFC.BLOOMERY.isFormed(world, getInternalBlock(), direction))
            {
                newMaxItems = 0;
            }

            maxFuel = newMaxItems;
            maxOre = newMaxItems;
            boolean turnOff = false;
            while (maxOre < oreStacks.size())
            {
                turnOff = true;
                // Structure lost one or more chimney levels
                InventoryHelper.spawnItemStack(world, getExternalBlock().getX(), getExternalBlock().getY(), getExternalBlock().getZ(), oreStacks.get(0));
                oreStacks.remove(0);
                markForSync();
            }
            while (maxFuel < fuelStacks.size())
            {
                turnOff = true;
                InventoryHelper.spawnItemStack(world, getExternalBlock().getX(), getExternalBlock().getY(), getExternalBlock().getZ(), fuelStacks.get(0));
                fuelStacks.remove(0);
                markForSync();
            }
            // Structure became compromised, unlit if needed
            if (turnOff && state.getValue(LIT))
            {
                state = state.withProperty(LIT, false);
                world.setBlockState(pos, state);
            }
            if (!BlocksTFC.BLOOMERY.canGateStayInPlace(world, pos, direction.getAxis()))
            {
                // Bloomery gate (the front facing) structure became compromised
                world.destroyBlock(pos, true);
                return;
            }
            if (!isInternalBlockComplete() && !fuelStacks.isEmpty())
            {
                dumpItems();
            }

            if (isInternalBlockComplete())
            {
                int oldFuel = fuelStacks.size();
                int oldOre = oreStacks.size();
                addItemsFromWorld();
                if (oldFuel != fuelStacks.size() || oldOre != oreStacks.size())
                {
                    markForSync();
                }
            }
            updateSlagBlock(state.getValue(LIT));
        }
    }

    @Override
    public void onLoad()
    {
        // This caches the bloomery block as otherwise it can be null when broken
        getExternalBlock();
    }

    public long getRemainingTicks()
    {
        return ConfigTFC.Devices.BLOOMERY.ticks - (CalendarTFC.PLAYER_TIME.getTicks() - litTick);
    }

    public boolean canIgnite()
    {
        if (world.isRemote) return false;
        if (this.fuelStacks.size() < this.oreStacks.size() || this.oreStacks.isEmpty())
        {
            return false;
        }
        return isInternalBlockComplete();
    }

    public void onIgnite()
    {
        this.litTick = CalendarTFC.PLAYER_TIME.getTicks();
    }

    /**
     * Gets the internal (charcoal pile / bloom) position
     *
     * @return BlockPos of the internal block
     */
    public BlockPos getInternalBlock()
    {
        if (internalBlock == null)
        {
            EnumFacing direction = world.getBlockState(pos).getValue(FACING);
            internalBlock = pos.up(OFFSET_INTERNAL.getY())
                .offset(direction, OFFSET_INTERNAL.getX())
                .offset(direction.rotateY(), OFFSET_INTERNAL.getZ());
        }
        return internalBlock;
    }

    /**
     * Gets the external (front facing) position
     *
     * @return BlockPos to dump items in world
     */
    public BlockPos getExternalBlock()
    {
        if (externalBlock == null)
        {
            EnumFacing direction = world.getBlockState(pos).getValue(FACING);
            externalBlock = pos.up(OFFSET_EXTERNAL.getY())
                .offset(direction, OFFSET_EXTERNAL.getX())
                .offset(direction.rotateY(), OFFSET_EXTERNAL.getZ());
        }
        return externalBlock;
    }

    protected void dumpItems()
    {
        //Dump everything in world
        for (int i = 1; i < 4; i++)
        {
            if (world.getBlockState(getInternalBlock().up(i)).getBlock() == BlocksTFC.MOLTEN)
            {
                world.setBlockToAir(getInternalBlock().up(i));
            }
        }
        oreStacks.forEach(i -> InventoryHelper.spawnItemStack(world, getExternalBlock().getX(), getExternalBlock().getY(), getExternalBlock().getZ(), i));
        fuelStacks.forEach(i -> InventoryHelper.spawnItemStack(world, getExternalBlock().getX(), getExternalBlock().getY(), getExternalBlock().getZ(), i));
    }

    protected boolean isInternalBlockComplete()
    {
        IBlockState inside = world.getBlockState(getInternalBlock());
        return inside.getBlock() == BlocksTFC.CHARCOAL_PILE && inside.getValue(BlockCharcoalPile.LAYERS) >= 8;
    }

    protected void addItemsFromWorld()
    {
        if (cachedRecipe == null && !oreStacks.isEmpty())
        {
            cachedRecipe = BloomeryRecipe.get(oreStacks.get(0));
            if (cachedRecipe == null)
            {
                this.dumpItems();
            }
        }
        for (EntityItem entityItem : world.getEntitiesWithinAABB(EntityItem.class, new AxisAlignedBB(getInternalBlock().up(), getInternalBlock().add(1, 4, 1)), EntitySelectors.IS_ALIVE))
        {
            ItemStack stack = entityItem.getItem();
            if (cachedRecipe == null)
            {
                cachedRecipe = BloomeryRecipe.get(stack);
            }
            if (cachedRecipe != null)
            {
                if (cachedRecipe.isValidInput(stack))
                {
                    if (oreStacks.size() < maxOre)
                    {
                        markDirty();
                    }
                    while (oreStacks.size() < maxOre)
                    {
                        oreStacks.add(stack.splitStack(1));
                        if (stack.getCount() <= 0)
                        {
                            entityItem.setDead();
                            break;
                        }
                    }
                }
                else if (cachedRecipe.isValidAdditive(stack))
                {
                    if (fuelStacks.size() < maxFuel)
                    {
                        markDirty();
                    }
                    while (fuelStacks.size() < maxFuel)
                    {
                        fuelStacks.add(stack.splitStack(1));
                        if (stack.getCount() <= 0)
                        {
                            entityItem.setDead();
                            break;
                        }
                    }
                }
            }
        }
    }

    protected void updateSlagBlock(boolean cooking)
    {
        int slag = fuelStacks.size() + oreStacks.size();
        //If there's at least one item, show one layer so player knows that it is holding stacks
        int slagLayers = slag > 0 && slag < 4 ? 1 : slag / 4;
        for (int i = 1; i < 4; i++)
        {
            if (slagLayers > 0)
            {
                if (slagLayers >= 4)
                {
                    slagLayers -= 4;
                    world.setBlockState(getInternalBlock().up(i), BlocksTFC.MOLTEN.getDefaultState().withProperty(LIT, cooking).withProperty(BlockMolten.LAYERS, 4));
                }
                else
                {
                    world.setBlockState(getInternalBlock().up(i), BlocksTFC.MOLTEN.getDefaultState().withProperty(LIT, cooking).withProperty(BlockMolten.LAYERS, slagLayers));
                    slagLayers = 0;
                }
            }
            else
            {
                //Remove any surplus slag(ie: after cooking/structure became compromised)
                if (world.getBlockState(getInternalBlock().up(i)).getBlock() == BlocksTFC.MOLTEN)
                {
                    world.setBlockToAir(getInternalBlock().up(i));
                }
            }
        }
    }

}

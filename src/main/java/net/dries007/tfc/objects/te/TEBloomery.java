/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.te;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

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
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.calendar.CalendarTFC;
import net.dries007.tfc.util.calendar.ICalendarTickable;

import static net.dries007.tfc.objects.blocks.property.ILightableBlock.LIT;
import static net.minecraft.block.BlockHorizontal.FACING;

@SuppressWarnings("WeakerAccess")
@ParametersAreNonnullByDefault
public class TEBloomery extends TEInventory implements ICalendarTickable, ITickable
{
    // Gets the internal block, should be charcoal pile/bloom
    private static final Vec3i OFFSET_INTERNAL = new Vec3i(1, 0, 0);
    // Gets the external block, the front of the facing to dump contents in world.
    private static final Vec3i OFFSET_EXTERNAL = new Vec3i(-1, 0, 0);
    private List<ItemStack> oreStacks = new ArrayList<>();
    private List<ItemStack> fuelStacks = new ArrayList<>();

    private int maxFuel = 0, maxOre = 0, delayTimer = 0; // Helper variables, not necessary to serialize
    private long burnTicksLeft; // Ticks left to finish the current operation
    private long lastPlayerTick; // Last player tick this bloomery was ticked (for purposes of catching up)
    private EnumFacing direction = null;

    private BlockPos internalBlock = null, externalBlock = null;

    private BloomeryRecipe cachedRecipe = null;

    public TEBloomery()
    {
        super(0);
        lastPlayerTick = CalendarTFC.PLAYER_TIME.getTicks();
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
        burnTicksLeft = tag.getLong("burnTicksLeft");
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
        tag.setLong("burnTicksLeft", burnTicksLeft);
        return super.writeToNBT(tag);
    }

    @Override
    public void onBreakBlock(World worldIn, BlockPos pos, IBlockState state)
    {
        dumpItems();
        super.onBreakBlock(world, pos, state);
    }

    @Override
    public void update()
    {
        ICalendarTickable.super.update();

        if (world.isRemote) return;
        IBlockState state = world.getBlockState(pos);
        if (--delayTimer <= 0)
        {
            delayTimer = 20;
            // Update multiblock status
            if (direction == null)
            {
                direction = world.getBlockState(pos).getValue(FACING);
            }

            int newMaxItems = BlocksTFC.BLOOMERY.getChimneyLevels(world, getInternalBlock()) * 8;
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
            }
            while (maxFuel < fuelStacks.size())
            {
                turnOff = true;
                InventoryHelper.spawnItemStack(world, getExternalBlock().getX(), getExternalBlock().getY(), getExternalBlock().getZ(), fuelStacks.get(0));
                fuelStacks.remove(0);
            }
            // Structure became compromised, unlit if needed
            if (turnOff && state.getValue(LIT))
            {
                burnTicksLeft = 0;
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
                addItemsFromWorld();
            }

            updateSlagBlock(state.getValue(LIT));
        }
        if (state.getValue(LIT))
        {
            if (--burnTicksLeft <= 0)
            {
                burnTicksLeft = 0;
                if (cachedRecipe == null && !oreStacks.isEmpty())
                {
                    cachedRecipe = BloomeryRecipe.get(oreStacks.get(0));
                    if (cachedRecipe == null)
                    {
                        this.dumpItems();
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
                world.setBlockState(pos, state.withProperty(LIT, false));
                markDirty();
            }
        }
    }

    @Override
    public void onCalendarUpdate(long playerTickDelta)
    {
        burnTicksLeft = Math.max(0, burnTicksLeft - playerTickDelta);
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

    public boolean canIgnite()
    {
        if (world.isRemote) return false;
        if (this.fuelStacks.size() < this.oreStacks.size() || this.oreStacks.isEmpty())
            return false;

        return isInternalBlockComplete();
    }

    public void onIgnite()
    {
        this.burnTicksLeft = ConfigTFC.GENERAL.bloomeryTime;
    }

    /**
     * Gets the internal (charcoal pile / bloom) position
     *
     * @return BlockPos of the internal block
     */
    protected BlockPos getInternalBlock()
    {
        if (internalBlock == null)
        {
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
    protected BlockPos getExternalBlock()
    {
        if (externalBlock == null)
        {
            externalBlock = pos.up(OFFSET_EXTERNAL.getY())
                .offset(direction, OFFSET_EXTERNAL.getX())
                .offset(direction.rotateY(), OFFSET_EXTERNAL.getZ());
        }
        return externalBlock;
    }

    private void dumpItems()
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

    private boolean isInternalBlockComplete()
    {
        IBlockState inside = world.getBlockState(getInternalBlock());
        return inside.getBlock() == BlocksTFC.CHARCOAL_PILE && inside.getValue(BlockCharcoalPile.LAYERS) >= 8;
    }

    private void addItemsFromWorld()
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

    private void updateSlagBlock(boolean cooking)
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

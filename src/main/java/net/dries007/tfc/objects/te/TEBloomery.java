/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.te;

import net.dries007.tfc.api.types.Metal;
import net.dries007.tfc.api.util.IMetalObject;
import net.dries007.tfc.objects.blocks.BlockCharcoalPile;
import net.dries007.tfc.objects.blocks.BlockMolten;
import net.dries007.tfc.objects.blocks.BlocksTFC;
import net.dries007.tfc.objects.items.metal.ItemOreTFC;
import net.dries007.tfc.util.FuelManager;
import net.dries007.tfc.util.Helpers;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Items;
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
import net.minecraftforge.common.util.Constants;

import java.util.ArrayList;
import java.util.List;

import static net.dries007.tfc.util.Helpers.getNull;
import static net.dries007.tfc.util.ILightableBlock.LIT;
import static net.minecraft.block.BlockHorizontal.FACING;

public class TEBloomery extends TEBase implements ITickable {

    //Gets the internal block, should be charcoal pile
    private static final Vec3i OFFSET_INTERNAL = new Vec3i(1, 0, 0);
    private List<ItemStack> oreStacks = new ArrayList<>();
    private List<ItemStack> fuelStacks = new ArrayList<>();

    private int maxFuel = 0, maxOre = 0, delayTimer = 0;
    private long burnTicksLeft;

    private BlockPos internalBlock = null;

    public TEBloomery() { }

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
        for (int i = 0; i < ores.tagCount(); i++)
        {
            fuelStacks.add(new ItemStack(fuels.getCompoundTagAt(i)));
        }
        burnTicksLeft = tag.getLong("burnTicksLeft");
        super.readFromNBT(tag);
    }

    @Override
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
        tag.setTag("fuels", ores);
        tag.setLong("burnTicksLeft", burnTicksLeft);
        return super.writeToNBT(tag);
    }

    public void onBreakBlock()
    {
        //Dump everything in world
        for(int i = 1; i < 4; i++)
        {
            if(world.getBlockState(getInternalBlock().up(i)).getBlock() == BlocksTFC.MOLTEN)
                world.setBlockToAir(getInternalBlock().up(i));
        }
        for(ItemStack stack : oreStacks)
        {
            InventoryHelper.spawnItemStack(world, pos.getX(), pos.getY(), pos.getZ(), stack);
        }
        for(ItemStack stack : fuelStacks)
        {
            InventoryHelper.spawnItemStack(world, pos.getX(), pos.getY(), pos.getZ(), stack);
        }
    }

    private BlockPos getInternalBlock()
    {
        if(internalBlock == null){
            EnumFacing direction = world.getBlockState(pos).getValue(FACING);
            internalBlock = pos.up(OFFSET_INTERNAL.getY())
                .offset(direction, OFFSET_INTERNAL.getX())
                .offset(direction.rotateY(), OFFSET_INTERNAL.getZ());
        }
        return internalBlock;
    }

    private boolean isInternalBlockComplete()
    {
        IBlockState inside = world.getBlockState(getInternalBlock());
        return (inside.getBlock() == BlocksTFC.CHARCOAL_PILE && inside.getValue(BlockCharcoalPile.LAYERS) >= 8);
    }

    public boolean canIgnite() {
        if (world.isRemote) return false;
        if (this.fuelStacks.size() < this.oreStacks.size() || this.oreStacks.isEmpty())
            return false;

        return isInternalBlockComplete();
    }

    public void onIgnite()
    {
        this.burnTicksLeft = 100; //15 in-game hours
    }

    private void addItemsFromWorld()
    {
        for (EntityItem entityItem : world.getEntitiesWithinAABB(EntityItem.class, new AxisAlignedBB(getInternalBlock().up(), getInternalBlock().add(1, 4, 1)), EntitySelectors.IS_ALIVE))
        {
            ItemStack stack = entityItem.getItem();
            if (FuelManager.isItemFuel(stack))
            {
                // Add fuel
                while (fuelStacks.size() < maxFuel)
                {
                    this.markDirty();
                    fuelStacks.add(stack.splitStack(1));
                    if(stack.getCount() <= 0) {
                        entityItem.setDead();
                        break;
                    }
                }
            }
            else if(stack.getItem() instanceof ItemOreTFC)
            {
                ItemOreTFC metal = (ItemOreTFC) stack.getItem();
                if(metal.getMetal(stack) == Metal.WROUGHT_IRON || metal.getMetal(stack) == Metal.PIG_IRON) {
                    while(oreStacks.size() < maxOre) {
                        this.markDirty();
                        oreStacks.add(stack.splitStack(1));
                        if (stack.getCount() <= 0) {
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
        //If there's at least one item show one layer so player knows that it is working
        int slagLayers = slag > 0 && slag < 4 ? 1 : slag / 4;
        int height = 0;
        BlockPos layer = getInternalBlock().up();
        while(slagLayers > 0)
        {
            //4 layers means one block
            if(slagLayers >= 4){
                slagLayers -= 4;
                world.setBlockState(layer, BlocksTFC.MOLTEN.getDefaultState().withProperty(LIT, cooking).withProperty(BlockMolten.LAYERS, 4));
            }else{
                world.setBlockState(layer, BlocksTFC.MOLTEN.getDefaultState().withProperty(LIT, cooking).withProperty(BlockMolten.LAYERS, slagLayers));
                slagLayers = 0;
            }
            height++;
            layer = layer.up();
        }
        //Remove any surplus slag blocks(ie: after cooking ore)
        int maxHeight = BlocksTFC.BLOOMERY.getChimneyLevels(world, pos);
        while(height < maxHeight)
        {
            if(world.getBlockState(layer).getBlock() == BlocksTFC.MOLTEN)
                world.setBlockToAir(layer);
            height++;
            layer = layer.up();
        }
    }

    @Override
    public void update() {
        if (world.isRemote) return;
        if (--delayTimer <= 0) {
            delayTimer = 20;
            // Update multiblock status
            int newMaxItems = BlocksTFC.BLOOMERY.getChimneyLevels(world, pos) * 8;
            this.maxFuel = newMaxItems;
            this.maxOre = newMaxItems;
            while (maxOre < oreStacks.size()) {
                InventoryHelper.spawnItemStack(world, pos.getX(), pos.getY(), pos.getZ(), oreStacks.get(0));
                oreStacks.remove(0);
            }
            while (maxFuel < fuelStacks.size()) {
                InventoryHelper.spawnItemStack(world, pos.getX(), pos.getY(), pos.getZ(), fuelStacks.get(0));
                fuelStacks.remove(0);
            }
            if (maxOre <= 0) {
                world.setBlockToAir(pos);
                return;
            }
            if(!isInternalBlockComplete() && (!fuelStacks.isEmpty() || !fuelStacks.isEmpty())){
                onBreakBlock();
            }
            if(isInternalBlockComplete())addItemsFromWorld();
            updateSlagBlock(this.burnTicksLeft > 0);
        }
        IBlockState state = world.getBlockState(pos);
        if (state.getValue(LIT)){
            if(--this.burnTicksLeft<=0){
                this.burnTicksLeft = 0;
                int totalOutput = 0;
                for(ItemStack stack : oreStacks)
                {
                    IMetalObject metal = (IMetalObject) stack.getItem();
                    totalOutput += metal.getSmeltAmount(stack);
                }
                oreStacks.clear();
                fuelStacks.clear();
                world.setBlockState(getInternalBlock(), BlocksTFC.BLOOM.getDefaultState());
                TEBloom te = Helpers.getTE(world, getInternalBlock(), TEBloom.class);
                if (te != null) te.setCount(totalOutput);
                updateSlagBlock(false);
                world.setBlockState(pos, state.withProperty(LIT, false));
                this.markDirty();
            }
        }
    }

}

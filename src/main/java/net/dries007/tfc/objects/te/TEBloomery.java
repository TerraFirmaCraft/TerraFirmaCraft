/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.te;

import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.api.types.Metal;
import net.dries007.tfc.api.types.Ore;
import net.dries007.tfc.api.util.IMetalObject;
import net.dries007.tfc.objects.blocks.BlockCharcoalPile;
import net.dries007.tfc.objects.blocks.BlockMolten;
import net.dries007.tfc.objects.blocks.BlocksTFC;
import net.dries007.tfc.objects.blocks.metal.BlockBloom;
import net.dries007.tfc.objects.items.metal.ItemOreTFC;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.OreDictionaryHelper;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;

import static net.dries007.tfc.util.ILightableBlock.LIT;
import static net.minecraft.block.BlockHorizontal.FACING;

public class TEBloomery extends TEBase implements ITickable {

    //Gets the internal block, should be charcoal pile
    private static final Vec3i OFFSET_INTERNAL = new Vec3i(1, 0, 0);

    public int charcoalCount, maxCharcoal;
    public long burnTicksLeft;
    public int oreCount, maxOre;
    public int outCount;

    private int delayTimer;

    public TEBloomery()
    {
        charcoalCount = 0;
        burnTicksLeft = 0;
        oreCount = 0;
        outCount = 0;
        maxOre = 0;
        maxCharcoal = 0;
        delayTimer = 0;
    }

    @Override
    public void readFromNBT(NBTTagCompound tag)
    {
        charcoalCount = tag.getInteger("charcoalCount");
        burnTicksLeft = tag.getLong("burnTicksLeft");
        oreCount = tag.getInteger("oreCount");
        outCount = tag.getInteger("outCount");
        super.readFromNBT(tag);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tag)
    {
        tag.setInteger("charcoalCount", charcoalCount);
        tag.setLong("burnTicksLeft", burnTicksLeft);
        tag.setInteger("oreCount", oreCount);
        tag.setInteger("outCount", outCount);
        return super.writeToNBT(tag);
    }

    private BlockPos getInternalBlock()
    {
        EnumFacing direction = world.getBlockState(pos).getValue(FACING);
        BlockPos posx = pos.up(OFFSET_INTERNAL.getY())
            .offset(direction, OFFSET_INTERNAL.getX())
            .offset(direction.rotateY(), OFFSET_INTERNAL.getZ());
        return posx;
    }

    private boolean isInternalBlockComplete()
    {
        IBlockState inside = world.getBlockState(getInternalBlock());
        return (inside.getBlock() == BlocksTFC.CHARCOAL_PILE && inside.getValue(BlockCharcoalPile.LAYERS) >= 8);
    }

    public boolean canIgnite() {
        if (world.isRemote) return false;
        if (this.charcoalCount < this.oreCount || oreCount == 0)
            return false;

        return isInternalBlockComplete();
    }

    public void onIgnite()
    {
        this.burnTicksLeft = 15000; //15 in-game hours
    }

    private void addItemsFromWorld()
    {
        for (EntityItem entityItem : world.getEntitiesWithinAABB(EntityItem.class, new AxisAlignedBB(getInternalBlock().up(), getInternalBlock().add(1, 4, 1)), EntitySelectors.IS_ALIVE))
        {
            ItemStack stack = entityItem.getItem();
            if (stack.getItem() == Items.COAL)
            {
                // Add charcoal
                while (charcoalCount < maxCharcoal)
                {
                    charcoalCount++;
                    stack.shrink(1);
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
                    while(oreCount < maxOre) {
                        oreCount++;
                        outCount += metal.getSmeltAmount(stack.splitStack(1));
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
        int slag = charcoalCount+oreCount;
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
            this.maxCharcoal = newMaxItems;
            this.maxOre = newMaxItems;
            if (maxOre < oreCount) {
                //TODO The chimney height shrink while ore count was high, should we dump in world or destroy the surplus?
            }
            if (maxCharcoal < charcoalCount) {
                //TODO The chimney height shrink while charcoal count was high, should we dump in world or destroy the surplus?
            }
            if (maxOre <= 0) {
                //TODO Multiblock became malformed, should we break the bloomery gate?
            }
            if(!isInternalBlockComplete() && (oreCount > 0 || charcoalCount > 0)){
                //TODO Internal block was modified while ore/charcoal was already inside. Should we dump in the world?
            }
            if(isInternalBlockComplete())addItemsFromWorld();
            updateSlagBlock(this.burnTicksLeft > 0);
        }
        IBlockState state = world.getBlockState(pos);
        if (state.getValue(LIT)){
            if(--this.burnTicksLeft<=0){
                this.burnTicksLeft = 0;
                this.charcoalCount = 0;
                this.oreCount = 0;
                world.setBlockState(getInternalBlock(), BlocksTFC.BLOOM.getDefaultState());
                TEBloom te = Helpers.getTE(world, getInternalBlock(), TEBloom.class);
                if (te != null) te.setCount(outCount);
                outCount = 0;
                updateSlagBlock(false);
                world.setBlockState(pos, state.withProperty(LIT, false));
            }
        }
    }

}

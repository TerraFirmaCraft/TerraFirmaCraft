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
import net.dries007.tfc.objects.items.metal.ItemOreTFC;
import net.dries007.tfc.util.OreDictionaryHelper;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;

import static net.dries007.tfc.util.ILightableBlock.LIT;

public class TEBloomery extends TEBase implements ITickable {

    //Gets the internal block, should be charcoal pile
    private static final Vec3i OFFSET_INTERNAL = new Vec3i(-1, 0, 0);

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

    private boolean convertToBloom(ItemStack stack)
    {
        if (stack.getItem() instanceof ItemOreTFC)
        {
            ItemOreTFC metal = (ItemOreTFC) stack.getItem();
            if (metal.getMetal(stack) == Metal.WROUGHT_IRON)
            {
                oreCount++;
                outCount += metal.getSmeltAmount(stack);
                return true;
            }
        }
        return false;
    }

    public boolean canIgnite() {
        if (world.isRemote) return false;
        if (this.charcoalCount < this.oreCount || oreCount == 0)
            return false;

        IBlockState inside = world.getBlockState(pos.add(OFFSET_INTERNAL));
        if (inside.getBlock() == BlocksTFC.CHARCOAL_PILE && inside.getValue(BlockCharcoalPile.LAYERS) >= 8)
            return true;
        return false;
    }

    public void onIgnite()
    {
        //TODO Change this to real value later(15-ingame hours)
        this.burnTicksLeft = 1200;
    }

    private void addItemsFromWorld()
    {
        for (EntityItem entityItem : world.getEntitiesWithinAABB(EntityItem.class, new AxisAlignedBB(pos.add(OFFSET_INTERNAL).up(), getPos().add(1, 5, 1)), EntitySelectors.IS_ALIVE))
        {
            ItemStack stack = entityItem.getItem();
            TerraFirmaCraft.getLog().warn("ITEM FOUND");
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
                if(metal.getMetal(stack) == Metal.WROUGHT_IRON) {
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

    private void updateSlagBlock()
    {
        int slag = charcoalCount+oreCount;
        int slagLayers = slag > 0 && slag < 4 ? 1 : charcoalCount+oreCount / 4;
        int height = 0;
        boolean isLit = burnTicksLeft > 0;
        BlockPos inside = pos.add(OFFSET_INTERNAL);
        BlockPos layer = inside.up();
        while(slagLayers > 0)
        {
            if(slagLayers >= 4){
                slagLayers -= 4;
                world.setBlockState(layer, BlocksTFC.MOLTEN.getDefaultState().withProperty(LIT, isLit).withProperty(BlockMolten.LAYERS, 4));
                layer = layer.up();
            }else{
                world.setBlockState(layer, BlocksTFC.MOLTEN.getDefaultState().withProperty(LIT, isLit).withProperty(BlockMolten.LAYERS, slagLayers));
                slagLayers = 0;
            }
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
                //TODO The chimney height shrink while ore count was high, should we output or destroy the surplus?
            }
            if (maxCharcoal < charcoalCount) {
                //TODO The chimney height shrink while charcoal count was high, should we output or destroy the surplus?
            }
            if (maxOre <= 0) {
                //TODO Multiblock became malformed, should we break the bloomery gate?
            }
            addItemsFromWorld();
            updateSlagBlock();
        }
        int count = charcoalCount + oreCount;
        IBlockState state = world.getBlockState(pos);
        if (state.getValue(LIT)) {
            if(--this.burnTicksLeft<=0){
                //TODO Finished cooking ore
            }
        }
    }

}

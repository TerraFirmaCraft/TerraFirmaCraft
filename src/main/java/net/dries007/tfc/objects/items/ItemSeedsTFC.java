package net.dries007.tfc.objects.items;

import java.util.EnumMap;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.EnumPlantType;
import net.minecraftforge.common.IPlantable;

import net.dries007.tfc.objects.Agriculture.Crop;
import net.dries007.tfc.objects.blocks.stone.BlockFarmlandTFC;

public class ItemSeedsTFC extends Item implements IPlantable
{
    private final Block crops;
    /** BlockID of the block the seeds can be planted on. */
    public final Crop seedbag;

    private static final EnumMap<Crop, ItemSeedsTFC> MAP = new EnumMap<>(Crop.class);

    public static ItemSeedsTFC get(Crop seedbag) { return MAP.get(seedbag); }

    public static ItemStack get(Crop seedbag, int amount) { return new ItemStack(MAP.get(seedbag), amount); }

    public ItemSeedsTFC(Crop seedbag, Block crops)
    {
        this.seedbag = seedbag;
        this.crops = crops;
        if (MAP.put(seedbag, this) != null) throw new IllegalStateException("There can only be one.");
    }
    /**
     * Called when a Block is right-clicked with this Item
     */
    public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        ItemStack itemstack = player.getHeldItem(hand);
        net.minecraft.block.state.IBlockState state = worldIn.getBlockState(pos);
        if (facing == EnumFacing.UP && player.canPlayerEdit(pos.offset(facing), facing, itemstack) && state.getBlock().canSustainPlant(state, worldIn, pos, EnumFacing.UP, this) && worldIn.isAirBlock(pos.up()) && state.getBlock() instanceof BlockFarmlandTFC)
        {
            worldIn.setBlockState(pos.up(), this.crops.getDefaultState());

            if (player instanceof EntityPlayerMP)
            {
                CriteriaTriggers.PLACED_BLOCK.trigger((EntityPlayerMP)player, pos.up(), itemstack);
            }

            itemstack.shrink(1);
            return EnumActionResult.SUCCESS;
        }
        else
        {
            return EnumActionResult.FAIL;
        }
    }

    @Override
    public net.minecraftforge.common.EnumPlantType getPlantType(net.minecraft.world.IBlockAccess world, BlockPos pos)
    {
        return EnumPlantType.Crop;
    }

    @Override
    public net.minecraft.block.state.IBlockState getPlant(net.minecraft.world.IBlockAccess world, BlockPos pos)
    {
        return this.crops.getDefaultState();
    }
}
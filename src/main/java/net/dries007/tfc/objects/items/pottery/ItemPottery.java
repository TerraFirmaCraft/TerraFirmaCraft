package net.dries007.tfc.objects.items.pottery;

import net.dries007.tfc.objects.Metal;
import net.dries007.tfc.objects.blocks.BlocksTFC;
import net.dries007.tfc.objects.te.TEPitKiln;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.IFireable;
import net.dries007.tfc.util.IPlacableItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemPottery extends Item implements IPlacableItem, IFireable
{
    @Override
    public ItemStack getFiringResult(ItemStack input, Metal.Tier tier)
    {
        return input; // Already fired pottery does noting.
    }

    @Override
    @SuppressWarnings("ConstantConditions")
    public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        if (worldIn.isRemote) return EnumActionResult.PASS;
        if (!player.isSneaking()) return EnumActionResult.PASS;

        if (facing != EnumFacing.UP) return EnumActionResult.FAIL;

        // todo: add player.canPlayerEdit check maybe?

        if (worldIn.getBlockState(pos).getBlock() != BlocksTFC.PIT_KILN && worldIn.isAirBlock(pos.add(0, 1, 0)))
        {
            if (!worldIn.isSideSolid(pos, EnumFacing.UP)) return EnumActionResult.FAIL;
            pos = pos.add(0, 1, 0);
            worldIn.setBlockState(pos, BlocksTFC.PIT_KILN.getDefaultState());
        }
        TEPitKiln te = Helpers.getTE(worldIn, pos, TEPitKiln.class);
        if (te == null) return EnumActionResult.FAIL;
        if (te.addItem(player.getHeldItem(hand), hitX < 0.5, hitZ < 0.5))
            return EnumActionResult.SUCCESS;
        return EnumActionResult.FAIL;
    }
}

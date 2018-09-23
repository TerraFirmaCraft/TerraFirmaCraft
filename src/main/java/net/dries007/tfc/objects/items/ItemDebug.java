/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.items;

import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import mcp.MethodsReturnNonnullByDefault;
import net.dries007.tfc.objects.te.TEBellows;
import net.dries007.tfc.objects.te.TEFirePit;
import net.dries007.tfc.util.Helpers;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class ItemDebug extends Item
{
    public ItemDebug()
    {
        setNoRepair();
        setMaxStackSize(1);
        setFull3D();
    }

    @Override
    public boolean hitEntity(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker)
    {
        return false;
    }

    @Override
    public boolean onBlockDestroyed(ItemStack stack, World worldIn, IBlockState state, BlockPos pos, EntityLivingBase entityLiving)
    {
        return false;
    }

    @Override
    public boolean canHarvestBlock(IBlockState blockIn)
    {
        return false;
    }

    @Override
    public boolean hasEffect(ItemStack stack)
    {
        return true;
    }

    @Override
    public EnumRarity getRarity(ItemStack stack)
    {
        return EnumRarity.EPIC;
    }

    @Override
    public int getEntityLifespan(ItemStack itemStack, World world)
    {
        return 60;
    }

    @Override
    public boolean canDestroyBlockInCreative(World world, BlockPos pos, ItemStack stack, EntityPlayer player)
    {
        return false;
    }

    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        // todo: make this modular? Or something? Or just remove it once it has served its purpose
        TEBellows te = Helpers.getTE(worldIn, pos, TEBellows.class);
        if (te != null)
        {
            te.debug();
        }
        TEFirePit te2 = Helpers.getTE(worldIn, pos, TEFirePit.class);
        if (te2 != null)
        {
            te2.debug();
        }
        return EnumActionResult.SUCCESS;
    }
}

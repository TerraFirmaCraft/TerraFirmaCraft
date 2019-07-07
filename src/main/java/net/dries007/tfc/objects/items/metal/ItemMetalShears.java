/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.items.metal;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import net.dries007.tfc.api.types.Metal;

@ParametersAreNonnullByDefault
public class ItemMetalShears extends ItemMetalTool
{
    public ItemMetalShears(Metal metal, Metal.ItemType type)
    {
        super(metal, type);
    }

    @Override
    public boolean itemInteractionForEntity(ItemStack itemstack, net.minecraft.entity.player.EntityPlayer player, EntityLivingBase entity, net.minecraft.util.EnumHand hand)
    {
        if (entity.world.isRemote)
        {
            return false;
        }
        if (entity instanceof net.minecraftforge.common.IShearable)
        {
            net.minecraftforge.common.IShearable target = (net.minecraftforge.common.IShearable) entity;
            BlockPos pos = new BlockPos(entity.posX, entity.posY, entity.posZ);
            if (target.isShearable(itemstack, entity.world, pos))
            {
                java.util.List<ItemStack> drops = target.onSheared(itemstack, entity.world, pos,
                    net.minecraft.enchantment.EnchantmentHelper.getEnchantmentLevel(net.minecraft.init.Enchantments.FORTUNE, itemstack));

                java.util.Random rand = new java.util.Random();
                for (ItemStack stack : drops)
                {
                    EntityItem ent = entity.entityDropItem(stack, 1.0F);
                    if (ent != null)
                    {
                        ent.motionY += rand.nextFloat() * 0.05F;
                        ent.motionX += (rand.nextFloat() - rand.nextFloat()) * 0.1F;
                        ent.motionZ += (rand.nextFloat() - rand.nextFloat()) * 0.1F;
                    }
                }
                itemstack.damageItem(1, entity);
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean onBlockStartBreak(ItemStack itemstack, BlockPos pos, net.minecraft.entity.player.EntityPlayer player)
    {
        if (player.world.isRemote || player.capabilities.isCreativeMode)
        {
            return false;
        }
        Block block = player.world.getBlockState(pos).getBlock();
        if (block instanceof net.minecraftforge.common.IShearable)
        {
            net.minecraftforge.common.IShearable target = (net.minecraftforge.common.IShearable) block;
            if (target.isShearable(itemstack, player.world, pos))
            {
                java.util.List<ItemStack> drops = target.onSheared(itemstack, player.world, pos,
                    net.minecraft.enchantment.EnchantmentHelper.getEnchantmentLevel(net.minecraft.init.Enchantments.FORTUNE, itemstack));
                java.util.Random rand = new java.util.Random();

                for (ItemStack stack : drops)
                {
                    float f = 0.7F;
                    double d = (double) (rand.nextFloat() * f) + (double) (1.0F - f) * 0.5D;
                    double d1 = (double) (rand.nextFloat() * f) + (double) (1.0F - f) * 0.5D;
                    double d2 = (double) (rand.nextFloat() * f) + (double) (1.0F - f) * 0.5D;
                    net.minecraft.entity.item.EntityItem entityitem = new net.minecraft.entity.item.EntityItem(player.world, (double) pos.getX() + d, (double) pos.getY() + d1, (double) pos.getZ() + d2, stack);
                    entityitem.setDefaultPickupDelay();
                    player.world.spawnEntity(entityitem);
                }

                itemstack.damageItem(1, player);
                //noinspection ConstantConditions
                player.addStat(StatList.getBlockStats(block));
                player.world.setBlockState(pos, Blocks.AIR.getDefaultState(), 11);
                return true;
            }
        }
        return false;
    }

    @Override
    public float getDestroySpeed(@Nonnull ItemStack stack, IBlockState state)
    {
        Block block = state.getBlock();

        if (block != Blocks.WEB && state.getMaterial() != Material.LEAVES)
        {
            return block == Blocks.WOOL ? 5.0F : super.getDestroySpeed(stack, state);
        }
        else
        {
            return 15.0F;
        }
    }

    @Override
    public boolean onBlockDestroyed(@Nonnull ItemStack stack, World worldIn, @Nonnull IBlockState state, @Nonnull BlockPos pos, @Nonnull EntityLivingBase entityLiving)
    {
        if (!worldIn.isRemote)
        {
            stack.damageItem(1, entityLiving);
        }

        Block block = state.getBlock();
        if (block instanceof net.minecraftforge.common.IShearable) return true;
        return state.getMaterial() == Material.LEAVES || block == Blocks.WEB || block == Blocks.TALLGRASS || block == Blocks.VINE || block == Blocks.TRIPWIRE || block == Blocks.WOOL || super.onBlockDestroyed(stack, worldIn, state, pos, entityLiving);
    }

    /**
     * Check whether this Item can harvest the given Block
     */
    public boolean canHarvestBlock(IBlockState blockIn)
    {
        Block block = blockIn.getBlock();
        return block == Blocks.WEB || block == Blocks.REDSTONE_WIRE || block == Blocks.TRIPWIRE;
    }
}

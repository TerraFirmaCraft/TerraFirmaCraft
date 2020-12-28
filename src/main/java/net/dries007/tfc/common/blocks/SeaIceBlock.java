package net.dries007.tfc.common.blocks;

import javax.annotation.Nullable;

import net.minecraft.block.BlockState;
import net.minecraft.block.IceBlock;
import net.minecraft.block.material.Material;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class SeaIceBlock extends IceBlock
{
    public SeaIceBlock(Properties properties)
    {
        super(properties);
    }

    /**
     * Override to change a reference to water to salt water
     */
    @Override
    public void playerDestroy(World worldIn, PlayerEntity player, BlockPos pos, BlockState state, @Nullable TileEntity te, ItemStack stack)
    {
        super.playerDestroy(worldIn, player, pos, state, te, stack);
        if (EnchantmentHelper.getItemEnchantmentLevel(Enchantments.SILK_TOUCH, stack) == 0)
        {
            if (worldIn.dimensionType().ultraWarm())
            {
                worldIn.removeBlock(pos, false);
                return;
            }

            Material material = worldIn.getBlockState(pos.below()).getMaterial();
            if (material.blocksMotion() || material.isLiquid())
            {
                worldIn.setBlockAndUpdate(pos, TFCBlocks.SALT_WATER.get().defaultBlockState());
            }
        }
    }

    @Override
    protected void melt(BlockState state, World worldIn, BlockPos pos)
    {
        if (worldIn.dimensionType().ultraWarm())
        {
            worldIn.removeBlock(pos, false);
        }
        else
        {
            // Use salt water here
            worldIn.setBlockAndUpdate(pos, TFCBlocks.SALT_WATER.get().defaultBlockState());
            worldIn.neighborChanged(pos, TFCBlocks.SALT_WATER.get(), pos);
        }
    }
}

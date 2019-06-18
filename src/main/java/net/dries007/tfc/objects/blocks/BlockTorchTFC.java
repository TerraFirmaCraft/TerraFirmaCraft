/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.blocks;

import java.util.Random;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.block.BlockTorch;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import net.dries007.tfc.api.capability.size.IItemSize;
import net.dries007.tfc.api.capability.size.Size;
import net.dries007.tfc.api.capability.size.Weight;
import net.dries007.tfc.objects.items.ItemFireStarter;
import net.dries007.tfc.objects.te.TETorchTFC;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.ILightableBlock;
import net.dries007.tfc.util.OreDictionaryHelper;

@ParametersAreNonnullByDefault
public class BlockTorchTFC extends BlockTorch implements IItemSize, ILightableBlock
{
    public static boolean canLight(ItemStack stack)
    {
        return stack.getItem() == Item.getItemFromBlock(BlocksTFC.TORCH) || ItemFireStarter.canIgnite(stack);
    }

    public BlockTorchTFC()
    {
        setDefaultState(blockState.getBaseState().withProperty(FACING, EnumFacing.UP).withProperty(LIT, true));
        setLightLevel(0.9375F);
        setTickRandomly(true);

        Blocks.FIRE.setFireInfo(this, 5, 20);

        OreDictionaryHelper.register(this, "torch");
    }

    @Nonnull
    @Override
    public Size getSize(@Nonnull ItemStack stack)
    {
        return Size.NORMAL;
    }

    @Nonnull
    @Override
    public Weight getWeight(@Nonnull ItemStack stack)
    {
        return Weight.LIGHT;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void randomDisplayTick(IBlockState stateIn, World worldIn, BlockPos pos, Random rand)
    {
        if (!stateIn.getValue(LIT)) return;
        super.randomDisplayTick(stateIn, worldIn, pos, rand);
    }

    @Override
    @Nonnull
    public IBlockState getStateFromMeta(int meta)
    {
        return getDefaultState().withProperty(LIT, meta >= 8).withProperty(FACING, EnumFacing.byIndex(meta & 0b111));
    }

    @Override
    public int getMetaFromState(IBlockState state)
    {
        return (state.getValue(LIT) ? 8 : 0) + state.getValue(FACING).getIndex();
    }

    @Override
    @Nonnull
    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, FACING, LIT);
    }

    @Override
    public void randomTick(World worldIn, BlockPos pos, IBlockState state, Random random)
    {
        TETorchTFC te = Helpers.getTE(worldIn, pos, TETorchTFC.class);
        if (te != null)
        {
            te.onRandomTick();
        }
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        if (!worldIn.isRemote)
        {
            TETorchTFC te = Helpers.getTE(worldIn, pos, TETorchTFC.class);
            ItemStack stack = playerIn.getHeldItem(hand);
            if (te != null && BlockTorchTFC.canLight(stack))
            {
                te.light();
            }
        }
        return true;
    }

    @Override
    public int getLightValue(IBlockState state, IBlockAccess world, BlockPos pos)
    {
        return state.getValue(LIT) ? super.getLightValue(state, world, pos) : 0;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(World world, IBlockState state)
    {
        return new TETorchTFC();
    }
}

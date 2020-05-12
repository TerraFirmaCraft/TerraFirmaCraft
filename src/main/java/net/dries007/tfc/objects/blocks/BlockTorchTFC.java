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
import net.minecraft.block.SoundType;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
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
import net.minecraftforge.items.ItemHandlerHelper;

import net.dries007.tfc.ConfigTFC;
import net.dries007.tfc.api.capability.size.IItemSize;
import net.dries007.tfc.api.capability.size.Size;
import net.dries007.tfc.api.capability.size.Weight;
import net.dries007.tfc.objects.advancements.TFCTriggers;
import net.dries007.tfc.objects.blocks.property.ILightableBlock;
import net.dries007.tfc.objects.items.ItemFireStarter;
import net.dries007.tfc.objects.te.TETickCounter;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.OreDictionaryHelper;

@ParametersAreNonnullByDefault
public class BlockTorchTFC extends BlockTorch implements IItemSize, ILightableBlock
{
    public static boolean canLight(ItemStack stack)
    {
        return stack.getItem() == Item.getItemFromBlock(Blocks.TORCH) || ItemFireStarter.canIgnite(stack);
    }

    public BlockTorchTFC()
    {
        setDefaultState(blockState.getBaseState().withProperty(FACING, EnumFacing.UP).withProperty(LIT, true));
        setHardness(0f);
        setLightLevel(0.9375F);
        setTickRandomly(true);
        setSoundType(SoundType.WOOD);

        OreDictionaryHelper.register(this, "torch");
    }

    @Nonnull
    @Override
    public Size getSize(@Nonnull ItemStack stack)
    {
        return Size.SMALL; // Can store anywhere
    }

    @Nonnull
    @Override
    public Weight getWeight(@Nonnull ItemStack stack)
    {
        return Weight.LIGHT; // Stacksize = 32
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
        return super.getStateFromMeta(meta % 6).withProperty(LIT, meta >= 6);
    }

    @Override
    public int getMetaFromState(IBlockState state)
    {
        return (state.getValue(LIT) ? 6 : 0) + super.getMetaFromState(state);
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
        TETickCounter te = Helpers.getTE(worldIn, pos, TETickCounter.class);
        if (te != null)
        {
            if (!worldIn.isRemote && te.getTicksSinceUpdate() > ConfigTFC.General.OVERRIDES.torchTime && ConfigTFC.General.OVERRIDES.torchTime > 0)
            {
                worldIn.setBlockState(pos, state.withProperty(LIT, false));
                te.resetCounter();
            }
        }
    }

    @Override
    @Nonnull
    public Item getItemDropped(IBlockState state, Random rand, int fortune)
    {
        return state.getValue(LIT) ? Item.getItemFromBlock(Blocks.TORCH) : Items.STICK;
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        if (!worldIn.isRemote)
        {
            ItemStack stack = playerIn.getHeldItem(hand);
            if (state.getValue(LIT))
            {
                if (OreDictionaryHelper.doesStackMatchOre(stack, "stickWood"))
                {
                    stack.shrink(1);
                    ItemHandlerHelper.giveItemToPlayer(playerIn, new ItemStack(Blocks.TORCH));
                }
            }
            else
            {
                if (BlockTorchTFC.canLight(stack))
                {
                    TFCTriggers.LIT_TRIGGER.trigger((EntityPlayerMP) playerIn, state.getBlock()); // Trigger lit block
                    worldIn.setBlockState(pos, worldIn.getBlockState(pos).withProperty(LIT, true));
                    TETickCounter tile = Helpers.getTE(worldIn, pos, TETickCounter.class);
                    if (tile != null)
                    {
                        tile.resetCounter();
                    }
                }
            }
        }
        return true;
    }

    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack)
    {
        // Set the initial counter value
        TETickCounter tile = Helpers.getTE(worldIn, pos, TETickCounter.class);
        if (tile != null)
        {
            tile.resetCounter();
        }
        super.onBlockPlacedBy(worldIn, pos, state, placer, stack);
    }

    @Override
    public int getLightValue(IBlockState state, IBlockAccess world, BlockPos pos)
    {
        return state.getValue(LIT) ? super.getLightValue(state, world, pos) : 0;
    }

    @Override
    public boolean hasTileEntity(IBlockState state)
    {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(World world, IBlockState state)
    {
        return new TETickCounter();
    }
}

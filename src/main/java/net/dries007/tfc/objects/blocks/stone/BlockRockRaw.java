/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.blocks.stone;

import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.block.Block;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import mcp.MethodsReturnNonnullByDefault;
import net.dries007.tfc.ConfigTFC;
import net.dries007.tfc.api.types.Rock;
import net.dries007.tfc.objects.Gem;
import net.dries007.tfc.objects.items.ItemGem;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.ICollapsableBlock;
import net.dries007.tfc.util.OreDictionaryHelper;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class BlockRockRaw extends BlockRockVariant implements ICollapsableBlock
{
    /* This is for the not-surrounded-on-all-sides-pop-off mechanic. It's a dirty fix to the stack overflow caused by placement during water / lava collisions in world gen */
    public static final PropertyBool CAN_FALL = PropertyBool.create("can_fall");

    public BlockRockRaw(Rock.Type type, Rock rock)
    {
        super(type, rock);

        setDefaultState(getBlockState().getBaseState().withProperty(CAN_FALL, true));
    }

    @Override
    public BlockRockVariantFallable getFallingVariant()
    {
        return (BlockRockVariantFallable) BlockRockVariant.get(rock, Rock.Type.COBBLE);
    }

    @Override
    @SuppressWarnings("deprecation")
    public IBlockState getStateFromMeta(int meta)
    {
        return getDefaultState().withProperty(CAN_FALL, meta == 0);
    }

    @Override
    public int getMetaFromState(IBlockState state)
    {
        return state.getValue(CAN_FALL) ? 0 : 1;
    }

    @Override
    public void onPlayerDestroy(World worldIn, BlockPos pos, IBlockState state)
    {
        // Trigger the collapsing mechanic!
        checkCollapsingArea(worldIn, pos);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos)
    {
        super.neighborChanged(state, worldIn, pos, blockIn, fromPos);
        // Raw blocks that can't fall also can't pop off
        if (state.getValue(CAN_FALL))
        {
            for (EnumFacing face : EnumFacing.values())
            {
                IBlockState faceState = worldIn.getBlockState(pos.offset(face));
                if (faceState.getBlock().isSideSolid(faceState, worldIn, pos.offset(face), face.getOpposite()))
                {
                    return;
                }
            }

            // No supporting solid blocks, so pop off as an item
            worldIn.setBlockToAir(pos);
            Helpers.spawnItemStack(worldIn, pos, new ItemStack(state.getBlock(), 1));
        }
    }

    @Override
    public void onExplosionDestroy(World worldIn, BlockPos pos, Explosion explosionIn)
    {
        if (ConfigTFC.General.FALLABLE.explosionCausesCollapse)
        {
            // Trigger the collapsing mechanic!
            checkCollapsingArea(worldIn, pos);
        }
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        ItemStack stack = playerIn.getHeldItemMainhand();
        if (ConfigTFC.General.OVERRIDES.enableStoneAnvil && OreDictionaryHelper.doesStackMatchOre(stack, "hammer") && !worldIn.isBlockNormalCube(pos.up(), true))
        {
            if (!worldIn.isRemote)
            {
                // Create a stone anvil
                BlockRockVariant anvil = BlockRockVariant.get(this.rock, Rock.Type.ANVIL);
                if (anvil instanceof BlockStoneAnvil)
                {
                    worldIn.setBlockState(pos, anvil.getDefaultState());
                }
            }
            return true;
        }
        return false;
    }

    @Override
    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, CAN_FALL);
    }

    @Override
    public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune)
    {
        super.getDrops(drops, world, pos, state, fortune);
        // Raw rocks drop random gems
        if (RANDOM.nextDouble() < ConfigTFC.General.MISC.stoneGemDropChance)
        {
            drops.add(ItemGem.get(Gem.getRandomDropGem(RANDOM), Gem.Grade.randomGrade(RANDOM), 1));
        }
    }

    @Override
    public int damageDropped(IBlockState state)
    {
        return 0;
    }
}
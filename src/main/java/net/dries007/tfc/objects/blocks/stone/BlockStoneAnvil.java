/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.blocks.stone;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

import net.dries007.tfc.api.types.Rock;
import net.dries007.tfc.api.types.RockCategory;
import net.dries007.tfc.api.util.IRockObject;
import net.dries007.tfc.client.TFCGuiHandler;
import net.dries007.tfc.objects.items.rock.ItemRock;
import net.dries007.tfc.objects.te.TEAnvilTFC;

@ParametersAreNonnullByDefault
public class BlockStoneAnvil extends Block implements ITileEntityProvider, IRockObject
{
    private static final Map<Rock, BlockStoneAnvil> MAP = new HashMap<>();

    public static BlockStoneAnvil get(Rock rock)
    {
        return MAP.get(rock);
    }

    private final Rock rock;

    public BlockStoneAnvil(Rock rock)
    {
        super(Material.ROCK);

        setSoundType(SoundType.STONE);
        setHardness(2.0F);
        setResistance(10.0F);
        setHarvestLevel("pickaxe", 0);

        this.rock = rock;
        MAP.put(rock, this);
    }

    @Override
    public int quantityDropped(Random random)
    {
        return 1 + random.nextInt(3);
    }

    @Override
    @Nonnull
    public Item getItemDropped(IBlockState state, Random rand, int fortune)
    {
        return ItemRock.get(rock);
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        if (!playerIn.isSneaking())
        {
            if (!worldIn.isRemote)
            {
                TFCGuiHandler.openGui(worldIn, pos, playerIn, TFCGuiHandler.Type.ANVIL);
            }
            return true;
        }
        return false;
    }

    @Override
    @Nonnull
    public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player)
    {
        return new ItemStack(BlockRockRaw.get(rock, Rock.Type.RAW));
    }

    @Nonnull
    @Override
    public Rock getRock(ItemStack stack)
    {
        return rock;
    }

    @Nonnull
    @Override
    public RockCategory getRockCategory(ItemStack stack)
    {
        return rock.getRockCategory();
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta)
    {
        return new TEAnvilTFC();
    }
}

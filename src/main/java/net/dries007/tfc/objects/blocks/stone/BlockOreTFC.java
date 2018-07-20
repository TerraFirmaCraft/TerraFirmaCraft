/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.blocks.stone;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.util.BlockRenderLayer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import net.dries007.tfc.objects.OreEnum;
import net.dries007.tfc.objects.Rock;
import net.dries007.tfc.objects.items.metal.ItemOreTFC;
import net.dries007.tfc.util.InsertOnlyEnumTable;

public class BlockOreTFC extends Block
{
    public static final PropertyEnum<OreEnum.Grade> GRADE = PropertyEnum.create("grade", OreEnum.Grade.class);
    private static final InsertOnlyEnumTable<OreEnum, Rock, BlockOreTFC> TABLE = new InsertOnlyEnumTable<>(OreEnum.class, Rock.class);

    public static BlockOreTFC get(OreEnum ore, Rock rock)
    {
        return TABLE.get(ore, rock);
    }

    public static IBlockState get(OreEnum ore, Rock rock, OreEnum.Grade grade)
    {
        IBlockState state = TABLE.get(ore, rock).getDefaultState();
        if (!ore.graded) return state;
        return state.withProperty(GRADE, grade);
    }

    public final OreEnum ore;
    public final Rock rock;

    public BlockOreTFC(OreEnum ore, Rock rock)
    {
        super(Rock.Type.RAW.material);
        TABLE.put(ore, rock, this);
        this.ore = ore;
        this.rock = rock;
        setDefaultState(blockState.getBaseState().withProperty(GRADE, OreEnum.Grade.NORMAL));
        setSoundType(SoundType.STONE);
        setHardness(2.0F).setResistance(10.0F);
        setHarvestLevel("pickaxe", 0);
    }

    @SuppressWarnings("deprecation")
    @Override
    public IBlockState getStateFromMeta(int meta)
    {
        return getDefaultState().withProperty(GRADE, OreEnum.Grade.byMetadata(meta));
    }

    @Override
    public int getMetaFromState(IBlockState state)
    {
        return state.getValue(GRADE).getMeta();
    }

    @Override
    public Item getItemDropped(IBlockState state, Random rand, int fortune)
    {
        // todo: handle coal
        // todo: handle kimberlite (diamond)
        // todo: handle saltpeter
        return ItemOreTFC.get(ore);
    }

    @Override
    public int damageDropped(IBlockState state)
    {
        return getMetaFromState(state);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public BlockRenderLayer getBlockLayer()
    {
        return BlockRenderLayer.CUTOUT;
    }

    @Override
    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, GRADE);
    }

    @Override
    public int quantityDropped(IBlockState state, int fortune, Random random)
    {
        return super.quantityDropped(state, fortune, random); // todo: see how 1710 handles this
    }
}

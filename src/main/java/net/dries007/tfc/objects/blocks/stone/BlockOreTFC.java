/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.blocks.stone;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import net.dries007.tfc.api.types.Ore;
import net.dries007.tfc.api.types.Rock;
import net.dries007.tfc.objects.items.metal.ItemOreTFC;

@ParametersAreNonnullByDefault
public class BlockOreTFC extends Block
{
    public static final PropertyEnum<Ore.Grade> GRADE = PropertyEnum.create("grade", Ore.Grade.class);
    private static final Map<Ore, Map<Rock, BlockOreTFC>> TABLE = new HashMap<>();

    public static BlockOreTFC get(Ore ore, Rock rock)
    {
        return TABLE.get(ore).get(rock);
    }

    public static IBlockState get(Ore ore, Rock rock, Ore.Grade grade)
    {
        IBlockState state = TABLE.get(ore).get(rock).getDefaultState();
        if (!ore.isGraded()) return state;
        return state.withProperty(GRADE, grade);
    }

    public final Ore ore;
    public final Rock rock;

    public BlockOreTFC(Ore ore, Rock rock)
    {
        super(Rock.Type.RAW.material);

        if (!TABLE.containsKey(ore))
            TABLE.put(ore, new HashMap<>());
        TABLE.get(ore).put(rock, this);

        this.ore = ore;
        this.rock = rock;
        setDefaultState(blockState.getBaseState().withProperty(GRADE, Ore.Grade.NORMAL));
        setSoundType(SoundType.STONE);
        setHardness(10.0F).setResistance(10.0F);
        setHarvestLevel("pickaxe", 0);
    }

    @SuppressWarnings("deprecation")
    @Override
    @Nonnull
    public IBlockState getStateFromMeta(int meta)
    {
        return getDefaultState().withProperty(GRADE, Ore.Grade.valueOf(meta));
    }

    @Override
    public int getMetaFromState(IBlockState state)
    {
        return state.getValue(GRADE).getMeta();
    }

    @Override
    @Nonnull
    public Item getItemDropped(IBlockState state, Random rand, int fortune)
    {
        return ItemOreTFC.get(ore);
    }

    @Override
    public int damageDropped(IBlockState state)
    {
        return getMetaFromState(state);
    }

    @Override
    @Nonnull
    @SideOnly(Side.CLIENT)
    public BlockRenderLayer getRenderLayer()
    {
        return BlockRenderLayer.CUTOUT;
    }

    /**
     * Handle drops separately, so will always drop
     */
    @Override
    public boolean canDropFromExplosion(Explosion explosionIn)
    {
        return false;
    }

    @Override
    @Nonnull
    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, GRADE);
    }

    /**
     * Ore blocks should always drop from explosions, see #1325
     */
    @Override
    public void onBlockExploded(World world, BlockPos pos, Explosion explosion)
    {
        if (!world.isRemote)
        {
            dropBlockAsItem(world, pos, world.getBlockState(pos), 0);
        }
        super.onBlockExploded(world, pos, explosion);
    }

    @Override
    @Nonnull
    public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player)
    {
        return new ItemStack(state.getBlock());
    }
}
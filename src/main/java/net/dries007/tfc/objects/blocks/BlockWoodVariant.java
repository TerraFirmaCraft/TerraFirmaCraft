package net.dries007.tfc.objects.blocks;


import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Random;

import static net.dries007.tfc.Constants.MOD_ID;

public class BlockWoodVariant extends Block
{

    public final Type type;
    public final Wood wood;

    private BlockWoodVariant[] variants;

    public BlockWoodVariant(Type type, Wood wood)
    {
        super(type.material);
        this.type = type;
        this.wood = wood;
    }

    @Override
    public Item getItemDropped(IBlockState state, Random rand, int fortune)
    {
        return Items.AIR; //todo
    }

    @Override
    public void dropBlockAsItemWithChance(World worldIn, BlockPos pos, IBlockState state, float chance, int fortune)
    {
        //todo
//        super.dropBlockAsItemWithChance(worldIn, pos, state, chance, fortune);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public BlockRenderLayer getBlockLayer()
    {
        return type.isColorIndexed ? BlockRenderLayer.CUTOUT_MIPPED : BlockRenderLayer.SOLID;
    }

    @SideOnly(Side.CLIENT)
    public boolean shouldSideBeRendered(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side)
    {
        return true; //needs more work, only for the leaves
    }

    @Override
    public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state)
    {

    }

    @Override
    public int tickRate(World worldIn)
    {
        return 1; // todo: tickrate in vanilla is 2, in tfc1710 it's 10
    }

    @Override
    public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand)
    {
        super.updateTick(worldIn, pos, state, rand);
        if (worldIn.isRemote) return;
    }

    public enum Type
    {
        LOG(Material.WOOD, true, false),
        LEAVES(Material.LEAVES, false, true);
        // todo: add all the other wood things... :P

        public final Material material;
        public final boolean isAxisIndexed;
        public final boolean isColorIndexed;

        Type(Material material,boolean isAxisIndexed, boolean isColorIndexed)
        {
            this.material = material;
            this.isAxisIndexed = isAxisIndexed;
            this.isColorIndexed = isColorIndexed;
        }
    }

    public enum Wood
    {
        ASH(12600, 5970),//Black Ash
        ASPEN(9230, 5220),//Black Poplar
        BIRCH(12300, 5690),//Paper Birch
        CHESTNUT(8600, 5320),//Sweet Chestnut
        DOUGLASFIR(12500, 6950),//Douglas Fir
        HICKORY(17100, 9040),//Bitternut Hickory
        MAPLE(13400, 6540),//Red Maple
        OAK(14830, 7370),//White Oak
        PINE(14500, 8470),//Longleaf Pine
        SEQUOIA(8950, 5690),//Redwood
        SPRUCE(8640, 4730),//White Spruce
        SYCAMORE(10000, 5380),//American Sycamore
        WHITECEDAR(6500, 3960),//Northern White Cedar
        WILLOW(8150, 3900),//White Willow
        KAPOK(14320, 6690),//Chakte Kok - No data on kapok so went with the brazilian Chakte Kok(Redheart Tree)
        ACACIA(12620, 7060), //Acacia Koa
        ROSEWOOD(19570, 9740),//Brazilian Rosewood
        BLACKWOOD(15020, 7770);//Australian Blackwood
        //PALM(12970, 9590);//Red Palm

        private BlockWoodVariant ref;

        Wood(int bend, int compression)
        {

        }
    }

    public static BlockWoodVariant get(Wood wood, Type type)
    {
        return wood.ref.getVariant(type);
    }

    public BlockWoodVariant getVariant(Type t)
    {
        if (this.type == t) return this;
        if (variants == null)
        {
            Type[] types = Type.values();
            variants = new BlockWoodVariant[types.length];
            for (int i = 0; i < types.length; i++)
            {
                //noinspection ConstantConditions
                String name = getRegistryName().getResourcePath().replace(type.name().toLowerCase(), types[i].name().toLowerCase());
                variants[i] = (BlockWoodVariant) ForgeRegistries.BLOCKS.getValue(new ResourceLocation(MOD_ID, name));
            }
        }
        return variants[t.ordinal()];
    }
}

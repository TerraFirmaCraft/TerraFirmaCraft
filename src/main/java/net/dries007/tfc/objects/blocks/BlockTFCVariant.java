package net.dries007.tfc.objects.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import java.util.Random;

import static net.dries007.tfc.Constants.MOD_ID;

public class BlockTFCVariant extends Block
{
    public final Type type;

    private BlockTFCVariant[] variants;

    public BlockTFCVariant(Material m, Type type)
    {
        super(m);
        this.type = type;
        if (type == Type.GRASS || type == Type.DRY_GRASS || type.isAffectedByGravity)
        {
//            this.setTickRandomly(true); //todo: everyone for caveins? For dirt rolling down?
        }
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
    public BlockRenderLayer getBlockLayer()
    {
        return type.isColorIndexed ? BlockRenderLayer.CUTOUT : BlockRenderLayer.SOLID;
    }

    public enum Type
    {
        RAW(false, false),
        SMOOTH(false, false),
        COBBLE(true, false),
        BRICK(false, false),
        SAND(true, false),
        GRAVEL(true, false),
        DIRT(false, false),
        GRASS(false, true),
        DRY_GRASS(false, true);
        // todo: add peat
        // todo: add clay

        public final boolean isAffectedByGravity, isColorIndexed;

        Type(boolean isAffectedByGravity, boolean isColorIndexed)
        {
            this.isAffectedByGravity = isAffectedByGravity;
            this.isColorIndexed = isColorIndexed;
        }
    }

    public BlockTFCVariant getVariant(Type t)
    {
        if (this.type == t) return this;
        if (variants == null)
        {
            Type[] types = Type.values();
            variants = new BlockTFCVariant[types.length];
            for (int i = 0; i < types.length; i++)
            {
                //noinspection ConstantConditions
                String name = getRegistryName().getResourcePath().replace(type.name().toLowerCase(), types[i].name().toLowerCase());
                variants[i] = (BlockTFCVariant) ForgeRegistries.BLOCKS.getValue(new ResourceLocation(MOD_ID, name));
            }
        }
        return variants[t.ordinal()];
    }
}

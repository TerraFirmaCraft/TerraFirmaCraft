package net.dries007.tfc.objects.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.EnumPlantType;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import java.util.Random;

import static net.dries007.tfc.Constants.MOD_ID;

public class BlockTFCVariant extends Block
{
    public final Material material;
    public final Rock rock;

    private BlockTFCVariant[] variants;

    public BlockTFCVariant(Material material, Rock rock)
    {
        super(material.material);
        this.material = material;
        this.rock = rock;
        if (material == Material.GRASS || material == Material.DRY_GRASS || material.isAffectedByGravity)
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
        return material.isColorIndexed ? BlockRenderLayer.CUTOUT : BlockRenderLayer.SOLID;
    }

    @Override
    public boolean canSustainPlant(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing direction, IPlantable plantable)
    {
        EnumPlantType plantType = plantable.getPlantType(world, pos.offset(direction));

        switch (plantType)
        {
            case Plains: return material == Material.DIRT || material == Material.GRASS; // todo: dry grass?
            case Crop: return material == Material.DIRT || material == Material.GRASS; // todo: dry grass? Should this even be true? Might be required for wild crops.
            case Desert: return material == Material.SAND;
            case Cave: return true;
            case Water: return false;
            case Beach:
                return (material == Material.DIRT || material == Material.GRASS || material == Material.SAND || material == Material.DRY_GRASS) && // todo: dry grass?
                        (BlocksTFC.isWater(world.getBlockState(pos.add(1, 0, 0))) ||
                         BlocksTFC.isWater(world.getBlockState(pos.add(-1, 0, 0))) ||
                         BlocksTFC.isWater(world.getBlockState(pos.add(0, 0, 1))) ||
                         BlocksTFC.isWater(world.getBlockState(pos.add(0, 0, -1))));
            case Nether: return false;
        }

        return false;
//        return super.canSustainPlant(state, world, pos, direction, plantable);
    }

    public enum Material
    {
        RAW(net.minecraft.block.material.Material.ROCK, false, false),
        SMOOTH(net.minecraft.block.material.Material.ROCK, false, false),
        COBBLE(net.minecraft.block.material.Material.ROCK, true, false),
        BRICK(net.minecraft.block.material.Material.ROCK, false, false),
        SAND(net.minecraft.block.material.Material.SAND, true, false),
        GRAVEL(net.minecraft.block.material.Material.SAND, true, false),
        DIRT(net.minecraft.block.material.Material.GROUND, false, false),
        GRASS(net.minecraft.block.material.Material.GRASS, false, true),
        DRY_GRASS(net.minecraft.block.material.Material.GRASS, false, true);
        // todo: add peat
        // todo: add clay

        public final net.minecraft.block.material.Material material;
        public final boolean isAffectedByGravity;
        public final boolean isColorIndexed;

        Material(net.minecraft.block.material.Material material, boolean isAffectedByGravity, boolean isColorIndexed)
        {
            this.material = material;
            this.isAffectedByGravity = isAffectedByGravity;
            this.isColorIndexed = isColorIndexed;
        }
    }

    public enum Rock
    {
        GRANITE(Category.IGNEOUS_INTRUSIVE),
        DIORITE(Category.IGNEOUS_INTRUSIVE),
        GABBRO(Category.IGNEOUS_INTRUSIVE),

        SHALE(Category.SEDIMENTARY),
        CLAYSTONE(Category.SEDIMENTARY),
        ROCKSALT(Category.SEDIMENTARY),
        LIMESTONE(Category.SEDIMENTARY),
        CONGLOMERATE(Category.SEDIMENTARY),
        DOLOMITE(Category.SEDIMENTARY),
        CHERT(Category.SEDIMENTARY),
        CHALK(Category.SEDIMENTARY),

        RHYOLITE(Category.IGNEOUS_EXTRUSIVE),
        BASALT(Category.IGNEOUS_EXTRUSIVE),
        ANDESITE(Category.IGNEOUS_EXTRUSIVE),
        DACITE(Category.IGNEOUS_EXTRUSIVE),

        QUARTZITE(Category.METAMORPHIC),
        SLATE(Category.METAMORPHIC),
        PHYLLITE(Category.METAMORPHIC),
        SCHIST(Category.METAMORPHIC),
        GNEISS(Category.METAMORPHIC),
        MARBLE(Category.METAMORPHIC);

        Rock(Category category)
        {
            this.category = category;
        }

        public enum Category
        {
            SEDIMENTARY, METAMORPHIC, IGNEOUS_INTRUSIVE, IGNEOUS_EXTRUSIVE
        }

        public final Category category;
    }

    public BlockTFCVariant getVariant(Material t)
    {
        if (this.material == t) return this;
        if (variants == null)
        {
            Material[] materials = Material.values();
            variants = new BlockTFCVariant[materials.length];
            for (int i = 0; i < materials.length; i++)
            {
                //noinspection ConstantConditions
                String name = getRegistryName().getResourcePath().replace(material.name().toLowerCase(), materials[i].name().toLowerCase());
                variants[i] = (BlockTFCVariant) ForgeRegistries.BLOCKS.getValue(new ResourceLocation(MOD_ID, name));
            }
        }
        return variants[t.ordinal()];
    }
}

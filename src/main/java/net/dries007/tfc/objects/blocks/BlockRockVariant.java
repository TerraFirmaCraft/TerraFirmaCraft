package net.dries007.tfc.objects.blocks;

import net.dries007.tfc.objects.entity.EntityFallingBlockTFC;
import net.dries007.tfc.util.IFallingBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFalling;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.EnumPlantType;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Random;

import static net.dries007.tfc.Constants.MOD_ID;

public class BlockRockVariant extends Block implements IFallingBlock
{
    public final Type type;
    public final Rock rock;

    private BlockRockVariant[] variants;

    public BlockRockVariant(Type type, Rock rock)
    {
        super(type.material);
        this.type = type;
        this.rock = rock;
        if (type == Type.RAW) rock.ref = this;
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
    @SideOnly(Side.CLIENT)
    public BlockRenderLayer getBlockLayer()
    {
        return type.isColorIndexed ? BlockRenderLayer.CUTOUT : BlockRenderLayer.SOLID;
    }

    @Override
    public boolean shouldFall(IBlockState state, World world, BlockPos pos)
    {
        return type.isAffectedByGravity && IFallingBlock.super.shouldFall(state, world, pos);
    }

    @Override
    public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state)
    {
        if (shouldFall(state, worldIn, pos)) worldIn.scheduleUpdate(pos, this, tickRate(worldIn));
    }

    // IDK what the alternative is supposed to be, so I'm gonna continue using this.
    @SuppressWarnings("deprecation")
    @Override
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos)
    {
        super.neighborChanged(state, worldIn, pos, blockIn, fromPos);
        if (shouldFall(state, worldIn, pos)) worldIn.scheduleUpdate(pos, this, tickRate(worldIn));
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
        if (shouldFall(state, worldIn, pos))
        {
            if (!BlockFalling.fallInstantly && worldIn.isAreaLoaded(pos.add(-32, -32, -32), pos.add(32, 32, 32)))
            {
                worldIn.spawnEntity(new EntityFallingBlockTFC(worldIn, pos, this, worldIn.getBlockState(pos)));
            }
            else
            {
                worldIn.setBlockToAir(pos);
                pos = pos.add(0, -1, 0);
                while (canFallThrough(worldIn.getBlockState(pos)) && pos.getY() > 0)
                    pos = pos.add(0, -1, 0);
                if (pos.getY() > 0) worldIn.setBlockState(pos.up(), state); // Includes Forge's fix for data loss.
            }
        }
    }

    @SideOnly(Side.CLIENT)
    public void randomDisplayTick(IBlockState stateIn, World worldIn, BlockPos pos, Random rand)
    {
        if (!this.type.isAffectedByGravity) return;
        if (rand.nextInt(16) != 0) return;
        if (shouldFall(stateIn, worldIn, pos))
        {
            double d0 = (double)((float)pos.getX() + rand.nextFloat());
            double d1 = (double)pos.getY() - 0.05D;
            double d2 = (double)((float)pos.getZ() + rand.nextFloat());
            worldIn.spawnParticle(EnumParticleTypes.FALLING_DUST, d0, d1, d2, 0.0D, 0.0D, 0.0D, Block.getStateId(stateIn));
        }
    }

    @Override
    public boolean canSustainPlant(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing direction, IPlantable plantable)
    {
        EnumPlantType plantType = plantable.getPlantType(world, pos.offset(direction));

        switch (plantType)
        {
            case Plains: return type == Type.DIRT || type == Type.GRASS; // todo: dry grass?
            case Crop: return type == Type.DIRT || type == Type.GRASS; // todo: dry grass? Should this even be true? Might be required for wild crops.
            case Desert: return type == Type.SAND;
            case Cave: return true;
            case Water: return false;
            case Beach:
                return (type == Type.DIRT || type == Type.GRASS || type == Type.SAND || type == Type.DRY_GRASS) && // todo: dry grass?
                        (BlocksTFC.isWater(world.getBlockState(pos.add(1, 0, 0))) ||
                         BlocksTFC.isWater(world.getBlockState(pos.add(-1, 0, 0))) ||
                         BlocksTFC.isWater(world.getBlockState(pos.add(0, 0, 1))) ||
                         BlocksTFC.isWater(world.getBlockState(pos.add(0, 0, -1))));
            case Nether: return false;
        }

        return false;
//        return super.canSustainPlant(state, world, pos, direction, plantable);
    }

    public enum Type
    {
        RAW(Material.ROCK, false, false),
        SMOOTH(Material.ROCK, false, false),
        COBBLE(Material.ROCK, true, false),
        BRICK(Material.ROCK, false, false),
        SAND(Material.SAND, true, false),
        GRAVEL(Material.SAND, true, false),
        DIRT(Material.GROUND, false, false),
        GRASS(Material.GRASS, false, true),
        DRY_GRASS(Material.GRASS, false, true),
        CLAY(Material.GRASS, false, false),
        CLAY_GRASS(Material.GRASS, false, true),

        // todo: add peat

        ;
        public final Material material;
        public final boolean isAffectedByGravity;
        public final boolean isColorIndexed;

        Type(Material material, boolean isAffectedByGravity, boolean isColorIndexed)
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

        public final Category category;
        private BlockRockVariant ref;

        Rock(Category category)
        {
            this.category = category;
        }

        public enum Category
        {
            SEDIMENTARY, METAMORPHIC, IGNEOUS_INTRUSIVE, IGNEOUS_EXTRUSIVE
        }
    }

    public static BlockRockVariant get(Rock rock, Type type)
    {
        return rock.ref.getVariant(type);
    }

    public BlockRockVariant getVariant(Type t)
    {
        if (this.type == t) return this;
        if (variants == null)
        {
            Type[] types = Type.values();
            variants = new BlockRockVariant[types.length];
            for (int i = 0; i < types.length; i++)
            {
                //noinspection ConstantConditions
                String name = getRegistryName().getResourcePath().replace(type.name().toLowerCase(), types[i].name().toLowerCase());
                variants[i] = (BlockRockVariant) ForgeRegistries.BLOCKS.getValue(new ResourceLocation(MOD_ID, name));
            }
        }
        return variants[t.ordinal()];
    }
}

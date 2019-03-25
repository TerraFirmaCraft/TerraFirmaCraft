/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.blocks.stone;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.block.Block;
import net.minecraft.block.BlockFarmland;
import net.minecraft.block.BlockGrassPath;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import mcp.MethodsReturnNonnullByDefault;
import net.dries007.tfc.api.types.Rock;
import net.dries007.tfc.objects.blocks.BlocksTFC;
import net.dries007.tfc.objects.blocks.plants.BlockPlantTFC;
import net.dries007.tfc.objects.items.rock.ItemRock;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.OreDictionaryHelper;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class BlockRockVariant extends Block
{
    private static final Map<Rock, EnumMap<Rock.Type, BlockRockVariant>> TABLE = new HashMap<>();

    public static BlockRockVariant get(Rock rock, Rock.Type type)
    {
        return TABLE.get(rock).get(type);
    }

    public static BlockRockVariant create(Rock rock, Rock.Type type)
    {
        switch (type)
        {
            case RAW:
                return new BlockRockRaw(type, rock);
            case FARMLAND:
                return new BlockFarmlandTFC(type, rock);
            case PATH:
                return new BlockPathTFC(type, rock);
            case GRASS:
            case DRY_GRASS:
            case CLAY_GRASS:
                return new BlockRockVariantConnected(type, rock);
            case SAND:
            case DIRT:
            case CLAY:
            case GRAVEL:
            case COBBLE:
                return new BlockRockVariantFallable(type, rock);
            default:
                return new BlockRockVariant(type, rock);
        }
    }

    public final Rock.Type type;
    public final Rock rock;

    public BlockRockVariant(Rock.Type type, Rock rock)
    {
        super(type.material);

        if (!TABLE.containsKey(rock))
            TABLE.put(rock, new EnumMap<>(Rock.Type.class));
        TABLE.get(rock).put(type, this);

        this.type = type;
        this.rock = rock;
        if (type.isGrass) setTickRandomly(true);
        switch (type)
        {
            case BRICKS:
            case RAW:
                setSoundType(SoundType.STONE);
                setHardness(2.0F).setResistance(10.0F);
                setHarvestLevel("pickaxe", 0);
                break;
            case COBBLE:
            case SMOOTH:
                setSoundType(SoundType.STONE);
                setHardness(1.5F).setResistance(10.0F);
                setHarvestLevel("pickaxe", 0);
                break;
            case SAND:
                setSoundType(SoundType.SAND);
                setHardness(0.5F);
                setHarvestLevel("shovel", 0);
                break;
            case DIRT:
            case PATH:
            case FARMLAND:
                setSoundType(SoundType.GROUND);
                setHardness(0.5F);
                setHarvestLevel("shovel", 0);
                break;
            case GRAVEL:
                setSoundType(SoundType.GROUND);
                setHardness(0.6F);
                setHarvestLevel("shovel", 0);
                break;
            case CLAY:
            case CLAY_GRASS:
            case GRASS:
            case DRY_GRASS:
                setSoundType(SoundType.PLANT);
                setHardness(0.6F);
                setHarvestLevel("shovel", 0);
                break;
        }
        OreDictionaryHelper.registerRockType(this, type, rock);
    }

    public BlockRockVariant getVariant(Rock.Type t)
    {
        return TABLE.get(rock).get(t);
    }

    @Override
    @SideOnly(Side.CLIENT)
    @SuppressWarnings("deprecation")
    public boolean shouldSideBeRendered(IBlockState blockState, IBlockAccess world, BlockPos pos, EnumFacing side)
    {
        switch (this.type)
        {
            case PATH:
            case FARMLAND:
                switch (side)
                {
                    case UP:
                        return true;
                    case NORTH:
                    case SOUTH:
                    case WEST:
                    case EAST:
                        IBlockState state = world.getBlockState(pos.offset(side));
                        Block block = state.getBlock();
                        if (state.isOpaqueCube()) return false;
                        if (block instanceof BlockFarmland || block instanceof BlockGrassPath) return false;
                        if (block instanceof BlockRockVariant)
                        {
                            switch (((BlockRockVariant) block).type)
                            {
                                case FARMLAND:
                                case PATH:
                                    return false;
                                default:
                                    return true;
                            }
                        }
                        return true;
                    case DOWN:
                        return super.shouldSideBeRendered(blockState, world, pos, side);
                }
            default:
                return super.shouldSideBeRendered(blockState, world, pos, side);
        }
    }

    @Override
    public void randomTick(World world, BlockPos pos, IBlockState state, Random rand)
    {
        if (world.isRemote) return;
        if (type.isGrass) Helpers.spreadGrass(world, pos, state, rand);
        super.randomTick(world, pos, state, rand);
    }

    @Override
    public Item getItemDropped(IBlockState state, Random rand, int fortune)
    {
        switch (type)
        {
            case RAW:
                return ItemRock.get(rock);
            case CLAY:
            case CLAY_GRASS:
                return Items.CLAY_BALL; // todo: own clay or event for clay making?
            default:
                return super.getItemDropped(state, rand, fortune);
            case GRASS:
            case DRY_GRASS:
            case PATH:
                return Item.getItemFromBlock(get(rock, Rock.Type.DIRT));
        }
    }

    @Override
    public int damageDropped(IBlockState state)
    {
        return getMetaFromState(state);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public BlockRenderLayer getRenderLayer()
    {
        return type.isGrass ? BlockRenderLayer.CUTOUT : BlockRenderLayer.SOLID;
    }

    @Override
    public int quantityDropped(IBlockState state, int fortune, Random random)
    {
        switch (type)
        {
            case CLAY:
            case CLAY_GRASS:
                return 4;
            case RAW:
                return 1 + random.nextInt(3);
            default:
                return super.quantityDropped(state, fortune, random);
        }
    }

    @Override
    public boolean canSustainPlant(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing direction, IPlantable plantable)
    {
        if (plantable instanceof BlockPlantTFC)
        {
            switch (((BlockPlantTFC) plantable).getPlantTypeTFC())
            {
                case CLAY:
                    return type == Rock.Type.DIRT || type == Rock.Type.GRASS || type == Rock.Type.DRY_GRASS || type == Rock.Type.CLAY || type == Rock.Type.CLAY_GRASS;
                case DRY_CLAY:
                    return type == Rock.Type.DRY_GRASS || type == Rock.Type.SAND || type == Rock.Type.CLAY || type == Rock.Type.CLAY_GRASS;
                case DRY:
                    return type == Rock.Type.DRY_GRASS || type == Rock.Type.SAND;
                case FRESH_WATER:
                    return type == Rock.Type.DIRT || type == Rock.Type.GRASS || type == Rock.Type.DRY_GRASS || type == Rock.Type.GRAVEL;
                case SALT_WATER:
                    return type == Rock.Type.DIRT || type == Rock.Type.GRASS || type == Rock.Type.DRY_GRASS || type == Rock.Type.SAND || type == Rock.Type.GRAVEL;
                case FRESH_BEACH:
                    // todo: expand? I think a 2x2 radius is much better in a world where you can't move water sources.
                    return (type == Rock.Type.DIRT || type == Rock.Type.GRASS || type == Rock.Type.SAND || type == Rock.Type.DRY_GRASS) &&
                        (BlocksTFC.isFreshWater(world.getBlockState(pos.add(1, 0, 0))) ||
                            BlocksTFC.isFreshWater(world.getBlockState(pos.add(-1, 0, 0))) ||
                            BlocksTFC.isFreshWater(world.getBlockState(pos.add(0, 0, 1))) ||
                            BlocksTFC.isFreshWater(world.getBlockState(pos.add(0, 0, -1))));
                case SALT_BEACH:
                    // todo: expand? I think a 2x2 radius is much better in a world where you can't move water sources.
                    return (type == Rock.Type.DIRT || type == Rock.Type.GRASS || type == Rock.Type.SAND || type == Rock.Type.DRY_GRASS) &&
                        (BlocksTFC.isSaltWater(world.getBlockState(pos.add(1, 0, 0))) ||
                            BlocksTFC.isSaltWater(world.getBlockState(pos.add(-1, 0, 0))) ||
                            BlocksTFC.isSaltWater(world.getBlockState(pos.add(0, 0, 1))) ||
                            BlocksTFC.isSaltWater(world.getBlockState(pos.add(0, 0, -1))));
            }
        }

        switch (plantable.getPlantType(world, pos.offset(direction)))
        {
            case Plains:
                return type == Rock.Type.DIRT || type == Rock.Type.GRASS || type == Rock.Type.DRY_GRASS;
            case Crop:
                return type == Rock.Type.DIRT || type == Rock.Type.GRASS || type == Rock.Type.FARMLAND || type == Rock.Type.DRY_GRASS;
            case Desert:
                return type == Rock.Type.SAND;
            case Cave:
                return true;
            case Water:
                return false;
            case Beach:
                // todo: expand? I think a 2x2 radius is much better in a world where you can't move water sources.
                return (type == Rock.Type.DIRT || type == Rock.Type.GRASS || type == Rock.Type.SAND || type == Rock.Type.DRY_GRASS) && // todo: dry grass?
                    (BlocksTFC.isWater(world.getBlockState(pos.add(1, 0, 0))) ||
                        BlocksTFC.isWater(world.getBlockState(pos.add(-1, 0, 0))) ||
                        BlocksTFC.isWater(world.getBlockState(pos.add(0, 0, 1))) ||
                        BlocksTFC.isWater(world.getBlockState(pos.add(0, 0, -1))));
            case Nether:
                return false;
        }

        return false;
    }
}

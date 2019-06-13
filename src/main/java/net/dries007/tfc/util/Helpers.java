/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.util;

import java.util.*;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.common.base.Joiner;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraft.world.ChunkCache;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.registries.IForgeRegistryEntry;

import io.netty.buffer.ByteBuf;
import net.dries007.tfc.api.registries.TFCRegistries;
import net.dries007.tfc.api.types.Plant;
import net.dries007.tfc.api.types.Rock;
import net.dries007.tfc.api.util.TFCConstants;
import net.dries007.tfc.objects.blocks.BlockPeat;
import net.dries007.tfc.objects.blocks.BlocksTFC;
import net.dries007.tfc.objects.blocks.plants.BlockShortGrassTFC;
import net.dries007.tfc.objects.blocks.stone.BlockRockVariant;
import net.dries007.tfc.util.functionalinterfaces.FacingChecker;
import net.dries007.tfc.world.classic.ClimateTFC;
import net.dries007.tfc.world.classic.chunkdata.ChunkDataTFC;

import static net.dries007.tfc.Constants.facingPriorityLists;
import static net.minecraft.util.EnumFacing.*;

public final class Helpers
{
    private static final Joiner JOINER_DOT = Joiner.on('.');


    public static void spreadGrass(World world, BlockPos pos, IBlockState us, Random rand)
    {
        if (world.getLightFromNeighbors(pos.up()) < 4 && world.getBlockState(pos.up()).getLightOpacity(world, pos.up()) > 2)
        {
            if (us.getBlock() instanceof BlockPeat)
            {
                world.setBlockState(pos, BlocksTFC.PEAT.getDefaultState());
            }
            else if (us.getBlock() instanceof BlockRockVariant)
            {
                BlockRockVariant block = ((BlockRockVariant) us.getBlock());
                world.setBlockState(pos, block.getVariant(block.getType().getNonGrassVersion()).getDefaultState());
            }
        }
        else
        {
            if (world.getLightFromNeighbors(pos.up()) < 9) return;

            for (int i = 0; i < 4; ++i)
            {
                BlockPos target = pos.add(rand.nextInt(3) - 1, rand.nextInt(5) - 3, rand.nextInt(3) - 1);
                if (world.isOutsideBuildHeight(target) || !world.isBlockLoaded(target)) return;
                BlockPos up = target.add(0, 1, 0);

                IBlockState current = world.getBlockState(target);
                if (!BlocksTFC.isSoil(current) || BlocksTFC.isGrass(current)) continue;
                if (world.getLightFromNeighbors(up) < 4 || world.getBlockState(up).getLightOpacity(world, up) > 3)
                    continue;

                if (current.getBlock() instanceof BlockPeat)
                {
                    world.setBlockState(target, BlocksTFC.PEAT_GRASS.getDefaultState());
                }
                else if (current.getBlock() instanceof BlockRockVariant)
                {
                    Rock.Type spreader = Rock.Type.GRASS;
                    if ((us.getBlock() instanceof BlockRockVariant) && ((BlockRockVariant) us.getBlock()).getType() == Rock.Type.DRY_GRASS)
                        spreader = Rock.Type.DRY_GRASS;

                    BlockRockVariant block = ((BlockRockVariant) current.getBlock());
                    world.setBlockState(target, block.getVariant(block.getType().getGrassVersion(spreader)).getDefaultState());
                }
            }

            for (Plant plant : TFCRegistries.PLANTS.getValuesCollection())
            {
                if (plant.getPlantType() == Plant.PlantType.SHORT_GRASS && rand.nextFloat() < 0.5f)
                {
                    float temp = ClimateTFC.getHeightAdjustedTemp(world, pos.up());
                    BlockShortGrassTFC plantBlock = BlockShortGrassTFC.get(plant);

                    if (world.isAirBlock(pos.up()) &&
                        plant.isValidLocation(temp, ChunkDataTFC.getRainfall(world, pos.up()), Math.subtractExact(world.getLightFor(EnumSkyBlock.SKY, pos.up()), world.getSkylightSubtracted())) &&
                        plant.isValidGrowthTemp(temp) &&
                        rand.nextDouble() < plantBlock.getGrowthRate(world, pos.up()))
                    {
                        world.setBlockState(pos.up(), plantBlock.getDefaultState());
                    }
                }
            }
        }
    }

    public static boolean containsAnyOfCaseInsensitive(Collection<String> input, String... items)
    {
        Set<String> itemsSet = Arrays.stream(items).map(String::toLowerCase).collect(Collectors.toSet());
        return input.stream().map(String::toLowerCase).anyMatch(itemsSet::contains);
    }

    @SuppressWarnings("unchecked")
    @Nullable
    public static <T extends TileEntity> T getTE(IBlockAccess world, BlockPos pos, Class<T> aClass)
    {
        TileEntity te = world.getTileEntity(pos);
        if (!aClass.isInstance(te)) return null;
        return (T) te;
    }

    /**
     * Used to get a tile entity when it is nessecary to make saftey checks
     * See {@link net.minecraft.block.Block#getActualState(IBlockState, IBlockAccess, BlockPos)}
     *
     * @param world  The world
     * @param pos    The position
     * @param aClass The TE class to return
     * @param <T>    The type of the TE
     * @return the te if it exists, or null if it doesn't
     */
    @SuppressWarnings("unchecked")
    @Nullable
    public static <T extends TileEntity> T getTESafely(IBlockAccess world, BlockPos pos, Class<T> aClass)
    {
        TileEntity te = world instanceof ChunkCache ? ((ChunkCache) world).getTileEntity(pos, Chunk.EnumCreateEntityType.CHECK) : world.getTileEntity(pos);
        if (!aClass.isInstance(te)) return null;
        return (T) te;
    }

    public static String getEnumName(Enum<?> anEnum)
    {
        return JOINER_DOT.join(TFCConstants.MOD_ID, "enum", anEnum.getDeclaringClass().getSimpleName(), anEnum).toLowerCase();
    }

    public static String getTypeName(IForgeRegistryEntry<?> type)
    {
        //noinspection ConstantConditions
        return JOINER_DOT.join(TFCConstants.MOD_ID, "types", type.getRegistryType().getSimpleName(), type.getRegistryName().getPath()).toLowerCase();
    }

    public static boolean playerHasItemMatchingOre(InventoryPlayer playerInv, String ore)
    {
        for (ItemStack stack : playerInv.mainInventory)
        {
            if (!stack.isEmpty() && OreDictionaryHelper.doesStackMatchOre(stack, ore))
            {
                return true;
            }
        }
        for (ItemStack stack : playerInv.armorInventory)
        {
            if (!stack.isEmpty() && OreDictionaryHelper.doesStackMatchOre(stack, ore))
            {
                return true;
            }
        }
        for (ItemStack stack : playerInv.offHandInventory)
        {
            if (!stack.isEmpty() && OreDictionaryHelper.doesStackMatchOre(stack, ore))
            {
                return true;
            }
        }
        return false;
    }

    @Nonnull
    public static ItemStack consumeItem(ItemStack stack, int amount)
    {
        if (stack.getCount() <= amount)
        {
            return ItemStack.EMPTY;
        }
        stack.shrink(amount);
        return stack;
    }

    @Nonnull
    public static ItemStack consumeItem(ItemStack stack, EntityPlayer player, int amount)
    {
        return player.isCreative() ? stack : consumeItem(stack, amount);
    }

    /**
     * Simple method to spawn items in the world at a precise location, rather than using InventoryHelper
     */
    public static void spawnItemStack(World world, BlockPos pos, ItemStack stack)
    {
        if (stack.isEmpty())
            return;
        EntityItem entityitem = new EntityItem(world, pos.getX(), pos.getY(), pos.getZ(), stack);
        world.spawnEntity(entityitem);
    }

    /**
     * @see #getAValidFacing
     * very simillar, made for horizontally rotatable blocks
     * @param preferredSide must be an {@link EnumFacing#HORIZONTALS}
     * @return A valid Horizontal facing or null if none is
     */
    public static EnumFacing getAValidHorizontal(World worldIn, BlockPos pos, FacingChecker checker, EnumFacing preferredSide)
    {
        int index = preferredSide.getHorizontalIndex();
        if (index == -1)
            throw new IllegalArgumentException("Received side was not a horizontal");
        return getAValidFacing(worldIn, pos, checker, facingPriorityLists.get(preferredSide.getHorizontalIndex()));
    }

    /**
     * This is meant to avoid Intellij's warnings about null fields that are injected to at runtime
     * Use this for things like @ObjectHolder, @CapabilityInject, etc.
     * AKA - The @Nullable is intentional. If it crashes your dev env, then fix your dev env, not this. :)
     *
     * @param <T> anything and everything
     * @return null, but not null
     */
    @Nonnull
    @SuppressWarnings("ConstantConditions")
    public static <T> T getNull()
    {
        return null;
    }

    /**
     * Primarily for use in placing checks. Determines a valid facing for a block.
     *
     * @param pos           position that the block does or is going to occupy.
     * @param checker       the checking algorithm. For simple solid side checking,
     * @param possibleSides a collection of all sides the block can face, sorted by priority.
     * @return Found facing or null is none is found.
     * @see FacingChecker#canHangAt
     */
    public static EnumFacing getAValidFacing(World worldIn, BlockPos pos, FacingChecker checker, Collection<EnumFacing> possibleSides)
    {
        for (EnumFacing side : possibleSides)
        {
            if (side != null && checker.canFace(worldIn, pos, side))
            {
                return side;
            }
        }
        return null;
    }

    public static void writeResourceLocation(ByteBuf buf, @Nullable ResourceLocation loc)
    {
        buf.writeBoolean(loc != null);
        if (loc != null)
        {
            ByteBufUtils.writeUTF8String(buf, loc.toString());
        }
    }


    @Nullable
    public static ResourceLocation readResourceLocation(ByteBuf buf)
    {
        if (buf.readBoolean())
        {
            return new ResourceLocation(ByteBufUtils.readUTF8String(buf));
        }
        return null;
    }
}

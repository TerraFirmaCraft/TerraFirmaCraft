/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.util;

import java.util.Arrays;
import java.util.Collection;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;

import com.google.common.base.Joiner;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.oredict.OreDictionary;

import net.dries007.tfc.Constants;
import net.dries007.tfc.objects.Rock;
import net.dries007.tfc.objects.blocks.BlockPeat;
import net.dries007.tfc.objects.blocks.BlocksTFC;
import net.dries007.tfc.objects.blocks.stone.BlockRockVariant;

public final class Helpers
{
    private static final Joiner JOINER_DOT = Joiner.on('.');

    public static void spreadGrass(World world, BlockPos pos, IBlockState us, Random rand)
    {
        if (world.getLightFromNeighbors(pos.up()) < 4 && world.getBlockState(pos.up()).getLightOpacity(world, pos.up()) > 2)
        {
            if (us.getBlock() instanceof BlockPeat)
            {
                //noinspection ConstantConditions
                world.setBlockState(pos, BlocksTFC.PEAT.getDefaultState());
            }
            else if (us.getBlock() instanceof BlockRockVariant)
            {
                BlockRockVariant block = ((BlockRockVariant) us.getBlock());
                world.setBlockState(pos, block.getVariant(block.type.getNonGrassVersion()).getDefaultState());
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
                    //noinspection ConstantConditions
                    world.setBlockState(target, BlocksTFC.PEAT_GRASS.getDefaultState());
                }
                else if (current.getBlock() instanceof BlockRockVariant)
                {
                    Rock.Type spreader = Rock.Type.GRASS;
                    if ((us.getBlock() instanceof BlockRockVariant) && ((BlockRockVariant) us.getBlock()).type == Rock.Type.DRY_GRASS)
                        spreader = Rock.Type.DRY_GRASS;

                    BlockRockVariant block = ((BlockRockVariant) current.getBlock());
                    world.setBlockState(target, block.getVariant(block.type.getGrassVersion(spreader)).getDefaultState());
                }
            }
        }
    }

    public static boolean containsAnyOfCaseInsensitive(Collection<String> input, String... items)
    {
        Set<String> itemsSet = Arrays.stream(items).map(String::toLowerCase).collect(Collectors.toSet());
        return input.stream().map(String::toLowerCase).anyMatch(itemsSet::contains);
    }

    public static <T extends TileEntity> T getTE(IBlockAccess world, BlockPos pos, Class<T> aClass)
    {
        TileEntity te = world.getTileEntity(pos);
        if (!aClass.isInstance(te)) return null;
        //noinspection unchecked
        return (T) te;
    }

    public static String getEnumName(Enum<?> anEnum)
    {
        return JOINER_DOT.join(Constants.MOD_ID, "enum", anEnum.getDeclaringClass().getSimpleName(), anEnum).toLowerCase();
    }

    @Nonnull
    public static ItemStack consumeItem(ItemStack stack, int amount)
    {
        if (stack.getCount() <= amount) return ItemStack.EMPTY;
        stack.shrink(amount);
        return stack;
    }

    @Nonnull
    public static ItemStack consumeItem(ItemStack stack, EntityPlayer player, int amount)
    {
        return player.isCreative() ? stack : consumeItem(stack, amount);
    }

    // Checks if an itemstack has the ore name 'name'
    public static boolean doesStackMatchOre(@Nonnull ItemStack stack, String name)
    {
        if (stack.isEmpty()) return false;
        int[] ids = OreDictionary.getOreIDs(stack);
        for (int id : ids)
        {
            String oreName = OreDictionary.getOreName(id);
            if (name.equals(oreName))
            {
                return true;
            }
        }
        return false;
    }

    // Checks is an ItemStack has ore names, which have a certain prefix
    // used to search for all 'ingots' / all 'plates' etc.
    public static boolean doesStackMatchOrePrefix(@Nonnull ItemStack stack, String prefix)
    {
        if (stack.isEmpty()) return false;
        int[] ids = OreDictionary.getOreIDs(stack);
        for (int id : ids)
        {
            String oreName = OreDictionary.getOreName(id);
            if (oreName.length() >= prefix.length())
            {
                if (oreName.substring(0, prefix.length()).equals(prefix))
                {
                    return true;
                }
            }
        }
        return false;
    }

    // This both checks if an ore dictionary entry exists, and it it has at least one itemstack
    public static boolean doesOreHaveStack(String ore)
    {
        if (!OreDictionary.doesOreNameExist(ore)) return false;
        NonNullList<ItemStack> stacks = OreDictionary.getOres(ore);
        return !stacks.isEmpty();
    }
}

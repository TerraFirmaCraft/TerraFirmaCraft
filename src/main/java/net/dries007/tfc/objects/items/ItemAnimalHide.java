/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.items;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import net.dries007.tfc.api.capability.size.Size;
import net.dries007.tfc.api.capability.size.Weight;
import net.dries007.tfc.api.util.IPlaceableItem;
import net.dries007.tfc.objects.blocks.BlocksTFC;
import net.dries007.tfc.util.OreDictionaryHelper;

import static net.dries007.tfc.objects.blocks.BlockPlacedHide.SIZE;

@ParametersAreNonnullByDefault
public class ItemAnimalHide extends ItemTFC
{
    private static final Map<HideType, Map<HideSize, ItemAnimalHide>> TABLE = new HashMap<>();

    @Nonnull
    public static ItemAnimalHide get(HideType type, HideSize size)
    {
        return TABLE.get(type).get(size);
    }

    protected final HideSize size;
    private final HideType type;

    ItemAnimalHide(HideType type, HideSize size)
    {
        this.type = type;
        this.size = size;

        if (!TABLE.containsKey(type))
        {
            TABLE.put(type, new HashMap<>());
        }
        TABLE.get(type).put(size, this);
    }

    @Override
    @Nonnull
    public Size getSize(ItemStack stack)
    {
        switch (size)
        {
            case LARGE:
                return Size.SMALL;
            case MEDIUM:
                return Size.VERY_SMALL;
            case SMALL:
            default:
                return Size.TINY;
        }
    }

    @Override
    @Nonnull
    public Weight getWeight(ItemStack stack)
    {
        return Weight.LIGHT;
    }

    public enum HideSize implements IStringSerializable
    {
        SMALL, MEDIUM, LARGE;

        private static final HideSize[] VALUES = values();

        @Nonnull
        public static HideSize valueOf(int index)
        {
            return index < 0 || index > VALUES.length ? MEDIUM : VALUES[index];
        }

        @Override
        public String getName()
        {
            return this.name().toLowerCase();
        }
    }

    public enum HideType
    {
        RAW, SOAKED, SCRAPED, PREPARED
    }

    public static class Soaked extends ItemAnimalHide implements IPlaceableItem
    {
        Soaked(HideType type, HideSize size)
        {
            super(type, size);
        }

        @Override
        public boolean placeItemInWorld(World world, BlockPos pos, ItemStack stack, EntityPlayer player, @Nullable EnumFacing facing, @Nullable Vec3d hitVec)
        {
            IBlockState stateAt = world.getBlockState(pos);
            BlockPos posAbove = pos.up();
            IBlockState stateAbove = world.getBlockState(posAbove);
            ItemStack stackAt = stateAt.getBlock().getPickBlock(stateAt, null, world, pos, player);
            if (facing == EnumFacing.UP && OreDictionaryHelper.doesStackMatchOre(stackAt, "logWood") && stateAbove.getBlock().isAir(stateAbove, world, posAbove))
            {
                if (!world.isRemote)
                {
                    world.setBlockState(posAbove, BlocksTFC.PLACED_HIDE.getDefaultState().withProperty(SIZE, size));
                }
                return true;
            }
            return false;
        }
    }
}

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

import net.minecraft.block.BlockBed;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
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

    @Nonnull
    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos footPos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        if (!worldIn.isRemote && this.size == HideSize.LARGE && facing == EnumFacing.UP && worldIn.getBlockState(footPos).getBlock() == BlocksTFC.THATCH && worldIn.getBlockState(footPos.offset(player.getHorizontalFacing())).getBlock() == BlocksTFC.THATCH)
        {
            ItemStack stack = player.getHeldItem(hand);
            BlockPos headPos = footPos.offset(player.getHorizontalFacing());
            //Creating a thatch bed
            if (player.canPlayerEdit(footPos, facing, stack) && player.canPlayerEdit(headPos, facing, stack))
            {
                IBlockState footState = BlocksTFC.THATCH_BED.getDefaultState().withProperty(BlockBed.OCCUPIED, false).withProperty(BlockBed.FACING, player.getHorizontalFacing()).withProperty(BlockBed.PART, BlockBed.EnumPartType.FOOT);
                IBlockState headState = BlocksTFC.THATCH_BED.getDefaultState().withProperty(BlockBed.OCCUPIED, false).withProperty(BlockBed.FACING, player.getHorizontalFacing().getOpposite()).withProperty(BlockBed.PART, BlockBed.EnumPartType.HEAD);
                worldIn.setBlockState(footPos, footState, 10);
                worldIn.setBlockState(headPos, headState, 10);
                SoundType soundtype = BlocksTFC.THATCH_BED.getSoundType(footState, worldIn, footPos, player);
                worldIn.playSound(null, footPos, soundtype.getPlaceSound(), SoundCategory.BLOCKS, (soundtype.getVolume() + 1.0F) / 2.0F, soundtype.getPitch() * 0.8F);
                stack.shrink(1);
                return EnumActionResult.SUCCESS;
            }
        }
        return EnumActionResult.PASS;
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
        RAW, SOAKED, SCRAPED, PREPARED, SHEEPSKIN
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

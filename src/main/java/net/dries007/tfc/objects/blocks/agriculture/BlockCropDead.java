/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.blocks.agriculture;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.block.Block;
import net.minecraft.block.BlockBush;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.EnumPlantType;

import net.dries007.tfc.api.capability.player.CapabilityPlayerData;
import net.dries007.tfc.api.types.ICrop;
import net.dries007.tfc.objects.items.ItemSeedsTFC;
import net.dries007.tfc.util.agriculture.Crop;
import net.dries007.tfc.util.skills.SimpleSkill;
import net.dries007.tfc.util.skills.SkillType;

@ParametersAreNonnullByDefault
public class BlockCropDead extends BlockBush
{
    /* true if the crop spawned in the wild, means it ignores growth conditions i.e. farmland */
    public static final PropertyBool MATURE = PropertyBool.create("mature");

    // binary flags for state and metadata conversion
    private static final int META_MATURE = 1;

    // static field and methods for conversion from crop to Block
    private static final Map<ICrop, BlockCropDead> MAP = new HashMap<>();

    public static BlockCropDead get(ICrop crop)
    {
        return MAP.get(crop);
    }

    public static Set<ICrop> getCrops()
    {
        return MAP.keySet();
    }

    protected final ICrop crop;

    public BlockCropDead(ICrop crop)
    {
        super(Material.PLANTS);

        this.crop = crop;
        if (MAP.put(crop, this) != null)
        {
            throw new IllegalStateException("There can only be one.");
        }

        setSoundType(SoundType.PLANT);
        setHardness(0.6f);
    }

    @Nonnull
    public ICrop getCrop()
    {
        return crop;
    }

    @Override
    @Nonnull
    @SuppressWarnings("deprecation")
    public IBlockState getStateFromMeta(int meta)
    {
        return getDefaultState().withProperty(MATURE, (meta & META_MATURE) > 0);
    }

    @Override
    public int getMetaFromState(IBlockState state)
    {
        return state.getValue(MATURE) ? META_MATURE : 0;
    }

    @Nonnull
    @Override
    public Item getItemDropped(IBlockState state, Random rand, int fortune)
    {
        return ItemSeedsTFC.get(crop);
    }

    @Override
    @Nonnull
    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, MATURE);
    }

    @Override
    @Nonnull
    public Block.EnumOffsetType getOffsetType()
    {
        return Block.EnumOffsetType.XZ;
    }

    @Override
    public int quantityDropped(IBlockState state, int fortune, Random random)
    {
        // dead crops always drop at least 1 seed
        int count = 1;
        if (state.getValue(MATURE))
        {
            // (mature and dead) crops always drop 1 extra seed
            count++;
            // mature crops have a chance to drop a bonus, dead or alive
            EntityPlayer player = harvesters.get();
            if (player != null)
            {
                SimpleSkill skill = CapabilityPlayerData.getSkill(player, SkillType.AGRICULTURE);
                if (skill != null)
                {
                    count += Crop.getSkillSeedBonus(skill, RANDOM);
                    skill.add(0.04f);
                }
            }
        }

        return count;
    }

    @Override
    @Nonnull
    public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player)
    {
        return new ItemStack(ItemSeedsTFC.get(crop));
    }

    @Override
    public boolean canBlockStay(World world, BlockPos pos, IBlockState state)
    {
        IBlockState soil = world.getBlockState(pos.down());
        return soil.getBlock().canSustainPlant(soil, world, pos.down(), EnumFacing.UP, this);
    }

    @Override
    @Nonnull
    @SuppressWarnings("deprecation")
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos)
    {
        return new AxisAlignedBB(0.125D, 0.0D, 0.125D, 0.875D, 1.0D, 0.875D);
    }

    @Nonnull
    @Override
    public EnumPlantType getPlantType(IBlockAccess world, BlockPos pos)
    {
        return EnumPlantType.Crop;
    }
}

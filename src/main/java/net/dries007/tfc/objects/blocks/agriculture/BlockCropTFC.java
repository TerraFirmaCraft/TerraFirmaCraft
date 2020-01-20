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
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.block.BlockBush;
import net.minecraft.block.IGrowable;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.EnumPlantType;

import net.dries007.tfc.api.capability.player.CapabilityPlayerData;
import net.dries007.tfc.api.types.ICrop;
import net.dries007.tfc.objects.blocks.BlocksTFC;
import net.dries007.tfc.objects.items.ItemSeedsTFC;
import net.dries007.tfc.objects.te.TEPlacedItemFlat;
import net.dries007.tfc.objects.te.TETickCounter;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.climate.ClimateTFC;
import net.dries007.tfc.util.skills.SimpleSkill;
import net.dries007.tfc.util.skills.SkillTier;
import net.dries007.tfc.util.skills.SkillType;
import net.dries007.tfc.world.classic.chunkdata.ChunkDataTFC;

@ParametersAreNonnullByDefault
public abstract class BlockCropTFC extends BlockBush implements IGrowable
{
    // model boxes
    private static final AxisAlignedBB[] CROPS_AABB = new AxisAlignedBB[] {
        new AxisAlignedBB(0.125D, 0.0D, 0.125D, 0.875D, 0.125D, 0.875D),
        new AxisAlignedBB(0.125D, 0.0D, 0.125D, 0.875D, 0.25D, 0.875D),
        new AxisAlignedBB(0.125D, 0.0D, 0.125D, 0.875D, 0.375D, 0.875D),
        new AxisAlignedBB(0.125D, 0.0D, 0.125D, 0.875D, 0.5D, 0.875D),
        new AxisAlignedBB(0.125D, 0.0D, 0.125D, 0.875D, 0.625D, 0.875D),
        new AxisAlignedBB(0.125D, 0.0D, 0.125D, 0.875D, 0.75D, 0.875D),
        new AxisAlignedBB(0.125D, 0.0D, 0.125D, 0.875D, 0.875D, 0.875D),
        new AxisAlignedBB(0.125D, 0.0D, 0.125D, 0.875D, 1.0D, 0.875D)
    };

    // stage properties
    public static final PropertyInteger STAGE_8 = PropertyInteger.create("stage", 0, 7);
    public static final PropertyInteger STAGE_7 = PropertyInteger.create("stage", 0, 6);
    public static final PropertyInteger STAGE_6 = PropertyInteger.create("stage", 0, 5);
    public static final PropertyInteger STAGE_5 = PropertyInteger.create("stage", 0, 4);

    /* true if the crop spawned in the wild, means it ignores growth conditions i.e. farmland */
    public static final PropertyBool WILD = PropertyBool.create("wild");

    // binary flags for state and metadata conversion
    private static final int META_WILD = 8;
    private static final int META_GROWTH = 7;

    private static final Map<ICrop, BlockCropTFC> MAP = new HashMap<>();

    public static BlockCropTFC get(ICrop crop)
    {
        return MAP.get(crop);
    }

    public static Set<ICrop> getCrops()
    {
        return MAP.keySet();
    }

    protected final ICrop crop;

    BlockCropTFC(ICrop crop)
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
    public boolean canGrow(World worldIn, BlockPos pos, IBlockState state, boolean isClient)
    {
        return true;
    }

    @Override
    public boolean canUseBonemeal(World worldIn, Random rand, BlockPos pos, IBlockState state)
    {
        return false;
    }

    @Override
    @Nonnull
    @SuppressWarnings("deprecation")
    public IBlockState getStateFromMeta(int meta)
    {
        return getDefaultState().withProperty(WILD, (meta & META_WILD) > 0).withProperty(getStageProperty(), meta & META_GROWTH);
    }

    @Override
    public int getMetaFromState(IBlockState state)
    {
        return state.getValue(getStageProperty()) + (state.getValue(WILD) ? META_WILD : 0);
    }

    @Override
    @Nonnull
    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, getStageProperty(), WILD);
    }

    @Override
    public void randomTick(World worldIn, BlockPos pos, IBlockState state, Random random)
    {
        super.updateTick(worldIn, pos, state, random);
        if (!worldIn.isRemote)
        {
            TETickCounter te = Helpers.getTE(worldIn, pos, TETickCounter.class);
            if (te != null)
            {
                boolean isAlive = true;
                while (te.getTicksSinceUpdate() > crop.getGrowthTime() && isAlive)
                {
                    te.reduceCounter((long) crop.getGrowthTime());

                    // find stats for the time in which the crop would have grown
                    float temp = ClimateTFC.getActualTemp(worldIn, pos, -te.getTicksSinceUpdate());
                    float rainfall = ChunkDataTFC.getRainfall(worldIn, pos);

                    // check if the crop could grow, if so, grow
                    if (crop.isValidForGrowth(temp, rainfall))
                    {
                        grow(worldIn, random, pos, worldIn.getBlockState(pos));
                    }

                    // If not valid conditions, die
                    if (!crop.isValidConditions(temp, rainfall))
                    {
                        worldIn.setBlockState(pos, BlocksTFC.PLACED_ITEM_FLAT.getDefaultState());
                        TEPlacedItemFlat tilePlaced = Helpers.getTE(worldIn, pos, TEPlacedItemFlat.class);
                        if (tilePlaced != null)
                        {
                            tilePlaced.setStack(getDeathItem(te));
                        }
                        isAlive = false;
                    }
                }
            }
        }
    }

    @Override
    public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune)
    {
        EntityPlayer player = harvesters.get();
        ItemStack seedStack = new ItemStack(ItemSeedsTFC.get(crop));
        ItemStack foodStack = crop.getFoodDrop(state.getValue(getStageProperty()));

        // if player and skills are present, update skills and increase amounts of items depending on skill
        if (player != null)
        {
            SimpleSkill skill = CapabilityPlayerData.getSkill(player, SkillType.AGRICULTURE);

            if (skill != null)
            {
                if (!foodStack.isEmpty())
                {
                    foodStack.setCount(1 + RANDOM.nextInt(2 + (int) (6 * skill.getTotalLevel())));
                    if (skill.getTier().isAtLeast(SkillTier.ADEPT) && RANDOM.nextInt(10 - 2 * skill.getTier().ordinal()) == 0)
                    {
                        seedStack.setCount(2);
                    }
                }
                skill.add(0.04f);
            }
        }

        // add items to drop
        if (!foodStack.isEmpty())
            drops.add(foodStack);
        if (!seedStack.isEmpty())
            drops.add(seedStack);
    }

    @Override
    public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state)
    {
        TETickCounter tile = Helpers.getTE(worldIn, pos, TETickCounter.class);
        if (tile != null)
        {
            tile.resetCounter();
        }
    }

    @Override
    @Nonnull
    @SuppressWarnings("deprecation")
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos)
    {
        return CROPS_AABB[state.getValue(getStageProperty())];
    }

    @Override
    public boolean hasTileEntity(IBlockState state)
    {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(World world, IBlockState state)
    {
        return new TETickCounter();
    }

    @Override
    @Nonnull
    public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player)
    {
        return new ItemStack(ItemSeedsTFC.get(crop));
    }

    @Nonnull
    @Override
    public EnumPlantType getPlantType(IBlockAccess world, BlockPos pos)
    {
        return EnumPlantType.Crop;
    }

    public abstract PropertyInteger getStageProperty();

    /**
     * Gets the item stack that the crop will create upon dying and turning into a placed item
     */
    protected ItemStack getDeathItem(TETickCounter cropTE)
    {
        return ItemSeedsTFC.get(crop, 1);
    }
}

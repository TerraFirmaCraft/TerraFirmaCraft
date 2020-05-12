/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.blocks.agriculture;

import java.util.Random;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.ItemHandlerHelper;

import net.dries007.tfc.api.capability.player.CapabilityPlayerData;
import net.dries007.tfc.api.types.ICrop;
import net.dries007.tfc.objects.items.ItemSeedsTFC;
import net.dries007.tfc.util.agriculture.Crop;
import net.dries007.tfc.util.skills.SimpleSkill;
import net.dries007.tfc.util.skills.SkillType;

@ParametersAreNonnullByDefault
public abstract class BlockCropSimple extends BlockCropTFC
{
    public static BlockCropSimple create(ICrop crop, boolean isPickable)
    {
        PropertyInteger property = getStagePropertyForCrop(crop);

        if (property == null)
            throw new IllegalStateException("Invalid growthstage property " + (crop.getMaxStage() + 1) + " for crop");

        return new BlockCropSimple(crop, isPickable)
        {
            @Override
            public PropertyInteger getStageProperty()
            {
                return property;
            }
        };
    }

    private final boolean isPickable;

    protected BlockCropSimple(ICrop crop, boolean isPickable)
    {
        super(crop);
        this.isPickable = isPickable;

        setDefaultState(getBlockState().getBaseState().withProperty(getStageProperty(), 0).withProperty(WILD, false));
    }

    @Override
    public void grow(World worldIn, BlockPos pos, IBlockState state, Random random)
    {
        if (!worldIn.isRemote)
        {
            if (state.getValue(getStageProperty()) < getCrop().getMaxStage())
            {
                worldIn.setBlockState(pos, state.withProperty(getStageProperty(), state.getValue(getStageProperty()) + 1), 2);
            }
        }
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        if (isPickable)
        {
            ItemStack foodDrop = getCrop().getFoodDrop(state.getValue(getStageProperty()));
            if (!foodDrop.isEmpty())
            {
                ItemStack seedDrop = new ItemStack(ItemSeedsTFC.get(getCrop()), 0);
                SimpleSkill skill = CapabilityPlayerData.getSkill(playerIn, SkillType.AGRICULTURE);

                if (skill != null)
                {
                    foodDrop.setCount(1 + Crop.getSkillFoodBonus(skill, RANDOM));
                    // omit the +1 because the plant stays alive.
                    seedDrop.setCount(Crop.getSkillSeedBonus(skill, RANDOM));
                }

                if (!worldIn.isRemote)
                {
                    worldIn.setBlockState(pos, state.withProperty(getStageProperty(), state.getValue(getStageProperty()) - 3));
                    ItemHandlerHelper.giveItemToPlayer(playerIn, foodDrop);
                    if (!seedDrop.isEmpty())
                        ItemHandlerHelper.giveItemToPlayer(playerIn, seedDrop);
                }
                return true;
            }
        }
        return false;
    }
}

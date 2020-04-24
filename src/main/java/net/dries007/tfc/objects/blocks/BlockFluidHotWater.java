/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.blocks;

import java.util.Random;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import net.dries007.tfc.Constants;
import net.dries007.tfc.api.capability.food.FoodStatsTFC;
import net.dries007.tfc.client.particle.TFCParticles;
import net.dries007.tfc.objects.fluids.FluidsTFC;

@ParametersAreNonnullByDefault
public class BlockFluidHotWater extends BlockFluidTFC
{
    public BlockFluidHotWater()
    {
        super(FluidsTFC.HOT_WATER.get(), Material.WATER, false);

        setLightOpacity(3);
        disableStats();
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void randomDisplayTick(IBlockState stateIn, World worldIn, BlockPos pos, Random rand)
    {
        if (rand.nextInt(4) == 0)
        {
            worldIn.spawnParticle(EnumParticleTypes.WATER_BUBBLE, pos.getX() + rand.nextFloat(), pos.getY() + 0.50D, pos.getZ() + rand.nextFloat(), 0.0D, 0.0D, 0.0D, Block.getStateId(stateIn));
        }
        if (worldIn.isAirBlock(pos.up()))
        {
            // Classic made 4 particles spawn at a time
            // imo just one per surface is good enough
            double posX = pos.getX() + 0.5D;
            double posY = pos.getY() + 1.0D;
            double posZ = pos.getZ() + 0.5D;
            TFCParticles.STEAM.spawn(worldIn, posX, posY, posZ, 0, 0, 0, (int) (12.0F / (rand.nextFloat() * 0.9F + 0.1F)));
        }
    }

    @Override
    public void onEntityCollision(World worldIn, BlockPos pos, IBlockState state, Entity entityIn)
    {
        super.onEntityCollision(worldIn, pos, state, entityIn);
        if (entityIn instanceof EntityLivingBase)
        {
            EntityLivingBase entityLiving = (EntityLivingBase) entityIn;
            if (Constants.RNG.nextInt(10) == 0 && entityLiving.getHealth() < entityLiving.getMaxHealth())
            {
                entityLiving.heal(FoodStatsTFC.PASSIVE_HEAL_AMOUNT * 7f);
            }
        }
    }
}

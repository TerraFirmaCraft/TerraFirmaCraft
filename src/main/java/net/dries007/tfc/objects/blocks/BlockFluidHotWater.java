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
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import net.dries007.tfc.Constants;
import net.dries007.tfc.api.capability.food.FoodStatsTFC;
import net.dries007.tfc.objects.fluids.FluidsTFC;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;

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
            ParticleSteam steam = new ParticleSteam(worldIn, posX, posY, posZ);
            Minecraft.getMinecraft().effectRenderer.addEffect(steam);
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

    /**
     * Inner Class, since it's only needed here
     * Should more custom particles become needed, feel free to move this to the client subpackage
     */
    @ParametersAreNonnullByDefault
    @SideOnly(Side.CLIENT)
    @Mod.EventBusSubscriber(modid = MOD_ID)
    public static final class ParticleSteam extends Particle
    {
        private static final ResourceLocation PARTICLES_LOCATION = new ResourceLocation(MOD_ID, "particle/steam");
        private static TextureAtlasSprite STEAM_SPRITE;

        @SubscribeEvent
        public static void onTextureStitchEvent(TextureStitchEvent.Pre event)
        {
            STEAM_SPRITE = event.getMap().registerSprite(PARTICLES_LOCATION);
        }


        public ParticleSteam(World worldIn, double x, double y, double z)
        {
            super(worldIn, x, y, z, 0.0D, 0.0D, 0.0D);
            this.setParticleTexture(STEAM_SPRITE);
            this.particleAlpha = 0.05F;
            this.particleMaxAge = (int) (12.0F / (this.rand.nextFloat() * 0.9F + 0.1F));
        }

        @Override
        public boolean shouldDisableDepth()
        {
            // This is needed to order the transparency later than the fluid block
            // Fix it being completely transparent
            return true;
        }

        @Override
        public int getFXLayer()
        {
            return 1;
        }
    }
}

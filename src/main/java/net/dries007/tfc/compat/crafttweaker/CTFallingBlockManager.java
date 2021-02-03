package net.dries007.tfc.compat.crafttweaker;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;

import net.minecraftforge.fml.common.registry.ForgeRegistries;

import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.block.IBlockDefinition;
import crafttweaker.api.block.IBlockState;
import crafttweaker.api.data.IData;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.minecraft.CraftTweakerMC;
import crafttweaker.api.world.IBlockPos;
import crafttweaker.api.world.IWorld;
import net.dries007.tfc.api.util.FallingBlockManager;
import net.dries007.tfc.client.TFCSounds;
import stanhebben.zenscript.annotations.Optional;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenRegister
@ZenClass("mods.terrafirmacraft.fallingblock.FallingBlockManager")
public class CTFallingBlockManager
{

    @ZenMethod
    public static void registerFallable(IBlockState state, CTSpecification ctSpecification)
    {
        FallingBlockManager.registerFallable((net.minecraft.block.state.IBlockState) state.getInternal(), ctSpecification.internalSpec);
    }

    @ZenMethod
    public static void registerFallable(IBlockState state, IBlockState existingState)
    {
        FallingBlockManager.Specification existingSpec = FallingBlockManager.getSpecification((net.minecraft.block.state.IBlockState) existingState.getInternal());
        if (existingSpec == null)
        {
            throw new IllegalArgumentException(existingState + " is not in the current specification definitions.");
        }
        FallingBlockManager.registerFallable((net.minecraft.block.state.IBlockState) state.getInternal(), existingSpec);
    }

    @ZenMethod
    public static void registerFallable(IBlockDefinition block, CTSpecification ctSpecification)
    {
        FallingBlockManager.registerFallable((Block) block.getInternal(), ctSpecification.internalSpec);
    }

    @ZenMethod
    public static void registerFallable(IBlockDefinition block, IBlockState existingState)
    {
        FallingBlockManager.Specification existingSpec = FallingBlockManager.getSpecification((net.minecraft.block.state.IBlockState) existingState.getInternal());
        if (existingSpec == null)
        {
            throw new IllegalArgumentException(existingState + " is not in the current specification definitions.");
        }
        FallingBlockManager.registerFallable((Block) block.getInternal(), existingSpec);
    }

    @ZenMethod
    public static void registerFallable(String blockId, CTSpecification ctSpecification)
    {
        Block block = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(blockId));
        if (block == null)
        {
            throw new IllegalArgumentException(blockId + " is not a valid Block.");
        }
        FallingBlockManager.registerFallable(block, ctSpecification.internalSpec);
    }

    @ZenMethod
    public static void registerFallable(String blockId, IBlockState existingState)
    {
        Block block = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(blockId));
        if (block == null)
        {
            throw new IllegalArgumentException(blockId + " is not a valid Block.");
        }
        FallingBlockManager.Specification existingSpec = FallingBlockManager.getSpecification((net.minecraft.block.state.IBlockState) existingState.getInternal());
        if (existingSpec == null)
        {
            throw new IllegalArgumentException(existingState + " is not in the current specification definitions.");
        }
        FallingBlockManager.registerFallable(block, existingSpec);
    }

    @ZenRegister
    @ZenClass("mods.terrafirmacraft.fallingblock.Specification")
    public static class CTSpecification
    {

        @ZenMethod
        public static CTSpecification create(boolean canFallHorizontally, boolean collapsable)
        {
            return new CTSpecification(canFallHorizontally, collapsable, () -> TFCSounds.ROCK_SLIDE_SHORT);
        }

        @ZenMethod
        public static CTSpecification create(boolean canFallHorizontally, boolean collapsable, String soundEventId, @Optional CTFallDropsProvider dropsProvider)
        {
            return new CTSpecification(canFallHorizontally, collapsable, new ResourceLocation(soundEventId), dropsProvider);
        }

        private static FallingBlockManager.Specification.FallDropsProvider transform(CTFallDropsProvider ctProvider)
        {
            return (world, pos, state, teData, fallTime, fallDistance) -> {
                IItemStack[] drops = ctProvider.getDropsFromFall(CraftTweakerMC.getIWorld(world), CraftTweakerMC.getIBlockPos(pos), CraftTweakerMC.getBlockState(state), CraftTweakerMC.getIData(teData), fallTime, fallDistance);
                List<ItemStack> iterableDrops = new ArrayList<>(drops.length);
                for (IItemStack drop : drops)
                {
                    iterableDrops.add((ItemStack) drop.getInternal());
                }
                return iterableDrops;
            };
        }

        final FallingBlockManager.Specification internalSpec;

        CTSpecification(boolean canFallHorizontally, boolean collapsable, ResourceLocation soundEventId)
        {
            this(canFallHorizontally, collapsable, () -> SoundEvent.REGISTRY.getObject(soundEventId));
        }

        CTSpecification(boolean canFallHorizontally, boolean collapsable, Supplier<SoundEvent> soundEvent)
        {
            this.internalSpec = new FallingBlockManager.Specification(canFallHorizontally, collapsable, soundEvent, FallingBlockManager.Specification.DEFAULT_DROPS_PROVIDER);
        }

        CTSpecification(boolean canFallHorizontally, boolean collapsable, ResourceLocation soundEventId, CTFallDropsProvider dropsProvider)
        {
            this(canFallHorizontally, collapsable, () -> SoundEvent.REGISTRY.getObject(soundEventId), dropsProvider);
        }

        CTSpecification(boolean canFallHorizontally, boolean collapsable, Supplier<SoundEvent> soundEvent, CTFallDropsProvider drops)
        {
            this.internalSpec = new FallingBlockManager.Specification(canFallHorizontally, collapsable, soundEvent, transform(drops));
        }

        @ZenMethod
        public void setResultingBlock(IBlockDefinition block)
        {
            this.internalSpec.setResultingState(((Block) block.getInternal()).getDefaultState());
        }

        @ZenMethod
        public void setResultingBlock(String blockId)
        {
            Block block = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(blockId));
            if (block == null)
            {
                throw new IllegalArgumentException(blockId + " is not a valid Block.");
            }
            this.internalSpec.setResultingState(block.getDefaultState());
        }

        @ZenMethod
        public void setResultingState(IBlockState block)
        {
            this.internalSpec.setResultingState(((net.minecraft.block.state.IBlockState) block.getInternal()));
        }

        @ZenRegister
        @FunctionalInterface
        @ZenClass("mods.terrafirmacraft.fallingblock.FallDropsProvider")
        public interface CTFallDropsProvider
        {
            IItemStack[] getDropsFromFall(IWorld world, IBlockPos pos, IBlockState state, IData teData, int fallTime, float fallDistance);
        }

    }

}

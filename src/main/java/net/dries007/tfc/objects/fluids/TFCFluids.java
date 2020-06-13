/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.fluids;

import java.util.EnumMap;
import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.block.material.Material;
import net.minecraft.fluid.FlowingFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.BucketItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import net.dries007.tfc.api.Metal;
import net.dries007.tfc.objects.blocks.TFCBlocks;
import net.dries007.tfc.objects.items.TFCItems;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;

public final class TFCFluids
{
    public static final DeferredRegister<Fluid> FLUIDS = new DeferredRegister<>(ForgeRegistries.FLUIDS, MOD_ID);
    private static final ResourceLocation LAVA_STILL = new ResourceLocation(MOD_ID, "block/lava_still");
    private static final ResourceLocation LAVA_FLOW = new ResourceLocation(MOD_ID, "block/lava_flow");
    public static final Map<Metal.Default, Map<Type, RegistryObject<FlowingFluid>>> METAL_FLUIDS = Util.make(new EnumMap<>(Metal.Default.class), map -> {
        for (Metal.Default metal : Metal.Default.values())
        {
            String sourceName = ("metal/" + metal.name()).toLowerCase();
            String flowingName = ("metal/flowing_" + metal.name()).toLowerCase();
            String blockName = ("fluid/metal/" + metal.name()).toLowerCase();
            String bucketName = ("bucket/metal/" + metal.name()).toLowerCase();

            // Fluid properties has to be created before the registry, but needs it later
            final Lazy<FlowingFluid> source = Lazy.of(() -> map.get(metal).get(Type.SOURCE).get());
            final Lazy<FlowingFluid> flowing = Lazy.of(() -> map.get(metal).get(Type.FLOWING).get());
            ForgeFlowingFluid.Properties properties = new ForgeFlowingFluid.Properties(source, flowing,
                FluidAttributes.builder(LAVA_STILL, LAVA_FLOW).color(metal.getColor()))
                .block(TFCBlocks.BLOCKS.register(blockName, () -> new FlowingFluidBlock(source, Block.Properties.create(Material.WATER).doesNotBlockMovement().hardnessAndResistance(100.0F).noDrops())))
                .bucket(TFCItems.ITEMS.register(bucketName, () -> new BucketItem(source, new Item.Properties().containerItem(Items.BUCKET).maxStackSize(1).group(ItemGroup.MISC))));

            Map<Type, RegistryObject<FlowingFluid>> inner = new EnumMap<>(Type.class);
            inner.put(Type.SOURCE, FLUIDS.register(sourceName, () -> new ForgeFlowingFluid.Source(properties)));
            inner.put(Type.FLOWING, FLUIDS.register(flowingName, () -> new ForgeFlowingFluid.Flowing(properties)));
            map.put(metal, inner);
        }
    });
    private static final ResourceLocation FLUID_FLOW = new ResourceLocation(MOD_ID, "block/fluid_flow");
    private static final ResourceLocation FLUID_STILL = new ResourceLocation(MOD_ID, "block/fluid_still");


    public enum Type
    {
        SOURCE,
        FLOWING
    }
}

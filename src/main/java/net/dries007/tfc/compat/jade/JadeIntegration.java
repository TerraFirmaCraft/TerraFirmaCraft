/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.compat.jade;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.Vec2;

import mcp.mobius.waila.api.*;
import mcp.mobius.waila.api.ui.IElement;
import net.dries007.tfc.common.blocks.BloomBlock;
import net.dries007.tfc.common.blocks.crop.CropBlock;
import net.dries007.tfc.common.blocks.crop.DeadCropBlock;
import net.dries007.tfc.common.blocks.crop.DeadDoubleCropBlock;
import net.dries007.tfc.common.blocks.devices.*;
import net.dries007.tfc.common.blocks.plant.fruit.*;
import net.dries007.tfc.common.blocks.soil.FarmlandBlock;
import net.dries007.tfc.common.blocks.soil.HoeOverlayBlock;
import net.dries007.tfc.common.blocks.wood.TFCSaplingBlock;
import net.dries007.tfc.common.entities.livestock.TFCAnimal;
import net.dries007.tfc.common.entities.livestock.horse.TFCChestedHorse;
import net.dries007.tfc.common.entities.livestock.horse.TFCHorse;
import net.dries007.tfc.compat.jade.provider.*;
import net.dries007.tfc.config.TFCConfig;
import net.dries007.tfc.util.Helpers;

@WailaPlugin
@SuppressWarnings("UnstableApiUsage")
public class JadeIntegration implements IWailaPlugin
{
    // Optionally we can register server side info providers here. We sync most everything so this is not needed right now.

    @Override
    public void registerClient(IWailaClientRegistration registry)
    {
        registry.registerComponentProvider(SaplingProvider.INSTANCE, TooltipPosition.BODY, TFCSaplingBlock.class);
        registry.registerComponentProvider(FruitTreeSaplingProvider.INSTANCE, TooltipPosition.BODY, FruitTreeSaplingBlock.class);
        registry.registerComponentProvider(ComposterProvider.INSTANCE, TooltipPosition.BODY, TFCComposterBlock.class);
        registry.registerComponentProvider(BarrelProvider.INSTANCE, TooltipPosition.BODY, BarrelBlock.class);
        registry.registerComponentProvider(BloomeryProvider.INSTANCE, TooltipPosition.BODY, BloomeryBlock.class);
        registry.registerComponentProvider(BloomProvider.INSTANCE, TooltipPosition.BODY, BloomBlock.class);
        registry.registerComponentProvider(NestBoxProvider.INSTANCE, TooltipPosition.BODY, NestBoxBlock.class);
        registry.registerComponentProvider(LampProvider.INSTANCE, TooltipPosition.BODY, LampBlock.class);
        registry.registerComponentProvider(CharcoalForgeProvider.INSTANCE, TooltipPosition.BODY, CharcoalForgeBlock.class);
        registry.registerComponentProvider(CrucibleProvider.INSTANCE, TooltipPosition.BODY, CrucibleBlock.class);
        registry.registerComponentProvider(BlastFurnaceProvider.INSTANCE, TooltipPosition.BODY, BlastFurnaceBlock.class);
        registry.registerComponentProvider(PitKilnProvider.INSTANCE, TooltipPosition.BODY, PitKilnBlock.class);
        registry.registerComponentProvider(PowderkegProvider.INSTANCE, TooltipPosition.BODY, PowderkegBlock.class);
        registry.registerComponentProvider(FirepitProvider.INSTANCE, TooltipPosition.BODY, FirepitBlock.class);
        registry.registerComponentProvider(CropProvider.INSTANCE, TooltipPosition.BODY, CropBlock.class);
        registry.registerComponentProvider(BellowsProvider.INSTANCE, TooltipPosition.BODY, BellowsBlock.class);

        registry.registerComponentProvider(AnimalProvider.INSTANCE, TooltipPosition.BODY, TFCAnimal.class);
        registry.registerComponentProvider(AnimalProvider.INSTANCE, TooltipPosition.BODY, TFCHorse.class);
        registry.registerComponentProvider(AnimalProvider.INSTANCE, TooltipPosition.BODY, TFCChestedHorse.class);

        registerHoeOverlay(registry, FarmlandBlock.class);
        registerHoeOverlay(registry, DeadCropBlock.class);
        registerHoeOverlay(registry, DeadDoubleCropBlock.class);
        registerHoeOverlay(registry, BananaPlantBlock.class);
        registerHoeOverlay(registry, FruitTreeBranchBlock.class);
        registerHoeOverlay(registry, FruitTreeLeavesBlock.class);
        registerHoeOverlay(registry, StationaryBerryBushBlock.class);
        registerHoeOverlay(registry, SpreadingBushBlock.class);

    }

    private void registerHoeOverlay(IWailaClientRegistration registry, Class<? extends Block> blockClass)
    {
        registry.registerComponentProvider(HoeOverlayProvider.INSTANCE, TooltipPosition.BODY, blockClass);
    }

    public static void loadHoeOverlay(HoeOverlayBlock block, ITooltip tooltip, BlockAccessor access)
    {
        if (TFCConfig.CLIENT.showHoeOverlaysInInfoMods.get())
        {
            final List<Component> text = new ArrayList<>();
            final BlockPos pos = access.getPosition();
            final Level level = access.getLevel();
            block.addHoeOverlayInfo(level, pos, level.getBlockState(pos), text, false);
            tooltip.addAll(text);
        }
    }

    public static void displayCountedItemName(ITooltip tooltip, ItemStack stack)
    {
        tooltip.add(Helpers.literal(String.valueOf(stack.getCount())).append("x ").append(stack.getHoverName()));
    }

    public static void displayHeat(ITooltip tooltip, float temperature)
    {
        final MutableComponent heat = TFCConfig.CLIENT.heatTooltipStyle.get().formatColored(temperature);
        if (heat != null)
        {
            tooltip.add(heat);
        }
    }

    public static IElement getItem(ITooltip tooltip, ItemStack item)
    {
        return tooltip.getElementHelper().item(item, 0.5f).size(new Vec2(10, 10)).translate(new Vec2(0, -1));
    }

}

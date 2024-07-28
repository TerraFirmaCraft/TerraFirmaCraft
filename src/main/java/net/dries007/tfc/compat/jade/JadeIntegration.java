/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.compat.jade;

import java.util.List;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import snownee.jade.addon.harvest.HarvestToolProvider;
import snownee.jade.addon.harvest.SimpleToolHandler;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.EntityAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.IEntityComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.JadeIds;
import snownee.jade.api.WailaPlugin;
import snownee.jade.api.config.IPluginConfig;

import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.blocks.rock.RockCategory;
import net.dries007.tfc.common.blocks.soil.SoilBlockType;
import net.dries007.tfc.common.items.TFCItems;
import net.dries007.tfc.util.tooltip.BlockEntityTooltip;
import net.dries007.tfc.util.tooltip.BlockEntityTooltips;
import net.dries007.tfc.util.tooltip.EntityTooltip;
import net.dries007.tfc.util.tooltip.EntityTooltips;
import net.dries007.tfc.util.Metal;

@WailaPlugin
public class JadeIntegration implements IWailaPlugin
{
    /**
     * Replaces the default Jade tool harvest checks with items from TFC (because the correspondence with vanilla tools
     * might not be obvious, and in TFC, there's no chance that vanilla tools will be used here).
     * @see HarvestToolProvider
     */
    public static void registerToolHandlers()
    {
        HarvestToolProvider.registerHandler(SimpleToolHandler.create(JadeIds.JADE("pickaxe"), List.of(
            metalTool(Metal.COPPER, Metal.ItemType.PICKAXE),
            metalTool(Metal.BRONZE, Metal.ItemType.PICKAXE),
            metalTool(Metal.STEEL, Metal.ItemType.PICKAXE),
            metalTool(Metal.BLUE_STEEL, Metal.ItemType.PICKAXE)
        )));
        register("axe", RockCategory.ItemType.AXE, Metal.ItemType.AXE);
        register("shovel", RockCategory.ItemType.SHOVEL, Metal.ItemType.SHOVEL);
        register("hoe", RockCategory.ItemType.HOE, Metal.ItemType.HOE);
        HarvestToolProvider.registerHandler(SimpleToolHandler.create(JadeIds.JADE("sword"), List.of(
            TFCItems.ROCK_TOOLS.get(RockCategory.SEDIMENTARY).get(RockCategory.ItemType.KNIFE).asItem()
        )));
    }

    private static void register(String name, RockCategory.ItemType stoneType, Metal.ItemType metalType)
    {
        HarvestToolProvider.registerHandler(SimpleToolHandler.create(JadeIds.JADE(name), List.of(
            TFCItems.ROCK_TOOLS.get(RockCategory.SEDIMENTARY).get(stoneType).asItem(),
            metalTool(Metal.COPPER, metalType),
            metalTool(Metal.BRONZE, metalType),
            metalTool(Metal.STEEL, metalType),
            metalTool(Metal.BLUE_STEEL, metalType)
        )));
    }

    private static Item metalTool(Metal metal, Metal.ItemType type)
    {
        return TFCItems.METAL_ITEMS.get(metal).get(type).asItem();
    }

    @Override
    public void registerClient(IWailaClientRegistration registry)
    {
        BlockEntityTooltips.register((name, tooltip, block) -> register(registry, name, tooltip, block));
        EntityTooltips.register((name, tooltip, entity) -> register(registry, name, tooltip, entity));
    }

    private void register(IWailaClientRegistration registry, ResourceLocation name, BlockEntityTooltip blockEntityTooltip, Class<? extends Block> block)
    {
        registry.registerBlockComponent(new IBlockComponentProvider() {
            @Override
            public void appendTooltip(ITooltip tooltip, BlockAccessor access, IPluginConfig config)
            {
                blockEntityTooltip.display(access.getLevel(), access.getBlockState(), access.getPosition(), access.getBlockEntity(), tooltip::add);
            }

            @Override
            public ResourceLocation getUid()
            {
                return name;
            }
        }, block);
    }

    private void register(IWailaClientRegistration registry, ResourceLocation name, EntityTooltip entityTooltip, Class<? extends Entity> entityClass)
    {
        registry.registerEntityComponent(new IEntityComponentProvider() {
            @Override
            public void appendTooltip(ITooltip tooltip, EntityAccessor access, IPluginConfig config)
            {
                entityTooltip.display(access.getLevel(), access.getEntity(), tooltip::add);
            }

            @Override
            public ResourceLocation getUid()
            {
                return name;
            }
        }, entityClass);
    }
}

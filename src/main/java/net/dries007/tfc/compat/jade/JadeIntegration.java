/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.compat.jade;

import java.util.List;
import mcp.mobius.waila.api.IWailaClientRegistration;
import mcp.mobius.waila.api.IWailaPlugin;
import mcp.mobius.waila.api.TooltipPosition;
import mcp.mobius.waila.api.WailaPlugin;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import snownee.jade.addon.harvest.HarvestToolProvider;
import snownee.jade.addon.harvest.SimpleToolHandler;

import net.dries007.tfc.common.items.TFCItems;
import net.dries007.tfc.compat.jade.common.BlockEntityTooltip;
import net.dries007.tfc.compat.jade.common.BlockEntityTooltips;
import net.dries007.tfc.compat.jade.common.EntityTooltip;
import net.dries007.tfc.compat.jade.common.EntityTooltips;
import net.dries007.tfc.util.Metal;

@WailaPlugin
@SuppressWarnings("UnstableApiUsage")
public class JadeIntegration implements IWailaPlugin
{
    /**
     * Replaces the default Jade tool harvest checks with items from TFC, which both includes more granularity (i.e. telling between black and colored steel), and also uses TFC items (because the correspondence with vanilla tools might not be obvious).
     */
    public static void registerToolHandlers()
    {
        record Info(String name, TagKey<Block> tag, Metal.ItemType itemType) {}

        final List<Info> toolTypes = List.of(
            new Info("pickaxe", BlockTags.MINEABLE_WITH_PICKAXE, Metal.ItemType.PICKAXE),
            new Info("axe", BlockTags.MINEABLE_WITH_AXE, Metal.ItemType.AXE),
            new Info("shovel", BlockTags.MINEABLE_WITH_SHOVEL, Metal.ItemType.SHOVEL),
            new Info("hoe", BlockTags.MINEABLE_WITH_HOE, Metal.ItemType.HOE)
        );

        final List<Metal.Default> metalTypes = List.of(
            Metal.Default.COPPER,
            Metal.Default.BRONZE,
            Metal.Default.WROUGHT_IRON,
            Metal.Default.STEEL,
            Metal.Default.BLACK_STEEL,
            Metal.Default.BLUE_STEEL
        );

        for (Info info : toolTypes)
        {
            HarvestToolProvider.registerHandler(new SimpleToolHandler(info.name, info.tag, metalTypes.stream().map(metal -> TFCItems.METAL_ITEMS.get(metal).get(info.itemType).get()).toArray(Item[]::new)));
        }
    }

    @Override
    public void registerClient(IWailaClientRegistration registry)
    {
        BlockEntityTooltips.register((tooltip, aClass) -> register(registry, tooltip, aClass));
        EntityTooltips.register((tooltip, aClass) -> register(registry, tooltip, aClass));
    }

    private void register(IWailaClientRegistration registry, BlockEntityTooltip blockEntityTooltip, Class<? extends Block> blockClass)
    {
        registry.registerComponentProvider((tooltip, access, config) -> blockEntityTooltip.display(access.getLevel(), access.getBlockState(), access.getPosition(), access.getBlockEntity(), tooltip::add), TooltipPosition.BODY, blockClass);
    }

    private void register(IWailaClientRegistration registry, EntityTooltip entityTooltip, Class<? extends Entity> entityClass)
    {
        registry.registerComponentProvider((tooltip, access, config) -> entityTooltip.display(access.getLevel(), access.getEntity(), tooltip::add), TooltipPosition.BODY, entityClass);
    }
}

/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.compat.jade;

import java.util.List;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.FloatTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import snownee.jade.addon.harvest.HarvestToolProvider;
import snownee.jade.addon.harvest.SimpleToolHandler;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.EntityAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.IEntityComponentProvider;
import snownee.jade.api.IServerDataProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaCommonRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.JadeIds;
import snownee.jade.api.WailaPlugin;
import snownee.jade.api.config.IPluginConfig;

import net.dries007.tfc.common.blockentities.PlacedItemBlockEntity;
import net.dries007.tfc.common.blocks.rock.RockCategory;
import net.dries007.tfc.common.component.heat.HeatCapability;
import net.dries007.tfc.common.items.TFCItems;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.Metal;
import net.dries007.tfc.util.tooltip.BlockEntityTooltip;
import net.dries007.tfc.util.tooltip.BlockEntityTooltips;
import net.dries007.tfc.util.tooltip.EntityTooltip;
import net.dries007.tfc.util.tooltip.EntityTooltips;

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
            metalTool(Metal.BLACK_STEEL, Metal.ItemType.PICKAXE)
        )));
        register("axe", RockCategory.ItemType.AXE, Metal.ItemType.AXE);
        register("shovel", RockCategory.ItemType.SHOVEL, Metal.ItemType.SHOVEL);
        register("hoe", RockCategory.ItemType.HOE, Metal.ItemType.HOE);
        HarvestToolProvider.registerHandler(SimpleToolHandler.create(JadeIds.JADE("sword"), List.of(
            TFCItems.ROCK_TOOLS.get(RockCategory.SEDIMENTARY).get(RockCategory.ItemType.KNIFE).asItem()
        )));
        HarvestToolProvider.registerHandler(SimpleToolHandler.create(JadeIds.JADE("glass_saw"), List.of(
            TFCItems.GEM_SAW.asItem()
        )));
    }

    private static void register(String name, RockCategory.ItemType stoneType, Metal.ItemType metalType)
    {
        HarvestToolProvider.registerHandler(SimpleToolHandler.create(JadeIds.JADE(name), List.of(
            TFCItems.ROCK_TOOLS.get(RockCategory.SEDIMENTARY).get(stoneType).asItem(),
            metalTool(Metal.COPPER, metalType),
            metalTool(Metal.BRONZE, metalType),
            metalTool(Metal.STEEL, metalType),
            metalTool(Metal.BLACK_STEEL, metalType)
        )));
    }

    private static Item metalTool(Metal metal, Metal.ItemType type)
    {
        return TFCItems.METAL_ITEMS.get(metal).get(type).asItem();
    }

    @Override
    public void register(IWailaCommonRegistration registry)
    {
        registry.registerBlockDataProvider(PlacedItemProvider.INSTANCE, PlacedItemBlockEntity.class);
    }

    @Override
    public void registerClient(IWailaClientRegistration registry)
    {
        BlockEntityTooltips.register((name, tooltip, block) -> register(registry, name, tooltip, block));
        EntityTooltips.register((name, tooltip, entity) -> register(registry, name, tooltip, entity));
    }

    private void register(IWailaClientRegistration registry, ResourceLocation name, BlockEntityTooltip blockEntityTooltip, Class<? extends Block> block)
    {
        if (blockEntityTooltip == BlockEntityTooltips.PLACED_ITEM)
        {
            registry.registerBlockComponent(PlacedItemProvider.INSTANCE, block);
            return;
        }
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

    /**
     * Placed items can be heated (i.e. in a kiln) without being synced, so their temperatures are requested from the server while being looked at.
     */
    private enum PlacedItemProvider implements IBlockComponentProvider, IServerDataProvider<BlockAccessor>
    {
        INSTANCE;

        private static final ResourceLocation UID = Helpers.identifier("placed_item");
        private static final String TEMPERATURES = "tfc:temperatures";

        @Override
        public void appendTooltip(ITooltip tooltip, BlockAccessor access, IPluginConfig config)
        {
            float[] temperatures = null;
            final CompoundTag data = access.getServerData();
            if (data.contains(TEMPERATURES, Tag.TAG_LIST))
            {
                final ListTag list = data.getList(TEMPERATURES, Tag.TAG_FLOAT);
                temperatures = new float[list.size()];
                for (int i = 0; i < temperatures.length; i++)
                {
                    temperatures[i] = list.getFloat(i);
                }
            }
            BlockEntityTooltips.placedItem(access.getBlockEntity(), tooltip::add, temperatures);
        }

        @Override
        public void appendServerData(CompoundTag data, BlockAccessor access)
        {
            if (access.getBlockEntity() instanceof PlacedItemBlockEntity placedItem)
            {
                final ListTag list = new ListTag();
                for (ItemStack stack : Helpers.iterate(placedItem.getInventory()))
                {
                    list.add(FloatTag.valueOf(HeatCapability.getTemperature(stack)));
                }
                data.put(TEMPERATURES, list);
            }
        }

        @Override
        public ResourceLocation getUid()
        {
            return UID;
        }
    }
}

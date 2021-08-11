/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks.wood;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.CraftingTableBlock;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.level.Level;

import net.dries007.tfc.common.blocks.ForgeBlockProperties;
import net.dries007.tfc.common.blocks.IForgeBlockProperties;
import net.dries007.tfc.common.container.TFCWorkbenchContainer;

public class TFCCraftingTableBlock extends CraftingTableBlock implements IForgeBlockProperties
{
    private static final Component CONTAINER_TITLE = new TranslatableComponent("container.crafting");
    private final ForgeBlockProperties properties;

    public TFCCraftingTableBlock(ForgeBlockProperties properties)
    {
        super(properties.properties());
        this.properties = properties;
    }

    @Override
    public ForgeBlockProperties getForgeProperties()
    {
        return properties;
    }

    @Override
    public MenuProvider getMenuProvider(BlockState state, Level world, BlockPos pos)
    {
        return new SimpleMenuProvider((id, inv, player) -> new TFCWorkbenchContainer(id, inv, ContainerLevelAccess.create(world, pos)), CONTAINER_TITLE);
    }
}

/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks.wood;

import net.minecraft.block.BlockState;
import net.minecraft.block.CraftingTableBlock;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.inventory.container.SimpleNamedContainerProvider;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import net.dries007.tfc.common.blocks.ForgeBlockProperties;
import net.dries007.tfc.common.blocks.IForgeBlockProperties;
import net.dries007.tfc.common.container.TFCWorkbenchContainer;

public class TFCCraftingTableBlock extends CraftingTableBlock implements IForgeBlockProperties
{
    private static final ITextComponent CONTAINER_TITLE = new TranslationTextComponent("container.crafting");
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
    public INamedContainerProvider getMenuProvider(BlockState state, World world, BlockPos pos)
    {
        return new SimpleNamedContainerProvider((id, inv, player) -> new TFCWorkbenchContainer(id, inv, IWorldPosCallable.create(world, pos)), CONTAINER_TITLE);
    }
}

/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.items;

import java.util.function.Consumer;
import java.util.function.Supplier;
import com.google.common.base.Suppliers;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;

import net.dries007.tfc.client.render.blockentity.ChestItemRenderer;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.registry.RegistryWood;

public class ChestBlockItem extends BlockItem
{
    private final ResourceLocation boatTexture;

    public ChestBlockItem(Block block, Properties properties, ResourceLocation boatTexture)
    {
        super(block, properties);
        this.boatTexture = boatTexture;
    }

    public ChestBlockItem(Block block, Properties properties, RegistryWood wood)
    {
        this(block, properties, Helpers.identifier("textures/entity/chest_boat/" + wood.getSerializedName() + ".png"));
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer)
    {
        consumer.accept(new IClientItemExtensions() {
            private final Supplier<ChestItemRenderer> renderer = Suppliers.memoize(() -> new ChestItemRenderer(getBlock()));

            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer()
            {
                return renderer.get();
            }
        });
    }

    public ResourceLocation getBoatTexture()
    {
        return boatTexture;
    }
}

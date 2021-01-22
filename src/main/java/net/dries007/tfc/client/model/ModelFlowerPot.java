package net.dries007.tfc.client.model;

import java.util.*;

import javax.annotation.Nullable;

import com.google.common.collect.ImmutableMap;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockModelShapes;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;

import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.client.model.pipeline.VertexBufferConsumer;
import net.minecraftforge.common.property.IExtendedBlockState;

import mcp.MethodsReturnNonnullByDefault;
import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.objects.blocks.BlockFlowerPotTFC;
import net.dries007.tfc.objects.blocks.plants.BlockPlantTFC;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;

@MethodsReturnNonnullByDefault
public class ModelFlowerPot implements IBakedModel
{
    private static final IModel FLOWER_POT_BLANK = ModelLoaderRegistry.getModelOrMissing(new ResourceLocation(MOD_ID, "block/flower_pot_cross"));
    private Block CACHED_BLOCK = null; // to avoid reloading models in certain cases
    private List<BakedQuad> CACHED_QUADS = null;

    @Override
    public List<BakedQuad> getQuads(@Nullable IBlockState state, @Nullable EnumFacing side, long rand)
    {
        if (state instanceof IExtendedBlockState && state.getBlock() instanceof BlockFlowerPotTFC)
        {
            IExtendedBlockState extendedBlockState = (IExtendedBlockState) state;
            IBlockState flower = extendedBlockState.getValue(BlockFlowerPotTFC.FLOWER);
            Block block = flower.getBlock();
            if (block instanceof BlockPlantTFC && ((BlockPlantTFC) block).getPlant().isCrossModel())
            {
                // if we think that it's just a cross model, let's do it the easy way.
                BlockModelShapes shapes = Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelShapes();
                TextureAtlasSprite flowerAsset = shapes.getModelForState(flower).getQuads(flower, null, rand).get(0).getSprite();
                Map<String, String> sprites = new HashMap<>();
                sprites.put("plant", flowerAsset.getIconName());
                return bake(FLOWER_POT_BLANK.retexture(ImmutableMap.copyOf(sprites))).getQuads(state, side, rand);
            }
            else if (CACHED_BLOCK == block && CACHED_QUADS != null) // custom potted stuff should not be state sensitive since they don't inherit block state json
            {
                return CACHED_QUADS;
            }
            else if (block != Blocks.AIR && block.getRegistryName() != null) // let's see if there's a custom one in the files
            {
                ResourceLocation regName = block.getRegistryName();
                ResourceLocation modelName = new ResourceLocation(regName.getNamespace(), "block/" + regName.getPath() + "_potted");
                IModel model;
                try
                {
                    model = ModelLoaderRegistry.getModel(modelName);
                }
                catch (Exception e)
                {
                    TerraFirmaCraft.getLog().warn("Unable to get model at location {} for flower pot, loading blank pot", modelName.toString());
                    model = null; // using instead of getModelOrMissing because we need to do different behavior depending on if we found the model. this won't happen unless addons screw up
                }
                if (model != null)
                {
                    CACHED_BLOCK = block;
                    CACHED_QUADS = bake(model).getQuads(state, side, rand);
                    return CACHED_QUADS;
                }
            }
        }
        return bake(FLOWER_POT_BLANK).getQuads(state, side, rand);
    }

    @Override
    public boolean isAmbientOcclusion()
    {
        return false;
    }

    @Override
    public boolean isGui3d()
    {
        return false;
    }

    @Override
    public boolean isBuiltInRenderer()
    {
        return false;
    }

    @Override
    public TextureAtlasSprite getParticleTexture()
    {
        return Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite("minecraft:blocks/hardened_clay");
    }

    @Override
    public ItemOverrideList getOverrides()
    {
        return ItemOverrideList.NONE;
    }

    private IBakedModel bake(IModel model)
    {
        return model.bake(model.getDefaultState(), DefaultVertexFormats.BLOCK, ModelLoader.defaultTextureGetter());
    }
}

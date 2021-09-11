package net.dries007.tfc.client.model;

// todo: fuck all of this shit
public class MoldModel {}/* implements IModelGeometry<MoldModel>
{
    public MoldModel(Map<Fluid, ResourceLocation> textures)
    {
        this.textures = textures;
    }

    @Override
    public BakedModel bake(IModelConfiguration owner, ModelBakery bakery, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelTransform, ItemOverrides overrides, ResourceLocation modelLocation)
    {
        SimpleBakedModel.Builder builder = new SimpleBakedModel.Builder(owner, new MoldOverride(overrides, bakery, owner, this));
        builder.particle(spriteGetter.apply(owner.resolveTexture("particle")));
        return builder.build();
    }

    @Override
    @SuppressWarnings("ConstantConditions")
    public Collection<Material> getTextures(IModelConfiguration owner, Function<ResourceLocation, UnbakedModel> modelGetter, Set<Pair<String, String>> missingTextureErrors)
    {
        final Set<Material> textures = new HashSet<>();
        textures.add(owner.resolveTexture("particle"));
        for (Fluid fluid : ForgeRegistries.FLUIDS.getValues())
        {
            final String name = fluid.getRegistryName().toString();
            if (owner.isTexturePresent(name))
            {
                textures.add(owner.resolveTexture(name));
            }
        }
        return textures;
    }

    public static class Loader implements IModelLoader<MoldModel>
    {
        @Override
        public IResourceType getResourceType()
        {
            return VanillaResourceType.MODELS;
        }

        @Override
        public void onResourceManagerReload(ResourceManager manager) {}

        @Override
        public MoldModel read(JsonDeserializationContext context, JsonObject json)
        {
            return new MoldModel(textures);
        }
    }

    private static final class MoldOverride extends ItemOverrides
    {
        private static final ResourceLocation ID = new ResourceLocation("tfc:mold_override");

        private final Map<Fluid, BakedModel> cache = new HashMap<>();
        private final ItemOverrides parent;
        private final ModelBakery bakery;
        private final IModelConfiguration owner;
        private final MoldModel model;

        private MoldOverride(ItemOverrides parent, ModelBakery bakery, IModelConfiguration owner, MoldModel model)
        {
            this.parent = parent;
            this.bakery = bakery;
            this.owner = owner;
            this.model = model;
        }

        @Override
        public BakedModel resolve(BakedModel originalModel, ItemStack stack, @Nullable ClientLevel world, @Nullable LivingEntity entity, int seed)
        {
            BakedModel overriden = parent.resolve(originalModel, stack, world, entity, seed);
            if (overriden != originalModel)
            {
                return overriden;
            }
            return FluidUtil.getFluidContained(stack)
                .map(fluidStack -> {
                    final Fluid fluid = fluidStack.getFluid();
                    final BakedModel cached = cache.get(fluid);
                    if (cached != null)
                    {
                        return cached;
                    }
                    final BakedModel baked = this.model.withFluid(fluid).bake(owner, bakery, ModelLoader.defaultTextureGetter(), BlockModelRotation.X0_Y0, this, ID);
                    cache.put(fluid, baked);
                    return baked;
                })
                .orElse(originalModel);
        }
    }
}
*/
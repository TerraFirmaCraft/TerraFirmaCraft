package net.dries007.tfc.compat.wailatop;

import java.util.List;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.block.Block;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;

import mcp.MethodsReturnNonnullByDefault;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import mcp.mobius.waila.api.IWailaRegistrar;
import net.dries007.tfc.ConfigTFC;
import net.dries007.tfc.api.recipes.barrel.BarrelRecipe;
import net.dries007.tfc.api.types.ICrop;
import net.dries007.tfc.api.types.Metal;
import net.dries007.tfc.api.types.Ore;
import net.dries007.tfc.api.types.Rock;
import net.dries007.tfc.objects.blocks.agriculture.*;
import net.dries007.tfc.objects.blocks.stone.BlockFarmlandTFC;
import net.dries007.tfc.objects.blocks.stone.BlockOreTFC;
import net.dries007.tfc.objects.blocks.stone.BlockRockVariant;
import net.dries007.tfc.objects.blocks.wood.BlockBarrel;
import net.dries007.tfc.objects.items.metal.ItemOreTFC;
import net.dries007.tfc.objects.items.metal.ItemSmallOre;
import net.dries007.tfc.objects.items.rock.ItemRock;
import net.dries007.tfc.objects.te.*;
import net.dries007.tfc.util.calendar.CalendarTFC;
import net.dries007.tfc.util.calendar.Month;
import net.dries007.tfc.util.climate.ClimateTFC;
import net.dries007.tfc.world.classic.chunkdata.ChunkDataTFC;


@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public final class WailaBlocks implements IWailaDataProvider
{
    public static void callbackRegister(IWailaRegistrar registrar)
    {

        registrar.addConfig("TFC", "tfc.displayTemp", true);
        registrar.addConfig("TFC", "tfc.newtotfc", true);


        WailaBlocks dataProvider = new WailaBlocks();

        //Stack
        registrar.registerStackProvider(dataProvider, BlockOreTFC.class);
        registrar.registerStackProvider(dataProvider, BlockCropTFC.class);
        //Head
        registrar.registerHeadProvider(dataProvider, BlockOreTFC.class);
        registrar.registerHeadProvider(dataProvider, BlockBarrel.class);
        registrar.registerHeadProvider(dataProvider, TECropBase.class);
        registrar.registerHeadProvider(dataProvider, BlockCropDead.class);
        registrar.registerHeadProvider(dataProvider, BlockFruitTreeLeaves.class);
        registrar.registerHeadProvider(dataProvider, BlockFruitTreeTrunk.class);
        registrar.registerHeadProvider(dataProvider, BlockFruitTreeBranch.class);
        //Body
        registrar.registerBodyProvider(dataProvider, BlockOreTFC.class);
        registrar.registerBodyProvider(dataProvider, TEPitKiln.class);
        registrar.registerBodyProvider(dataProvider, TEFirePit.class);
        registrar.registerBodyProvider(dataProvider, TEBloomery.class);
        registrar.registerBodyProvider(dataProvider, TEBlastFurnace.class);
        registrar.registerBodyProvider(dataProvider, TECharcoalForge.class);
        registrar.registerBodyProvider(dataProvider, TEBarrel.class);
        registrar.registerBodyProvider(dataProvider, TELogPile.class);
        registrar.registerBodyProvider(dataProvider, TEPlacedItemFlat.class);
        registrar.registerBodyProvider(dataProvider, TECropBase.class);
        registrar.registerBodyProvider(dataProvider, BlockFruitTreeLeaves.class);
        registrar.registerBodyProvider(dataProvider, BlockBerryBush.class);
        registrar.registerBodyProvider(dataProvider, BlockCropDead.class);
        //Tail
        registrar.registerTailProvider(dataProvider, BlockRockVariant.class);


    }

    @Override
    public ItemStack getWailaStack(IWailaDataAccessor accessor, IWailaConfigHandler config)
    {
        Block b = accessor.getBlock();
        ItemStack stack = ItemStack.EMPTY;

        if (b instanceof BlockOreTFC)
            stack = getOreTFCStack(accessor, config);

        else if (b instanceof BlockCropSimple)
            stack = getCropSimpleStack(accessor, config);

        return stack;
    }

    @Override
    public List<String> getWailaHead(ItemStack stack, List<String> currentTooltip, IWailaDataAccessor accessor, IWailaConfigHandler config)
    {
        Block b = accessor.getBlock();
        TileEntity te = accessor.getTileEntity();

        if (b instanceof BlockOreTFC)
            currentTooltip = getOreTFCHead(stack, currentTooltip, accessor, config);
        if (b instanceof BlockBarrel)
            currentTooltip = getBarrelHead(stack, currentTooltip, accessor, config);
        if (te instanceof TECropBase)
            currentTooltip = getCropSimpleHead(stack, currentTooltip, accessor, config);
        if (b instanceof BlockCropDead)
            currentTooltip = getCropDeadHead(stack, currentTooltip, accessor, config);
        if (b instanceof BlockFruitTreeTrunk | b instanceof BlockFruitTreeLeaves | b instanceof BlockFruitTreeBranch)
            currentTooltip = getFruitTreeHead(stack, currentTooltip, accessor, config);

        return currentTooltip;
    }

    @Override
    public List<String> getWailaBody(ItemStack stack, List<String> currentTooltip, IWailaDataAccessor accessor, IWailaConfigHandler config)
    {
        Block b = accessor.getBlock();
        TileEntity te = accessor.getTileEntity();

        // Mechanics
        if (b instanceof BlockOreTFC)
            currentTooltip = getOreTFCBody(stack, currentTooltip, accessor, config);
        else if (te instanceof TEPlacedItemFlat)
            currentTooltip = getPlacedItemFlatBody(stack, currentTooltip, accessor, config);
        else if (te instanceof TEPitKiln)
            currentTooltip = getPitKilnBody(stack, currentTooltip, accessor, config);
        else if (te instanceof TECharcoalForge)
            currentTooltip = getCharcoalForgeBody(stack, currentTooltip, accessor, config);
        else if (te instanceof TELogPile)
            currentTooltip = getLogPileBody(stack, currentTooltip, accessor, config);
        else if (te instanceof TEBarrel)
            currentTooltip = getBarrelBody(stack, currentTooltip, accessor, config);
        else if (te instanceof TEBloomery)
            currentTooltip = getBloomeryBody(stack, currentTooltip, accessor, config);
        else if (te instanceof TEBlastFurnace)
            currentTooltip = getBlastFurnaceBody(stack, currentTooltip, accessor, config);
        else if (te instanceof TEFirePit)
            currentTooltip = getFirepitBody(stack, currentTooltip, accessor, config);
            // Crops and Trees
        else if (te instanceof TECropBase)
            currentTooltip = getCropSimpleBody(stack, currentTooltip, accessor, config);
        else if (b instanceof BlockFruitTreeLeaves)
            currentTooltip = getFruitTreeLeavesBody(stack, currentTooltip, accessor, config);
        else if (b instanceof BlockBerryBush)
            currentTooltip = getBerryBushBody(stack, currentTooltip, accessor, config);
        else if (b instanceof BlockCropDead)
            currentTooltip = getCropDeadBody(stack, currentTooltip, accessor, config);
        else if (b instanceof BlockFruitTreeLeaves)
            currentTooltip = getFruitTreeLeavesBody(stack, currentTooltip, accessor, config);

        return currentTooltip;
    }

    @Override
    public List<String> getWailaTail(ItemStack stack, List<String> currentTooltip, IWailaDataAccessor accessor, IWailaConfigHandler config)
    {
        if (config.getConfig("tfc.displayTemp"))
        {
            currentTooltip.add(new TextComponentTranslation("waila.tfc.temperature").getFormattedText() + " : " + String.valueOf(Math.round(ClimateTFC.getActualTemp(accessor.getWorld(), accessor.getPosition(), 0))) + new TextComponentTranslation("waila.tfc.tempsymbol").getFormattedText());
        }
        return currentTooltip;
    }

    private List<String> getFirepitBody(ItemStack stack, List<String> currentTooltip, IWailaDataAccessor accessor, IWailaConfigHandler config)
    {
        return currentTooltip;
    }

    private List<String> getPitKilnBody(ItemStack stack, List<String> currentTooltip, IWailaDataAccessor accessor, IWailaConfigHandler config)
    {
        Block b = accessor.getBlock();
        TEPitKiln te = (TEPitKiln) accessor.getTileEntity();
        String key;
        boolean isLit = te.isLit();
        long litTick = te.getLitTick();

        if (isLit)
        {
            long remainingMinutes = Math.round(((long) ConfigTFC.GENERAL.pitKilnTime - (CalendarTFC.PLAYER_TIME.getTicks() - litTick)) / 1200)+ 1;
            key = remainingMinutes + " " + new TextComponentTranslation("waila.tfc.remaining").getFormattedText();
        }
        else
        {
            int straw = te.getStrawCount();
            int logs = te.getLogCount();
            if (straw == 8 && logs == 8)
            {
                key = new TextComponentTranslation("unlit").getFormattedText();
            }
            else
            {
                key = straw + " " + new TextComponentTranslation("waila.tfc.straw").getFormattedText() + " " + logs + " " + new TextComponentTranslation("waila.tfc.logs").getFormattedText();
            }
        }
        currentTooltip.add(key);

        return currentTooltip;
    }

    private List<String> getOreTFCBody(ItemStack stack, List<java.lang.String> currentTooltip, IWailaDataAccessor accessor, IWailaConfigHandler config)
    {

        BlockOreTFC b = (BlockOreTFC) accessor.getBlock();
        int metadata = accessor.getMetadata();
        Ore.Grade gradevalue = Ore.Grade.valueOf(metadata);
        Metal metal = b.ore.getMetal();
        String orename = b.ore.toString();
        String key;

        if (gradevalue == Ore.Grade.NORMAL)
        {
            key = new TextComponentTranslation("waila.tfc.normal").getFormattedText() + " " + new TextComponentTranslation("item.tfc.ore." + orename + ".name").getFormattedText();
        }
        else
        {
            String gradename = gradevalue.toString().toLowerCase();
            key = new TextComponentTranslation("item.tfc.ore." + orename + "." + gradename + ".name").getFormattedText();
        }
        currentTooltip.add(key);
        if (metal != null && config.getConfig("tfc.newtotfc"))
        {
            currentTooltip.add("(" + new TextComponentTranslation(metal.getTranslationKey()).getFormattedText() + ")");
        }

        return currentTooltip;
    }

    private ItemStack getOreTFCStack(IWailaDataAccessor accessor, IWailaConfigHandler config)
    {

        BlockOreTFC b = (BlockOreTFC) accessor.getBlock();
        BlockStateContainer state = b.getBlockState();
        ItemStack stack = ItemOreTFC.get(b.ore, 1);

        return stack;
    }

    private List<String> getBlastFurnaceBody(ItemStack stack, List<String> currentTooltip, IWailaDataAccessor accessor, IWailaConfigHandler config)
    {
        //todo
        TEBlastFurnace te = (TEBlastFurnace) accessor.getTileEntity();
        NBTTagCompound nbt = accessor.getNBTData();
        // Something is borked with Waila reading the nbt second time round. Dunno
        return currentTooltip;
    }

    private List<String> getFruitTreeHead(ItemStack stack, List<String> currentTooltip, IWailaDataAccessor accessor, IWailaConfigHandler config)
    {
        String name = accessor.getBlock().getTranslationKey();
        String output = "waila.tfc.fruit_trees." + name.substring(name.lastIndexOf(".") + 1) + ".name";

        currentTooltip.set(0, new TextComponentTranslation(output).getFormattedText());

        return currentTooltip;
    }

    private ItemStack getCropSimpleStack(IWailaDataAccessor accessor, IWailaConfigHandler config)
    {

        BlockCropTFC b = (BlockCropTFC) accessor.getBlock();
        ICrop crop = b.getCrop();
        IBlockState blockstate = accessor.getBlockState();
        int curStage = blockstate.getValue(b.getStageProperty());
        ItemStack stack = crop.getFoodDrop(curStage);


        return stack;
    }

    private List<String> getCropDeadHead(ItemStack stack, List<String> currentTooltip, IWailaDataAccessor accessor, IWailaConfigHandler config)
    {
        BlockCropDead b = (BlockCropDead) accessor.getBlock();
        ICrop crop = b.getCrop();
        currentTooltip.set(0, TextFormatting.WHITE.toString() + new TextComponentTranslation("tile.tfc.crop." + crop.toString().toLowerCase() + ".name").getFormattedText());
        return currentTooltip;
    }

    private List<String> getCropSimpleHead(ItemStack stack, List<String> currentTooltip, IWailaDataAccessor accessor, IWailaConfigHandler config)
    {
        BlockCropTFC b = (BlockCropTFC) accessor.getBlock();
        currentTooltip.set(0, TextFormatting.WHITE.toString() + new TextComponentTranslation(b.getTranslationKey() + ".name").getFormattedText());

        return currentTooltip;
    }

    private List<String> getPlacedItemFlatBody(ItemStack stack, List<String> currentTooltip, IWailaDataAccessor accessor, IWailaConfigHandler config)
    {
        if (stack.getItem() instanceof ItemSmallOre)
        {
            ItemSmallOre nugget = (ItemSmallOre) stack.getItem();
            Ore ore = nugget.getOre();
            Metal metal = ore.getMetal();
            if (metal != null && config.getConfig("tfc.newtotfc"))
            {
                currentTooltip.add("(" + new TextComponentTranslation(metal.getTranslationKey()).getFormattedText() + ")");
            }
        }
        if (stack.getItem() instanceof ItemRock)
        {
            ItemRock pebble = (ItemRock) stack.getItem();
            Rock rock = pebble.getRock(stack);
            if (rock.isFluxStone() && config.getConfig("tfc.newtotfc"))
            {
                currentTooltip.add("(" + new TextComponentTranslation("waila.tfc.fluxstone").getFormattedText() + ")");
            }

        }
        return currentTooltip;

    }

    private List<String> getBarrelHead(ItemStack stack, List<String> currentTooltip, IWailaDataAccessor accessor, IWailaConfigHandler config)
    {
        BlockBarrel b = (BlockBarrel) accessor.getBlock();
        TEBarrel te = (TEBarrel) accessor.getTileEntity();

        if (te.isSealed())
        {
            String sealedDate;
            sealedDate = te.getSealedDate();
            currentTooltip.set(0, TextFormatting.WHITE.toString() + new TextComponentTranslation(b.getTranslationKey() + ".sealed.name").getFormattedText());
            currentTooltip.add(new TextComponentTranslation("waila.tfc.sealed").getFormattedText() + ":" + sealedDate);


        }
        else
        {
            currentTooltip.set(0, TextFormatting.WHITE.toString() + new TextComponentTranslation(b.getTranslationKey() + ".name").getFormattedText());
        }
        return currentTooltip;
    }

    private List<String> getCropDeadBody(ItemStack stack, List<String> currentTooltip, IWailaDataAccessor accessor, IWailaConfigHandler config)
    {
        currentTooltip.add(new TextComponentTranslation("waila.tfc.deadcrop").getFormattedText());
        return currentTooltip;
    }


    private List<String> getBerryBushBody(ItemStack stack, List<String> currentTooltip, IWailaDataAccessor accessor, IWailaConfigHandler config)
    {
        BlockBerryBush b = (BlockBerryBush) accessor.getBlock();
        String text = "";

        for (int i = 0; i < 12; i++)
        {
            if (b.bush.isHarvestMonth(Month.valueOf(i)))
            {
                text = text + " " + new TextComponentTranslation("tfc.enum.month." + Month.valueOf(i).name().toLowerCase()).getFormattedText();

            }
        }
        currentTooltip.add(text.trim());

        return currentTooltip;
    }

    private List<String> getFruitTreeLeavesBody(ItemStack stack, List<String> currentTooltip, IWailaDataAccessor accessor, IWailaConfigHandler config)
    {
        BlockFruitTreeLeaves b = (BlockFruitTreeLeaves) accessor.getBlock();
        String text = "";

        for (int i = 0; i < 12; i++)
        {
            if (b.tree.isHarvestMonth(Month.valueOf(i)))
            {
                text = text + " " + new TextComponentTranslation("tfc.enum.month." + Month.valueOf(i).name().toLowerCase()).getFormattedText();

            }
        }
        currentTooltip.add(text.trim());

        return currentTooltip;
    }

    private List<String> getCropSimpleBody(ItemStack stack, List<String> currentTooltip, IWailaDataAccessor accessor, IWailaConfigHandler config)
    {
        TECropBase te = (TECropBase) accessor.getTileEntity();
        BlockCropSimple bs = (BlockCropSimple) accessor.getBlock();
        ICrop crop = bs.getCrop();
        int maxStage = crop.getMaxStage();
        float totalGrowthTime = crop.getGrowthTime();
        IBlockState blockstate = accessor.getBlockState();
        int curStage = blockstate.getValue(bs.getStageProperty());
        boolean isWild = blockstate.getValue(BlockCropTFC.WILD);
        long tick = te.getLastUpdateTick();
        float totalTime = totalGrowthTime * maxStage;
        float currentTime = (curStage * totalGrowthTime) + (CalendarTFC.PLAYER_TIME.getTicks() - tick);
        int completionPerc = Math.round(currentTime / totalTime * 100);
        float temp = ClimateTFC.getActualTemp(accessor.getWorld(), accessor.getPosition(), -tick);
        float rainfall = ChunkDataTFC.getRainfall(accessor.getWorld(), accessor.getPosition());
        String text;
        if (isWild)
        {
            text = new TextComponentTranslation("waila.tfc.wild").getFormattedText();
            if (completionPerc <= 100)
            {
                text = text + TextFormatting.GRAY.toString() + " : " + completionPerc + "%";
            }
        }
        else if (crop.isValidForGrowth(temp, rainfall))
        {
            text = TextFormatting.GREEN.toString() + new TextComponentTranslation("waila.tfc.growing").getFormattedText();
            if (completionPerc <= 100)
            {
                text = text + TextFormatting.GRAY.toString() + " : " + completionPerc + "%";
            }
        }
        else
        {
            text = TextFormatting.RED.toString() + new TextComponentTranslation("waila.tfc.notgrowing").getFormattedText();
        }

        if (completionPerc > 100)
        {
            //Should test here if crop is 'pickable' and indicate as the action is different for each
            text = text + TextFormatting.GREEN.toString() + " : " + new TextComponentTranslation("waila.tfc.mature").getFormattedText();
        }
        currentTooltip.add(text);

        return currentTooltip;
    }

    private List<String> getBloomeryBody(ItemStack stack, List<String> currentTooltip, IWailaDataAccessor accessor, IWailaConfigHandler config)
    {
        //todo
        return currentTooltip;
    }

    private List<String> getBarrelBody(ItemStack stack, List<String> currentTooltip, IWailaDataAccessor accessor, IWailaConfigHandler config)
    {
        TEBarrel te = (TEBarrel) accessor.getTileEntity();

        String result;
        NBTTagCompound itemTag = te.getItemTag();

        NBTTagCompound tank = itemTag.getCompoundTag("tank");
        String fluid = tank.getString("FluidName");
        String fullfluid = new TextComponentTranslation("fluid." + fluid).getFormattedText();
        int amount = tank.getInteger("Amount");

        if (te.isSealed())
        {
            BarrelRecipe recipe = te.getRecipe();
            if (recipe != null)
            {

                result = new TextComponentTranslation("waila.tfc.making").getFormattedText() + " " + recipe.getResultName();
                currentTooltip.add(result);
            }
            else
            {
                result = new TextComponentTranslation("waila.tfc.norecipe").getFormattedText();
                currentTooltip.add(result);
            }
        }
        if (amount > 0)
        {
            result = new TextComponentTranslation("waila.tfc.contains").getFormattedText() + " " + amount + " units of " + fullfluid;
            currentTooltip.add(result);
        }

        return currentTooltip;
    }

    private List<String> getLogPileBody(ItemStack stack, List<String> currentTooltip, IWailaDataAccessor accessor, IWailaConfigHandler config)
    {
        //todo
        return currentTooltip;
    }

    private List<String> getCharcoalForgeBody(ItemStack stack, List<String> currentTooltip, IWailaDataAccessor accessor, IWailaConfigHandler config)
    {
        //todo
        return currentTooltip;
    }

    private List<String> getOreTFCHead(ItemStack stack, List<String> currentTooltip, IWailaDataAccessor accessor, IWailaConfigHandler config)
    {
        BlockOreTFC b = (BlockOreTFC) accessor.getBlock();
        currentTooltip.set(0, TextFormatting.WHITE.toString() + new TextComponentTranslation(b.getTranslationKey() + ".name").getFormattedText());
        return currentTooltip;
    }

}

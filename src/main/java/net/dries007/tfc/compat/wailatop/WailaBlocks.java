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

        registrar.addConfig("TFC", "tfc.displayTemp");

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
        registrar.registerBodyProvider(dataProvider, BlockFarmlandTFC.class);
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
        ItemStack itemstack = ItemStack.EMPTY;

        if (b instanceof BlockOreTFC)
        {
            itemstack = OreTFCStack(accessor, config);
        }
        else if (b instanceof BlockCropSimple)
        {
            itemstack = CropSimpleStack(accessor, config);
        }
        return itemstack;
    }

    @Override
    public List<String> getWailaHead(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config)
    {
        Block b = accessor.getBlock();
        TileEntity te = accessor.getTileEntity();

        if (b instanceof BlockOreTFC) currenttip = OreTFCHead(itemStack, currenttip, accessor, config);
        if (b instanceof BlockBarrel) currenttip = BarrelHead(itemStack, currenttip, accessor, config);
        if (te instanceof TECropBase) currenttip = CropSimpleHead(itemStack, currenttip, accessor, config);
        if (b instanceof BlockCropDead) currenttip = CropDeadHead(itemStack, currenttip, accessor, config);
        if (b instanceof BlockFruitTreeTrunk | b instanceof BlockFruitTreeLeaves | b instanceof BlockFruitTreeBranch)
        {
            currenttip = FruitTreeHead(itemStack, currenttip, accessor, config);
        }
        return currenttip;
    }

    @Override
    public List<String> getWailaBody(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config)
    {
        Block b = accessor.getBlock();
        TileEntity te = accessor.getTileEntity();

        // Mechanics
        if (b instanceof BlockOreTFC) currenttip = OreTFCBody(itemStack, currenttip, accessor, config);
        else if (te instanceof TEPlacedItemFlat)
            currenttip = PlacedItemFlatBody(itemStack, currenttip, accessor, config);
        else if (te instanceof TEPitKiln) currenttip = PitKilnBody(itemStack, currenttip, accessor, config);
        else if (te instanceof TECharcoalForge)
            currenttip = CharcoalForgeBody(itemStack, currenttip, accessor, config);
        else if (te instanceof TELogPile) currenttip = LogPileBody(itemStack, currenttip, accessor, config);
        else if (te instanceof TEBarrel) currenttip = BarrelBody(itemStack, currenttip, accessor, config);
        else if (te instanceof TEBloomery) currenttip = BloomeryBody(itemStack, currenttip, accessor, config);
        else if (te instanceof TEBlastFurnace)
            currenttip = BlastFurnaceBody(itemStack, currenttip, accessor, config);
        else if (te instanceof TEFirePit) currenttip = FirepitBody(itemStack, currenttip, accessor, config);
            // Crops and Trees
        else if (te instanceof TECropBase) currenttip = CropSimpleBody(itemStack, currenttip, accessor, config);
        else if (te instanceof TECropSpreading)
            currenttip = CropSpreadingBody(itemStack, currenttip, accessor, config);
        else if (b instanceof BlockFruitTreeLeaves)
            currenttip = FruitTreeLeavesBody(itemStack, currenttip, accessor, config);
        else if (b instanceof BlockBerryBush) currenttip = BerryBushBody(itemStack, currenttip, accessor, config);
        else if (b instanceof BlockFarmlandTFC)
            currenttip = FarmlandTFCBody(itemStack, currenttip, accessor, config);
        else if (b instanceof BlockCropDead) currenttip = CropDeadBody(itemStack, currenttip, accessor, config);
        else if (b instanceof BlockFruitTreeLeaves)
            currenttip = FruitTreeLeavesBody(itemStack, currenttip, accessor, config);

        return currenttip;
    }

    @Override
    public List<String> getWailaTail(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config)
    {
        if (config.getConfig("tfc.displayTemp"))
        {
            currenttip.add(new TextComponentTranslation("waila.temperature").getFormattedText() + " : " + String.valueOf(Math.round(ClimateTFC.getActualTemp(accessor.getWorld(), accessor.getPosition(), 0))) + new TextComponentTranslation("waila.tempsymbol").getFormattedText());
        }
        return currenttip;
    }

    public List<String> FirepitBody(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config)
    {
        return currenttip;
    }

    public List<String> PitKilnBody(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config)
    {
        Block b = accessor.getBlock();
        TEPitKiln te = (TEPitKiln) accessor.getTileEntity();
        String key;
        Boolean isLit = te.isLit();
        Long litTick = te.getLitTick();

        if (isLit)
        {
            long remainingMinutes = Math.round(((long) ConfigTFC.GENERAL.pitKilnTime - (CalendarTFC.PLAYER_TIME.getTicks() - litTick)) / 1200);
            key = remainingMinutes + " " + new TextComponentTranslation("waila.remaining").getFormattedText();
        }
        else
        {
            Integer straw = te.getStrawCount();
            Integer logs = te.getLogCount();
            if (straw == 8 && logs == 8)
            {
                key = new TextComponentTranslation("unlit").getFormattedText();
            }
            else
            {
                key = straw + " " + new TextComponentTranslation("waila.straw").getFormattedText() + " " + logs + " " + new TextComponentTranslation("waila.logs").getFormattedText();
            }
        }
        currenttip.add(key);

        return currenttip;
    }

    public List<String> OreTFCBody(ItemStack itemStack, List<java.lang.String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config)
    {

        BlockOreTFC b = (BlockOreTFC) accessor.getBlock();
        int metadata = accessor.getMetadata();
        Ore.Grade gradevalue = Ore.Grade.valueOf(metadata);
        Metal metal = b.ore.getMetal();
        String orename = b.ore.toString();
        String key;

        if (gradevalue == Ore.Grade.NORMAL)
        {
            key = new TextComponentTranslation("waila.normal").getFormattedText() + " " + new TextComponentTranslation("item.tfc.ore." + orename + ".name").getFormattedText();
        }
        else
        {
            String gradename = gradevalue.toString().toLowerCase();
            key = new TextComponentTranslation("item.tfc.ore." + orename + "." + gradename + ".name").getFormattedText();
        }
        currenttip.add(key);
        if (metal != null)
        {
            currenttip.add("(" + new TextComponentTranslation(metal.getTranslationKey()).getFormattedText() + ")");
        }

        return currenttip;
    }

    public ItemStack OreTFCStack(IWailaDataAccessor accessor, IWailaConfigHandler config)
    {

        BlockOreTFC b = (BlockOreTFC) accessor.getBlock();
        BlockStateContainer state = b.getBlockState();
        ItemStack itemstack = ItemOreTFC.get(b.ore, 1);

        return itemstack;
    }

    private List<String> BlastFurnaceBody(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config)
    {
        //todo
        TEBlastFurnace te = (TEBlastFurnace) accessor.getTileEntity();
        NBTTagCompound nbt = accessor.getNBTData();
        // Something is borked with Waila reading the nbt second time round. Dunno
        return currenttip;
    }

    private List<String> FruitTreeHead(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config)
    {
        String name = accessor.getBlock().getTranslationKey();
        String output = "waila.fruit_trees." + name.substring(name.lastIndexOf(".") + 1) + ".name";

        currenttip.set(0, new TextComponentTranslation(output).getFormattedText());

        return currenttip;
    }

    private ItemStack CropSimpleStack(IWailaDataAccessor accessor, IWailaConfigHandler config)
    {

        BlockCropTFC b = (BlockCropTFC) accessor.getBlock();
        ICrop crop = b.getCrop();
        IBlockState blockstate = accessor.getBlockState();
        Integer curStage = blockstate.getValue(b.getStageProperty());
        ItemStack foodDrop = crop.getFoodDrop(curStage);


        return foodDrop;
    }

    private List<String> CropDeadHead(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config)
    {
        BlockCropDead b = (BlockCropDead) accessor.getBlock();
        ICrop crop = b.getCrop();
        currenttip.set(0, TextFormatting.WHITE.toString() + new TextComponentTranslation("tile.tfc.crop." + crop.toString().toLowerCase() + ".name").getFormattedText());
        return currenttip;
    }

    private List<String> CropSimpleHead(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config)
    {
        BlockCropTFC b = (BlockCropTFC) accessor.getBlock();
        currenttip.set(0, TextFormatting.WHITE.toString() + new TextComponentTranslation(b.getTranslationKey() + ".name").getFormattedText());

        return currenttip;
    }

    private List<String> PlacedItemFlatBody(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config)
    {
        TEPlacedItemFlat te = (TEPlacedItemFlat) accessor.getTileEntity();
        if (itemStack.getItem() instanceof ItemSmallOre)
        {
            ItemSmallOre nugget = (ItemSmallOre) itemStack.getItem();
            Ore ore = nugget.getOre();
            Metal metal = ore.getMetal();
            if (metal != null)
            {
                currenttip.add("(" + new TextComponentTranslation(metal.getTranslationKey()).getFormattedText() + ")");
            }
        }
        if (itemStack.getItem() instanceof ItemRock)
        {
            ItemRock pebble = (ItemRock) itemStack.getItem();
            Rock rock = pebble.getRock(itemStack);
            if (rock.isFluxStone())
            {
                currenttip.add("(" + new TextComponentTranslation("waila.fluxstone").getFormattedText() + ")");
            }

        }
        return currenttip;

    }

    private List<String> BarrelHead(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config)
    {
        BlockBarrel b = (BlockBarrel) accessor.getBlock();
        TEBarrel te = (TEBarrel) accessor.getTileEntity();
        currenttip.set(0, TextFormatting.WHITE.toString() + new TextComponentTranslation(b.getTranslationKey() + ".name").getFormattedText());
        if (te.isSealed())
        {
            String sealedDate;
            sealedDate = te.getSealedDate();
            currenttip.add(sealedDate);


        }
        return currenttip;
    }

    private List<String> CropDeadBody(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config)
    {
        currenttip.add(new TextComponentTranslation("waila.deadcrop").getFormattedText());
        return currenttip;
    }

    private List<String> FarmlandTFCBody(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config)
    {
        //anything to do here?
        return currenttip;
    }

    private List<String> BerryBushBody(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config)
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
        currenttip.add(text.trim());

        return currenttip;
    }

    private List<String> FruitTreeLeavesBody(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config)
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
        currenttip.add(text.trim());

        return currenttip;
    }

    private List<String> CropSpreadingBody(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config)
    {
        //Currently unused
        return currenttip;
    }

    private List<String> CropSimpleBody(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config)
    {
        TECropBase te = (TECropBase) accessor.getTileEntity();
        BlockCropSimple bs = (BlockCropSimple) accessor.getBlock();
        ICrop crop = bs.getCrop();
        Integer maxStage = crop.getMaxStage();
        Float totalGrowthTime = crop.getGrowthTime();
        IBlockState blockstate = accessor.getBlockState();
        Integer curStage = blockstate.getValue(bs.getStageProperty());

        Boolean isWild = blockstate.getValue(BlockCropTFC.WILD);
        long tick = te.getLastUpdateTick();
        Float totalTime = totalGrowthTime * maxStage;
        Float currentTime = (curStage * totalGrowthTime) + (CalendarTFC.PLAYER_TIME.getTicks() - tick);
        int completionPerc = Math.round(currentTime / totalTime * 100);
        float temp = ClimateTFC.getActualTemp(accessor.getWorld(), accessor.getPosition(), -tick);
        float rainfall = ChunkDataTFC.getRainfall(accessor.getWorld(), accessor.getPosition());
        String text;
        if (isWild)
        {
            text = new TextComponentTranslation("waila.wild").getFormattedText();
            if (completionPerc <= 100)
            {
                text = text + TextFormatting.GRAY.toString() + " : " + completionPerc + "%";
            }
        }
        else if (crop.isValidForGrowth(temp, rainfall))
        {
            text = TextFormatting.GREEN.toString() + new TextComponentTranslation("waila.growing").getFormattedText();
            if (completionPerc <= 100)
            {
                text = text + TextFormatting.GRAY.toString() + " : " + completionPerc + "%";
            }
        }
        else
        {
            text = TextFormatting.RED.toString() + new TextComponentTranslation("waila.notgrowing").getFormattedText();
        }

        if (completionPerc > 100)
        {
            //Should test here if crop is 'pickable' and indicate as the action is different for each
            text = text + TextFormatting.GREEN.toString() + " : " + new TextComponentTranslation("waila.mature").getFormattedText();
        }
        currenttip.add(text);

        return currenttip;
    }

    private List<String> BloomeryBody(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config)
    {
        //todo
        return currenttip;
    }

    private List<String> BarrelBody(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config)
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

                result = new TextComponentTranslation("waila.making").getFormattedText() + " " + recipe.getResultName();
                currenttip.add(result);
            }
            else
            {
                result = new TextComponentTranslation("waila.norecipe").getFormattedText();
                currenttip.add(result);
            }
        }
        if (amount > 0)
        {
            result = new TextComponentTranslation("waila.contains").getFormattedText() + " " + amount + " units of " + fullfluid;
            currenttip.add(result);
        }

        return currenttip;
    }

    private List<String> LogPileBody(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config)
    {
        //todo
        return currenttip;
    }

    private List<String> CharcoalForgeBody(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config)
    {
        //todo
        return currenttip;
    }

    private List<String> OreTFCHead(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config)
    {
        BlockOreTFC b = (BlockOreTFC) accessor.getBlock();
        currenttip.set(0, TextFormatting.WHITE.toString() + new TextComponentTranslation(b.getTranslationKey() + ".name").getFormattedText());
        return currenttip;
    }

}

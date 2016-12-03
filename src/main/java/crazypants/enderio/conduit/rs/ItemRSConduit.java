package crazypants.enderio.conduit.rs;

import crazypants.enderio.EnderIO;
import crazypants.enderio.ModObject;
import crazypants.enderio.conduit.AbstractItemConduit;
import crazypants.enderio.conduit.ConduitDisplayMode;
import crazypants.enderio.conduit.ItemConduitSubtype;
import crazypants.enderio.config.Config;
import crazypants.enderio.gui.IconEIO;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Loader;

public class ItemRSConduit extends AbstractItemConduit {
    private static ItemConduitSubtype[] subtypes = new ItemConduitSubtype[]{
            new ItemConduitSubtype(ModObject.itemRSConduit.name(), EnderIO.DOMAIN + ":itemRSConduit")
    };

    protected ItemRSConduit() {
        super(ModObject.itemRSConduit, subtypes);
    }

    public static ItemRSConduit create() {
        ItemRSConduit result = new ItemRSConduit();
        if (Loader.isModLoaded("refinedstorage") && Config.enableRSConduits) {
            result.init();
            ConduitDisplayMode.registerDisplayMode(new ConduitDisplayMode(IRSConduit.class, IconEIO.WRENCH_OVERLAY_RS, IconEIO.WRENCH_OVERLAY_RS_OFF));
            MinecraftForge.EVENT_BUS.register(RSConduitCapabilityProvider.class);
        }
        return result;
    }

    @Override
    public boolean shouldHideFacades(ItemStack stack, EntityPlayer player) {
        return true;
    }

    @Override
    public Class<IRSConduit> getBaseConduitType() {
        return IRSConduit.class;
    }

    @Override
    public IRSConduit createConduit(ItemStack item, EntityPlayer player) {
        return new RSConduit();
    }
}

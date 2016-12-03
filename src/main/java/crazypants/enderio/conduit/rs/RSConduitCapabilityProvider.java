package crazypants.enderio.conduit.rs;

import com.raoulvdberge.refinedstorage.api.network.INetworkNode;
import crazypants.enderio.EnderIO;
import crazypants.enderio.conduit.ConnectionMode;
import crazypants.enderio.conduit.TileConduitBundle;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import javax.annotation.Nullable;

public class RSConduitCapabilityProvider implements ICapabilityProvider {
    private static final ResourceLocation KEY = new ResourceLocation(EnderIO.DOMAIN, "EioCapRSNetworkNode");

    @CapabilityInject(INetworkNode.class)
    public static Capability<INetworkNode> NETWORK_NODE_CAPABILITY = null;

    private final TileConduitBundle tile;

    public RSConduitCapabilityProvider(TileConduitBundle tile) {
        this.tile = tile;
    }

    @SubscribeEvent
    public static void attachCapabilities(AttachCapabilitiesEvent<TileEntity> event) {
        Object object = event.getObject();
        if (object instanceof TileConduitBundle) {
            event.addCapability(KEY, new RSConduitCapabilityProvider((TileConduitBundle) object));
        }
    }

    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
        if (capability == NETWORK_NODE_CAPABILITY) {
            IRSConduit conduit = tile.getConduit(IRSConduit.class);
            return conduit != null && !conduit.isRemoved() &&
                    conduit.getConnectionMode(facing) == ConnectionMode.IN_OUT;
        }
        return false;
    }

    @Override
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
        if (capability == NETWORK_NODE_CAPABILITY) {
            IRSConduit conduit = tile.getConduit(IRSConduit.class);
            if (conduit != null && !conduit.isRemoved() &&
                    conduit.getConnectionMode(facing) == ConnectionMode.IN_OUT) {
                return NETWORK_NODE_CAPABILITY.<T>cast(conduit.getRSNetworkNode());
            }
        }
        return null;
    }
}

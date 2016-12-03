package crazypants.enderio.conduit.rs;

import com.raoulvdberge.refinedstorage.api.network.INetworkMaster;
import com.raoulvdberge.refinedstorage.api.network.INetworkNode;
import crazypants.enderio.conduit.IConduitBundle;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Optional.Interface;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static crazypants.enderio.conduit.rs.RSConduitCapabilityProvider.NETWORK_NODE_CAPABILITY;

@Interface(modid = "refinedstorage", iface = "com.raoulvdberge.refinedstorage.api.network.INetworkNode")
public class RSNetworkNode implements INetworkNode {
    private final RSConduit conduit;
    private INetworkMaster rsNetwork;

    public RSNetworkNode(RSConduit conduit) {
        this.conduit = conduit;
    }

    @Override
    public int getEnergyUsage() {
        return 0;
    }

    @Override
    public BlockPos getPosition() {
        IConduitBundle bundle = conduit.getBundle();
        return bundle != null ? bundle.getEntity().getPos() : null;
    }

    @Nonnull
    @Override
    public ItemStack getItemStack() {
        return conduit.createItem();
    }

    @Override
    public void onConnected(INetworkMaster network) {
        this.rsNetwork = network;
    }

    @Override
    public void onDisconnected(INetworkMaster iNetworkMaster) {
        this.rsNetwork = null;
    }

    @Override
    public boolean canUpdate() {
        return false;
    }

    @Override
    public boolean canConduct(EnumFacing enumFacing) {
        return conduit.canConnectToExternal(enumFacing, true);
    }

    @Override
    public INetworkMaster getNetwork() {
        return this.rsNetwork;
    }

    @Override
    public World getNodeWorld() {
        IConduitBundle bundle = conduit.getBundle();
        return bundle != null ? bundle.getEntity().getWorld() : null;
    }

    public boolean canConnectToSide(EnumFacing side) {
        TileEntity tile = conduit.getBundle().getEntity();
        World world = tile.getWorld();
        BlockPos pos = tile.getPos();
        TileEntity neighborTile = world.getTileEntity(pos.offset(side));
        if (neighborTile != null) {
            INetworkNode neighborCapability = neighborTile.getCapability(NETWORK_NODE_CAPABILITY, side.getOpposite());
            if (neighborCapability != null) {
                INetworkMaster neighborNetwork = neighborCapability.getNetwork();
                return neighborNetwork == null || rsNetwork == null || neighborNetwork == rsNetwork;
            }
        }
        return false;
    }

    public boolean canConnectToConduit(IRSConduit conduit) {
        if (rsNetwork != null) {
            RSNetworkNode otherNetworkNode = conduit.getRSNetworkNode();
            INetworkMaster otherRSNetwork = otherNetworkNode.getNetwork();
            if (otherRSNetwork != null && otherRSNetwork != rsNetwork) {
                return false;
            }
        }
        return true;
    }

    public void removeNode() {
        if (rsNetwork != null) {
            rsNetwork.getNodeGraph().rebuild();
        }
    }

    public void connectToSide(EnumFacing side) {
        if (rsNetwork != null) {
            rsNetwork.getNodeGraph().rebuild();
            return;
        }

        TileEntity tile = conduit.getBundle().getEntity();
        World world = tile.getWorld();
        BlockPos pos = tile.getPos();
        TileEntity neighborTile = world.getTileEntity(pos.offset(side));
        if (neighborTile != null) {
            INetworkNode neighborCapability = neighborTile.getCapability(NETWORK_NODE_CAPABILITY, side.getOpposite());
            if (neighborCapability != null) {
                INetworkMaster neighborNetwork = neighborCapability.getNetwork();
                if (neighborNetwork != null) {
                    neighborNetwork.getNodeGraph().rebuild();
                }
            }
        }
    }

    public void disconnectFromSide(EnumFacing fromDirection) {
        if (rsNetwork != null) {
            rsNetwork.getNodeGraph().rebuild();
        }
    }
}

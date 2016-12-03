package crazypants.enderio.conduit.rs;

import com.enderio.core.common.vecmath.Vector4f;
import crazypants.enderio.conduit.AbstractConduit;
import crazypants.enderio.conduit.AbstractConduitNetwork;
import crazypants.enderio.conduit.ConnectionMode;
import crazypants.enderio.conduit.IConduit;
import crazypants.enderio.conduit.geom.CollidableComponent;
import crazypants.enderio.render.registry.TextureRegistry;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import static crazypants.enderio.ModObject.itemRSConduit;

public class RSConduit extends AbstractConduit implements IRSConduit {
    private static final TextureRegistry.TextureSupplier coreTexture = TextureRegistry.registerTexture("blocks/rsConduitCore");
    private static final TextureRegistry.TextureSupplier longTexture = TextureRegistry.registerTexture("blocks/rsConduit");
    private RSNetworkNode rsNetworkNode;
    private RSConduitNetwork eioNetwork;
    private boolean removed = false;

    @SideOnly(Side.CLIENT)
    public static void initIcons() {
    }

    @Override
    public Class<IRSConduit> getBaseConduitType() {
        return IRSConduit.class;
    }

    @Override
    public ItemStack createItem() {
        return new ItemStack(itemRSConduit.getItem(), 1, 0);
    }

    @Override
    public RSConduitNetwork getNetwork() {
        return eioNetwork;
    }

    @Override
    public boolean setNetwork(AbstractConduitNetwork<?, ?> network) {
        if (network != null && !(network instanceof RSConduitNetwork)) {
            return false;
        }
        eioNetwork = (RSConduitNetwork) network;
        return true;
    }

    @Override
    public void onRemovedFromBundle() {
        removed = true;
        if (rsNetworkNode != null) {
            rsNetworkNode.removeNode();
        }
        super.onRemovedFromBundle();
    }

    @Override
    public ConnectionMode getNextConnectionMode(EnumFacing dir) {
        ConnectionMode mode = getConnectionMode(dir);
        mode = mode == ConnectionMode.IN_OUT ? ConnectionMode.DISABLED : ConnectionMode.IN_OUT;
        return mode;
    }

    @Override
    public ConnectionMode getPreviousConnectionMode(EnumFacing dir) {
        return getNextConnectionMode(dir);
    }


    @Override
    public void conduitConnectionAdded(EnumFacing fromDirection) {
        super.conduitConnectionAdded(fromDirection);
        getRSNetworkNode().connectToSide(fromDirection);
    }

    @Override
    public void conduitConnectionRemoved(EnumFacing fromDirection) {
        getRSNetworkNode().disconnectFromSide(fromDirection);
        super.conduitConnectionRemoved(fromDirection);
    }

    @Override
    public void externalConnectionAdded(EnumFacing fromDirection) {
        super.externalConnectionAdded(fromDirection);
        getRSNetworkNode().connectToSide(fromDirection);
    }

    @Override
    public void externalConnectionRemoved(EnumFacing fromDirection) {
        getRSNetworkNode().disconnectFromSide(fromDirection);
        super.externalConnectionRemoved(fromDirection);
    }

    @Override
    public TextureAtlasSprite getTextureForState(CollidableComponent component) {
        if (component.dir == null) {
            return coreTexture.get(TextureAtlasSprite.class);
        } else {
            return longTexture.get(TextureAtlasSprite.class);
        }
    }

    @Override
    public TextureAtlasSprite getTransmitionTextureForState(CollidableComponent component) {
        return null;
    }

    @Override
    public Vector4f getTransmitionTextureColorForState(CollidableComponent component) {
        return null;
    }

    @Override
    public AbstractConduitNetwork<?, ?> createNetworkForType() {
        return new RSConduitNetwork();
    }

    @Override
    public boolean canConnectToConduit(EnumFacing direction, IConduit conduit) {
        if (!super.canConnectToConduit(direction, conduit)) {
            return false;
        }
        if (!(conduit instanceof IRSConduit)) {
            return false;
        }
        return getRSNetworkNode().canConnectToConduit((IRSConduit) conduit);
    }

    @Override
    public boolean canConnectToExternal(EnumFacing direction, boolean ignoreConnectionMode) {
        return getConnectionMode(direction) != ConnectionMode.DISABLED
                && getRSNetworkNode().canConnectToSide(direction);
    }

    @Override
    public RSNetworkNode getRSNetworkNode() {
        if (rsNetworkNode == null) {
            rsNetworkNode = new RSNetworkNode(this);
        }
        return rsNetworkNode;
    }

    public boolean isRemoved() {
        return removed;
    }
}

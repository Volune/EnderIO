package crazypants.enderio.conduit.rs;

import crazypants.enderio.conduit.IConduit;

public interface IRSConduit extends IConduit {
    RSNetworkNode getRSNetworkNode();

    boolean isRemoved();
}

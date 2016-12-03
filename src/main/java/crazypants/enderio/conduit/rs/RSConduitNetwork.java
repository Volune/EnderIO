package crazypants.enderio.conduit.rs;

import crazypants.enderio.conduit.AbstractConduitNetwork;

public class RSConduitNetwork extends AbstractConduitNetwork<IRSConduit, RSConduit> {
    protected RSConduitNetwork() {
        super(RSConduit.class, IRSConduit.class);
    }
}

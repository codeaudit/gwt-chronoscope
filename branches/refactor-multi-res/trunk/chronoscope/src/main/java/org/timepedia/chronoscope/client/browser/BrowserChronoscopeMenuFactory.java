package org.timepedia.chronoscope.client.browser;

import org.timepedia.chronoscope.client.ChronoscopeMenu;
import org.timepedia.chronoscope.client.ChronoscopeMenuFactory;

/**
 * A menu factory which creates GWT Menus
 *
 * @author Ray Cromwell &lt;ray@timepedia.org&gt;
 */
public class BrowserChronoscopeMenuFactory implements ChronoscopeMenuFactory {
    public ChronoscopeMenu createChronoscopeMenu(int x, int y) {
        return new BrowserChronoscopeMenu(x, y);
    }
}

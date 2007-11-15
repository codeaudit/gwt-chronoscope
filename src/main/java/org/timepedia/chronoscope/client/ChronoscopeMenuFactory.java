package org.timepedia.chronoscope.client;

/**
 * Implement this to swap out the types of popup menus created by Chronoscope
 *
 * @author Ray Cromwell &lt;ray@timepedia.org&gt;
 */
public interface ChronoscopeMenuFactory {
    public ChronoscopeMenu createChronoscopeMenu(int x, int y);
}

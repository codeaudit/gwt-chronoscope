package org.timepedia.chronoscope.client.canvas;

/**
 * A DisplayList is a Layer with deferred execution.
 * <p/>
 * Drawing operations on DisplayList are buffered and replayed later.
 *
 * @author Ray Cromwell &lt;ray@timepedia.org&gt;
 */
public interface DisplayList extends Layer {
    /**
     * Execute the given displaylist
     */
    void execute();
}

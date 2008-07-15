package org.timepedia.chronoscope.client.browser;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.PopupPanel;

import org.timepedia.chronoscope.client.ChronoscopeMenu;
import org.timepedia.exporter.client.Export;

/**
 * An implementation of ChronoscopeMenu using GWT PopupPanel and
 * MenuBar/Menuitem
 */
public class BrowserChronoscopeMenu extends PopupPanel
    implements ChronoscopeMenu {

  static class ChronoscopeMenuBar extends MenuBar {

    /**
     * Creates an empty horizontal menu bar.
     */
    public ChronoscopeMenuBar() {
    }

    /**
     * Creates an empty menu bar.
     *
     * @param vertical <code>true</code> to orient the menu bar vertically
     */
    public ChronoscopeMenuBar(boolean vertical) {
      super(vertical);
    }

    /**
     * This method is called when a widget is attached to the browser's
     * document. It must not be overridden, except by {@link
     * com.google.gwt.user.client.ui.Panel}. To receive notification when a
     * widget is attached to the document, override the {@link #onLoad} method.
     */
    protected void onAttach() {
      super.onAttach();
      // HACK: until GWT RFE to set style on implicit PopupPanel is fixed
      DOM.setIntStyleAttribute(getParent().getElement(), "zIndex", 999);
    }
  }

  final MenuBar items;

  public BrowserChronoscopeMenu(int x, int y) {
    super(true);
    setPopupPosition(x, y);
    items = new ChronoscopeMenuBar(true);
    setWidget(items);
    items.setAutoOpen(true);
  }

  /**
   * @gwt.export addMenu
   */
  @Export("addMenu")
  public void addMenuBar(String label, ChronoscopeMenu subMenu) {
    items.addItem(label, ((BrowserChronoscopeMenu) subMenu).getMenuBar());
  }

  /**
   * @gwt.export
   */
  @Export
  public void addMenuItem(final String label,
      final ChronoscopeClickListener ccl) {
    items.addItem(label, new Command() {
      public void execute() {
        if (ccl != null) {
          ccl.click(label);
        } else {
//                    Window.alert("Clicked " + label);
        }
        BrowserChronoscopeMenu.this.hide();
      }
    });
  }

  @Export
  public void removeAllMenuItems() {
    items.clearItems();
  }

  /**
   * Hides the popup. This has no effect if it is not currently visible.
   *
   * @gwt.export
   */
  @Export
  public void hide() {

    super.hide();
  }

  /**
   * @gwt.export
   */
  @Export
  public void show(int x, int y) {
    setPopupPosition(x, y);
    super.show();
  }

  public MenuBar getMenuBar() {
    return items;
  }
}

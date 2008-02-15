package org.timepedia.chronoscope.client.browser;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.PopupPanel;

import org.timepedia.chronoscope.client.ChronoscopeMenu;

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
      getParent().addStyleName("chronoscopeMenu");
      // HACK: until GWT RFE to set style on implicit PopupPanel is fixed
      DOM.setIntStyleAttribute(getParent().getElement(), "zIndex", 100);
    }
  }

  final MenuBar items;

  public BrowserChronoscopeMenu(int x, int y) {
    super(true);
    setPopupPosition(x, y);
    items = new ChronoscopeMenuBar(true);
    setWidget(items);
    setStyleName("chronoscopeMenu");
    items.setAutoOpen(true);
  }

  /**
   * @gwt.export addMenu
   */
  public void addMenuBar(String label, ChronoscopeMenu subMenu) {
    items.addItem(label, ((BrowserChronoscopeMenu) subMenu).getMenuBar());
  }

  /**
   * @gwt.export
   */
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

  /**
   * Hides the popup. This has no effect if it is not currently visible.
   *
   * @gwt.export
   */
  public void hide() {

    super.hide();
  }

  /**
   * @gwt.export
   */
  public void show(int x, int y) {
    setPopupPosition(x, y);
    super.show();
  }

  MenuBar getMenuBar() {
    return items;
  }
}

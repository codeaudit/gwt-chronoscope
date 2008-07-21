package org.timepedia.chronoscope.client.browser;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.KeyboardListener;

import org.timepedia.chronoscope.client.Chart;
import org.timepedia.chronoscope.client.Cursor;

/**
 *
 */
public class ChartEventHandler {

  private static final int TAB_KEY = 9;

  private boolean selectionMode;

  private int selStart;

  private boolean maybeDrag;

  private int startDragX;

  public boolean handleChartEvent(Event evt, Chart chart, int x, int y) {

    boolean handled = true;

    switch (DOM.eventGetType(evt)) {

      case Event.ONMOUSEDOWN:
        if (selectionMode || DOM.eventGetShiftKey(evt)) {
          selStart = x;
          selectionMode = true;
          chart.setCursor(Cursor.SELECTING);
        } else {
          maybeDrag = true;

          startDragX = x;
        }
        chart.setPlotFocus(x, y);
        handled = true;
        break;
      case Event.ONMOUSEOUT:
        chart.setAnimating(false);
        chart.setCursor(Cursor.DEFAULT);
        chart.redraw();
        handled = true;
        break;

      case Event.ONMOUSEOVER:
        chart.setPlotFocus(x, y);
        chart.setCursor(
            chart.isInsidePlot(x, y) ? Cursor.DRAGGABLE : Cursor.DEFAULT);
        ((DOMView) chart.getView()).focus();
        maybeDrag = false;
        handled = true;
        break;
      case Event.ONMOUSEUP:
        if (selectionMode) {
          selectionMode = false;
          chart.setAnimating(false);
          selStart = -1;
          if (DOM.eventGetShiftKey(evt)) {
            chart.zoomToHighlight();
          }
        } else if (maybeDrag && x != startDragX) {
          ((DOMView) chart.getView()).pushHistory();
          chart.setAnimating(false);
          chart.redraw();
        }

        chart.setCursor(Cursor.DRAGGING);
        maybeDrag = false;
        ((DOMView) chart.getView()).focus();
        handled = true;
        break;
      case Event.ONMOUSEMOVE:
        if (chart.isInsidePlot(x, y)) {
          if (selectionMode && selStart > -1) {
            chart.setCursor(Cursor.SELECTING);
            chart.setAnimating(true);
            chart.setHighlight(selStart, x);
          } else {
            if (maybeDrag && Math.abs(startDragX - x) > 10) {
              chart.setAnimating(true);
              chart.setCursor(Cursor.DRAGGING);
              chart.scrollPixels(startDragX - x);
              startDragX = x;
              DOM.eventCancelBubble(evt, true);
              DOM.eventPreventDefault(evt);
            } else {
              if (chart.setHover(x, y)) {
                chart.setCursor(Cursor.CLICKABLE);
              } else {
                chart.setCursor(Cursor.DRAGGABLE);
              }
            }
          }
        } else {
          chart.setCursor(Cursor.DEFAULT);
        }
//              else if (maybeDrag && chart.getOverviewBounds().inside(x, y)) {
//                    chart.getOverviewAxis().drag(view, startDragX, x, y);
//                }
        handled = true;
        break;

      case Event.ONKEYDOWN:
        int keyCode2 = DOM.eventGetKeyCode(evt);
        if (keyCode2 == KeyboardListener.KEY_PAGEUP
            || keyCode2 == KeyboardListener.KEY_PAGEDOWN
            || keyCode2 == KeyboardListener.KEY_UP
            || keyCode2 == KeyboardListener.KEY_DOWN || keyCode2 == TAB_KEY) {
          handled = handleTabKey(evt, chart, keyCode2);
        }
        break;
      case Event.ONKEYUP:

        int keyCode = DOM.eventGetKeyCode(evt);
        handled = true;

        if (keyCode == KeyboardListener.KEY_LEFT ||

            keyCode == KeyboardListener.KEY_PAGEUP
            || keyCode == SafariKeyboardConstants
            .SAFARI_LEFT || keyCode == SafariKeyboardConstants.SAFARI_LEFT
            || keyCode == SafariKeyboardConstants
            .SAFARI_PGUP) {
          chart.pageLeft(keyCode == KeyboardListener.KEY_PAGEUP
              || keyCode == SafariKeyboardConstants.SAFARI_PGUP ? 1.0 : 0.5);
        } else if (keyCode == KeyboardListener.KEY_RIGHT ||

            keyCode == KeyboardListener.KEY_PAGEDOWN
            || keyCode == SafariKeyboardConstants
            .SAFARI_RIGHT || keyCode == SafariKeyboardConstants.SAFARI_RIGHT
            || keyCode == SafariKeyboardConstants
            .SAFARI_PDWN) {
          chart.pageRight(keyCode == KeyboardListener.KEY_PAGEDOWN
              || keyCode == SafariKeyboardConstants.SAFARI_PDWN ? 1.0 : 0.5);
        } else if (keyCode == KeyboardListener.KEY_UP || keyCode == 90 + 32
            || keyCode == SafariKeyboardConstants.SAFARI_UP) {
          chart.nextZoom();
        } else if (keyCode == KeyboardListener.KEY_DOWN
            || keyCode == SafariKeyboardConstants.SAFARI_DOWN
            || keyCode == 88 + 32) {
          chart.prevZoom();
        } else if (keyCode == KeyboardListener.KEY_BACKSPACE) {
          History.back();
        } else if (keyCode == KeyboardListener.KEY_HOME
            || keyCode == SafariKeyboardConstants
            .SAFARI_HOME) {
          chart.maxZoomOut();
        } else {
          handled = false;
        }

        break;
      case Event.ONKEYPRESS:
        int keyCode3 = DOM.eventGetKeyCode(evt);
        handled = true;
        if (keyCode3 == TAB_KEY) {
          handled = handleTabKey(evt, chart, keyCode3);
        } else if (keyCode3 == 90 + 32) {
          chart.nextZoom();
        } else if (keyCode3 == 88 + 32) {
          chart.prevZoom();
        } else if (keyCode3 == 83 + 32) {
          selectionMode = !selectionMode;
        } else if (keyCode3 == KeyboardListener.KEY_ENTER) {
          chart.maxZoomToFocus();
        } else {
          handled = false;
        }
        break;
      case Event.ONCLICK:
        maybeDrag = false;
        chart.setAnimating(false);
        handled = true;
        if (DOM.eventGetButton(evt) == Event.BUTTON_RIGHT) {
          chart.getView().fireContextMenuEvent(x, y);
        } else if (chart.click(x, y)) {
          // do nothing
        } else {
          handled = false;
        }
        ((DOMView) chart.getView()).focus();
        break;
      case Event.ONDBLCLICK:
        maybeDrag = false;
        chart.setAnimating(false);
        handled = true;
        if (chart.maxZoomTo(x, y)) {
          DOM.eventCancelBubble(evt, true);
          DOM.eventPreventDefault(evt);
        } else {
          handled = false;
        }
        break;

      case Event.ONMOUSEWHEEL:
        int dir = DOM.eventGetMouseWheelVelocityY(evt);
        if (dir <= 0) {
          onMouseWheelUp(chart, dir);
        } else {
          onMouseWheelDown(chart, dir);
        }
        handled = true;
        break;
      default:
        handled = false;
    }
    
    return handled;
  }

  public void onMouseWheelDown(Chart chart, int intensity) {
    maybeDrag = false;
    chart.prevZoom();
  }

  public void onMouseWheelUp(Chart chart, int intensity) {
    maybeDrag = false;
    chart.nextZoom();
  }

  private boolean handleTabKey(Event evt, Chart chart, int keyCode2) {
    if(DOM.eventGetType(evt) != getTabKeyEventType()) {
      return false;
    }
    if (keyCode2 == TAB_KEY) {
      if (DOM.eventGetShiftKey(evt)) {
        chart.prevFocus();
      } else {
        chart.nextFocus();
      }
      return true;
    }
    return false;
  }

  /**
   * Safari and IE use KEYDOWN for TAB, FF uses KEYPRESS
   * @return
   */
  public int getTabKeyEventType() {
    return Event.ONKEYDOWN;
  }
}

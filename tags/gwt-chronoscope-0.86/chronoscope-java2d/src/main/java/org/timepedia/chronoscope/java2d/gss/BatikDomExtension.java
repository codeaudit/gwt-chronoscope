package org.timepedia.chronoscope.java2d.gss;

import org.apache.batik.css.engine.CSSEngine;
import org.apache.batik.css.engine.CSSStylableElement;
import org.apache.batik.css.engine.StyleMap;
import org.apache.batik.css.engine.value.FloatValue;
import org.apache.batik.css.engine.value.LengthManager;
import org.apache.batik.css.engine.value.RGBColorValue;
import org.apache.batik.css.engine.value.Value;
import org.apache.batik.css.engine.value.svg.SVGPaintManager;
import org.apache.batik.css.engine.value.svg.SVGValueConstants;
import org.apache.batik.css.engine.value.svg12.MarginLengthManager;
import org.apache.batik.dom.AbstractDocument;
import org.apache.batik.dom.DomExtension;
import org.apache.batik.dom.ExtensibleDOMImplementation;
import org.apache.batik.extension.PrefixableStylableExtensionElement;
import org.w3c.css.sac.LexicalUnit;
import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.TypeInfo;
import org.w3c.dom.UserDataHandler;
import org.w3c.dom.css.CSSPrimitiveValue;

/**
 * Extends Batik to support all of our custom GSS element names
 *
 * @author <a href="mailto:thomas.deweese@kodak.com">Thomas DeWeese</a>
 * @version $Id: BatikDomExtension.java 498740 2007-01-22 18:35:57Z dvholten $
 */
public class BatikDomExtension implements DomExtension {

  /**
   * To create a 'regularPolygon' element.
   */
  protected static class TimepediaElementFactory
      implements ExtensibleDOMImplementation.ElementFactory {

    private String localName;

    public TimepediaElementFactory(String localName) {

      this.localName = localName;
    }

    /**
     * Creates an instance of the associated element type.
     */
    public Element create(String prefix, Document doc) {
      return new TimepediaElement(prefix, (AbstractDocument) doc, localName);
    }
  }

  static class RGBAColorValue extends RGBColorValue {

    private Value alpha;

    public RGBAColorValue(Value red, Value green, Value blue, Value alpha) {
      super(red, green, blue);

      this.alpha = alpha;
    }

    public String getCssText() {
      return "rgba(" + red.getCssText() + ", " + green.getCssText() + ", "
          + blue.getCssText() + ", " + alpha.getCssText() + ")";
    }

    public short getPrimitiveType() {
      return CSSPrimitiveValue.CSS_CUSTOM;
    }
  }

  static class RGBAPaintManager extends SVGPaintManager {

    public RGBAPaintManager(String s) {
      super(s);
    }

    public Value computeValue(CSSStylableElement cssStylableElement, String s,
        CSSEngine cssEngine, int i, StyleMap styleMap, Value value) {
      if (value.getCssValueType() != CSSPrimitiveValue.CSS_CUSTOM) {
        return super
            .computeValue(cssStylableElement, s, cssEngine, i, styleMap, value);
      }
      return value;
    }

    public Value createValue(LexicalUnit lu, CSSEngine engine)
        throws DOMException {
      if (lu.getLexicalUnitType() == LexicalUnit.SAC_FUNCTION && lu
          .getFunctionName().equalsIgnoreCase("rgba")) {
        lu = lu.getParameters();
        Value red = createColorComponent(lu);
        lu = lu.getNextLexicalUnit().getNextLexicalUnit();
        Value green = createColorComponent(lu);
        lu = lu.getNextLexicalUnit().getNextLexicalUnit();
        Value blue = createColorComponent(lu);
        lu = lu.getNextLexicalUnit().getNextLexicalUnit();
        Value alpha = createColorComponent(lu);
        return createRGBAColor(red, green, blue, alpha);
      } else if (lu.getLexicalUnitType() == LexicalUnit.SAC_IDENT && lu
          .getStringValue().equalsIgnoreCase("transparent")) {
        return createRGBAColor(new FloatValue(CSSPrimitiveValue.CSS_NUMBER, 0),
            new FloatValue(CSSPrimitiveValue.CSS_NUMBER, 0),
            new FloatValue(CSSPrimitiveValue.CSS_NUMBER, 0),
            new FloatValue(CSSPrimitiveValue.CSS_NUMBER, 0));
      } else {
        return super.createValue(lu, engine);
      }
    }

    private Value createRGBAColor(Value red, Value green, Value blue,
        Value alpha) {
      return new RGBAColorValue(red, green, blue, alpha);
    }
  }

  static class TimepediaElement extends PrefixableStylableExtensionElement
      implements Element {

    String localName;

    /**
     * Creates a new BatikStarElement object.
     */
    public TimepediaElement(String localName) {
      this.localName = localName;
    }

    /**
     * Creates a new BatikStarElement object.
     *
     * @param prefix The namespace prefix.
     * @param owner  The owner document.
     */
    public TimepediaElement(String prefix, AbstractDocument owner,
        String localName) {
      super(prefix, owner);
      this.localName = localName;
    }

    public short compareDocumentPosition(Node node) throws DOMException {
      return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public String getBaseURI() {
      return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public Object getFeature(String s, String s1) {
      return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    /**
     * <b>DOM</b>: Implements {@link org.w3c.dom.Node#getLocalName()}.
     */
    public String getLocalName() {
      return localName;
    }

    /**
     * <b>DOM</b>: Implements {@link org.w3c.dom.Node#getNamespaceURI()}.
     */
    public String getNamespaceURI() {
      return TIMEPEDIA_NAMESPACE_URI;
    }

    public TypeInfo getSchemaTypeInfo() {
      return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public String getTextContent() throws DOMException {
      return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public Object getUserData(String s) {
      return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public boolean isDefaultNamespace(String s) {
      return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public boolean isEqualNode(Node node) {
      return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public boolean isSameNode(Node node) {
      return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public String lookupNamespaceURI(String s) {
      return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public String lookupPrefix(String s) {
      return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public void setIdAttribute(String s, boolean b) throws DOMException {
      //To change body of implemented methods use File | Settings | File Templates.
    }

    public void setIdAttributeNode(Attr attr, boolean b) throws DOMException {
      //To change body of implemented methods use File | Settings | File Templates.
    }

    public void setIdAttributeNS(String s, String s1, boolean b)
        throws DOMException {
      //To change body of implemented methods use File | Settings | File Templates.
    }

    public void setTextContent(String s) throws DOMException {
      //To change body of implemented methods use File | Settings | File Templates.
    }

    public Object setUserData(String s, Object o,
        UserDataHandler userDataHandler) {
      return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    /**
     * Returns a new uninitialized instance of this object's class.
     */
    protected Node newNode() {
      return new TimepediaElement(localName);
    }
  }

  public static final String TIMEPEDIA_NAMESPACE_URI
      = "http://www.timepedia.com/chronoscope";

  private static final String TIMEPEDIA_AXES_TAG = "axes";

  private static final String TIMEPEDIA_AXIS_TAG = "axis";

  private static final String TIMEPEDIA_LINE_TAG = "line";

  private static final String TIMEPEDIA_POINT_TAG = "point";

  private static final String TIMEPEDIA_SHADOW_TAG = "shadow";

  private static final String TIMEPEDIA_PLOT_TAG = "plot";

  private static final String TIMEPEDIA_AXISLEGEND_TAG = "axislegend";

  private static final String TIMEPEDIA_LABEL_TAG = "label";

  private static final String TIMEPEDIA_GRID_TAG = "grid";

  private static final String TIMEPEDIA_TICK_TAG = "tick";

  private static final String TIMEPEDIA_BAR_TAG = "bar";

  private static final String TIMEPEDIA_BARMAKER_TAG = "barmarker";

  private static final String TIMEPEDIA_MARKER_TAG = "marker";

  private static final String TIMEPEDIA_OVERVIEW_TAG = "overview";

  private static final String TIMEPEDIA_SERIES_TAG = "series";

  private static final String TIMEPEDIA_CIRCLE_TAG = "circle";

  private static final String TIMEPEDIA_FILL_TAG = "fill";

  private static final String[] timepediaTagExtensions = {TIMEPEDIA_AXES_TAG,
      TIMEPEDIA_AXIS_TAG, TIMEPEDIA_AXISLEGEND_TAG, TIMEPEDIA_BAR_TAG,
      TIMEPEDIA_BARMAKER_TAG, TIMEPEDIA_GRID_TAG, TIMEPEDIA_LABEL_TAG,
      TIMEPEDIA_LINE_TAG, TIMEPEDIA_MARKER_TAG, TIMEPEDIA_OVERVIEW_TAG,
      TIMEPEDIA_PLOT_TAG, TIMEPEDIA_POINT_TAG, TIMEPEDIA_SERIES_TAG,
      TIMEPEDIA_SHADOW_TAG, TIMEPEDIA_TICK_TAG, TIMEPEDIA_CIRCLE_TAG,
      TIMEPEDIA_FILL_TAG};

  /**
   * This should return the individual or company name responsible for the this
   * implementation of the extension.
   */
  public String getAuthor() {
    return "Ray Cromwell";
  }

  /**
   * This should contain a contact address (usually an e-mail address).
   */
  public String getContactAddress() {
    return "ray@timepedia.org";
  }

  /**
   * Human readable description of the extension. Perhaps that should be a
   * resource for internationalization? (although I suppose it could be done
   * internally)
   */
  public String getDescription() {
    return "Extends DOM to handle axes/series/etc elements";
  }

  /**
   * Return the priority of this Extension.  Extensions are registered from
   * lowest to highest priority.  So if for some reason you need to come
   * before/after another existing extension make sure your priority is
   * lower/higher than theirs.
   */
  public float getPriority() {
    return 1.0f;
  }

  /**
   * This should return a URL where information can be obtained on this
   * extension.
   */
  public String getURL() {
    return "http://www.timepedia.org/chronoscope";
  }

  /**
   * This method should update the DomContext with support for the tags in this
   * extension.  In some rare cases it may be necessary to replace existing tag
   * handlers, although this is discouraged.
   *
   * @param di The ExtensibleDOMImplementation to register the extension
   *           elements with.
   */
  public void registerTags(ExtensibleDOMImplementation di) {
    for (String timepediaTagExtension : timepediaTagExtensions) {
      di.registerCustomElementFactory(TIMEPEDIA_NAMESPACE_URI,
          timepediaTagExtension,
          new TimepediaElementFactory(timepediaTagExtension));
    }
/*

        di.registerCustomElementFactory
                (TIMEPEDIA_NAMESPACE_URI,
                        TIMEPEDIA_AXIS_TAG,
                        new TimepediaElementFactory(TIMEPEDIA_AXIS_TAG));
        di.registerCustomElementFactory
                (TIMEPEDIA_NAMESPACE_URI,
                        TIMEPEDIA_LINE_TAG,
                        new TimepediaElementFactory(TIMEPEDIA_LINE_TAG));
        di.registerCustomElementFactory
                (TIMEPEDIA_NAMESPACE_URI,
                        TIMEPEDIA_POINT_TAG,
                        new TimepediaElementFactory(TIMEPEDIA_POINT_TAG));
        di.registerCustomElementFactory
                (TIMEPEDIA_NAMESPACE_URI,
                        TIMEPEDIA_SHADOW_TAG,
                        new TimepediaElementFactory(TIMEPEDIA_SHADOW_TAG));
        di.registerCustomElementFactory
                (TIMEPEDIA_NAMESPACE_URI,
                        TIMEPEDIA_PLOT_TAG,
                        new TimepediaElementFactory(TIMEPEDIA_PLOT_TAG));

        di.registerCustomElementFactory
                (TIMEPEDIA_NAMESPACE_URI,
                        TIMEPEDIA_AXISLEGEND_TAG,
                        new TimepediaElementFactory(TIMEPEDIA_AXISLEGEND_TAG));
         di.registerCustomElementFactory
                (TIMEPEDIA_NAMESPACE_URI,
                        TIMEPEDIA_LABEL_TAG,
                        new TimepediaElementFactory(TIMEPEDIA_LABEL_TAG));
        di.registerCustomElementFactory
                     (TIMEPEDIA_NAMESPACE_URI,
                             TIMEPEDIA_TICK_TAG,
                             new TimepediaElementFactory(TIMEPEDIA_TICK_TAG));
        di.registerCustomElementFactory
                     (TIMEPEDIA_NAMESPACE_URI,
                             TIMEPEDIA_GRID_TAG,
                             new TimepediaElementFactory(TIMEPEDIA_GRID_TAG));
*/

    di.registerCustomCSSValueManager(new LengthManager() {
      public Value createValue(LexicalUnit lexicalUnit, CSSEngine cssEngine)
          throws DOMException {
        return super.createValue(lexicalUnit, cssEngine);
      }

      public Value getDefaultValue() {
        return SVGValueConstants.NUMBER_0;
      }

      public int getOrientation() {
        return LengthManager.HORIZONTAL_ORIENTATION;
      }

      public String getPropertyName() {
        return "width";
      }

      public boolean isInheritedProperty() {
        return true;
      }
    });

    di.registerCustomCSSValueManager(new RGBAPaintManager("background-color"));

    di.registerCustomCSSValueManager(new SVGPaintManager("background-image"));
    di.registerCustomCSSValueManager(
        new MarginLengthManager("border-left-width") {

          public Value getDefaultValue() {
            return SVGValueConstants.NUMBER_1;
          }
        });
    di.registerCustomCSSValueManager(new MarginLengthManager("top") {

      public Value getDefaultValue() {
        return SVGValueConstants.NUMBER_1;
      }
    });
    di.registerCustomCSSValueManager(new MarginLengthManager("left") {

      public Value getDefaultValue() {
        return SVGValueConstants.NUMBER_1;
      }
    });

    /* di.registerCustomCSSValueManager(new FontSizeManager()
    {

        public Value getDefaultValue() {
            return new FloatValue(CSSPrimitiveValue.CSS_PT, 9);
        }
    });*/
    // di.registerCustomCSSValueManager(new org.apache.batik.css.engine.value.css2.);
  }
}

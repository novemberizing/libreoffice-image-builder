package novemberizing;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import com.sun.star.awt.Point;
import com.sun.star.awt.Size;
import com.sun.star.beans.*;
import com.sun.star.container.XIndexAccess;
import com.sun.star.drawing.*;
import com.sun.star.frame.XStorable;
import com.sun.star.io.IOException;
import com.sun.star.lang.IndexOutOfBoundsException;
import com.sun.star.lang.WrappedTargetException;
import com.sun.star.lang.XMultiServiceFactory;
import com.sun.star.style.ParagraphAdjust;
import com.sun.star.text.XText;
import com.sun.star.text.XTextCursor;
import com.sun.star.text.XTextRange;
import com.sun.star.uno.UnoRuntime;
import com.sun.star.view.PaperFormat;
import com.sun.star.view.XPrintable;

import java.util.Map;


public class LibreofficeDraw extends Libreoffice {

    private static final String url = "private:factory/sdraw";

    public LibreofficeDraw() {
        super(LibreofficeDraw.url);
    }

    public LibreofficeDraw(int width, int height) {
        super(LibreofficeDraw.url);
        this.open();
        this.size(width, height);
    }

    public XDrawPage page(int index) {
        XDrawPage page = null;
        try {
            XDrawPagesSupplier supplier = UnoRuntime.queryInterface(XDrawPagesSupplier.class, __component);
            XDrawPages pages = supplier.getDrawPages();
            XIndexAccess indices = UnoRuntime.queryInterface(XIndexAccess.class, pages);
            page = UnoRuntime.queryInterface(XDrawPage.class, indices.getByIndex(0));
        } catch (IndexOutOfBoundsException | WrappedTargetException e) {
            e.printStackTrace();
        }
        return page;
    }

    public void add(int index, int x, int y, int width, int height, String v, JsonObject object) {

        if(__component != null) {
            try {
                XDrawPage page = this.page(index);
                XMultiServiceFactory factory = UnoRuntime.queryInterface(XMultiServiceFactory.class, __component);
                XShapes xShapes = UnoRuntime.queryInterface(XShapes.class, page);
                Object instance = factory.createInstance("com.sun.star.drawing.TextShape");

                XShape shape = UnoRuntime.queryInterface(XShape.class, instance);
                xShapes.add(shape);

                Point position = new Point();
                position.X = (int)(0.39f * 2540) + (int) (x * 26.458333333f);
                position.Y = (int)(0.39f * 2540) + (int) (y * 26.458333333f);
                shape.setPosition(position);

                Size size = new Size();
                size.Width = (int)(width * 26.458333333f - (0.39f * 2540) * 2);
                size.Height = (int)(height * 26.458333333f - (0.39f * 2540) * 2);
                shape.setSize(size);

                XText text = UnoRuntime.queryInterface(XText.class, shape);

                XTextCursor cursor = text.createTextCursor();
                cursor.gotoEnd(false);
                XTextRange range = UnoRuntime.queryInterface(XTextRange.class, cursor);
                range.setString(v);
                cursor.gotoEnd(true);
                XPropertySet properties = UnoRuntime.queryInterface(XPropertySet.class, instance);

                for(Map.Entry<String, JsonElement> entry : object.entrySet()) {
                    Object value = Libreoffice.value(entry.getKey(), Primitive.object.from(entry.getValue()));
                    properties.setPropertyValue(entry.getKey(), value);
                }

            } catch(Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void size(int width, int height) {
        if(__component != null) {
            XPrintable printable = UnoRuntime.queryInterface(XPrintable.class, __component);
            PropertyValue[] values = printable.getPrinter();
            for (PropertyValue value : values) {
                if (value.Name.equals("PaperFormat")) {
                    value.Value = PaperFormat.USER;
                    break;
                }
            }

            try {
                XDrawPage page = this.page(0);
                if(page != null) {
                    XPropertySet properties = UnoRuntime.queryInterface(XPropertySet.class, page);
                    properties.setPropertyValue("Width", (int) (width * 26.458333333f));
                    properties.setPropertyValue("Height", (int) (height * 26.458333333f));
                }
            } catch (UnknownPropertyException | WrappedTargetException | PropertyVetoException e) {
                e.printStackTrace();
            }
        }
    }

    public void export(String url, String type) {
        if(__component != null) {
            try {
                XStorable storable = UnoRuntime.queryInterface(XStorable.class, __component);
                PropertyValue[] values = new PropertyValue[2];
                // Setting the flag for overwriting
                values[0] = new PropertyValue();
                values[0].Name = "Overwrite";
                values[0].Value = Boolean.TRUE;
                // Setting the filter name
                values[1] = new PropertyValue();
                values[1].Name = "FilterName";
                switch(type) {
                    case "png": values[1].Value = "draw_png_Export"; break;
                }
                storable.storeToURL(url, values);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

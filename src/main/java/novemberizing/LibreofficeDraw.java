package novemberizing;

import com.sun.star.awt.Point;
import com.sun.star.awt.Size;
import com.sun.star.beans.PropertyValue;
import com.sun.star.beans.PropertyVetoException;
import com.sun.star.beans.UnknownPropertyException;
import com.sun.star.beans.XPropertySet;
import com.sun.star.container.XIndexAccess;
import com.sun.star.drawing.*;
import com.sun.star.frame.XStorable;
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

    public void add(int index, int x, int y, int width, int height, String v) {
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
                XPropertySet properties = UnoRuntime.queryInterface(XPropertySet.class, range);

                System.out.println("Option How To");
                properties.setPropertyValue("CharFontName", "Noto Serif CJK JP ExtraLight");
                properties.setPropertyValue("CharFontNameAsian", "Noto Serif CJK JP ExtraLight");
                properties.setPropertyValue("CharHeightAsian", 70);
                properties.setPropertyValue("CharHeight", 70);
                properties.setPropertyValue("ParaAdjust", ParagraphAdjust.CENTER);
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
                System.out.println(value.Name);
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
}

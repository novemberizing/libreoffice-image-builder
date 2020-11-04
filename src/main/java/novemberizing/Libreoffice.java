package novemberizing;

import com.sun.star.beans.PropertyValue;

import com.sun.star.comp.helper.Bootstrap;
import com.sun.star.comp.helper.BootstrapException;
import com.sun.star.container.XIndexAccess;
import com.sun.star.frame.XComponentLoader;
import com.sun.star.frame.XDesktop;
import com.sun.star.lang.XComponent;
import com.sun.star.drawing.XDrawPage;
import com.sun.star.drawing.XDrawPages;
import com.sun.star.drawing.XDrawPagesSupplier;
import com.sun.star.lang.XMultiComponentFactory;
import com.sun.star.uno.Exception;
import com.sun.star.uno.UnoRuntime;
import com.sun.star.uno.XComponentContext;
import com.sun.star.view.XPrintable;

import java.awt.print.PageFormat;

public class Libreoffice {

    protected static XComponentContext __context = null;

    public static void init() throws BootstrapException {
        synchronized (Libreoffice.class) {
            if(__context == null) {
                __context = Bootstrap.bootstrap();
            }
        }
    }

    protected String __url;
    protected Object __desktop = null;
    protected XComponent __component = null;

    protected Libreoffice(String url) {
        __url = url;
    }

    public void open() {
        XMultiComponentFactory factory = __context.getServiceManager();
        try {
            __desktop = factory.createInstanceWithContext("com.sun.star.frame.Desktop", __context);
            XComponentLoader loader = UnoRuntime.queryInterface(XComponentLoader.class, __desktop);
            PropertyValue[] values = new PropertyValue[1];
            values[0] = new PropertyValue();
            values[0].Name = "Hidden";
            values[0].Value = false;
            __component = loader.loadComponentFromURL(__url, "_blank", 0, values);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void close() {
        if(__component != null) {
            __component.dispose();
            __component = null;
            if(__desktop != null) {
                XDesktop desktop = UnoRuntime.queryInterface(XDesktop.class, __desktop);
                desktop.terminate();
            }
        }
    }

    public static void main(String[] args) {
        try {
            Libreoffice.init();
        } catch (BootstrapException e) {
            e.printStackTrace();
        }

        LibreofficeDraw draw = new LibreofficeDraw(1200, 630);

        draw.add(0, 0, 0, 1200, 630, "學而時習之 不亦說乎\n有朋自遠方來 不亦樂乎\n人不知而不慍 不亦君子乎");



        System.out.println("Hello World");

//        draw.close();
//
//        System.exit(0);
    }
}

package novemberizing;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.sun.star.beans.PropertyValue;

import com.sun.star.comp.helper.Bootstrap;
import com.sun.star.comp.helper.BootstrapException;
import com.sun.star.frame.XComponentLoader;
import com.sun.star.frame.XDesktop;
import com.sun.star.lang.XComponent;
import com.sun.star.lang.XMultiComponentFactory;
import com.sun.star.uno.Exception;
import com.sun.star.uno.UnoRuntime;
import com.sun.star.uno.XComponentContext;

public class Libreoffice {

    public static Object value(String key, Object value) {
        switch(key) {
            case "CharFontName": return value;
            case "CharFontNameAsian": return value;
            case "CharHeightAsian": return Primitive.integer.from((Number) value);
            case "CharHeight": return Primitive.integer.from((Number) value);
            case "ParaAdjust": return LibreofficeStyle.ParagraphAdjust.from((String) value);
            case "TextVerticalAdjust": return LibreofficeStyle.TextVerticalAdjust.from((String) value);
        }
        throw new RuntimeException();
    }

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

    public void save() {

    }

    public void export(String url, String type) {
//        if(__component != null) {
//            try {
//                XStorable storable = UnoRuntime.queryInterface(XStorable.class, __component);
//                PropertyValue[] values = new PropertyValue[2];
//                // Setting the flag for overwriting
//                values[0] = new PropertyValue();
//                values[0].Name = "Overwrite";
//                values[0].Value = Boolean.TRUE;
//                // Setting the filter name
//                values[1] = new PropertyValue();
//                values[1].Name = "FilterName";
//                values[1].Value = "draw_png_Export";
//                storable.storeToURL(url, values);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
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

        JsonObject object = new JsonObject();
        object.add("CharFontName", new JsonPrimitive("Noto Serif CJK JP ExtraLight"));
        object.add("CharFontNameAsian", new JsonPrimitive("Noto Serif CJK JP ExtraLight"));
        object.add("CharHeight", new JsonPrimitive(70));
        object.add("CharHeightAsian", new JsonPrimitive(70));
        object.add("ParaAdjust", new JsonPrimitive("center"));
        object.add("TextVerticalAdjust", new JsonPrimitive("center"));

        draw.add(0, 0, 0, 1200, 630, "學而時習之 不亦說乎\n有朋自遠方來 不亦樂乎\n人不知而不慍 不亦君子乎", object);

        draw.export("file:///home/novemberizing/test.png", "png");

        System.out.println("Hello World");

//        draw.close();
//        System.exit(0);
    }
}

package novemberizing;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
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

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;

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
            values[0].Value = true;
            __component = loader.loadComponentFromURL(__url, "_blank", 0, values);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void save() {
    }

    public void export(String url, String type) {
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

    public static void gen(String book, String category, String section, String prefix, String content, String destination) {

        LibreofficeDraw draw = new LibreofficeDraw(1200, 630);

        JsonObject object = new JsonObject();
        object.add("CharFontName", new JsonPrimitive("Noto Serif CJK JP ExtraLight"));
        object.add("CharFontNameAsian", new JsonPrimitive("Noto Serif CJK JP ExtraLight"));
        object.add("CharHeight", new JsonPrimitive(60));
        object.add("CharHeightAsian", new JsonPrimitive(60));
        object.add("ParaAdjust", new JsonPrimitive("center"));
        object.add("TextVerticalAdjust", new JsonPrimitive("center"));

        content = content.replaceAll("\\\\n", "\n");

        draw.add(0, 0, 0, 1200, 630, content, object);

        object.add("CharFontName", new JsonPrimitive("Noto Serif CJK JP ExtraLight"));
        object.add("CharFontNameAsian", new JsonPrimitive("Noto Serif CJK JP ExtraLight"));
        object.add("CharHeight", new JsonPrimitive(14));
        object.add("CharHeightAsian", new JsonPrimitive(14));
        object.add("ParaAdjust", new JsonPrimitive("left"));
        object.add("TextVerticalAdjust", new JsonPrimitive("top"));
        draw.add(0, 0, -35, 1200, 100, book + " / " + category + " / " + section + " " + prefix, object);

        draw.export("file://" + destination, "png");
    }

    public static String extension(File file) {
        String name = file.getName();
        int index = name.lastIndexOf(".");
        if(index >= 0) {
            return name.substring(index + 1);
        }
        return null;
    }

    public static String name(File file) {
        String name = file.getName();
        int index = name.lastIndexOf(".");
        if(index > 0) {
            return name.substring(0, index);
        }
        return null;
    }

    private static final String json = "json";
    private static final String index = "index";

    public static void scan(File file, String exclude, String output) {
        if(file != null) {
            if(file.isFile()) {
                if(json.equals(Libreoffice.extension(file)) && !index.equals(Libreoffice.name(file))) {
                    try {
                        String txt = new String(Files.readAllBytes(Paths.get(file.getAbsolutePath())));
                        JsonObject source = JsonParser.parseString(txt).getAsJsonObject();
                        String section = source.get("section").getAsString();
                        String content = source.get("content").getAsString();
                        String book = source.get("book").getAsString();
                        String category = source.get("category").getAsString();
                        String prefix = source.get("prefix").getAsString();
                        String destination = output + "/" + book + "/" + category + "/" + section + ".png";
                        Libreoffice.gen(book, category, section, prefix, content, destination);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } else if(file.isDirectory()) {
                File[] files = file.listFiles();
                if(files != null) {
                    for(File item: files) {
                        if(!exclude.equals(item.getName())) {
                            Libreoffice.scan(item, exclude, output);
                        }
                    }
                }
            }
        }
    }

    public static void main(String[] args) {
        if(args.length < 2) {
            System.out.println("program [source] [destination]");
            return;
        }

        try {
            Libreoffice.init();
        } catch (BootstrapException e) {
            e.printStackTrace();
        }

        String source = args[0];
        String destination = args[1];

        Libreoffice.scan(new File(source), "fontawesome-free-5.15.1-web", destination);

        System.exit(0);
    }
}

package novemberizing;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

public class Primitive {

    public static class integer {
        public static Integer from(Number number) {
            return number.intValue();
        }
    }
    public static class object {
        public static Object from(JsonElement element) {
            if(element.isJsonNull()) {
                return null;
            } else if(element.isJsonPrimitive()) {
                JsonPrimitive primitive = element.getAsJsonPrimitive();
                if(primitive.isBoolean()) {
                    return primitive.getAsBoolean();
                } else if(primitive.isString()) {
                    return primitive.getAsString();
                } else if(primitive.isNumber()) {
                    return primitive.getAsNumber();
                }
                throw new RuntimeException();
            }
            throw new RuntimeException();
        }
    }
}

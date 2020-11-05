package novemberizing;

public class LibreofficeStyle {
    public static class ParagraphAdjust {
        public static com.sun.star.style.ParagraphAdjust from(String v) {
            switch(v.toLowerCase()) {
                case "left": return com.sun.star.style.ParagraphAdjust.LEFT;
                case "right": return com.sun.star.style.ParagraphAdjust.RIGHT;
                case "block": return com.sun.star.style.ParagraphAdjust.BLOCK;
                case "center": return com.sun.star.style.ParagraphAdjust.CENTER;
                case "stretch": return com.sun.star.style.ParagraphAdjust.STRETCH;
                default: throw new RuntimeException();
            }
        }
    }
}

package AnimalRaceGame;

import AnimalRaceGame.AnimalRaceGame;

import javax.swing.*;
import javax.swing.plaf.FontUIResource;
import java.awt.*;
import java.io.File;
public class GlobalUI {

    private static Font loadedFont;

    /**
     * Loads a font from the given file path and applies it globally.
     *
     * @param fontPath Path to the TTF font file (e.g., "resources/gamefont.ttf")
     * @param size     Font size to use
     */
    public static void applyGlobalFont(String fontPath, float size) {
        try {
            if (loadedFont == null) {
                loadedFont = Font.createFont(Font.TRUETYPE_FONT, new File(fontPath)).deriveFont(size);
                GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
                ge.registerFont(loadedFont);
            }

            FontUIResource fontRes = new FontUIResource(loadedFont);

            // Apply to all UI components
            for (Object key : UIManager.getLookAndFeelDefaults().keySet()) {
                Object value = UIManager.get(key);
                if (value instanceof FontUIResource) {
                    UIManager.put(key, fontRes);
                }
            }

        } catch (Exception e) {
            System.err.println("Failed to load or apply font: " + e.getMessage());
        }
    }

    public static Font getLoadedFont() {
        return loadedFont;
    }
}
package de.quantum.core.utils;

import java.awt.*;

public class ColorUtils {



    // Method to lighten a color
    public static Color lighten(Color color, double fraction) {
        int red = (int) Math.min(255, color.getRed() + 255 * fraction);
        int green = (int) Math.min(255, color.getGreen() + 255 * fraction);
        int blue = (int) Math.min(255, color.getBlue() + 255 * fraction);
        return new Color(red, green, blue);
    }

    // Method to darken a color
    public static Color darken(Color color, double fraction) {
        int red = (int) Math.max(0, color.getRed() - 255 * fraction);
        int green = (int) Math.max(0, color.getGreen() - 255 * fraction);
        int blue = (int) Math.max(0, color.getBlue() - 255 * fraction);
        return new Color(red, green, blue);
    }

    // Method to increase saturation
    public static Color saturate(Color color, double fraction) {
        float[] hsb = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null);
        hsb[1] = (float) Math.min(1.0, hsb[1] + fraction);
        return Color.getHSBColor(hsb[0], hsb[1], hsb[2]);
    }

    // Method to decrease saturation
    public static Color desaturate(Color color, double fraction) {
        float[] hsb = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null);
        hsb[1] = (float) Math.max(0.0, hsb[1] - fraction);
        return Color.getHSBColor(hsb[0], hsb[1], hsb[2]);
    }

    public static Color applyOffset(Color color, int offset) {
        // Extract the RGB components
        int red = color.getRed();
        int green = color.getGreen();
        int blue = color.getBlue();

        // Apply the offset, ensuring that the values remain within the 0-255 range
        red = Math.min(255, Math.max(0, red + (offset & 0xFF)));
        green = Math.min(255, Math.max(0, green + ((offset >> 8) & 0xFF)));
        blue = Math.min(255, Math.max(0, blue + ((offset >> 16) & 0xFF)));

        // Return the new color
        return new Color(red, green, blue);
    }

}
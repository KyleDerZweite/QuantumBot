package de.luxury.core.utils;

import com.google.gson.JsonObject;
import net.dv8tion.jda.api.entities.Member;
import org.jetbrains.annotations.NotNull;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URL;

public class PictureUtils {

    public static final String TOP_PICTURE_PATH = "./images/top_image.png";
    public static final String BOTTOM_PICTURE_PATH = "./images/bottom_image.png";
    public static final String EDITED_FOLDER = "./images/edited/";
    public static final String WELCOME_FOLDER = "./images/welcome/";
    public static final String RANK_FOLDER = "./images/rank/";

    public static File INITIAL_TOP_FILE;
    public static File INITIAL_BOTTOM_FILE;
    public static BufferedImage BUFFERED_TOP_IMAGE;
    private static final int SHADOW_SIZE = 10;

    public static void init() {
        INITIAL_TOP_FILE = new File(TOP_PICTURE_PATH);
        INITIAL_BOTTOM_FILE = new File(BOTTOM_PICTURE_PATH);
        try {
            BUFFERED_TOP_IMAGE = ImageIO.read(INITIAL_TOP_FILE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static File ensureTopPictureFile(String pictureValue) {

        String replacedPictureValue = pictureValue.replace(" ", "").toLowerCase();

        File file = new File(EDITED_FOLDER + replacedPictureValue + ".png");

        if (!file.exists()) {
            try {
                BufferedImage temp = new BufferedImage(
                        BUFFERED_TOP_IMAGE.getWidth(), BUFFERED_TOP_IMAGE.getHeight(),
                        BufferedImage.TYPE_INT_RGB);

                Font font = new Font("Arial", Font.BOLD, 150);

                Graphics graphics = temp.getGraphics();
                FontMetrics metrics = graphics.getFontMetrics(font);

                graphics.drawImage(BUFFERED_TOP_IMAGE, 0, 0, null);
                graphics.setFont(font);
                //graphics.setColor(new Color(0x6B1C24)); Bordon-Rot (NyaBot Icon)
                graphics.setColor(Color.WHITE);

                Rectangle rect = new Rectangle(BUFFERED_TOP_IMAGE.getWidth(), BUFFERED_TOP_IMAGE.getHeight());

                int x = (BUFFERED_TOP_IMAGE.getWidth() / 2) - (metrics.stringWidth(pictureValue.toUpperCase()) / 2);
                int y = rect.y + ((rect.height - metrics.getHeight()) / 2) + metrics.getAscent();

                graphics.drawString(pictureValue.toUpperCase(), x, y);
                graphics.dispose();
                ImageIO.write(temp, "png", file);
                System.out.println("Picture created!");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return file;
    }
    public static File drawWelcomePicture(JsonObject fileData, String welcomeText, @NotNull Member member) {

        BufferedImage baseImg;
        File userFile = new File(WELCOME_FOLDER + member.getId() + ".png");

        try {
            baseImg = ImageIO.read(new File(fileData.get("file_path").getAsString()));
            BufferedImage temp = new BufferedImage(baseImg.getWidth(), baseImg.getHeight(), BufferedImage.TYPE_INT_ARGB);

            baseImg = makeRoundedCorner(baseImg,fileData.get("image_radius").getAsInt() - 1);

            Graphics2D graphics = temp.createGraphics();
            RenderingHints rh = new RenderingHints(
                    RenderingHints.KEY_TEXT_ANTIALIASING,
                    RenderingHints.VALUE_TEXT_ANTIALIAS_ON
            );
            double lumi = drawLumi(graphics,temp);
            temp = makeRoundedCorner(temp,fileData.get("image_radius").getAsInt());

            //Draw base Image to temporary Image

            graphics.setRenderingHints(rh);
            graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            graphics.drawImage(baseImg, 0, 0, null);

            //Get user avatar as BufferedImage and make it rounded

            String avatarUrl = member.getEffectiveAvatarUrl() + "?size=" + fileData.get("user_icon_get_size").getAsString();
            BufferedImage userAvatarRound = ImageIO.read(new URL(avatarUrl));
            if ((fileData.get("user_icon_height").getAsInt() != 0) && (fileData.get("user_icon_width").getAsInt() != 0)) {
                userAvatarRound = resizeImage(
                        userAvatarRound,
                        fileData.get("user_icon_width").getAsInt(),
                        fileData.get("user_icon_height").getAsInt());
            }
            userAvatarRound = makeRoundedCorner(userAvatarRound, fileData.get("user_icon_radius").getAsInt());

            //set text font

            String fontValue = fileData.get("text_location").getAsString();
            Font textFont;
            Font userFont;
            switch (fileData.get("text_font_style").getAsString()) {
                case "1" -> {
                    textFont = new Font(fontValue, Font.BOLD, fileData.get("text_font_size").getAsInt());
                    userFont = new Font(fontValue, Font.BOLD, fileData.get("user_font_size").getAsInt());
                } //BOLD
                case "2" -> {
                    textFont = new Font(fontValue, Font.ITALIC, fileData.get("text_font_size").getAsInt());
                    userFont = new Font(fontValue, Font.ITALIC, fileData.get("user_font_size").getAsInt());
                } //ITALIC
                default -> {
                    textFont = new Font(fontValue, Font.PLAIN, fileData.get("text_font_size").getAsInt());
                    userFont = new Font(fontValue, Font.PLAIN, fileData.get("user_font_size").getAsInt());
                } //PLAIN
            }

            FontMetrics textMetrics = graphics.getFontMetrics(textFont);
            FontMetrics userMetrics = graphics.getFontMetrics(userFont);

            int userTagX;
            int welcomeX;

            welcomeX = fileData.get("text_location_x").getAsInt();
            if (fileData.get("text_location").getAsString().equals("center")) {
                welcomeX += (baseImg.getWidth() / 2) - (textMetrics.stringWidth(welcomeText) / 2);
            }
            userTagX = fileData.get("user_location_x").getAsInt();
            if (fileData.get("user_location").getAsString().equals("center")) {
                userTagX += (baseImg.getWidth() / 2) - (userMetrics.stringWidth(member.getEffectiveName()) / 2);
            }

            graphics.setColor(Color.WHITE);

            float shadowOpacity = (float) (0.18 * lumi);

            drawImageShadow(graphics,SHADOW_SIZE, shadowOpacity,userAvatarRound,fileData);
            graphics.drawImage(userAvatarRound, fileData.get("image_location_x").getAsInt(), fileData.get("image_location_y").getAsInt(), null);

            graphics.setFont(userFont);
            drawTextShadow(graphics,SHADOW_SIZE, shadowOpacity,member.getEffectiveName(),userTagX,fileData.get("user_location_y").getAsInt());
            graphics.drawString(member.getEffectiveName(), userTagX, fileData.get("user_location_y").getAsInt());

            graphics.setFont(textFont);
            drawTextShadow(graphics,SHADOW_SIZE, shadowOpacity,member.getEffectiveName(),userTagX,fileData.get("user_location_y").getAsInt());
            graphics.drawString(welcomeText, welcomeX, fileData.get("text_location_y").getAsInt());

            ImageIO.write(temp, "png", userFile);
            graphics.dispose();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return userFile;
    }
    public static File drawRankCardImage(JsonObject data) {
        BufferedImage baseImg;
        File userFile = new File(RANK_FOLDER + data.get("userID").getAsString() + ".png");

        String filePath;
        if (data.get("filePath").isJsonNull()) {
            filePath = "./images/rank_base.png";
        } else {
            filePath = data.get("filePath").getAsString();
        }

        try {
            baseImg = ImageIO.read(new File(filePath));
            BufferedImage temp = new BufferedImage(baseImg.getWidth(), baseImg.getHeight(), BufferedImage.TYPE_INT_ARGB);

            BufferedImage userAvatar = makeRoundedCorner(resizeImage(
                            ImageIO.read(new URL(data.get("userAvatar").getAsString())),
                            150, 140),
                    300);

            Graphics2D graphics = temp.createGraphics();

            graphics.drawImage(userAvatar, 143, 50, null);
            graphics.drawImage(baseImg, 0, 0, null);

            int max = 575;
            double percentOfNeededXp = data.get("percentOfNeededXp").getAsDouble();
            int finalProgressValue = (int) ((max * percentOfNeededXp) / 100);

            BufferedImage progressBar = makeRoundedCorner(getProgressBar(finalProgressValue, 25), 25);
            graphics.drawImage(progressBar, 10, 300, null); //TODO use values and so -> Customizable via Json kekw

            Font textOverProgressBar = new Font("Arial", Font.PLAIN, 20);
            Font textInProgressBar = new Font("Arial", Font.PLAIN, 15);

            drawStringToGraphic(
                    data.get("userTag").getAsString(), baseImg.getWidth(), 30,
                    new Font("Arial", Font.BOLD, 30), Color.WHITE, graphics, true);
            drawStringToGraphic(
                    "#" + data.get("userRank").getAsInt(), (baseImg.getWidth() / 2) + 20, 105,
                    new Font("Arial", Font.PLAIN, 60), Color.WHITE, graphics, false);
            drawStringToGraphic(
                    "Total: " + String.format("%.02f", data.get("total_xp").getAsFloat()) + " xp", (baseImg.getWidth() / 2) - 40, 165,
                    new Font("Arial", Font.PLAIN, 22), Color.WHITE, graphics, false);

            drawStringToGraphic(
                    String.format("%.02f", data.get("current_xp").getAsFloat()) + " xp", 15, 290,
                    textOverProgressBar, Color.WHITE, graphics, false);
            drawStringToGraphic(
                    "" + data.get("currentLevel").getAsInt(), 15, 317,
                    textInProgressBar, Color.WHITE, graphics, false);

            FontMetrics overBarMetrics = graphics.getFontMetrics(textOverProgressBar);
            FontMetrics inBarMetrics = graphics.getFontMetrics(textInProgressBar);
            String neededXpForNextLevelValue = String.format("%.02f", data.get("neededXpForNextLevel").getAsFloat()) + " xp";

            drawStringToGraphic(
                    neededXpForNextLevelValue,
                    baseImg.getWidth() - (overBarMetrics.stringWidth(neededXpForNextLevelValue) + 15), 290,
                    textOverProgressBar, Color.WHITE, graphics, false);
            drawStringToGraphic(
                    "" + data.get("nextLevel").getAsInt(),
                    baseImg.getWidth() - (inBarMetrics.stringWidth(data.get("nextLevel").getAsString()) + 15), 317,
                    textInProgressBar, Color.WHITE, graphics, false);

            graphics.dispose();
            temp = makeRoundedCorner(temp, 30);
            ImageIO.write(temp, "png", userFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return userFile;
    }

    public static BufferedImage getProgressBar(int x, int y) {
        BufferedImage output = new BufferedImage(x, y, BufferedImage.TYPE_INT_BGR);
        Graphics2D g2 = output.createGraphics();
        int halfHeight = y / 2;
        GradientPaint gp = new GradientPaint(
                0, halfHeight, Color.decode("#974C6A"),
                x, halfHeight, Color.decode("#302141"));
        g2.setPaint(gp);
        g2.fillRect(0, 0, x, y);
        return output;
    }
    public static BufferedImage makeRoundedCorner(BufferedImage image, int cornerRadius) {
        int w = image.getWidth();
        int h = image.getHeight();
        BufferedImage output = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = output.createGraphics();
        RenderingHints rh = new RenderingHints(
                RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON
        );
        g2.setRenderingHints(rh);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(Color.WHITE);
        g2.setComposite(AlphaComposite.Src);
        g2.fill(new RoundRectangle2D.Float(0, 0, w, h, cornerRadius, cornerRadius));
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g2.setComposite(AlphaComposite.SrcAtop);
        g2.drawImage(image, 0, 0, w, h, null);
        g2.dispose();
        return output;
    }
    public static BufferedImage resizeImage(BufferedImage originalImage, int targetWidth, int targetHeight) {
        BufferedImage resizedImage = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics2D = resizedImage.createGraphics();
        graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        graphics2D.drawImage(originalImage, 0, 0, targetWidth, targetHeight, null);
        graphics2D.dispose();
        return resizedImage;
    }

    private static void drawStringToGraphic(String value, int x, int y, Font font, Color color, Graphics graphics, boolean shouldBeCentered) {

        //If should be centered -> The give x should be the imageWidth
        if (shouldBeCentered) {
            FontMetrics metrics = graphics.getFontMetrics(font);
            x = (x / 2) - (metrics.stringWidth(value) / 2);
        }

        graphics.setFont(font);
        graphics.setColor(color);
        graphics.drawString(value, x, y);
    }
    private static void drawImageShadow(Graphics2D graphics2D,int shadowSize, float shadowOpacity,BufferedImage image,JsonObject fileData) {
        Composite compositeCache = graphics2D.getComposite();
        BufferedImage shadowImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
        graphics2D.setColor(Color.BLACK);
        for (int i = 0; i < (double) shadowSize; i++) {
            float alpha = (25.0f * ((i + 1) / (float) shadowSize) * (int) shadowOpacity);
            AlphaComposite ac = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha / 100.0f);
            graphics2D.setComposite(ac);
            int offset = shadowSize - i;
            graphics2D.drawImage(shadowImage,fileData.get("image_location_x").getAsInt() + offset,fileData.get("image_location_x").getAsInt() + offset,null);
        }
        graphics2D.setColor(Color.WHITE);
        graphics2D.setComposite(compositeCache);
    }
    private static void drawTextShadow(Graphics2D graphics2D,int shadowSize, float shadowOpacity, String value, int textX,int textY) {
        Composite compositeCache = graphics2D.getComposite();
        graphics2D.setColor(Color.BLACK);

        System.out.println("TextX: " + textX);
        System.out.println("TextY: " + textY);

        for (int i = 0; i < shadowSize; i++) {
            float alpha = (25.0f * ((i + 1) / (float) shadowSize) * shadowOpacity);
            float compositeAlpha = alpha / 100F;

            System.out.println("Alpha: " + alpha);
            System.out.println("CompositeAlpha: " + compositeAlpha);

            AlphaComposite ac = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, compositeAlpha);
            graphics2D.setComposite(ac);

            int xOffset = textX + shadowSize - i;
            int yOffset = textY + shadowSize - i;

            System.out.println("xOffset: " + xOffset);
            System.out.println("yOffset: " + yOffset);

            graphics2D.drawString(value, xOffset,yOffset);
        }
        graphics2D.setColor(Color.WHITE);
        graphics2D.setComposite(compositeCache);
    }

    private static double getAverageLuminance(BufferedImage image) {
        double totalLuminance = 0;
        int n = 0;
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = image.getWidth() / 3; x < image.getWidth(); x++) {
                int color = image.getRGB(x, y);

                // extract each color component
                int red = (color >>> 16) & 0xFF;
                int green = (color >>> 8) & 0xFF;
                int blue = (color) & 0xFF;

                // calc luminance in range 0.0 to 1.0; using SRGB luminance constants
                totalLuminance += Math.sqrt((red * 0.2126f + green * 0.7152f + blue * 0.0722f) / 255);
                n++;
            }
        }

        return totalLuminance / (double) n;
    }
    private static double drawLumi(Graphics2D g2d, BufferedImage drawImage) {
        double lumi = getAverageLuminance(drawImage);
        if (lumi >= 0.4) {
            g2d.setColor(new Color(0, 0, 0, (float) (lumi - 0.4)));
            g2d.fillRect(0, 0, drawImage.getWidth(), drawImage.getHeight());
        }
        return lumi;
    }


}

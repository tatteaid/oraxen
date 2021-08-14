package io.th0rgal.oraxen.utils;

import io.th0rgal.oraxen.items.ItemBuilder;
import io.th0rgal.oraxen.items.OraxenItems;
import org.bukkit.Color;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class CustomArmorsTextures {

    private final List<BufferedImage> layers1 = new ArrayList<>();
    private final List<BufferedImage> layers2 = new ArrayList<>();
    private BufferedImage layer1;
    private int layer1Width = 0;
    private int layer1Height = 0;
    private BufferedImage layer2;
    private int layer2Width = 0;
    private int layer2Height = 0;

    public boolean registerImage(File file) throws IOException {
        if (!file.getName().endsWith(".png"))
            return false;

        if (file.getName().equals("leather_layer_1.png")) {
            layer1 = ImageIO.read(file);
            layer1Width += layer1.getWidth();
            if (layer1.getHeight() > layer1Height)
                layer1Height = layer1.getHeight();
            return true;
        }

        if (file.getName().equals("leather_layer_2.png")) {
            layer2 = ImageIO.read(file);
            layer2Width += layer2.getWidth();
            if (layer2.getHeight() > layer2Height)
                layer2Height = layer2.getHeight();
            return true;
        }

        if (file.getName().contains("armor_layer_")) {
            BufferedImage image = ImageIO.read(file);
            String prefix = file.getName().split("armor_layer_")[0];

            ItemBuilder builder = null;
            for (String suffix : new String[]{"helmet", "chestplate", "leggings", "boots"}) {
                builder = OraxenItems.getItemById(prefix + "chestplate");
                if (builder != null)
                    break;
            }
            Graphics2D g2d = image.createGraphics();
            WritableRaster raster = image.getRaster();
            Color color = builder.getColor();
            raster.setPixel(0, 0, new int[]{color.getRed(), color.getGreen(), color.getBlue(), 255});
            if (file.getName().contains("armor_layer_1")) {
                layers1.add(image);
                layer1Width += image.getWidth();
                if (image.getHeight() > layer1Height)
                    layer1Height = image.getHeight();
            } else {
                layers2.add(image);
                layer2Width += image.getWidth();
                if (image.getHeight() > layer2Height)
                    layer2Height = image.getHeight();
            }
            return true;
        }
        return false;
    }

    public boolean hasCustomArmors() {
        return !(layers1.isEmpty() || layers2.isEmpty() || layer1 == null || layer2 == null);
    }

    public InputStream getLayerOne() throws IOException {
        return getInputStream(layer1Width, layer1Height, layer1, layers1);
    }

    public InputStream getLayerTwo() throws IOException {
        return getInputStream(layer2Width, layer2Height, layer2, layers2);
    }

    private InputStream getInputStream(int layer1Width, int layer1Height,
                                       BufferedImage layer, List<BufferedImage> layers) throws IOException {
        BufferedImage concatImage = new BufferedImage(layer1Width, layer1Height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = concatImage.createGraphics();
        g2d.drawImage(layer, 0, 0, null);
        int currentWidth = layer.getWidth();
        for (BufferedImage bufferedImage : layers) {
            g2d.drawImage(bufferedImage, currentWidth, 0, null);
            currentWidth += bufferedImage.getWidth();
        }
        g2d.dispose();
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        ImageIO.write(concatImage, "png", os);
        return new ByteArrayInputStream(os.toByteArray());
    }


}
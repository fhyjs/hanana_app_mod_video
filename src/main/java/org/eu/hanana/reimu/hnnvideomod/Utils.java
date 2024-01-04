package org.eu.hanana.reimu.hnnvideomod;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Random;
import java.util.Scanner;

public class Utils {
    public static String convertInputStreamToString(InputStream inputStream) {
        Scanner scanner = new Scanner(inputStream, StandardCharsets.UTF_8);

        String var2;
        try {
            scanner.useDelimiter("\\A");
            var2 = scanner.hasNext() ? scanner.next() : "";
        } catch (Throwable var5) {
            try {
                scanner.close();
            } catch (Throwable var4) {
                var5.addSuppressed(var4);
            }

            throw var5;
        }

        scanner.close();
        return var2;
    }
    public static BufferedImage createImageFromByteBuffer(ByteBuffer buffer, int width, int height) {
        int[] pixels = new int[width * height];
        int[] pixelArray = new int[4];

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int index = (x + (height - y - 1) * width) * 4;
                pixelArray[0] = buffer.get(index) & 0xFF;
                pixelArray[1] = buffer.get(index + 1) & 0xFF;
                pixelArray[2] = buffer.get(index + 2) & 0xFF;
                pixelArray[3] = buffer.get(index + 3) & 0xFF;

                // Combine the RGBA values into a single int
                pixels[x + y * width] = (pixelArray[3] << 24) | (pixelArray[0] << 16) | (pixelArray[1] << 8) | pixelArray[2];
            }
        }

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        image.setRGB(0, 0, width, height, pixels, 0, width);

        return image;
    }
    public static ByteBuffer convertImageToByteBuffer(BufferedImage image) {
        int[] pixels = new int[image.getWidth() * image.getHeight()];
        image.getRGB(0, 0, image.getWidth(), image.getHeight(), pixels, 0, image.getWidth());

        ByteBuffer buffer = BufferUtils.createByteBuffer(image.getWidth() * image.getHeight() * 4);

        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                int pixel = pixels[y * image.getWidth() + x];
                buffer.put((byte) ((pixel >> 16) & 0xFF));
                buffer.put((byte) ((pixel >> 8) & 0xFF));
                buffer.put((byte) (pixel & 0xFF));
                buffer.put((byte) ((pixel >> 24) & 0xFF));
            }
        }

        buffer.flip();
        return buffer;
    }
    static Random random = new Random();
    public static int getRandomIntInRange(int max, int min) {
        if (min > max) {
            throw new IllegalArgumentException("最小值不能大于最大值");
        }
        // 使用 nextInt 方法生成 [min, max+1) 范围内的随机整数
        return random.nextInt(max - min + 1) + min;
    }
    public static void renderImage(BufferedImage imageBuffer, int width, int height) {
        renderImage(convertImageToByteBuffer(imageBuffer),width,height);
    }
    public static void renderImage(ByteBuffer imageBuffer, int width, int height) {
        GL11.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);

        GL11.glPushMatrix();

        // 绘制纹理
        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, width, height, 0,
                GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, imageBuffer);

        GL11.glBegin(GL11.GL_QUADS);
        GL11.glTexCoord2f(0, 0);
        GL11.glVertex2f(100, 100);

        GL11.glTexCoord2f(1, 0);
        GL11.glVertex2f(100 + width, 100);

        GL11.glTexCoord2f(1, 1);
        GL11.glVertex2f(100 + width, 100 + height);

        GL11.glTexCoord2f(0, 1);
        GL11.glVertex2f(100, 100 + height);
        GL11.glEnd();

        GL11.glPopMatrix();
    }
    public static String getAssets(ClassLoader classLoader, String path) {
        try {
            InputStream inputStream = classLoader.getResourceAsStream(path);

            String var3;
            label50: {
                try {
                    if (inputStream != null) {
                        var3 = convertInputStreamToString(inputStream);
                        break label50;
                    }
                } catch (Throwable var6) {
                    if (inputStream != null) {
                        try {
                            inputStream.close();
                        } catch (Throwable var5) {
                            var6.addSuppressed(var5);
                        }
                    }

                    throw var6;
                }

                if (inputStream != null) {
                    inputStream.close();
                }

                return null;
            }

            if (inputStream != null) {
                inputStream.close();
            }

            return var3;
        } catch (IOException var7) {
            var7.printStackTrace();
            return null;
        }
    }
    // 比较两个double是否相等
    public static boolean areDoublesEqual(double d1, double d2) {
        double epsilon = 1; // 设置一个很小的阈值

        return Math.abs(d1 - d2) < epsilon;
    }
    public static String fetchTextFromURL(String urlString) throws IOException {
        URL url = new URL(urlString);
        URLConnection urlConnection = url.openConnection();

        // 设置连接超时时间，单位为毫秒（可选）
        urlConnection.setConnectTimeout(5000);

        try (InputStream inputStream = urlConnection.getInputStream();
             InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
             BufferedReader bufferedReader = new BufferedReader(inputStreamReader)) {

            StringBuilder stringBuilder = new StringBuilder();
            String line;

            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line).append("\n");
            }

            return stringBuilder.toString();
        }
    }
}

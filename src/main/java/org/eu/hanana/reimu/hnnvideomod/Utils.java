package org.eu.hanana.reimu.hnnvideomod;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
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
}

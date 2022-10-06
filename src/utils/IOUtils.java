package utils;

import java.io.*;

public class IOUtils {
    public static String read() {
        StringBuilder s = new StringBuilder();
        File file = new File("testfile.txt");
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            String tmp;
            while ((tmp = reader.readLine()) != null) {
                s.append(tmp);
                s.append("\n");
            }
            s.append("\0");
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return s.toString();
    }

    public static void write(String s) {
        File file = new File("error.txt");  // 词法分析、语法分析时 pathname=output.txt, 错误处理时 pathname=error.txt
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(file, false));  // 语法分析时 append = true, 词法分析、错误处理时 append = false
            writer.write(s);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

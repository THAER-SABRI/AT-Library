package library_system;

import java.util.List;

public class TABLE {
    public static void print(String title, String[] headers, List<String[]> rows) {
        int cols = headers.length;
        int[] w = new int[cols];
        for (int c = 0; c < cols; c++) w[c] = headers[c] == null ? 0 : headers[c].length();
        for (String[] r : rows) for (int c = 0; c < cols; c++) if (r[c] != null && r[c].length() > w[c]) w[c] = r[c].length();

        String top = "╔";
        for (int c = 0; c < cols; c++) { top += rep("═", w[c] + 2) + (c == cols - 1 ? "╗" : "╦"); }
        String mid = "╠";
        for (int c = 0; c < cols; c++) { mid += rep("═", w[c] + 2) + (c == cols - 1 ? "╣" : "╬"); }
        String bot = "╚";
        for (int c = 0; c < cols; c++) { bot += rep("═", w[c] + 2) + (c == cols - 1 ? "╝" : "╩"); }

        if (title != null && !title.trim().isEmpty()) System.out.println(title);
        System.out.println(top);
        System.out.print("║");
        for (int c = 0; c < cols; c++) System.out.print(" " + pad(headers[c], w[c]) + " ║");
        System.out.println();
        System.out.println(mid);
        for (String[] r : rows) {
            System.out.print("║");
            for (int c = 0; c < cols; c++) System.out.print(" " + pad(r[c], w[c]) + " ║");
            System.out.println();
        }
        System.out.println(bot);
    }

    private static String pad(String s, int w) {
        if (s == null) s = "";
        if (s.length() >= w) return s;
        StringBuilder sb = new StringBuilder(s);
        while (sb.length() < w) sb.append(' ');
        return sb.toString();
    }

    private static String rep(String s, int n) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < n; i++) sb.append(s);
        return sb.toString();
    }
}

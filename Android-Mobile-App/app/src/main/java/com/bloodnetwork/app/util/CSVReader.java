package com.bloodnetwork.app.util;

import android.content.Context;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class CSVReader {

    public static List<String[]> readFromAssets(Context context, String assetFileName) throws IOException {
        List<String[]> rows = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(context.getAssets().open(assetFileName)))) {
            String line;
            boolean header = true;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                if (header) {
                    header = false;
                    continue;
                }
                rows.add(line.split(",", -1));
            }
        }
        return rows;
    }
}

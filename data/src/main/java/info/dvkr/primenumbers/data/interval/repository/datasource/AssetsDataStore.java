package info.dvkr.primenumbers.data.interval.repository.datasource;


import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;

import info.dvkr.primenumbers.data.interval.parser.IntervalParser;
import info.dvkr.primenumbers.domain.model.Interval;

public class AssetsDataStore {

    private final Context context;
    private final IntervalParser parser;

    public AssetsDataStore(@NonNull final Context context, @NonNull final IntervalParser intervalParser) {
        this.context = context;
        this.parser = intervalParser;
    }

    public List<Interval> intervalList() {
        System.out.println("AssetsDataStore: intervalList");
        final String xmlFromAssets = getXMLFromAssets("intervals.xml");
        return parser.parseIntervals(xmlFromAssets);
    }

    private String getXMLFromAssets(final String fileName) {
        final StringBuilder sb = new StringBuilder();
        String line;
        try (final BufferedReader reader =
                     new BufferedReader(
                             new InputStreamReader(context.getAssets().open(fileName), StandardCharsets.UTF_8)
                     )) {
            while ((line = reader.readLine()) != null) sb.append(line.toCharArray());
        } catch (IOException ignore) {
            // TODO Need some proper error handling
        }
        return sb.toString();
    }
}
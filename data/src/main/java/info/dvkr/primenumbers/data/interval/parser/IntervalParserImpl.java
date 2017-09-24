package info.dvkr.primenumbers.data.interval.parser;


import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import javax.inject.Inject;
import javax.inject.Singleton;

import info.dvkr.primenumbers.domain.model.Interval;

@Singleton
public class IntervalParserImpl implements IntervalParser {
    private final static String ROOT = "ROOT";
    private final static String INTERVALS = "INTERVALS";
    private final static String INTERVAL = "INTERVAL";
    private final static String ID = "ID";
    private final static String LOW = "LOW";
    private final static String HIGH = "HIGH";

    private final ExecutorService executor;

    @Inject
    public IntervalParserImpl(final ExecutorService executor) {
        this.executor = executor;
    }

    @Override // No error handling implemented
    public List<Interval> parseIntervals(@NonNull final String data) {
        final Future<List<Interval>> listFuture = executor.submit(new Callable<List<Interval>>() {
            @Override
            public List<Interval> call() throws Exception {
                final List<Interval> intervalList = new ArrayList<>();

                String root = data.trim().toUpperCase();
                root = root.substring(root.indexOf(getStartTag(ROOT)) + getStartTag(ROOT).length(), root.indexOf(getEndTag(ROOT)));
                String intervals = root.substring(root.indexOf(getStartTag(INTERVALS)) + getStartTag(INTERVALS).length(), root.indexOf(getEndTag(INTERVALS)));

                final String startTagInterval = getStartTag(INTERVAL);
                final String startTagId = getStartTag(ID);
                final String endTagId = getEndTag(ID);
                final String startTagLow = getStartTag(LOW);
                final String endTagLow = getEndTag(LOW);
                final String startTagHigh = getStartTag(HIGH);
                final String endTagHigh = getEndTag(HIGH);

                int startIndex;
                int endIndex;
                String id;
                String low;
                String high;

                while (!intervals.isEmpty()) {
                    startIndex = intervals.indexOf(startTagInterval) + startTagInterval.length();
                    endIndex = intervals.indexOf(startTagInterval, startIndex);

                    String interval = intervals.substring(startIndex, endIndex).trim();

                    // Parsing interval
                    id = interval.substring(interval.indexOf(startTagId) + startTagId.length(), interval.indexOf(endTagId)).trim();
                    low = interval.substring(interval.indexOf(startTagLow) + startTagLow.length(), interval.indexOf(endTagLow)).trim();
                    high = interval.substring(interval.indexOf(startTagHigh) + startTagHigh.length(), interval.indexOf(endTagHigh)).trim();

                    intervalList.add(new Interval(Integer.valueOf(id), Integer.valueOf(low), Integer.valueOf(high)));

                    // Removing current interval
                    intervals = intervals.substring(endIndex + startTagInterval.length(), intervals.length()).trim();
                }

                return intervalList;
            }
        });

        try {
            return listFuture.get();
        } catch (InterruptedException | ExecutionException e) {
            return new ArrayList<>();
        }
    }

    private String getStartTag(String s) {
        return "<" + s + ">";
    }

    private String getEndTag(String s) {
        return "</" + s + ">";
    }
}

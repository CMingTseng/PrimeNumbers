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
    private final static String INTERVAL = "INTERVAL";
    private final static String ID = "ID";
    private final static String LOW = "LOW";
    private final static String HIGH = "HIGH";

    private final ExecutorService executor;

    public static class Item {
        private final List<Item> downItemList = new ArrayList<>();
        private final String tag;
        private final String content;

        public Item(final String tag, final String content) {
            this.tag = tag;
            this.content = content;
        }

        public void addAll(final List<Item> items) {
            downItemList.addAll(items);
        }

        public String getTag() {
            return tag;
        }

        public String getContent() {
            return content;
        }

        public List<Item> getDownItemList() {
            return downItemList;
        }
    }

    @Inject
    public IntervalParserImpl(final ExecutorService executor) {
        this.executor = executor;
    }

    @Override // No error handling implemented
    public List<Interval> parseIntervals(@NonNull final String data) {
        final Future<List<Interval>> listFuture = executor.submit(new Callable<List<Interval>>() {
            @Override
            public List<Interval> call() throws Exception {
                return collectIntervals(getItems(data.trim().toUpperCase()));
            }
        });

        try {
            return listFuture.get();
        } catch (InterruptedException | ExecutionException e) {
            return new ArrayList<>();
        }
    }

    // Return tree on Items that represent XML tree
    private List<Item> getItems(final String source) {
        final List<Item> items = new ArrayList<>();
        String workingSource = source.trim();
        while (!workingSource.isEmpty()) {
            final int startTagIndex = workingSource.indexOf('<');

            //No tag, end of recursion
            if (startTagIndex < 0) return null;

            final String tag = workingSource.substring(startTagIndex + 1, workingSource.indexOf('>'));
            final int closeTagIndex = workingSource.indexOf("</" + tag + '>');
            final String tagContent = workingSource.substring(startTagIndex + tag.length() + 2, closeTagIndex);

            final Item item = new Item(tag, tagContent);
            final List<Item> childItemList = getItems(tagContent);
            if (childItemList != null) item.addAll(childItemList);
            items.add(item);

            workingSource = workingSource.substring(closeTagIndex + tag.length() + 3);
        }
        return items;
    }

    // Return lis of intervals
    private List<Interval> collectIntervals(final List<Item> items) {
        final List<Interval> intervalList = new ArrayList<>();

        for (final Item item : items) {
            if (INTERVAL.equals(item.getTag())) { // Interval
                final Interval interval = getInterval(item.downItemList);
                if (interval != null) intervalList.add(interval);
            } else { // Going deeper
                intervalList.addAll(collectIntervals(item.getDownItemList()));
            }
        }

        return intervalList;
    }

    // Returns interval or null if error
    private Interval getInterval(final List<Item> items) {
        if (items.size() != 3) return null;

        int id = -1;
        int low = -1;
        int high = -1;

        for (final Item item : items) {
            if (ID.equals(item.getTag())) id = Integer.parseInt(item.content);
            if (LOW.equals(item.getTag())) low = Integer.parseInt(item.content);
            if (HIGH.equals(item.getTag())) high = Integer.parseInt(item.content);
        }

        if (id < 0 || low < 0 || high < 0) return null;

        return new Interval(id, low, high);
    }
}
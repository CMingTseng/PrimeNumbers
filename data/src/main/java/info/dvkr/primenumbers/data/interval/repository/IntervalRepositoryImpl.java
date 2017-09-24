package info.dvkr.primenumbers.data.interval.repository;


import android.content.Context;
import android.support.annotation.NonNull;

import java.util.List;

import info.dvkr.primenumbers.data.interval.parser.IntervalParser;
import info.dvkr.primenumbers.data.interval.repository.datasource.AssetsDataStore;
import info.dvkr.primenumbers.domain.model.Interval;
import info.dvkr.primenumbers.domain.repository.IntervalRepository;

public class IntervalRepositoryImpl implements IntervalRepository {

    private final Context context;
    private final IntervalParser parser;

    public IntervalRepositoryImpl(final Context context, final IntervalParser parser) {
        if (context == null)
            throw new IllegalArgumentException("Context can not be null");
        if (parser == null)
            throw new IllegalArgumentException("IntervalParser can not be null");

        this.context = context.getApplicationContext();
        this.parser = parser;
    }

    @Override
    @NonNull
    public List<Interval> getIntervals() {
        System.out.println("IntervalRepositoryImpl: getIntervals" + " Thread: " + Thread.currentThread().getName());
        return new AssetsDataStore(context, parser).intervalList();
    }
}
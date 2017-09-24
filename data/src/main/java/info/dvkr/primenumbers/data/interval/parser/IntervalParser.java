package info.dvkr.primenumbers.data.interval.parser;

import android.support.annotation.NonNull;

import java.util.List;

import info.dvkr.primenumbers.domain.model.Interval;

public interface IntervalParser {

    List<Interval> parseIntervals(@NonNull final String data);
}

package info.dvkr.primenumbers.domain.repository;


import android.support.annotation.NonNull;

import java.util.List;

import info.dvkr.primenumbers.domain.model.Interval;

public interface IntervalRepository {

    @NonNull
    List<Interval> getIntervals();
}
package info.dvkr.primenumbers.domain.interactor;


import android.support.annotation.NonNull;

import java.util.List;

import info.dvkr.primenumbers.domain.model.Interval;

public interface PrimeNumberCalculation {

    void calculatePrimeNumbers(@NonNull List<Interval> intervals);
}
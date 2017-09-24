package info.dvkr.primenumbers.domain.interactor;


import android.support.annotation.NonNull;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeoutException;

import info.dvkr.primenumbers.domain.model.Interval;
import info.dvkr.primenumbers.domain.model.PrimeNumber;

public class PrimeNumberCalculationImpl implements PrimeNumberCalculation {

    private final ExecutorService executor;

    private final PrimeNumberAggregator aggregator;

    public PrimeNumberCalculationImpl(@NonNull final ExecutorService executor,
                                      @NonNull final PrimeNumberAggregator aggregator) {
        if (executor == null)
            throw new IllegalArgumentException("ThreadExecutor can not be null");
        if (aggregator == null)
            throw new IllegalArgumentException("PrimeNumberAggregator can not be null");

        this.executor = executor;
        this.aggregator = aggregator;
    }

    @Override
    public void calculatePrimeNumbers(@NonNull final List<Interval> intervals) {
        if (intervals == null) throw new IllegalArgumentException("Intervals list can not be null");

        for (final Interval interval : intervals) {
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    calcPrimeForInterval(interval, aggregator);
                }
            });
        }
    }

    // Runs on PrimeNumberCalculation pool
    private void calcPrimeForInterval(@NonNull final Interval interval,
                                      @NonNull final PrimeNumberAggregator aggregator) {
        if (interval == null) {
            aggregator.onError(new IllegalArgumentException("Interval cannot be null"));
            return;
        }

        for (int i = interval.getLow(); i <= interval.getHigh(); i++) {
            if (isPrime(i, interval.getId())) {
                try {
                    aggregator.onNext(new PrimeNumber(interval.getId(), i));
                } catch (InterruptedException ex) {
                    aggregator.onError(new TimeoutException("PrimeNumberAggregatorQueue timeout"));
                    return;
                }
            }
        }
    }

    private boolean isPrime(final int number, final int id) {
        if (number < 2) return false;
        for (int i = 2; i <= number / 2; i++) {
            if (number % i == 0) return false;
        }
        return true;
    }
}
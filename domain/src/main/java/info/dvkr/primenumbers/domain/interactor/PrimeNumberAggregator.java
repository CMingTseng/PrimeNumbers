package info.dvkr.primenumbers.domain.interactor;


import android.support.annotation.NonNull;

import info.dvkr.primenumbers.domain.model.PrimeNumber;

public interface PrimeNumberAggregator {

    void onNext(@NonNull final PrimeNumber number) throws InterruptedException;

    void onError(@NonNull Exception exception);
}
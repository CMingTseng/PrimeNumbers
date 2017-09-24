package info.dvkr.primenumbers.domain.repository;


import android.support.annotation.NonNull;

import java.util.List;

import info.dvkr.primenumbers.domain.model.PrimeNumber;

public interface OnPrimeNumbersUpdateCallback {
    void onPrimeNumbersUpdate(@NonNull List<PrimeNumber> primeNumbers);

    void onError(@NonNull Exception exception);
}
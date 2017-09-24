package info.dvkr.primenumbers.domain.repository;


import android.support.annotation.NonNull;

import java.util.List;

import info.dvkr.primenumbers.domain.model.PrimeNumber;

public interface PrimeNumberRepository {

    void addPrimeNumber(@NonNull final PrimeNumber primeNumber);

    @NonNull
    List<PrimeNumber> getPrimeNumbers();

    void addOnUpdateCallback(@NonNull final OnPrimeNumbersUpdateCallback callback);

    void removeOnUpdateCallback(@NonNull final OnPrimeNumbersUpdateCallback callback);

    void onPrimeNumberError(@NonNull Exception exception);
}
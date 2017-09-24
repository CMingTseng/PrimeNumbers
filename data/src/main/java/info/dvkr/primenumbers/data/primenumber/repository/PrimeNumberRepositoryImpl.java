package info.dvkr.primenumbers.data.primenumber.repository;


import android.support.annotation.NonNull;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;

import info.dvkr.primenumbers.data.primenumber.repository.datasource.DataStoreCallback;
import info.dvkr.primenumbers.data.primenumber.repository.datasource.NetworkDataStore;
import info.dvkr.primenumbers.domain.model.PrimeNumber;
import info.dvkr.primenumbers.domain.repository.OnPrimeNumbersUpdateCallback;
import info.dvkr.primenumbers.domain.repository.PrimeNumberRepository;

public class PrimeNumberRepositoryImpl implements PrimeNumberRepository, DataStoreCallback {

    private final CopyOnWriteArrayList<OnPrimeNumbersUpdateCallback> callbackList = new CopyOnWriteArrayList<>();

    @NonNull
    private final ExecutorService executor;
    @NonNull
    private final NetworkDataStore networkDataStore;

    public PrimeNumberRepositoryImpl(@NonNull final ExecutorService executor,
                                     @NonNull final NetworkDataStore networkDataStore) {
        this.executor = executor;
        this.networkDataStore = networkDataStore;
        this.networkDataStore.setCallback(this);
        executor.execute(new Runnable() {
            @Override
            public void run() {
                Thread.currentThread().setName("PrimeNumberRepository");
            }
        });
    }

    @Override // Runs on Caller thread
    public void addPrimeNumber(@NonNull final PrimeNumber primeNumber) {
        if (primeNumber == null) throw new IllegalArgumentException("PrimeNumber cannot be null");
        networkDataStore.addPrimeNumber(primeNumber);
    }

    @Override
    @NonNull
    public List<PrimeNumber> getPrimeNumbers() {
        return networkDataStore.primeNumbersList();
    }

    @Override
    public void addOnUpdateCallback(@NonNull final OnPrimeNumbersUpdateCallback callback) {
        callbackList.addIfAbsent(callback);
    }

    @Override
    public void removeOnUpdateCallback(@NonNull final OnPrimeNumbersUpdateCallback callback) {
        callbackList.remove(callback);
    }

    @Override
    public void onPrimeNumberError(@NonNull final Exception exception) {
        onError(exception);
    }

    @Override
    public void onNetworkError(@NonNull final Exception exception) {
        onError(exception);
    }

    @Override
    public void onDataStoreUpdate() {
        if (callbackList.isEmpty()) return;
        executor.execute(new Runnable() {
            @Override
            public void run() {
                final List<PrimeNumber> primeNumbers = getPrimeNumbers();
                for (final OnPrimeNumbersUpdateCallback onPrimeNumbersUpdateCallback : callbackList) {
                    onPrimeNumbersUpdateCallback.onPrimeNumbersUpdate(primeNumbers);
                }
            }
        });
    }

    private void onError(@NonNull final Exception exception) {
        if (callbackList.isEmpty()) return;
        executor.execute(new Runnable() {
            @Override
            public void run() {
                for (final OnPrimeNumbersUpdateCallback onPrimeNumbersUpdateCallback : callbackList) {
                    onPrimeNumbersUpdateCallback.onError(exception);
                }
            }
        });
    }


}
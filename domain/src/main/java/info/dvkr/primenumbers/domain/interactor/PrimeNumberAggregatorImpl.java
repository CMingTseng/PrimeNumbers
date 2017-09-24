package info.dvkr.primenumbers.domain.interactor;


import android.support.annotation.NonNull;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import info.dvkr.primenumbers.domain.model.PrimeNumber;
import info.dvkr.primenumbers.domain.repository.PrimeNumberRepository;

public class PrimeNumberAggregatorImpl implements PrimeNumberAggregator {
    // Prime number queue max length.
    // In real application can be adjusted base on memory requirements
    private final int MAX_PRIME_NUMBER_QUEUE_LENGTHS = 10;

    private final BlockingQueue<PrimeNumber> primeNumberQueue =
            new LinkedBlockingQueue<>(MAX_PRIME_NUMBER_QUEUE_LENGTHS);

    private final PrimeNumberRepository repository;

    public PrimeNumberAggregatorImpl(@NonNull final ExecutorService executor,
                                     @NonNull final PrimeNumberRepository numberRepository) {
        if (executor == null)
            throw new IllegalArgumentException("ThreadExecutor can not be null");
        if (numberRepository == null)
            throw new IllegalArgumentException("PrimeNumberRepository can not be null");

        repository = numberRepository;

        executor.execute(new Runnable() {
            @Override
            public void run() {
                Thread.currentThread().setName("PrimeNumberAggregator");
                while (!Thread.currentThread().isInterrupted()) {
                    try {
                        final PrimeNumber primeNumber = primeNumberQueue.poll(10, TimeUnit.MILLISECONDS);
                        if (primeNumber == null) continue;
                        repository.addPrimeNumber(primeNumber);
                    } catch (InterruptedException ignore) {
                    }
                }
            }
        });
    }

    @Override // Runs on Caller thread
    public void onNext(@NonNull final PrimeNumber number) throws InterruptedException {
        primeNumberQueue.put(number);
    }

    @Override // Runs on Caller thread
    public void onError(@NonNull final Exception exception) {
        repository.onPrimeNumberError(exception);
    }
}
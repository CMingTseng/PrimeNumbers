package info.dvkr.primenumbers.data.primenumber.repository.datasource;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import info.dvkr.primenumbers.domain.model.PrimeNumber;

public class NetworkDataStore {

    // Prime number queue max length.
    // In real application can be adjusted base on memory requirements
    private final int MAX_PRIME_NUMBER_QUEUE_LENGTHS = 100;

    private final BlockingQueue<PrimeNumber> primeNumberQueue =
            new LinkedBlockingQueue<>(MAX_PRIME_NUMBER_QUEUE_LENGTHS);

    // Use this for saving numbers
    private final ConcurrentLinkedQueue<PrimeNumber> primeNumbers = new ConcurrentLinkedQueue<>();

    @Nullable
    private DataStoreCallback callback;


    public NetworkDataStore(final Context context, // For saving file,
                            @NonNull final ExecutorService executor) {

        executor.execute(new Runnable() {
            @Override
            public void run() {
                Thread.currentThread().setName("NetworkDataStore");

                FakeSocket fakeSocket = null;
                boolean isWriteSuccess;
                int retryCount;
                while (!Thread.currentThread().isInterrupted()) {
                    try {
                        final PrimeNumber primeNumber = primeNumberQueue.poll(10, TimeUnit.MILLISECONDS);
                        if (primeNumber == null) continue;

                        isWriteSuccess = false;
                        retryCount = 0;
                        while (true) {
                            try {
                                if (fakeSocket == null)
                                    fakeSocket = new FakeSocket(context, "PrimeNumbers.txt");

                                fakeSocket.write(primeNumber.toString() + "\n");

                                primeNumbers.add(primeNumber);
                                if (callback != null) callback.onDataStoreUpdate();
                                isWriteSuccess = true;

                            } catch (IOException e) { // We have error
                                if (callback != null)
                                    callback.onNetworkError(new IOException("NetError on saving: " + primeNumber + " Retry: " + retryCount, e));

                                // Closing current connection
                                if (fakeSocket != null) {
                                    try {
                                        fakeSocket.close();
                                    } catch (IOException ex) {
                                        if (callback != null) callback.onNetworkError(ex);
                                    }
                                    fakeSocket = null;
                                }
                                retryCount++;
                            }

                            if (isWriteSuccess) break;
                            if (retryCount >= 3) {
                                if (callback != null)
                                    callback.onNetworkError(new IOException("Too many network errors"));
                                if (fakeSocket != null) {
                                    try {
                                        fakeSocket.close();
                                    } catch (IOException ex) {
                                        if (callback != null) callback.onNetworkError(ex);
                                    }
                                }
                                return;
                            }
                        }
                    } catch (InterruptedException ignore) {
                    }
                }

                if (fakeSocket != null) {
                    try {
                        fakeSocket.close();
                    } catch (IOException ex) {
                        if (callback != null) callback.onNetworkError(ex);
                    }
                }
            }
        });
    }

    // Runs on Caller thread
    public void addPrimeNumber(@NonNull final PrimeNumber primeNumber) {
        try {
            primeNumberQueue.put(primeNumber);
        } catch (InterruptedException e) {
            if (callback != null)
                callback.onNetworkError(new TimeoutException("NetworkDataStoreQueue timeout"));
        }
    }

    // Runs on Caller thread
    public List<PrimeNumber> primeNumbersList() {
        return new ArrayList<>(primeNumbers);
    }

    // Runs on Caller thread
    public void setCallback(@Nullable final DataStoreCallback callback) {
        this.callback = callback;
    }
}
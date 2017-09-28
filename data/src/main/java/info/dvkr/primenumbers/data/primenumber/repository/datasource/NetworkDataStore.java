package info.dvkr.primenumbers.data.primenumber.repository.datasource;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import info.dvkr.primenumbers.domain.model.PrimeNumber;

public class NetworkDataStore {

    // Prime number queue max length.
    // In real application can be adjusted base on memory requirements
    private final int MAX_PRIME_NUMBER_QUEUE_LENGTHS = 500;

    private final BlockingQueue<PrimeNumber> primeNumberQueue =
            new LinkedBlockingQueue<>(MAX_PRIME_NUMBER_QUEUE_LENGTHS);

    // Use this for saving numbers
    private final ConcurrentLinkedQueue<PrimeNumber> primeNumbers = new ConcurrentLinkedQueue<>();


    @Nullable
    private DataStoreCallback callback;

    private static class PrimeNumberFuture {
        private final PrimeNumber primeNumber;
        private final Future<Object> primeNumberFuture;
        private final FakeSocket fakeSocket;

        public PrimeNumberFuture(final PrimeNumber primeNumber, final Future<Object> primeNumberFuture, final FakeSocket fakeSocket) {
            this.primeNumber = primeNumber;
            this.primeNumberFuture = primeNumberFuture;
            this.fakeSocket = fakeSocket;
        }


        public PrimeNumber getPrimeNumber() {
            return primeNumber;
        }

        public Future<Object> getPrimeNumberFuture() {
            return primeNumberFuture;
        }

        public FakeSocket getFakeSocket() {
            return fakeSocket;
        }
    }

    private final List<PrimeNumberFuture> primeNumbersFutures = new LinkedList<>();

    public NetworkDataStore(final Context context, // For saving file,
                            @NonNull final ExecutorService executor) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                Thread.currentThread().setName("NetworkDataStore");

                PrimeNumber primeNumberToSave;
                FakeSocket fakeSocket = null;

                int retryCount = 0;
                while (!Thread.currentThread().isInterrupted()) {
                    primeNumberToSave = null;
                    try { // getting net number to save
                        primeNumberToSave = primeNumberQueue.poll(10, TimeUnit.MILLISECONDS);
                    } catch (InterruptedException ignore) {
                    }

                    // Sending number to socket
                    if (primeNumberToSave != null) {
                        try {
                            if (fakeSocket == null) {
                                fakeSocket = new FakeSocket(context, "PrimeNumbers.txt");
                                fakeSocket.connect();
                            }
                            final Future<Object> primeNumberFuture = fakeSocket.write(primeNumberToSave);
                            primeNumbersFutures.add(new PrimeNumberFuture(primeNumberToSave, primeNumberFuture, fakeSocket));
                        } catch (IOException | RejectedExecutionException ex) {
                            retryCount++;

                            if (fakeSocket != null && fakeSocket.isConnected() && !fakeSocket.isClosed())
                                try {
                                    fakeSocket.close();
                                } catch (IOException e) {
                                    if (callback != null) callback.onNetworkError(e);
                                }
                            fakeSocket = null;

                            addPrimeNumber(primeNumberToSave); // Queering to store again
                            if (callback != null)
                                callback.onNetworkError(new IOException("NetError on saving: " + primeNumberToSave));
                        }
                    }

                    // Checking current futures results
                    PrimeNumber primeNumberAfterSave;
                    for (int i = 0; i < primeNumbersFutures.size(); i++) {
                        if (primeNumbersFutures.get(i).getPrimeNumberFuture().isDone()) { // Task is completed of failed
                            final PrimeNumberFuture primeNumberFuture = primeNumbersFutures.remove(i);
                            primeNumberAfterSave = null;
                            try {
                                primeNumberAfterSave = primeNumberFuture.getPrimeNumber();
                                primeNumberFuture.getPrimeNumberFuture().get();
                                // No exception, successful store
                                primeNumbers.add(primeNumberAfterSave); // Adding to local cache
                                if (callback != null) callback.onDataStoreUpdate();
                            } catch (InterruptedException | ExecutionException e) {
                                // Store error
                                retryCount++;
                                addPrimeNumber(primeNumberAfterSave); // Queering to store again
                                if (callback != null)
                                    callback.onNetworkError(new IOException("NetError on saving: " + primeNumberAfterSave));
                                fakeSocket = null;
                            }
                        } else if (primeNumbersFutures.get(i).getFakeSocket() != fakeSocket) {
                            // Socket failed for this task
                            final PrimeNumberFuture primeNumberFuture = primeNumbersFutures.remove(i);
                            addPrimeNumber(primeNumberFuture.getPrimeNumber()); // Queering to store again
                        }
                    }

                    if (retryCount >= 20) {
                        if (callback != null)
                            callback.onNetworkError(new IOException("Too many network errors"));
                        if (fakeSocket != null && fakeSocket.isConnected() && !fakeSocket.isClosed()) {
                            try {
                                fakeSocket.close();
                            } catch (IOException ex) {
                                if (callback != null) callback.onNetworkError(ex);
                            }
                        }
                        return;
                    }
                }

                if (fakeSocket != null && fakeSocket.isConnected() && !fakeSocket.isClosed())
                    try {
                        fakeSocket.close();
                    } catch (IOException e) {
                        if (callback != null) callback.onNetworkError(e);
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
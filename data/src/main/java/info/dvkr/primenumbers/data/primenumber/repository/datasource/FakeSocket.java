package info.dvkr.primenumbers.data.primenumber.repository.datasource;

import android.content.Context;
import android.support.annotation.NonNull;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.SocketException;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import info.dvkr.primenumbers.domain.model.PrimeNumber;

public class FakeSocket {
    volatile private boolean isConnected;
    volatile private boolean isClosed;

    // Do not inject via Dagger because it's a demo fake implementation
    private ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private Random r = new Random();
    private FileOutputStream outputStream;

    // Using Future<PrimeNumber> to implement socket read/write delay without blocking thread
    public FakeSocket(final Context context,
                      @NonNull String filename) throws IOException {
        outputStream = context.openFileOutput(filename, Context.MODE_APPEND);
    }

    public void connect() throws IOException {
        if (isConnected) throw new SocketException("Socket is connected");
        if (isClosed) throw new SocketException("Socket is closed");
        isConnected = true;
    }

    public Future<Object> write(final PrimeNumber primeNumber) throws RejectedExecutionException {
        return scheduler.schedule(new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                if (!isConnected) throw new SocketException("Socket is not connected");
                if (isClosed) throw new SocketException("Socket is closed");

                if (r.nextInt(20) < 1) { // Make random network error
                    close(); // Closing socket
                    throw new IOException("This is demo network error.");
                }
                outputStream.write((primeNumber.toString() + "\n").getBytes());
                return null;
            }
        }, r.nextInt(100), TimeUnit.MILLISECONDS); // Random delay - simulating slow connection
    }

    public synchronized void close() throws IOException {
        if (!isConnected) throw new SocketException("Socket is not connected");
        if (isClosed) throw new SocketException("Socket is closed");

        outputStream.close();
        scheduler.shutdownNow();
        isClosed = true;
    }

    public boolean isConnected() {
        return isConnected;
    }

    public boolean isClosed() {
        return isClosed;
    }
}
package info.dvkr.primenumbers.data.primenumber.repository.datasource;

import android.content.Context;
import android.support.annotation.NonNull;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Random;

public class FakeSocket {
    private Random r = new Random();
    private FileOutputStream outputStream;

    public FakeSocket(final Context context,
                      @NonNull String filename) throws IOException {
        outputStream = context.openFileOutput(filename, Context.MODE_APPEND);
    }

    public void write(String string) throws IOException {
        try {
            Thread.sleep(100); // Writing slowly
        } catch (InterruptedException ignore) {
        }

        // Make random network error
        if (r.nextInt(10) < 1) throw new IOException("This is demo network error.");
        outputStream.write(string.getBytes());
    }

    public void close() throws IOException {
        outputStream.close();
    }
}
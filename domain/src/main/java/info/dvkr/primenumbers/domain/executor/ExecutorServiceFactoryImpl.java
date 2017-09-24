package info.dvkr.primenumbers.domain.executor;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ExecutorServiceFactoryImpl implements ExecutorServiceFactory {
    private final List<ExecutorService> createdExecutorServices = new ArrayList<>();

    @Override
    public ExecutorService getExecutorService(final int maxThreadCount) {
        if (maxThreadCount == 1) {
            final ExecutorService executorService = Executors.newSingleThreadExecutor();
            createdExecutorServices.add(executorService);
            return executorService;
        } else {
            final ExecutorService executorService = Executors.newFixedThreadPool(maxThreadCount);
            createdExecutorServices.add(executorService);
            return executorService;
        }
    }

    @Override
    public void shutdownExecutorServices() {
        for (final ExecutorService createdExecutorService : createdExecutorServices) {
            createdExecutorService.shutdownNow();
        }
    }

}
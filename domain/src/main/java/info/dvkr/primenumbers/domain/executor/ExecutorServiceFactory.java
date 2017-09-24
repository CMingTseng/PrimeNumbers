package info.dvkr.primenumbers.domain.executor;


import java.util.concurrent.ExecutorService;

public interface ExecutorServiceFactory {

    ExecutorService getExecutorService(final int maxThreadCount);

    void shutdownExecutorServices();
}

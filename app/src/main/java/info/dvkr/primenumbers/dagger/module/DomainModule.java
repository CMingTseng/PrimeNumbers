package info.dvkr.primenumbers.dagger.module;

import java.util.concurrent.ExecutorService;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import info.dvkr.primenumbers.domain.executor.ExecutorServiceFactory;
import info.dvkr.primenumbers.domain.executor.ExecutorServiceFactoryImpl;
import info.dvkr.primenumbers.domain.interactor.PrimeNumberAggregator;
import info.dvkr.primenumbers.domain.interactor.PrimeNumberAggregatorImpl;
import info.dvkr.primenumbers.domain.interactor.PrimeNumberCalculation;
import info.dvkr.primenumbers.domain.interactor.PrimeNumberCalculationImpl;
import info.dvkr.primenumbers.domain.repository.PrimeNumberRepository;

@Singleton
@Module
public class DomainModule {
    public static final String SHARED_EXECUTOR = "SHARED_EXECUTOR";
    public static final String SINGLE_EXECUTOR = "SINGLE_EXECUTOR";

    @Named(SHARED_EXECUTOR)
    @Provides
    @Singleton
    ExecutorService providesExecutorService(final ExecutorServiceFactory factory) {
        return factory.getExecutorService(Runtime.getRuntime().availableProcessors());
    }

    @Named(SINGLE_EXECUTOR)
    @Provides
    ExecutorService providesSingleExecutorService(final ExecutorServiceFactory factory) {
        return factory.getExecutorService(1);
    }

    @Provides
    @Singleton
    ExecutorServiceFactory providesExecutorServiceFactory() {
        return new ExecutorServiceFactoryImpl();
    }

    @Provides
    @Singleton
    PrimeNumberCalculation providePrimeNumberCalculation(@Named(SHARED_EXECUTOR) final ExecutorService executor,
                                                         final PrimeNumberAggregator primeNumberAggregator) {
        return new PrimeNumberCalculationImpl(executor, primeNumberAggregator);
    }

    @Provides
    @Singleton
    PrimeNumberAggregator providePrimeNumberAggregator(@Named(SINGLE_EXECUTOR) final ExecutorService executorService,
                                                       final PrimeNumberRepository primeNumberRepository) {
        return new PrimeNumberAggregatorImpl(executorService, primeNumberRepository);
    }
}
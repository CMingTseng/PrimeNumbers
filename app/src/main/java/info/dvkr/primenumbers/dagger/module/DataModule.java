package info.dvkr.primenumbers.dagger.module;

import android.content.Context;

import java.util.concurrent.ExecutorService;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import info.dvkr.primenumbers.data.interval.parser.IntervalParser;
import info.dvkr.primenumbers.data.interval.parser.IntervalParserImpl;
import info.dvkr.primenumbers.data.interval.repository.IntervalRepositoryImpl;
import info.dvkr.primenumbers.data.primenumber.repository.PrimeNumberRepositoryImpl;
import info.dvkr.primenumbers.data.primenumber.repository.datasource.NetworkDataStore;
import info.dvkr.primenumbers.domain.repository.IntervalRepository;
import info.dvkr.primenumbers.domain.repository.PrimeNumberRepository;

import static info.dvkr.primenumbers.dagger.module.DomainModule.SINGLE_EXECUTOR;


@Singleton
@Module
public class DataModule {

    @Provides
    @Singleton
    IntervalRepository provideIntervalRepository(final Context context, final IntervalParser parser) {
        return new IntervalRepositoryImpl(context, parser);
    }

    @Provides
    @Singleton
    IntervalParser provideIntervalParser(@Named(SINGLE_EXECUTOR) final ExecutorService executor) {
        return new IntervalParserImpl(executor);
    }

    @Provides
    @Singleton
    PrimeNumberRepository providePrimeNumberRepository(@Named(SINGLE_EXECUTOR) final ExecutorService executor,
                                                       final NetworkDataStore networkDataStore) {
        return new PrimeNumberRepositoryImpl(executor, networkDataStore);
    }

    @Provides
    @Singleton
    NetworkDataStore provideNetworkDataStore(final Context context, // For saving file
                                             @Named(SINGLE_EXECUTOR) final ExecutorService executor) {
        return new NetworkDataStore(context, executor);
    }
}
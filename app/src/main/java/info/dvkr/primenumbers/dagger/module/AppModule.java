package info.dvkr.primenumbers.dagger.module;


import android.app.Application;
import android.content.Context;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import info.dvkr.primenumbers.PrimeNumbersApp;

@Singleton
@Module
public class AppModule {
    private PrimeNumbersApp application;

    public AppModule(final PrimeNumbersApp application) {
        this.application = application;
    }

    @Provides
    @Singleton
    public Context provideContext() {
        return application.getApplicationContext();
    }

    @Provides
    @Singleton
    public Application providesApplication() {
        return application;
    }

}
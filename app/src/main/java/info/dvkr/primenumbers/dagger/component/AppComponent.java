package info.dvkr.primenumbers.dagger.component;


import javax.inject.Singleton;

import dagger.Component;
import info.dvkr.primenumbers.PrimeNumbersApp;
import info.dvkr.primenumbers.background.BackgroundService;
import info.dvkr.primenumbers.dagger.module.AppModule;
import info.dvkr.primenumbers.dagger.module.DataModule;
import info.dvkr.primenumbers.dagger.module.DomainModule;

@Singleton
@Component(modules = {
        AppModule.class,
        DataModule.class,
        DomainModule.class
})

public interface AppComponent {

    NonConfigurationComponent plusNonConfiguration();

    void inject(PrimeNumbersApp application);
}
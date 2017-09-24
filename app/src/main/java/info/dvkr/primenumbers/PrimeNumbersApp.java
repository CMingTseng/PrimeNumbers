package info.dvkr.primenumbers;

import android.app.Application;

import info.dvkr.primenumbers.background.BackgroundService;
import info.dvkr.primenumbers.dagger.component.AppComponent;
import info.dvkr.primenumbers.dagger.component.DaggerAppComponent;
import info.dvkr.primenumbers.dagger.module.AppModule;
import info.dvkr.primenumbers.dagger.module.DataModule;
import info.dvkr.primenumbers.dagger.module.DomainModule;


public class PrimeNumbersApp extends Application {

    protected AppComponent component;

    @Override
    public void onCreate() {
        super.onCreate();

        component = DaggerAppComponent.builder()
                .appModule(new AppModule(this))
                .dataModule(new DataModule())
                .domainModule(new DomainModule())
                .build();

        startService(BackgroundService.getStartIntent(this));
    }

    public AppComponent getAppComponent() {
        return component;
    }
}

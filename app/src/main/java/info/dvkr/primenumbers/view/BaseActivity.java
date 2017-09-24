package info.dvkr.primenumbers.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import info.dvkr.primenumbers.PrimeNumbersApp;
import info.dvkr.primenumbers.dagger.component.NonConfigurationComponent;

public abstract class BaseActivity extends AppCompatActivity {
    private NonConfigurationComponent injector;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        injector = restoreInjector();
        inject(injector);
    }

    public abstract void inject(NonConfigurationComponent injector);

    private NonConfigurationComponent restoreInjector() {
        Object o = getLastCustomNonConfigurationInstance();
        if (o == null) {
            return getApp().getAppComponent().plusNonConfiguration();
        } else {
            return (NonConfigurationComponent) o;
        }
    }

    @Override
    public Object onRetainCustomNonConfigurationInstance() {
        return injector;
    }

    public PrimeNumbersApp getApp() {
        return (PrimeNumbersApp) super.getApplication();
    }
}
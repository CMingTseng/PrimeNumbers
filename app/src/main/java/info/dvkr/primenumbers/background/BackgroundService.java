package info.dvkr.primenumbers.background;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.NonNull;

import javax.inject.Inject;

import info.dvkr.primenumbers.PrimeNumbersApp;
import info.dvkr.primenumbers.domain.executor.ExecutorServiceFactory;
import info.dvkr.primenumbers.domain.interactor.PrimeNumberCalculation;
import info.dvkr.primenumbers.domain.repository.IntervalRepository;

public class BackgroundService extends Service {
    @Inject
    ExecutorServiceFactory executorServiceFactory;

    @Inject
    IntervalRepository intervalRepository;

    @Inject
    PrimeNumberCalculation primeNumberCalculation;

    public static Intent getStartIntent(@NonNull final Context context) {
        return new Intent(context, BackgroundService.class);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        ((PrimeNumbersApp) getApplication()).getAppComponent().plusNonConfiguration().inject(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        primeNumberCalculation.calculatePrimeNumbers(intervalRepository.getIntervals());
        return Service.START_NOT_STICKY;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        executorServiceFactory.shutdownExecutorServices();
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
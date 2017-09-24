package info.dvkr.primenumbers.presenter;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;

import java.util.List;

import javax.inject.Inject;

import info.dvkr.primenumbers.dagger.PersistentScope;
import info.dvkr.primenumbers.domain.model.PrimeNumber;
import info.dvkr.primenumbers.domain.repository.OnPrimeNumbersUpdateCallback;
import info.dvkr.primenumbers.domain.repository.PrimeNumberRepository;
import info.dvkr.primenumbers.view.MainActivity.MainActivityView;

@PersistentScope
public class MainActivityPresenter {

    private final PrimeNumberRepository primeNumberRepository;
    private MainActivityView activityView;
    private OnPrimeNumbersUpdateCallback updateCallback;

    // Wish you allow RxJava
    private Handler mainHandler = new Handler(Looper.getMainLooper());

    @Inject
    public MainActivityPresenter(final PrimeNumberRepository primeNumberRepository) {
        this.primeNumberRepository = primeNumberRepository;
    }

    public void attach(MainActivityView view) {
        if (activityView != null) {
            detach();
        }
        activityView = view;

        updateCallback = new OnPrimeNumbersUpdateCallback() {
            @Override
            public void onPrimeNumbersUpdate(@NonNull final List<PrimeNumber> primeNumbers) {
                if (activityView != null) {
                    mainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            activityView.showPrimeNumberList(primeNumbers);
                        }
                    });
                }
            }

            @Override
            public void onError(@NonNull final Exception exception) {
                if (activityView != null) {
                    mainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            activityView.showError(exception.getMessage());
                        }
                    });
                }
            }
        };

        primeNumberRepository.addOnUpdateCallback(updateCallback);

        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                activityView.showPrimeNumberList(primeNumberRepository.getPrimeNumbers());
            }
        });
    }

    public void detach() {
        primeNumberRepository.removeOnUpdateCallback(updateCallback);
        activityView = null;
    }
}
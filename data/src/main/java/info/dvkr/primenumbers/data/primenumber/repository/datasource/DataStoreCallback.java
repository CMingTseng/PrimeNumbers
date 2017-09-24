package info.dvkr.primenumbers.data.primenumber.repository.datasource;


import android.support.annotation.NonNull;

public interface DataStoreCallback {
    void onDataStoreUpdate();
    void onNetworkError(@NonNull Exception exception);
}

package info.dvkr.primenumbers.view.MainActivity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import info.dvkr.primenumbers.R;
import info.dvkr.primenumbers.dagger.component.NonConfigurationComponent;
import info.dvkr.primenumbers.domain.model.PrimeNumber;
import info.dvkr.primenumbers.presenter.MainActivityPresenter;
import info.dvkr.primenumbers.view.BaseActivity;

public class MainActivity extends BaseActivity implements MainActivityView {

    private RecyclerView recyclerView;
    private MainActivityRecyclerViewAdapter adapter;
    @Inject
    MainActivityPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = findViewById(R.id.mainActivity_recyclerView);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MainActivityRecyclerViewAdapter(getApplicationContext(), new ArrayList<PrimeNumber>());
        recyclerView.setAdapter(adapter);

        presenter.attach(this);
    }

    @Override
    public void inject(final NonConfigurationComponent injector) {
        injector.inject(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.detach();
    }

    @Override
    public void showPrimeNumberList(final List<PrimeNumber> primeNumbers) {
        adapter.updateData(primeNumbers);
    }

    @Override
    public void showError(final String message) {
        Log.w("MainActivity", message);
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }
}
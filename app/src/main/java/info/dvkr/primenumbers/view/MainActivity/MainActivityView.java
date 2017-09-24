package info.dvkr.primenumbers.view.MainActivity;


import java.util.List;

import info.dvkr.primenumbers.domain.model.PrimeNumber;

public interface MainActivityView {

    void showPrimeNumberList(List<PrimeNumber> primeNumbers);

    void showError(String message);
}

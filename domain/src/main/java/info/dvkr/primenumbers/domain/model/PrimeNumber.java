package info.dvkr.primenumbers.domain.model;


public class PrimeNumber {
    private final int intervalId;
    private final int primeNumber;

    public PrimeNumber(final int intervalId, final int primeNumber) {
        this.intervalId = intervalId;
        this.primeNumber = primeNumber;
    }

    public int getIntervalId() {
        return intervalId;
    }

    public int getPrimeNumber() {
        return primeNumber;
    }

    @Override
    public String toString() {
        return "PN[ID:" + intervalId + " N:" + primeNumber + ']';
    }
}

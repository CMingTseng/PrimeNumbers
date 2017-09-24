package info.dvkr.primenumbers.domain.model;


public class Interval {
    private final int id;
    private final int low;
    private final int high;

    public Interval(final int id, final int low, final int high) {
        this.id = id;
        this.low = low;
        this.high = high;
    }

    public int getId() {
        return id;
    }

    public int getLow() {
        return low;
    }

    public int getHigh() {
        return high;
    }
}

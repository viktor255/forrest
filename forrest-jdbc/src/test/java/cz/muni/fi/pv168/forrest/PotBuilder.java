package cz.muni.fi.pv168.forrest;

/**
 * @author Viktor Lehotsky on 15.03.2017.
 */
public class PotBuilder {

    private Long id;
    private int row;
    private int column;
    private int capacity = 1;
    private String note;

    public PotBuilder id(Long id) {
        this.id = id;
        return this;
    }

    public PotBuilder column(int column) {
        this.column = column;
        return this;
    }

    public PotBuilder row(int row) {
        this.row = row;
        return this;
    }

    public PotBuilder capacity(int capacity) {
        this.capacity = capacity;
        return this;
    }

    public PotBuilder note(String note) {
        this.note = note;
        return this;
    }

    public Pot build() {
        return new Pot(id, column, row, capacity, note);
    }
}

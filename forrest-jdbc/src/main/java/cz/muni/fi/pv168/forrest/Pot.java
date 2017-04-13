package cz.muni.fi.pv168.forrest;

/**
 * @author Viktor Lehotsky on 08.03.2017.
 */
public class Pot {
    private Long potId;
    private int row;
    private int column;
    private int capacity;
    private String note;

    public Pot(Long potId, int row, int column, int capacity, String note) {
        this.potId = potId;
        this.row = row;
        this.column = column;
        this.capacity = capacity;
        this.note = note;
    }

    public Long getId() {
        return potId;
    }

    public void setId(Long potId) {
        this.potId = potId;
    }

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public int getColumn() {
        return column;
    }

    public void setColumn(int column) {
        this.column = column;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}

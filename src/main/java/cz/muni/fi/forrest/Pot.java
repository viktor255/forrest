package cz.muni.fi.forrest;

/**
 * @author Viktor Lehotsky on 08.03.2017.
 */
public class Pot {
    private Long potId;
    private int row;
    private int column;
    private int capacity;
    private String note;

    public Long getId() {
        return potId;
    }

    public void setId(Long potIdd) {
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

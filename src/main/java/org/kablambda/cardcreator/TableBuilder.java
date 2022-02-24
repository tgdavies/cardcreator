package org.kablambda.cardcreator;

import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Table;

public class TableBuilder implements AutoCloseable {
    private final Table table;
    private final Document document;
    private int cellCount = 0;

    public TableBuilder(Document document, int numRows, int numColumns) {
        this.document = document;
        table = new Table(numColumns);
    }

    public void addCell(Cell cell) {
        ++cellCount;
        table.addCell(cell);
    }

    @Override
    public void close() throws Exception {
        document.add(table);
        document.close();
    }
}

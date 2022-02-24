package org.kablambda.cardcreator;

import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.colorspace.PdfColorSpace;
import com.itextpdf.kernel.pdf.colorspace.PdfDeviceCs;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.DottedBorder;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.ListItem;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.properties.ListNumberingType;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.VerticalAlignment;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import java.io.FileReader;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Main {
    private static final int COLUMNS = 3;
    private static final int ROWS = 3;
    private static final int CARD_WIDTH_MM = 63;
    private static final int CARD_HEIGHT_MM = 88;
    private static final int CELL_COUNT = ROWS * COLUMNS;

    public static void main(String[] args) throws Exception {

        try (FileReader reader = new FileReader("/tmp/Questions and Answers - Sheet1.csv");
             TableBuilder tableBuilder = new TableBuilder(new Document(new PdfDocument(new PdfWriter("/tmp/foo.pdf"))), ROWS, COLUMNS)) {
            Iterable<CSVRecord> records = CSVFormat.DEFAULT
                    .parse(reader);
            for (CSVRecord q : records) {
                if (q.size() != 6) {
                    throw new RuntimeException("Invalid line: " + q.toString());
                }
                int correctIndex = Integer.parseInt(q.get(5));
                tableBuilder.addCell(createQCell(q.get(0)));
                tableBuilder.addCell(createACell(
                        IntStream.range(1, 5).mapToObj(i -> q.get(i)).collect(Collectors.toList()),
                        correctIndex
                ));
            }
        }
    }


    private static Cell createQCell(String question) {
        Cell c = blankCell();
        c.add(new Paragraph(question)
                .setMargin(mmToPoints(5))
                .setBold()
                .setTextAlignment(TextAlignment.CENTER)
        );
        return c;
    }

    private static Cell blankCell() {
        Cell cell = new Cell();
        cell.setHeight(mmToPoints(CARD_HEIGHT_MM));
        cell.setWidth(mmToPoints(CARD_WIDTH_MM));
        cell.setBorder(new DottedBorder(Color.makeColor(new PdfDeviceCs.Gray(), new float[] {0.5f}), 0.5f));
        cell.setHeight(mmToPoints(CARD_HEIGHT_MM));
        cell.setVerticalAlignment(VerticalAlignment.MIDDLE);
        return cell;
    }

    private static Cell createACell(List<String> answers, int correctIndex) {
        Cell c = blankCell();
        com.itextpdf.layout.element.List list = new com.itextpdf.layout.element.List(ListNumberingType.ENGLISH_UPPER);
        int i = 0;
        for (String s : answers) {
            i++;
            ListItem item = new ListItem(s);
            if (correctIndex == i) {
                item.setBold();
            }
            list.add(item);
        }
        c.add(list
                .setMargin(mmToPoints(5))
                .setTextAlignment(TextAlignment.LEFT)
        );
        return c;
    }

    private static float mmToPoints(float mm) {
        return mm * 2.83465f;
    }
}

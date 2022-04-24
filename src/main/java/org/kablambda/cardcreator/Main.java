package org.kablambda.cardcreator;

import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.colorspace.PdfDeviceCs;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.DottedBorder;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.ListItem;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.properties.ListNumberingType;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.VerticalAlignment;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import java.io.FileReader;
import java.util.List;
import java.util.stream.StreamSupport;

import static java.util.stream.Collectors.toList;
import static java.util.stream.IntStream.range;

public class Main {
    private static final int COLUMNS = 3;
    private static final int ROWS = 3;
    private static final int CARD_WIDTH_MM = 63;
    private static final double CARD_HEIGHT_MM = 87.5;

    public static void main(String[] args) throws Exception {

        try (FileReader reader = new FileReader("/tmp/Questions and Answers - Sheet1.csv")) {
            Iterable<CSVRecord> records = CSVFormat.DEFAULT
                    .parse(reader);
            createCards(StreamSupport.stream(records.spliterator(), false)
                    .map(r -> new Card(r.get(0), range(1, 5).mapToObj(r::get).collect(toList()), Integer.parseInt(r.get(5))))
                    .collect(toList()));
        }
    }

    public static void createCards(List<Card> cards) throws Exception {
        try (TableBuilder tableBuilder = new TableBuilder(new Document(new PdfDocument(
                new PdfWriter("/tmp/foo.pdf"))), ROWS, COLUMNS)) {
            for (Card c : cards) {
                tableBuilder.addCell(createQCell(c));
                tableBuilder.addCell(createACell(c));
            }
        }
    }


    private static Cell createQCell(Card card) {
        Cell c = blankCell();
        c.add(new Paragraph(card.question())
                .setMargin(mmToPoints(5))
                .setBold()
                .setTextAlignment(TextAlignment.CENTER)
        );
        com.itextpdf.layout.element.List list = new com.itextpdf.layout.element.List(ListNumberingType.ENGLISH_UPPER);
        int i = 0;
        for (String s : card.answers()) {
            if (!s.isBlank()) {
                i++;
                ListItem item = new ListItem(s);
//                if (card.correctIndex() == i) {
//                    item.setBold();
//                }
                list.add(item);
            }
        }
        c.add(list
                .setMargin(mmToPoints(5))
                .setTextAlignment(TextAlignment.LEFT)
        );
        return c;
    }

    private static Cell blankCell() {
        Cell cell = new Cell();
        cell.setHeight(mmToPoints(CARD_HEIGHT_MM));
        cell.setWidth(mmToPoints(CARD_WIDTH_MM));
        cell.setBorder(new DottedBorder(Color.makeColor(new PdfDeviceCs.Gray(), new float[]{0.5f}), 0.5f));
        cell.setHeight(mmToPoints(CARD_HEIGHT_MM));
        cell.setVerticalAlignment(VerticalAlignment.MIDDLE);
        return cell;
    }

    private static Cell createACell(Card card) {
        Cell c = blankCell();
        c.add(new Paragraph(new Text(Character.toString('A' + card.correctIndex() - 1)))
                .setTextAlignment(TextAlignment.CENTER)
                .setFontSize(24.0f)
        );
        c.add(new Paragraph(card.answers().get(card.correctIndex() - 1))
                .setTextAlignment(TextAlignment.CENTER)
                .setItalic()
        );
        return c;
    }

    private static float mmToPoints(double mm) {
        return (float) (mm * 2.83465);
    }
}

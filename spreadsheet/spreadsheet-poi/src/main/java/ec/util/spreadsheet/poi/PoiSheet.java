/*
 * Copyright 2013 National Bank of Belgium
 *
 * Licensed under the EUPL, Version 1.1 or – as soon they will be approved 
 * by the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 *
 * http://ec.europa.eu/idabc/eupl
 *
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and 
 * limitations under the Licence.
 */
package ec.util.spreadsheet.poi;

import java.util.Iterator;
import javax.annotation.Nonnull;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

/**
 *
 * @author Philippe Charles
 */
final class PoiSheet extends ec.util.spreadsheet.Sheet {

    private final Sheet sheet;
    private final PoiCell flyweightCell;
    private final int rowCount;
    private final int columnCount;

    public PoiSheet(@Nonnull Sheet sheet) {
        this.sheet = sheet;
        this.flyweightCell = new PoiCell();
        int maxRow = 0;
        int maxColumn = 0;
        Iterator<Row> rowIterator = sheet.rowIterator();
        while (rowIterator.hasNext()) {
            Row row = rowIterator.next();
            maxRow = row.getRowNum() + 1;
            short lastCellNum = row.getLastCellNum();
            if (lastCellNum > maxColumn) {
                maxColumn = lastCellNum;
            }
        }
        this.rowCount = maxRow;
        this.columnCount = maxColumn;
    }

    @Override
    public int getRowCount() {
        return rowCount;
    }

    @Override
    public int getColumnCount() {
        return columnCount;
    }

    @Override
    public ec.util.spreadsheet.Cell getCell(int rowIdx, int columnIdx) {
        Row row = sheet.getRow(rowIdx);
        if (row == null) {
            return null;
        }
        Cell cell = row.getCell(columnIdx);
        return cell != null ? flyweightCell.withCell(cell) : null;
    }

    @Override
    public String getName() {
        return sheet.getSheetName();
    }
}

package cz.upce.webapp.utils.xlsprocessors;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import cz.upce.webapp.dao.stock.model.Item;
import cz.upce.webapp.dao.stock.model.Supplier;
import cz.upce.webapp.dao.stock.repository.ItemRepository;
import cz.upce.webapp.dao.stock.repository.SupplierRepository;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author Tomas Kodym
 */

@Component
public class NutSheetProcessor implements ISheetProcessor
{
    private static final Logger LOGGER = LoggerFactory.getLogger(NutSheetProcessor.class);
    private static final List<Integer> OMITTED_ROW_INDEXES = Arrays.asList(0, 1, 2, 3, 4);
    private static final List<String> OMITTED_COLUMN_HEADERS = Arrays.asList("název zboží", "balení kg", "Kč/kg", "+DPH %");
    private static final int SUPPLIER_ID = 1;

    @Autowired
    private SupplierRepository supplierRepository;

    @Autowired
    private ItemRepository itemRepository;

    private static boolean isRowOmitted(int rowNumber)
    {
        return OMITTED_ROW_INDEXES.contains(rowNumber);
    }


    @Override
    public void iterateSheetValues(FormulaEvaluator formulaEvaluator, Iterator<Row> rowIterator, int maxRow)
    {
        LOGGER.info("Started parsing the values from the file with:" + this.getClass().getName());
        StringBuilder sheetData = new StringBuilder();
        Row row;
        //Iterate through all rows
        while (rowIterator.hasNext())
        {
            row = rowIterator.next();
            if (isRowEmpty(row) || isRowOmitted(row.getRowNum()))
                continue;

            List<String> rowData = new ArrayList<>();
            parseRow(row, formulaEvaluator, rowData, maxRow);
            sheetData.append(String.join(DELIMITER, rowData));

            List<Item> itemList = disintegrateIntoItemNut(sheetData.toString());
            if (!itemList.isEmpty())
            {
                for (Item item : itemList)
                {
                    //save object to the database
                    item.setItemQuantity(countKilosToGrams(item.getItemQuantity()));
                    item.setItemPrice(countValueForOneGram(item.getItemPrice(), item.getItemQuantity()));

                    persistLoadedObject(item, sheetData, itemRepository);
                }
            }
        }
    }

    private void parseRow(Row row, FormulaEvaluator formulaEvaluator, List<String> rowData, int maxRow)
    {
        Cell cell;
        for (int i = 0; i < maxRow; i++)
        {
            cell = row.getCell(i);
            switch (cell.getCellType())
            {
                case Cell.CELL_TYPE_NUMERIC:
                    rowData.add(String.valueOf(cell.getNumericCellValue()).replaceFirst("\\.0+$", EMPTY_SPACE));
                    break;
                case Cell.CELL_TYPE_STRING:
                    if (validateCellValue(cell))
                        break;

                    rowData.add(cell.getStringCellValue());
                    break;
                case Cell.CELL_TYPE_BLANK:
                    rowData.add(EMPTY_SPACE);
                    break;
                case Cell.CELL_TYPE_FORMULA:
                    rowData.add(formulaEvaluator.evaluate(cell).formatAsString().replaceFirst("\\.0+$", EMPTY_SPACE));
                    break;
                default:
                    rowData.add(String.valueOf(cell));
            }
        }
    }

    private List<Item> disintegrateIntoItemNut(String string)
    {
        if (!string.isEmpty())
        {
            List<Item> itemsList = new ArrayList<>();
            //split values from list to array
            String[] values = Arrays
                    .stream(string.split(DELIMITER))
                    .map(String::trim)
                    .toArray(String[]::new);

            //for two columns in excel we need to parse both
            Supplier supplier = supplierRepository.getOne(SUPPLIER_ID);
            if (values.length >= 3)
            {
                if (StringUtils.isNumeric(values[1]))
                    itemsList.add(new Item(values[0], Double.parseDouble(values[1]), Double.parseDouble(values[2]), Integer.parseInt(values[3]), supplier));
                else
                    LOGGER.warn("Item was not created, because of non numeric value in quantity column!");
            }
            if (values.length >= 9)
            {
                if (StringUtils.isNumeric(values[6]))
                    itemsList.add(new Item(values[5], Double.parseDouble(values[6]), Double.parseDouble(values[7]), Integer.parseInt(values[8]), supplier));
                else
                    LOGGER.warn("Item was not created, because of non numeric value in quantity column!");
            }

            return itemsList;
        }
        else
        {
            return new ArrayList<>();
        }
    }

    private boolean validateCellValue(Cell cell)
    {
        if (OMITTED_COLUMN_HEADERS.contains(cell.getStringCellValue()))
        {
            LOGGER.warn("Row which contains information about column headers, was skipped!");
            return true;
        }

        return false;
    }

    private double countKilosToGrams(Double kilos)
    {
        return kilos * 1000;
    }
}

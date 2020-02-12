package cz.upce.webapp.utils.xlsprocessors


import cz.upce.webapp.dao.stock.model.Supplier
import cz.upce.webapp.dao.stock.repository.SupplierRepository

class MKMPackSheetProcessorTest extends AbstractSheetProcessorTest {

    SupplierRepository supplierRepo = Mock()

    @Override
    protected String getPricelistResourcePath() {
        return  "/mkm/MKM_pack_B_cenik_SUROVINY_unor_2020.xlsx"
    }

    def "IterateSheetValues"() {
        def f = getClass().getResource(getPricelistResourcePath()).getFile()
        supplierRepo.getOne(_) >> new Supplier()

        when:
        def items = new MkmPackSheetProcessor(supplierRepository: supplierRepo).parseItemsAsMap(new File(f))
        then:

        items.size() > 0
        def amarantZrno = items["Amarant zrno (OBILOVINY)_5000"]
        amarantZrno.itemQuantity == 5000
        amarantZrno.itemTax == 15
        amarantZrno.itemPrice == 0.0451

        def spalda = items["Špalda pufovaná (PUFOVANÉ)_1000"]
        spalda.itemQuantity == 1000
        spalda.itemTax == 15
        spalda.itemPrice == 0.0359



    }

    def "Make Order"() {
        given:
        def processor = new NutSheetProcessor(supplierRepository: supplierRepo)
        def workbook = processor
        def sheetRead = fillWriteAndReadSheet(processor)
        def sheetWithOrder = sheetRead.getWorkbook().getSheet("Objednávka")

        expect:
        sheetWithOrder.getRow(3).getCell(5).getNumericCellValue() == 3
        sheetWithOrder.getRow(46).getCell(5).getNumericCellValue() == 1

    }
}

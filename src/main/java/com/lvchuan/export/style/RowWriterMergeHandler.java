package com.lvchuan.export.style;

import com.alibaba.excel.write.handler.RowWriteHandler;
import com.alibaba.excel.write.metadata.holder.WriteSheetHolder;
import com.alibaba.excel.write.metadata.holder.WriteTableHolder;
import com.lvchuan.common.excel.ExcelMergeField;
import com.lvchuan.common.excel.ExcelMergeFieldId;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @description: 行合并
 * @author: lvchuan
 * @createTime: 2024-05-14 14:46
 */
public class RowWriterMergeHandler implements RowWriteHandler {
    /**
     * 需要合并的列，从0开始
     */
    private List<Integer> mergeColList;

    /**
     * 以哪一列的数据为合并基础
     */
    private Integer mergeColBase;

    /**
     * 需要合并的行数
     */
    private Integer count = 0;
    /**
     * 最后一行
     */
    private int lastRow;

    /**
     * 构造处理器
     * @param clazz 模板class
     * @param lastRow
     */
    public RowWriterMergeHandler(Class clazz, int lastRow) {
        if (clazz == null) {
            throw new RuntimeException("模板类型不能为空");
        }
        List<Integer> mergeColList = new ArrayList<>();
        int idx = 0;
        for (Field field : clazz.getDeclaredFields()) {
            boolean mergeField = field.isAnnotationPresent(ExcelMergeField.class);
            boolean mergeFieldId = field.isAnnotationPresent(ExcelMergeFieldId.class);
            if (mergeField || mergeFieldId) {
                mergeColList.add(idx);
            }
            if (mergeFieldId) {
                this.mergeColBase = idx;
            }
            idx ++;
        }
        this.mergeColList = mergeColList;
        this.lastRow = lastRow;
    }

    @Override
    public void beforeRowCreate(WriteSheetHolder writeSheetHolder, WriteTableHolder writeTableHolder, Integer rowIndex, Integer relativeRowIndex, Boolean isHead) {

    }

    @Override
    public void afterRowCreate(WriteSheetHolder writeSheetHolder, WriteTableHolder writeTableHolder, Row row, Integer relativeRowIndex, Boolean isHead) {

    }

    @Override
    public void afterRowDispose(WriteSheetHolder writeSheetHolder, WriteTableHolder writeTableHolder, Row row, Integer relativeRowIndex, Boolean isHead) {
        //当前行索引
        int curRowNum = row.getRowNum();
        if (isHead || curRowNum <= 1 || Objects.isNull(this.mergeColBase)) {
            return;
        }
        Cell curA1Cell = row.getCell(this.mergeColBase);
        Object curA1Data = curA1Cell.getCellTypeEnum() == CellType.STRING ? curA1Cell.getStringCellValue() : curA1Cell.getNumericCellValue();
        //上一行第一列单元格
        Cell preA1Cell = row.getSheet().getRow(curRowNum - 1).getCell(mergeColBase);
        Object preA1Data = preA1Cell.getCellTypeEnum() == CellType.STRING ? preA1Cell.getStringCellValue() : preA1Cell.getNumericCellValue();

        if (curA1Data != preA1Data) {
            if (count > 0) {
                this.addCellRangeAddress(writeSheetHolder, curRowNum - count - 1, curRowNum - 1);
                count = 0;
            }
        } else {
            count ++;
            if (curRowNum == lastRow && count > 0) {
                //如果是最后一行
                this.addCellRangeAddress(writeSheetHolder, curRowNum - count, curRowNum);
            }
        }
    }

    /**
     * 设置合并单元格
     * @param writeSheetHolder
     * @param mergeStartRow
     * @param mergeEndRow
     */
    private void addCellRangeAddress(WriteSheetHolder writeSheetHolder, int mergeStartRow, int mergeEndRow) {
        Sheet sheet = writeSheetHolder.getSheet();
        for (Integer col : mergeColList) {
            sheet.addMergedRegion(new CellRangeAddress(mergeStartRow, mergeEndRow, col, col));
        }
    }
}

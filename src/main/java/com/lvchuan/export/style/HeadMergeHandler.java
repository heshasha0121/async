package com.lvchuan.export.style;

import com.alibaba.excel.write.handler.RowWriteHandler;
import com.alibaba.excel.write.metadata.holder.WriteSheetHolder;
import com.alibaba.excel.write.metadata.holder.WriteTableHolder;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;

/**
 * @description: 表头合并
 * @author: lvchuan
 * @createTime: 2024-05-14 14:46
 */
public class HeadMergeHandler implements RowWriteHandler {
    /**
     * 合并开始列
     */
    private int mergeStartCol;

    /**
     * 合并结束列
     */
    private int mergeEndCol;

    public HeadMergeHandler(int mergeStartCol, int mergeEndCol) {
        this.mergeStartCol = mergeStartCol;
        this.mergeEndCol = mergeEndCol;
    }

    @Override
    public void beforeRowCreate(WriteSheetHolder writeSheetHolder, WriteTableHolder writeTableHolder, Integer rowIndex, Integer relativeRowIndex, Boolean isHead) {

    }

    @Override
    public void afterRowCreate(WriteSheetHolder writeSheetHolder, WriteTableHolder writeTableHolder, Row row, Integer relativeRowIndex, Boolean isHead) {

    }

    @Override
    public void afterRowDispose(WriteSheetHolder writeSheetHolder, WriteTableHolder writeTableHolder, Row row, Integer relativeRowIndex, Boolean isHead) {
        if (isHead) {
            Sheet sheet = writeSheetHolder.getSheet();
            sheet.addMergedRegion(new CellRangeAddress(0, 0, mergeStartCol, mergeEndCol));
        }
    }
}

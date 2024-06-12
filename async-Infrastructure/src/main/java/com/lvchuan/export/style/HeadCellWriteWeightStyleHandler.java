package com.lvchuan.export.style;

import com.alibaba.excel.metadata.CellData;
import com.alibaba.excel.metadata.Head;
import com.alibaba.excel.write.metadata.holder.WriteSheetHolder;
import com.alibaba.excel.write.style.column.AbstractColumnWidthStyleStrategy;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @description: 文件头自适应宽度
 * @author: lvchuan
 * @createTime: 2023-12-08 16:45
 */
public class HeadCellWriteWeightStyleHandler extends AbstractColumnWidthStyleStrategy {
    private Map<Integer, Map<Integer, Integer>> cache = new HashMap<>();

    @Override
    protected void setColumnWidth(WriteSheetHolder writeSheetHolder, List<CellData> cellDataList, Cell cell, Head head, Integer integer, Boolean isHead) {
        if (BooleanUtils.isFalse(isHead)) {
            return;
        }
        Map<Integer, Integer> maxColumnWidthMap = cache.get(writeSheetHolder.getSheetNo());
        if (maxColumnWidthMap == null) {
            maxColumnWidthMap = new HashMap<>();
            cache.put(writeSheetHolder.getSheetNo(), maxColumnWidthMap);
        }

        Integer columnWidth = cell.getStringCellValue().getBytes().length;
        if (columnWidth >= 0) {
            if (columnWidth > 254) {
                columnWidth = 254;
            }
            Integer maxColumnWidth = maxColumnWidthMap.get(cell.getColumnIndex());
            if (maxColumnWidth == null || columnWidth > maxColumnWidth) {
                maxColumnWidthMap.put(cell.getColumnIndex(), columnWidth);
                Sheet sheet = writeSheetHolder.getSheet();
                sheet.setColumnWidth(cell.getColumnIndex(), columnWidth * 2 * 256);
            }

        }
    }
}

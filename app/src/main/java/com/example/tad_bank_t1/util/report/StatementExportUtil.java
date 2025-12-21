package com.example.tad_bank_t1.util.report;

import android.content.Context;

import com.example.tad_bank_t1.data.model.Transaction;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class StatementExportUtil {
    private static final SimpleDateFormat DF =
            new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

    // ==========================
    // EXCEL EXPORT
    // ==========================
    public static File exportCheckingExcelToCache(
            Context context,
            String uid,
            String accountNumber,
            Date from,
            Date to,
            List<Transaction> transactions
    ) throws Exception {

        XSSFWorkbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Checking Statement");

        // ===== Header style =====
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);

        CellStyle headerStyle = workbook.createCellStyle();
        headerStyle.setFont(headerFont);

        int rowIndex = 0;

        // ===== Title =====
        Row titleRow = sheet.createRow(rowIndex++);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue("SAO KÊ TÀI KHOẢN CHECKING");
        titleCell.setCellStyle(headerStyle);

        // ===== Info =====
        rowIndex++;
        Row info1 = sheet.createRow(rowIndex++);
        info1.createCell(0).setCellValue("UID:");
        info1.createCell(1).setCellValue(uid);

        Row info2 = sheet.createRow(rowIndex++);
        info2.createCell(0).setCellValue("Account:");
        info2.createCell(1).setCellValue(maskAccount(accountNumber));

        Row info3 = sheet.createRow(rowIndex++);
        info3.createCell(0).setCellValue("Từ ngày:");
        info3.createCell(1).setCellValue(DF.format(from));

        Row info4 = sheet.createRow(rowIndex++);
        info4.createCell(0).setCellValue("Đến ngày:");
        info4.createCell(1).setCellValue(DF.format(to));

        rowIndex++;

        // ===== Table header =====
        Row headerRow = sheet.createRow(rowIndex++);
        String[] headers = {
                "Ngày",
                "Mã giao dịch",
                "Loại",
                "Nội dung",
                "Số tiền",
                "Số dư sau GD"
        };

        for (int i = 0; i < headers.length; i++) {
            Cell c = headerRow.createCell(i);
            c.setCellValue(headers[i]);
            c.setCellStyle(headerStyle);
        }

        // ===== Data rows =====
        for (Transaction t : transactions) {
            Row r = sheet.createRow(rowIndex++);

            r.createCell(0).setCellValue(
                    t.getCreatedAt() != null
                            ? DF.format(t.getCreatedAt())
                            : ""
            );
            r.createCell(1).setCellValue(safe(t.getTransactionId()));
            r.createCell(2).setCellValue(safe(t.getType()));
            r.createCell(3).setCellValue(safe(t.getDescription()));
            r.createCell(4).setCellValue(t.getAmount());
            r.createCell(5).setCellValue(t.getBalanceAfter());
        }

        // Auto size
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }

        // ===== Write file =====
        File outFile = new File(
                context.getCacheDir(),
                buildFileName("checking_statement", accountNumber, "xlsx")
        );

        FileOutputStream fos = new FileOutputStream(outFile);
        workbook.write(fos);
        fos.close();
        workbook.close();

        return outFile;
    }

    // ==========================
    // PDF EXPORT
    // ==========================
    public static File exportCheckingPdfToCache(
            Context context,
            String uid,
            String accountNumber,
            Date from,
            Date to,
            List<Transaction> transactions
    ) throws Exception {

        PdfDocument pdf = new PdfDocument();
        Paint paint = new Paint();
        Paint bold = new Paint();
        bold.setTextSize(14);
        bold.setFakeBoldText(true);

        paint.setTextSize(11);

        int pageWidth = 595;
        int pageHeight = 842;
        int y = 40;

        PdfDocument.Page page = pdf.startPage(
                new PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 1).create()
        );
        Canvas canvas = page.getCanvas();

        // ===== Title =====
        canvas.drawText("SAO KÊ TÀI KHOẢN CHECKING", 40, y, bold);
        y += 30;

        canvas.drawText("UID: " + uid, 40, y, paint); y += 18;
        canvas.drawText("Account: " + maskAccount(accountNumber), 40, y, paint); y += 18;
        canvas.drawText("Từ ngày: " + DF.format(from), 40, y, paint); y += 18;
        canvas.drawText("Đến ngày: " + DF.format(to), 40, y, paint); y += 25;

        // ===== Table header =====
        bold.setTextSize(11);
        canvas.drawText("Ngày", 40, y, bold);
        canvas.drawText("Mã GD", 90, y, bold);
        canvas.drawText("Loại", 160, y, bold);
        canvas.drawText("Nội dung", 220, y, bold);
        canvas.drawText("Số tiền", 420, y, bold);
        canvas.drawText("Số dư", 500, y, bold);
        y += 15;

        // ===== Rows =====
        for (Transaction t : transactions) {
            if (y > pageHeight - 40) {
                pdf.finishPage(page);
                page = pdf.startPage(
                        new PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pdf.getPages().size() + 1).create()
                );
                canvas = page.getCanvas();
                y = 40;
            }

            canvas.drawText(
                    t.getCreatedAt() != null ? DF.format(t.getCreatedAt()) : "",
                    40, y, paint
            );
            canvas.drawText(safe(t.getTransactionId()), 90, y, paint);
            canvas.drawText(safe(t.getType()), 160, y, paint);
            canvas.drawText(shortText(safe(t.getDescription()), 30), 220, y, paint);
            canvas.drawText(String.valueOf(t.getAmount()), 420, y, paint);
            canvas.drawText(String.valueOf(t.getBalanceAfter()), 500, y, paint);
            y += 14;
        }

        pdf.finishPage(page);

        File outFile = new File(
                context.getCacheDir(),
                buildFileName("checking_statement", accountNumber, "pdf")
        );

        FileOutputStream fos = new FileOutputStream(outFile);
        pdf.writeTo(fos);
        fos.close();
        pdf.close();

        return outFile;
    }

    // ==========================
    // HELPERS
    // ==========================
    private static String buildFileName(String prefix, String accNum, String ext) {
        String time = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
                .format(new Date());
        return prefix + "_" + maskAccount(accNum) + "_" + time + "." + ext;
    }

    private static String maskAccount(String accNum) {
        if (accNum == null) return "XXXX";
        String d = accNum.replaceAll("\\s+", "");
        if (d.length() <= 4) return "****" + d;
        return "****" + d.substring(d.length() - 4);
    }

    private static String safe(String s) {
        return s == null ? "" : s;
    }

    private static String shortText(String s, int max) {
        if (s == null) return "";
        if (s.length() <= max) return s;
        return s.substring(0, max - 3) + "...";
    }
}

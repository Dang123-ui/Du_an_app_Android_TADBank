package com.example.tad_bank_t1.util.report;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;

import com.example.tad_bank_t1.R;
import com.example.tad_bank_t1.data.model.Transaction;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class StatementExportUtil {
    private static final SimpleDateFormat DF = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
    private static final SimpleDateFormat DF_SHORT = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
    private static final NumberFormat NF = NumberFormat.getInstance(new Locale("vi", "VN"));

    // ==========================
    // EXCEL EXPORT (.xlsx)
    // ==========================
    public static File exportCheckingExcelToCache(
            Context context,
            String uid,
            String accountNumber,
            Date from,
            Date to,
            List<Transaction> transactions
    ) throws Exception {

        XSSFWorkbook wb = new XSSFWorkbook();
        Sheet sheet = wb.createSheet("Checking Statement");

        // ---- styles
        CellStyle titleStyle = createTitleStyle(wb);
        CellStyle labelStyle = createLabelStyle(wb);
        CellStyle headerStyle = createHeaderStyle(wb);
        CellStyle textStyle = createTextStyle(wb);
        CellStyle moneyStyle = createMoneyStyle(wb);
        CellStyle dateStyle = createDateStyle(wb);

        int r = 0;

        // Title
        Row rowTitle = sheet.createRow(r++);
        Cell cTitle = rowTitle.createCell(0);
        cTitle.setCellValue("TAD bank - SAO KÊ TÀI KHOẢN CHECKING");
        cTitle.setCellStyle(titleStyle);

        // blank
        r++;

        // Info block
        r = writeInfoRow(sheet, r, "UID", uid, labelStyle, textStyle);
        r = writeInfoRow(sheet, r, "Tài khoản", maskAccount(accountNumber), labelStyle, textStyle);
        r = writeInfoRow(sheet, r, "Từ ngày", DF_SHORT.format(from), labelStyle, textStyle);
        r = writeInfoRow(sheet, r, "Đến ngày", DF_SHORT.format(to), labelStyle, textStyle);
        r++;

        // Table header
        Row header = sheet.createRow(r++);
        String[] headers = new String[]{
                "Thời gian",
                "Mã giao dịch",
                "Loại",
                "Trạng thái",
                "Kênh",
                "Nội dung",
                "Đối tác",
                "Số tiền",
                "Phí",
                "Tiền tệ"
        };

        for (int i = 0; i < headers.length; i++) {
            Cell hc = header.createCell(i);
            hc.setCellValue(headers[i]);
            hc.setCellStyle(headerStyle);
        }

        // Data rows
        if (transactions != null) {
            for (Transaction t : transactions) {
                Row rr = sheet.createRow(r++);

                // createdAt
                Cell c0 = rr.createCell(0);
                Date created = t.getCreatedAt();
                if (created != null) {
                    c0.setCellValue(created);
                    c0.setCellStyle(dateStyle);
                } else {
                    c0.setCellValue("");
                    c0.setCellStyle(textStyle);
                }

                // transactionId
                Cell c1 = rr.createCell(1);
                c1.setCellValue(safe(t.getTransactionId()));
                c1.setCellStyle(textStyle);

                // type/status/channel (enum)
                Cell c2 = rr.createCell(2);
                c2.setCellValue(enumText(t.getType()));
                c2.setCellStyle(textStyle);

                Cell c3 = rr.createCell(3);
                c3.setCellValue(enumText(t.getStatus()));
                c3.setCellStyle(textStyle);

                Cell c4 = rr.createCell(4);
                c4.setCellValue(enumText(t.getChannel()));
                c4.setCellStyle(textStyle);

                // description
                Cell c5 = rr.createCell(5);
                c5.setCellValue(safe(t.getDescription()));
                c5.setCellStyle(textStyle);

                // counterparty
                String cp = buildCounterpartyLine(t);
                Cell c6 = rr.createCell(6);
                c6.setCellValue(cp);
                c6.setCellStyle(textStyle);

                // amount
                Cell c7 = rr.createCell(7);
                c7.setCellValue(longOrZero(t.getAmount()));
                c7.setCellStyle(moneyStyle);

                // fee
                Cell c8 = rr.createCell(8);
                c8.setCellValue(longOrZero(t.getFeeAmount()));
                c8.setCellStyle(moneyStyle);

                // currency
                Cell c9 = rr.createCell(9);
                c9.setCellValue(safeCurrency(t.getCurrency()));
                c9.setCellStyle(textStyle);
            }
        }

        // autosize
        setDefaultColumnWidths(sheet);
        // write file
        File out = new File(context.getCacheDir(),
                buildFileName("TAD_bank_checking_statement", accountNumber, "xlsx"));

        FileOutputStream fos = new FileOutputStream(out);
        wb.write(fos);
        fos.close();
        wb.close();

        return out;
    }

    private static int writeInfoRow(Sheet sheet, int r, String k, String v,
                                    CellStyle labelStyle, CellStyle valueStyle) {
        Row row = sheet.createRow(r++);
        Cell ck = row.createCell(0);
        ck.setCellValue(k + ":");
        ck.setCellStyle(labelStyle);

        Cell cv = row.createCell(1);
        cv.setCellValue(v == null ? "" : v);
        cv.setCellStyle(valueStyle);
        return r;
    }

    private static CellStyle createTitleStyle(Workbook wb) {
        Font f = wb.createFont();
        f.setBold(true);
        f.setFontHeightInPoints((short) 14);

        CellStyle st = wb.createCellStyle();
        st.setFont(f);
        st.setAlignment(HorizontalAlignment.LEFT);
        st.setVerticalAlignment(VerticalAlignment.CENTER);
        return st;
    }

    private static CellStyle createLabelStyle(Workbook wb) {
        Font f = wb.createFont();
        f.setBold(true);

        CellStyle st = wb.createCellStyle();
        st.setFont(f);
        st.setAlignment(HorizontalAlignment.LEFT);
        return st;
    }

    private static CellStyle createHeaderStyle(Workbook wb) {
        Font f = wb.createFont();
        f.setBold(true);

        CellStyle st = wb.createCellStyle();
        st.setFont(f);
        st.setAlignment(HorizontalAlignment.CENTER);
        st.setVerticalAlignment(VerticalAlignment.CENTER);
        return st;
    }

    private static CellStyle createTextStyle(Workbook wb) {
        CellStyle st = wb.createCellStyle();
        st.setAlignment(HorizontalAlignment.LEFT);
        st.setVerticalAlignment(VerticalAlignment.TOP);
        st.setWrapText(true);
        return st;
    }

    private static CellStyle createMoneyStyle(Workbook wb) {
        DataFormat df = wb.createDataFormat();
        CellStyle st = wb.createCellStyle();
        st.setAlignment(HorizontalAlignment.RIGHT);
        st.setVerticalAlignment(VerticalAlignment.TOP);
        st.setDataFormat(df.getFormat("#,##0"));
        return st;
    }

    private static CellStyle createDateStyle(Workbook wb) {
        DataFormat df = wb.createDataFormat();
        CellStyle st = wb.createCellStyle();
        st.setAlignment(HorizontalAlignment.LEFT);
        st.setVerticalAlignment(VerticalAlignment.TOP);
        st.setDataFormat(df.getFormat("dd/mm/yyyy hh:mm"));
        return st;
    }

    // ==========================
    // PDF EXPORT (.pdf)
    // ==========================
    public static File exportCheckingPdfToCache(
            Context context,
            String uid,
            String accountNumber,
            Date from,
            Date to,
            List<Transaction> transactions
    ) throws Exception {

        // A4 portrait: 595 x 842
        final int pageW = 595;
        final int pageH = 842;
        final int margin = 36;

        PdfDocument pdf = new PdfDocument();

        Paint pText = new Paint(Paint.ANTI_ALIAS_FLAG);
        pText.setTextSize(10f);

        Paint pSmall = new Paint(Paint.ANTI_ALIAS_FLAG);
        pSmall.setTextSize(9f);

        Paint pBold = new Paint(Paint.ANTI_ALIAS_FLAG);
        pBold.setTextSize(14f);
        pBold.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));

        Paint pBold11 = new Paint(Paint.ANTI_ALIAS_FLAG);
        pBold11.setTextSize(11f);
        pBold11.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));

        Paint pLine = new Paint(Paint.ANTI_ALIAS_FLAG);
        pLine.setStrokeWidth(1f);

        // Load logo bitmap from drawable
        Bitmap logo = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_backgroud);

        int pageNo = 1;
        PdfDocument.Page page = pdf.startPage(new PdfDocument.PageInfo.Builder(pageW, pageH, pageNo).create());
        Canvas canvas = page.getCanvas();

        int y = margin;

        // ----- Header with logo + bank name
        y = drawHeader(canvas, logo, pageW, margin, y, pBold, pSmall);

        // ----- Title
        y += 8;
        canvas.drawText("SAO KÊ TÀI KHOẢN CHECKING", margin, y, pBold11);
        y += 18;

        // ----- Info block
        canvas.drawText("UID: " + safe(uid), margin, y, pText); y += 14;
        canvas.drawText("Tài khoản: " + maskAccount(accountNumber), margin, y, pText); y += 14;
        canvas.drawText("Từ ngày: " + DF_SHORT.format(from), margin, y, pText); y += 14;
        canvas.drawText("Đến ngày: " + DF_SHORT.format(to), margin, y, pText); y += 18;

        // ----- Table header
        // Columns: Time | TxnId | Type | Desc | Amount | Fee
        int x0 = margin;
        int xTime = x0;
        int xId = x0 + 85;
        int xType = x0 + 185;
        int xDesc = x0 + 260;
        int xAmt = x0 + 450;
        int xFee = x0 + 520;

        canvas.drawLine(margin, y, pageW - margin, y, pLine); y += 12;

        canvas.drawText("Thời gian", xTime, y, pBold11);
        canvas.drawText("Mã GD", xId, y, pBold11);
        canvas.drawText("Loại", xType, y, pBold11);
        canvas.drawText("Nội dung", xDesc, y, pBold11);
        canvas.drawText("Số tiền", xAmt, y, pBold11);
        canvas.drawText("Phí", xFee, y, pBold11);
        y += 10;

        canvas.drawLine(margin, y, pageW - margin, y, pLine);
        y += 12;

        long totalAmount = 0L;
        long totalFee = 0L;

        // ----- Rows
        if (transactions != null) {
            for (Transaction t : transactions) {

                // Page break check
                if (y > pageH - margin - 60) {
                    // footer
                    drawFooter(canvas, pageW, pageH, margin, pageNo, pSmall);

                    pdf.finishPage(page);
                    pageNo++;
                    page = pdf.startPage(new PdfDocument.PageInfo.Builder(pageW, pageH, pageNo).create());
                    canvas = page.getCanvas();
                    y = margin;

                    // re-draw header + table header on new page
                    y = drawHeader(canvas, logo, pageW, margin, y, pBold, pSmall);
                    y += 8;
                    canvas.drawText("SAO KÊ TÀI KHOẢN CHECKING (tiếp)", margin, y, pBold11);
                    y += 18;

                    canvas.drawLine(margin, y, pageW - margin, y, pLine); y += 12;
                    canvas.drawText("Thời gian", xTime, y, pBold11);
                    canvas.drawText("Mã GD", xId, y, pBold11);
                    canvas.drawText("Loại", xType, y, pBold11);
                    canvas.drawText("Nội dung", xDesc, y, pBold11);
                    canvas.drawText("Số tiền", xAmt, y, pBold11);
                    canvas.drawText("Phí", xFee, y, pBold11);
                    y += 10;
                    canvas.drawLine(margin, y, pageW - margin, y, pLine);
                    y += 12;
                }

                Date created = t.getCreatedAt();
                String time = created != null ? DF.format(created) : "";

                String txnId = safe(t.getTransactionId());
                if (txnId.length() > 10) txnId = txnId.substring(0, 10) + "...";

                String type = enumText(t.getType());

                // Nội dung: ưu tiên description + counterparty name/account
                String desc = safe(t.getDescription());
                String cp = buildCounterpartyLine(t);
                if (!cp.isEmpty()) desc = desc.isEmpty() ? cp : (desc + " | " + cp);

                desc = ellipsize(desc, 38);

                long amt = longOrZero(t.getAmount());
                long fee = longOrZero(t.getFeeAmount());
                totalAmount += amt;
                totalFee += fee;

                canvas.drawText(time, xTime, y, pText);
                canvas.drawText(txnId, xId, y, pText);
                canvas.drawText(type, xType, y, pText);
                canvas.drawText(desc, xDesc, y, pText);
                canvas.drawText(formatMoney(amt), xAmt, y, pText);
                canvas.drawText(formatMoney(fee), xFee, y, pText);

                y += 14;
            }
        }

        // ----- Summary
        y += 6;
        canvas.drawLine(margin, y, pageW - margin, y, pLine); y += 14;
        canvas.drawText("Tổng số tiền: " + formatMoney(totalAmount) + " " + safeCurrency(null), margin, y, pBold11);
        y += 14;
        canvas.drawText("Tổng phí: " + formatMoney(totalFee) + " " + safeCurrency(null), margin, y, pBold11);
        y += 18;

        // footer for last page
        drawFooter(canvas, pageW, pageH, margin, pageNo, pSmall);

        pdf.finishPage(page);

        File out = new File(context.getCacheDir(),
                buildFileName("TAD_bank_checking_statement", accountNumber, "pdf"));

        FileOutputStream fos = new FileOutputStream(out);
        pdf.writeTo(fos);
        fos.close();
        pdf.close();

        return out;
    }

    private static int drawHeader(Canvas canvas, Bitmap logo, int pageW, int margin, int y,
                                  Paint pBold, Paint pSmall) {

        // logo at left
        if (logo != null) {
            // draw in a box 48x48
            int size = 48;
            Bitmap scaled = Bitmap.createScaledBitmap(logo, size, size, true);
            canvas.drawBitmap(scaled, margin, y, null);

            // bank name aligned with logo
            int textX = margin + size + 10;
            int textY = y + 18;
            canvas.drawText("TAD bank", textX, textY, pBold);

            // subtitle
            canvas.drawText("Official Statement", textX, textY + 14, pSmall);

            return y + size; // next y after logo block
        } else {
            canvas.drawText("TAD bank", margin, y + 18, pBold);
            canvas.drawText("Official Statement", margin, y + 34, pSmall);
            return y + 44;
        }
    }

    private static void drawFooter(Canvas canvas, int pageW, int pageH, int margin, int pageNo, Paint pSmall) {
        String footer = "TAD bank • Trang " + pageNo;
        canvas.drawText(footer, margin, pageH - margin + 10, pSmall);
    }

    // ==========================
    // Helpers
    // ==========================
    private static String buildFileName(String prefix, String accNum, String ext) {
        String time = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
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

    private static String safeCurrency(String c) {
        return (c == null || c.trim().isEmpty()) ? "VND" : c.trim();
    }

    private static long longOrZero(Long v) {
        return v == null ? 0L : v;
    }

    private static String enumText(Object e) {
        return e == null ? "" : String.valueOf(e);
    }

    private static String formatMoney(long v) {
        return NF.format(v);
    }

    private static String ellipsize(String s, int max) {
        if (s == null) return "";
        s = s.replace("\n", " ").trim();
        if (s.length() <= max) return s;
        return s.substring(0, Math.max(0, max - 3)) + "...";
    }

    private static String buildCounterpartyLine(Transaction t) {
        if (t == null) return "";
        String name = safe(t.getCounterpartyName());
        String acc = safe(t.getCounterpartyAccount());
        String bank = safe(t.getCounterpartyBankName());

        StringBuilder sb = new StringBuilder();
        if (!name.isEmpty()) sb.append(name);
        if (!acc.isEmpty()) {
            if (sb.length() > 0) sb.append(" ");
            sb.append("(").append(maskAccount(acc)).append(")");
        }
        if (!bank.isEmpty()) {
            if (sb.length() > 0) sb.append(" - ");
            sb.append(bank);
        }
        return sb.toString();
    }
    private static void setDefaultColumnWidths(Sheet sheet) {
        // width unit = 1/256 ký tự
        sheet.setColumnWidth(0, 18 * 256); // Thời gian
        sheet.setColumnWidth(1, 18 * 256); // Mã giao dịch
        sheet.setColumnWidth(2, 12 * 256); // Loại
        sheet.setColumnWidth(3, 12 * 256); // Trạng thái
        sheet.setColumnWidth(4, 12 * 256); // Kênh
        sheet.setColumnWidth(5, 40 * 256); // Nội dung
        sheet.setColumnWidth(6, 28 * 256); // Đối tác
        sheet.setColumnWidth(7, 14 * 256); // Số tiền
        sheet.setColumnWidth(8, 12 * 256); // Phí
        sheet.setColumnWidth(9, 10 * 256); // Tiền tệ
    }
}

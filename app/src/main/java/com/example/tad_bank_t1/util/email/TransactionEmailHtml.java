package com.example.tad_bank_t1.util.email;

import com.example.tad_bank_t1.data.model.Transaction;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TransactionEmailHtml {

    private static final Locale VI = new Locale("vi", "VN");
    private static final SimpleDateFormat DF = new SimpleDateFormat("dd/MM/yyyy HH:mm", VI);

    public static EmailContent buildReceipt(
            String title,
            String subtitle,
            String statusText,
            String statusColor, // e.g. "#16a34a"
            String amountText,
            String amountSubText,
            String tableRowsHtml,
            String footerNote,
            Transaction txn
    ) {
        String subject = "[TAD Bank] " + title + " - " + safe(txn.getTransactionId());

        String html =
                "<!doctype html><html><head><meta charset='utf-8'/>" +
                        "<meta name='viewport' content='width=device-width, initial-scale=1.0'/>" +
                        "</head><body style='margin:0;padding:0;background:#f6f7fb;font-family:Arial,Helvetica,sans-serif;color:#111827;'>" +

                        "<table role='presentation' width='100%' cellspacing='0' cellpadding='0' style='background:#f6f7fb;padding:24px 0;'>" +
                        "  <tr><td align='center'>" +

                        "    <table role='presentation' width='600' cellspacing='0' cellpadding='0' style='width:600px;max-width:92%;background:#ffffff;border-radius:16px;overflow:hidden;box-shadow:0 10px 30px rgba(17,24,39,0.08);'>" +

                        // Header
                        "      <tr>" +
                        "        <td style='padding:18px 22px;background:linear-gradient(135deg,#0ea5e9,#2563eb);color:#fff;'>" +
                        "          <div style='font-size:16px;font-weight:700;letter-spacing:0.2px;'>TAD Bank</div>" +
                        "          <div style='font-size:12px;opacity:0.9;margin-top:4px;'>Biên lai giao dịch điện tử</div>" +
                        "        </td>" +
                        "      </tr>" +

                        // Title + status
                        "      <tr>" +
                        "        <td style='padding:20px 22px 8px 22px;'>" +
                        "          <div style='font-size:18px;font-weight:700;margin:0 0 6px 0;'>" + safe(title) + "</div>" +
                        "          <div style='font-size:13px;color:#6b7280;line-height:1.5;'>" + safe(subtitle) + "</div>" +
                        "          <div style='margin-top:12px;display:inline-block;padding:6px 10px;border-radius:999px;background:" + safe(statusColor) + ";color:#fff;font-size:12px;font-weight:700;'>" +
                        "            " + safe(statusText) +
                        "          </div>" +
                        "        </td>" +
                        "      </tr>" +

                        // Amount
                        "      <tr>" +
                        "        <td style='padding:8px 22px 12px 22px;'>" +
                        "          <div style='font-size:30px;font-weight:800;letter-spacing:-0.2px;'>" + safe(amountText) + "</div>" +
                        "          <div style='font-size:12px;color:#6b7280;margin-top:4px;'>" + safe(amountSubText) + "</div>" +
                        "        </td>" +
                        "      </tr>" +

                        // Divider
                        "      <tr><td style='padding:0 22px;'><div style='height:1px;background:#e5e7eb;'></div></td></tr>" +

                        // Details table
                        "      <tr>" +
                        "        <td style='padding:14px 22px 18px 22px;'>" +
                        "          <div style='font-size:14px;font-weight:700;margin:0 0 10px 0;'>Chi tiết giao dịch</div>" +
                        "          <table role='presentation' width='100%' cellspacing='0' cellpadding='0' style='border-collapse:collapse;'>" +
                        tableRowsHtml +
                        "          </table>" +
                        "        </td>" +
                        "      </tr>" +

                        // Footer note
                        "      <tr>" +
                        "        <td style='padding:14px 22px;background:#f9fafb;border-top:1px solid #e5e7eb;'>" +
                        "          <div style='font-size:12px;color:#6b7280;line-height:1.6;'>" +
                        safe(footerNote) +
                        "          </div>" +
                        "          <div style='margin-top:10px;font-size:11px;color:#9ca3af;'>" +
                        "            Mã GD: <b style='color:#374151;'>" + safe(txn.getTransactionId()) + "</b> &nbsp; • &nbsp; Ref: <b style='color:#374151;'>" + safe(txn.getTransactionReference()) + "</b>" +
                        "          </div>" +
                        "        </td>" +
                        "      </tr>" +

                        "    </table>" +

                        "    <div style='width:600px;max-width:92%;font-size:11px;color:#9ca3af;margin-top:10px;text-align:left;line-height:1.6;'>" +
                        "      Email này được gửi tự động. Nếu bạn không thực hiện giao dịch, vui lòng liên hệ hotline/CSKH ngay." +
                        "    </div>" +

                        "  </td></tr></table>" +
                        "</body></html>";

        return new EmailContent(subject, html);
    }

    public static String row(String label, String value) {
        return "<tr>" +
                "<td style='padding:8px 0;color:#6b7280;font-size:12px;width:42%;'>" + safe(label) + "</td>" +
                "<td style='padding:8px 0;color:#111827;font-size:12px;font-weight:600;text-align:right;'>" + safe(value) + "</td>" +
                "</tr>";
    }

    public static String money(Long amount, String currency) {
        if (amount == null) return "-";
        NumberFormat nf = NumberFormat.getInstance(VI);
        String cur = (currency == null || currency.trim().isEmpty()) ? "VND" : currency.trim();
        return nf.format(amount) + " " + cur;
    }

    public static String dt(Date d) {
        return d == null ? "-" : DF.format(d);
    }

    public static String maskLast4(String s) {
        if (s == null) return "-";
        String t = s.trim();
        if (t.length() <= 4) return "****" + t;
        return "****" + t.substring(t.length() - 4);
    }

    // Escape HTML to avoid injection
    public static String safe(String s) {
        if (s == null) return "";
        return s.replace("&","&amp;")
                .replace("<","&lt;")
                .replace(">","&gt;")
                .replace("\"","&quot;")
                .replace("'","&#39;");
    }
}

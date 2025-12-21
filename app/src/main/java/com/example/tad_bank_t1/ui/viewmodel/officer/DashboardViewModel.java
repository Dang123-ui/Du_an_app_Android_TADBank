package com.example.tad_bank_t1.ui.viewmodel.officer;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.tad_bank_t1.data.model.Transaction;
import com.example.tad_bank_t1.data.model.enums.AccountStatus;
import com.example.tad_bank_t1.data.model.enums.TnxStatus;
import com.example.tad_bank_t1.data.repository.account.AccountRepository;
import com.example.tad_bank_t1.data.repository.account.FirebaseAccountRepository;
import com.example.tad_bank_t1.data.repository.transaction.FirebaseTransactionRepository;
import com.example.tad_bank_t1.data.repository.transaction.TransactionRepository;
import com.example.tad_bank_t1.data.repository.users.FirebaseUserRepository;
import com.example.tad_bank_t1.data.repository.users.UserRepository;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieEntry;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class DashboardViewModel extends ViewModel {
    private static final String TAG = "DashboardViewModel";
    private final UserRepository userRepo = new FirebaseUserRepository();
    private final TransactionRepository txnRepo = new FirebaseTransactionRepository();
    private final AccountRepository accountRepo = new FirebaseAccountRepository();
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    // ===== NEW USERS
    private static final String USERS_COLLECTION = "users";
    private static final String USER_CREATED_AT_FIELD = "createdAt";
    // ==== Card 1: Người dùng ====
    private final MutableLiveData<Integer> onlineUsers = new MutableLiveData<>(0);
    private final MutableLiveData<Integer> offlineUsers = new MutableLiveData<>(0);

    // ==== KPI 1: DAU ====
    // Removed default "0" to avoid showing a stale value before data is loaded.
    // Initialize KPI LiveData with empty strings to avoid delivering a null
    // value to observers. Passing null to TextView#setText can cause a crash
    // if the TextView or its bindings expect a non-null CharSequence.
    private final MutableLiveData<String> dauValueText = new MutableLiveData<>("");
    private final MutableLiveData<String> dauDeltaText = new MutableLiveData<>("N/A");

    // ==== KPI 2: GVM (Giá trị giao dịch / Gross Volume) ====
    private final MutableLiveData<String> gvmValueText = new MutableLiveData<>("");
    private final MutableLiveData<String> gvmDeltaText = new MutableLiveData<>("N/A");

    // ==== KPI 3: Tổng số giao dịch ====
    private final MutableLiveData<String> txnCountText = new MutableLiveData<>("");
    private final MutableLiveData<String> txnDeltaText = new MutableLiveData<>("N/A");

    // ==== KPI 4: Người dùng mới ====
    private final MutableLiveData<String> newUsersText = new MutableLiveData<>("");
    private final MutableLiveData<String> newUsersDeltaText = new MutableLiveData<>("N/A");

    // ==== KPI 5: Tỉ lệ thất bại giao dịch ====
    private final MutableLiveData<String> failRateText = new MutableLiveData<>("0%");
    private final MutableLiveData<String> failRateDeltaText = new MutableLiveData<>("N/A");
    // ==== Charts (MPAndroidChart models) ====
    public static class LineChartModel {
        public final List<Entry> entries;
        public final List<String> xLabels;

        public LineChartModel(List<Entry> entries, List<String> xLabels) {
            this.entries = entries;
            this.xLabels = xLabels;
        }
    }
    public static class HBarChartModel {
        public final List<BarEntry> entries;
        public final List<String> yLabels; // labels aligned with y = index

        public HBarChartModel(List<BarEntry> entries, List<String> yLabels) {
            this.entries = entries;
            this.yLabels = yLabels;
        }
    }
    public static class PieChartModel {
        public final List<PieEntry> entries;

        public PieChartModel(List<PieEntry> entries) {
            this.entries = entries;
        }
    }
    private final MutableLiveData<LineChartModel> lineChartModel = new MutableLiveData<>(new LineChartModel(Collections.emptyList(), Collections.emptyList()));
    private final MutableLiveData<HBarChartModel> topDistributionBarModel = new MutableLiveData<>(new HBarChartModel(Collections.emptyList(), Collections.emptyList()));
    private final MutableLiveData<PieChartModel> failurePieModel = new MutableLiveData<>(new PieChartModel(Collections.emptyList()));
    private final MutableLiveData<HBarChartModel> accountFunnelModel = new MutableLiveData<>(new HBarChartModel(Collections.emptyList(), Collections.emptyList()));
    // Listener Firestore
    private ListenerRegistration userStatusListener;
    private final MutableLiveData<TimeFilter> _timeFilter =
            new MutableLiveData<>(TimeFilter.TODAY);
    public LiveData<TimeFilter> timeFilter = _timeFilter;

    // ================== EXPOSE LIVEDATA RA NGOÀI ==================

    public LiveData<Integer> getOnlineUsers() {
        return onlineUsers;
    }

    public LiveData<Integer> getOfflineUsers() {
        return offlineUsers;
    }
    public LiveData<String> getDauValueText() {
        return dauValueText;
    }

    public LiveData<String> getDauDeltaText() {
        return dauDeltaText;
    }

    public LiveData<String> getGvmValueText() {
        return gvmValueText;
    }

    public LiveData<String> getGvmDeltaText() {
        return gvmDeltaText;
    }

    public LiveData<String> getTxnCountText() {
        return txnCountText;
    }

    public LiveData<String> getTxnDeltaText() {
        return txnDeltaText;
    }

    public LiveData<String> getNewUsersText() {
        return newUsersText;
    }

    public LiveData<String> getNewUsersDeltaText() {
        return newUsersDeltaText;
    }

    public LiveData<String> getFailRateText() {
        return failRateText;
    }

    public LiveData<String> getFailRateDeltaText() {
        return failRateDeltaText;
    }
    public LiveData<LineChartModel> getLineChartModel() {
        return lineChartModel;
    }
    public LiveData<HBarChartModel> getTopDistributionBarModel() {
        return topDistributionBarModel;
    }
    public LiveData<PieChartModel> getFailurePieModel() {
        return failurePieModel;
    }
    public LiveData<HBarChartModel> getAccountFunnelModel() {
        return accountFunnelModel;
    }
    public enum TimeFilter{
        TODAY,
        LAST_7_DAYS,
        LAST_30_DAYS,
        ALL
    }
    public DashboardViewModel(){
        _timeFilter.setValue(TimeFilter.TODAY);
        // mark KPI fields as loading by setting placeholders
        dauValueText.setValue("…");
        gvmValueText.setValue("…");
        txnCountText.setValue("…");
        newUsersText.setValue("…");
        failRateText.setValue("0%");
        Log.d(TAG, "Initializing DashboardViewModel, default filter=" + _timeFilter.getValue());
        loadDashboard(TimeFilter.TODAY);
        loadCharts(TimeFilter.TODAY);
    }
    public void startListening(){
        Log.d(TAG, "startListening called");
        listenUserCards();
    }
    public void onFilterChanged(TimeFilter filter) {
        TimeFilter current = _timeFilter.getValue();
        _timeFilter.setValue(filter);
        // reset placeholders when time filter changes
        dauValueText.setValue("…");
        gvmValueText.setValue("…");
        txnCountText.setValue("…");
        newUsersText.setValue("…");
        failRateText.setValue("…");
        Log.d(TAG, "Filter changed from " + current + " to " + filter);
        loadDashboard(filter);
        loadCharts(filter);
    }
    private void listenUserCards() {
        if(userStatusListener != null) return;
        userStatusListener = ((FirebaseUserRepository) userRepo)
                .listenUserOnlineOffline((online, offline) -> {
                    onlineUsers.postValue(online);
                    offlineUsers.postValue(offline);
                    Log.d(TAG, "User status updated: online=" + online + ", offline=" + offline);
                    // Simply update the online/offline counts.  Dashboard metrics
                    // are loaded once in the constructor and when the filter changes.
                });
    }
    private static class DateRange {
        final Date start;
        final Date end;

        DateRange(Date start, Date end) {
            this.start = start;
            this.end = end;
        }
    }
    private DateRange buildDateRange(TimeFilter filter) {
        Calendar cal = Calendar.getInstance();
        Calendar endCal = Calendar.getInstance();
        Date endDate = endCal.getTime();

        switch (filter) {
            case TODAY:
                cal.set(Calendar.HOUR_OF_DAY, 0);
                cal.set(Calendar.MINUTE, 0);
                cal.set(Calendar.SECOND, 0);
                cal.set(Calendar.MILLISECOND, 0);
                break;

            case LAST_7_DAYS:
                cal.add(Calendar.DAY_OF_YEAR, -7);
                break;

            case LAST_30_DAYS:
                cal.add(Calendar.DAY_OF_YEAR, -30);
                break;

            case ALL:
            default:
                cal.set(2000, Calendar.JANUARY, 1, 0, 0, 0);
                cal.set(Calendar.MILLISECOND, 0);
                break;
        }
        Date startDate = cal.getTime();
        return new DateRange(startDate, endDate);
    }
    private DateRange buildPreviousRange(DateRange current) {
        long lenMs = current.end.getTime() - current.start.getTime();
        Date prevEnd = new Date(current.start.getTime() - 1);
        Date prevStart = new Date(prevEnd.getTime() - lenMs);
        return new DateRange(prevStart, prevEnd);
    }
    private static class MetricsPack {
        int dau = 0;
        Double gmv = 0.0;
        int totalTx = 0;
        int failedTx = 0;
        Double failRate = 0.0;
        int newUsers = 0;
    }
    private interface MetricsCallback {
        void onSuccess(MetricsPack pack);
        void onFailure(Exception e);
    }
    private void loadDashboard(TimeFilter filter) {
        Log.d(TAG, "loadDashboard: filter=" + filter);
        DateRange cur = buildDateRange(filter);
        DateRange prev = buildPreviousRange(cur);

        loadMetricsForRange(cur, new MetricsCallback() {
            @Override
            public void onSuccess(MetricsPack curPack) {
                Log.d(TAG, "Current metrics loaded");
                loadMetricsForRange(prev, new MetricsCallback() {
                    @Override
                    public void onSuccess(MetricsPack prevPack) {
                        Log.d(TAG, "Previous metrics loaded");
                        postAllKpis(curPack, prevPack);
                    }

                    @Override
                    public void onFailure(Exception e) {
                        // không có prev => show current, delta N/A
                        postAllKpis(curPack, null);
                    }
                });
            }

            @Override
            public void onFailure(Exception e) {
                Log.e(TAG, "Failed to load current metrics", e);
            }
        });
    }
    private void loadCharts(TimeFilter filter) {
        Log.d(TAG, "loadCharts: filter=" + filter);
        DateRange range = buildDateRange(filter);
        loadLineChartGmv(range, filter);
        loadTopDistributionBar(range);
        loadFailurePie(range);
        loadAccountFunnel(range);
    }
    private void loadLineChartGmv(DateRange range, TimeFilter filter) {
        Log.d(TAG, "loadLineChartGmv: range=" + range.start + " - " + range.end + ", filter=" + filter);
        txnRepo.getTransactionsInRange(range.start, range.end, new TransactionRepository.TransactionListCallback() {
            @Override
            public void onSuccess(List<Transaction> transactions) {
                Log.d(TAG, "Line chart transactions count=" + (transactions != null ? transactions.size() : 0));
                if (filter == TimeFilter.TODAY) {
                    Map<Integer, Double> hourlySum = new LinkedHashMap<>();
                    // Initialize all 24 hours with 0 to ensure continuous X axis
                    for (int h = 0; h < 24; h++) {
                        hourlySum.put(h, 0.0);
                    }
                    Calendar cal = Calendar.getInstance();
                    for (Transaction t : transactions) {
                        if (t.getStatus() != TnxStatus.COMPLETED) continue;
                        Long amount = t.getAmount();
                        Date createdAt = t.getCreatedAt();
                        if (amount == null || createdAt == null) continue;
                        cal.setTime(createdAt);
                        int hour = cal.get(Calendar.HOUR_OF_DAY);
                        // accumulate into the hour bucket
                        hourlySum.put(hour, hourlySum.get(hour) + amount);
                    }
                    List<Entry> entries = new ArrayList<>();
                    List<String> labels = new ArrayList<>();
                    int i = 0;
                    for (Map.Entry<Integer, Double> e : hourlySum.entrySet()) {
                        entries.add(new Entry((float) i, e.getValue().floatValue()));
                        // format label as e.g. "00h", "13h"
                        labels.add(String.format(Locale.getDefault(), "%02dh", e.getKey()));
                        i++;
                    }
                    lineChartModel.postValue(new LineChartModel(entries, labels));
                } else {
                    // Group by date (yyyyMMdd), then map to label mm/dd
                    Map<String, Double> daySum = new LinkedHashMap<>();
                    Calendar cal = Calendar.getInstance();
                    for (Transaction t : transactions) {
                        if (t.getStatus() != TnxStatus.COMPLETED) continue;
                        Long amount = t.getAmount();
                        Date createdAt = t.getCreatedAt();
                        if (amount == null || createdAt == null) continue;

                        cal.setTime(createdAt);
                        int y = cal.get(Calendar.YEAR);
                        int m = cal.get(Calendar.MONTH) + 1;
                        int d = cal.get(Calendar.DAY_OF_MONTH);
                        String key = String.format(Locale.getDefault(), "%04d%02d%02d", y, m, d);
                        daySum.put(key, (daySum.containsKey(key) ? daySum.get(key) : 0.0) + amount);
                    }
                    List<Entry> entries = new ArrayList<>();
                    List<String> labels = new ArrayList<>();
                    if (daySum.isEmpty()) {
                        entries.add(new Entry(0f, 0f));
                        labels.add("0");
                    } else {
                        int i = 0;
                        for (Map.Entry<String, Double> e : daySum.entrySet()) {
                            entries.add(new Entry((float) i, e.getValue().floatValue()));
                            labels.add(formatDayLabel(e.getKey()));
                            i++;
                        }
                    }
                    lineChartModel.postValue(new LineChartModel(entries, labels));
                }
            }

            @Override
            public void onFailure(Exception e) {
                Log.e(TAG, "Failed to load GMV line chart", e);
                lineChartModel.postValue(new LineChartModel(Collections.emptyList(), Collections.emptyList()));
            }
        });
    }
    private void loadTopDistributionBar(DateRange range) {
        Log.d(TAG, "loadTopDistributionBar: range=" + range.start + " - " + range.end);

        txnRepo.getTransactionsInRange(range.start, range.end, new TransactionRepository.TransactionListCallback() {
            @Override
            public void onSuccess(List<Transaction> transactions) {
                Log.d(TAG, "Top distribution transactions count=" + (transactions != null ? transactions.size() : 0));

                Map<String, Double> sumByAccount = new LinkedHashMap<>();
                for (Transaction t : transactions) {
                    if (t.getStatus() != TnxStatus.COMPLETED) continue;
                    if (t.getAccountId() == null) continue;
                    Long amount = t.getAmount();
                    if (amount == null) continue;

                    String accId = t.getAccountId();
                    sumByAccount.put(accId, sumByAccount.getOrDefault(accId, 0.0) + amount);
                }

                if (sumByAccount.isEmpty()) {
                    topDistributionBarModel.postValue(new HBarChartModel(Collections.emptyList(), Collections.emptyList()));
                    return;
                }

                // Sắp xếp và lấy top 5 (hoặc 10 nếu bạn muốn)
                List<Map.Entry<String, Double>> sortedList = new ArrayList<>(sumByAccount.entrySet());
                sortedList.sort((a, b) -> Double.compare(b.getValue(), a.getValue()));

                int displayLimit = 5; // Có thể đổi thành 8 hoặc 10
                if (sortedList.size() > displayLimit) {
                    sortedList = sortedList.subList(0, displayLimit);
                }

                List<BarEntry> entries = new ArrayList<>();
                List<String> labels = new ArrayList<>();

                // Đầu tiên: thêm entry và label tạm (để postValue ngay, tránh delay UI)
                for (int i = 0; i < sortedList.size(); i++) {
                    Map.Entry<String, Double> entry = sortedList.get(i);
                    entries.add(new BarEntry(i, entry.getValue().floatValue()));
                    labels.add("Loading..."); // hoặc "Account " + shortId(entry.getKey())
                }

                // Post ngay model với label tạm → chart hiện cột trước
                topDistributionBarModel.postValue(new HBarChartModel(entries, labels));

                // Sau đó async load tên thật và cập nhật từng cái
                for (int i = 0; i < sortedList.size(); i++) {
                    final int index = i; // final để lambda dùng đúng
                    final String accountId = sortedList.get(i).getKey(); // copy ra biến final

                    accountRepo.getById(accountId)
                            .addOnSuccessListener(acc -> {
                                String name = (acc != null && acc.getAccountName() != null && !acc.getAccountName().trim().isEmpty())
                                        ? acc.getAccountName().trim()
                                        : "Account " + accountId.substring(Math.max(0, accountId.length() - 6));

                                labels.set(index, name);

                                // Cập nhật lại chart sau mỗi lần load thành công
                                topDistributionBarModel.postValue(new HBarChartModel(entries, new ArrayList<>(labels)));
                            })
                            .addOnFailureListener(err -> {
                                Log.w(TAG, "Failed to load account name for " + accountId, err);
                                labels.set(index, "Unknown (" + accountId.substring(Math.max(0, accountId.length() - 6)) + ")");

                                topDistributionBarModel.postValue(new HBarChartModel(entries, new ArrayList<>(labels)));
                            });
                }
            }

            @Override
            public void onFailure(Exception e) {
                Log.e(TAG, "Failed to load top distribution bar", e);
                topDistributionBarModel.postValue(new HBarChartModel(Collections.emptyList(), Collections.emptyList()));
            }
        });
    }
    private void loadFailurePie(DateRange range){
        Log.d(TAG, "loadFailurePie: range=" + range.start + " - " + range.end);
        Timestamp startTs = new Timestamp(range.start);
        Timestamp endTs = new Timestamp(range.end);
        db.collection("transactions")
                .whereGreaterThanOrEqualTo("createdAt", startTs)
                .whereLessThanOrEqualTo("createdAt", endTs)
                .whereEqualTo("status", TnxStatus.FAILED.name())
                .get()
                .addOnSuccessListener(snap -> {
                    Log.d(TAG, "Failure pie transactions count=" + snap.size());
                    Map<String, Integer> cnt = new LinkedHashMap<>();
                    snap.getDocuments().forEach(doc -> {
                        String cause = doc.getString("failureCause");
                        if (cause == null) cause = doc.getString("errorCode");
                        if (cause == null) cause = "unknown";
                        cnt.put(cause, (cnt.containsKey(cause) ? cnt.get(cause) : 0) + 1);
                    });

                    List<PieEntry> entries = new ArrayList<>();
                    for (Map.Entry<String, Integer> e : cnt.entrySet()) {
                        entries.add(new PieEntry(e.getValue(), e.getKey()));
                    }
                    failurePieModel.postValue(new PieChartModel(entries));
                })
                .addOnFailureListener(e -> failurePieModel.postValue(new PieChartModel(Collections.emptyList())));
    }
    private void loadAccountFunnel(DateRange range) {
        Log.d(TAG, "loadAccountFunnel: range=" + range.start + " - " + range.end);
        Timestamp startTs = new Timestamp(range.start);
        Timestamp endTs = new Timestamp(range.end);

        db.collection("accounts")
                .whereGreaterThanOrEqualTo("createdAt", startTs)
                .whereLessThanOrEqualTo("createdAt", endTs)
                .get()
                .addOnSuccessListener(created -> {
                    db.collection("accounts")
                            .whereGreaterThanOrEqualTo("updatedAt", startTs)
                            .whereLessThanOrEqualTo("updatedAt", endTs)
                            .get()
                            .addOnSuccessListener(snap -> {
                                Log.d(TAG, "Account funnel accounts updated count=" + snap.size());
                                Log.e("FUNNEL_DEBUG", "Total accounts: " + snap.size());
                                // init đủ 3 status + UNKNOWN để UI luôn có cột
                                Map<String, Integer> cnt = new LinkedHashMap<>();
                                cnt.put(AccountStatus.OPEN.name(), 0);
                                cnt.put(AccountStatus.FROZEN.name(), 0);
                                cnt.put(AccountStatus.CLOSED.name(), 0);
                                cnt.put("UNKNOWN", 0);

                                for (DocumentSnapshot doc : snap.getDocuments()) {
                                    String raw = doc.getString("status");
                                    String st = normalizeAccountStatus(raw); // OPEN/FROZEN/CLOSED/UNKNOWN
                                    cnt.put(st, cnt.get(st) + 1);
                                    Log.e("FUNNEL_DEBUG", "Account ID: " + doc.getId() +
                                            ", Status: " + raw + " -> " + st);
                                }
                                for (Map.Entry<String, Integer> entry : cnt.entrySet()) {
                                    Log.e("FUNNEL_DEBUG", entry.getKey() + ": " + entry.getValue());
                                }
                                // thứ tự hiển thị bar ngang
                                List<String> order = Arrays.asList(
                                        "UNKNOWN",
                                        AccountStatus.OPEN.name(),
                                        AccountStatus.FROZEN.name(),
                                        AccountStatus.CLOSED.name()
                                );

                                List<BarEntry> entries = new ArrayList<>();
                                List<String> labels = new ArrayList<>();

                                for (int i = 0; i < order.size(); i++) {
                                    String status = order.get(i);
                                    int count = cnt.get(status);
                                    entries.add(new BarEntry(i, count));
                                    labels.add(status);

                                    // DEBUG
                                    Log.e("FUNNEL_DEBUG", "Adding to chart - Label: " + status +
                                            ", Count: " + count + ", Index: " + i);
                                }
                                Log.e("FUNNEL_DEBUG", "labels = " + labels);
                                Log.e("FUNNEL_DEBUG", "entries size = " + entries.size());
                                accountFunnelModel.postValue(new HBarChartModel(entries, labels));
                            })
                            .addOnFailureListener(e ->
                                    accountFunnelModel.postValue(
                                            new HBarChartModel(Collections.emptyList(), Collections.emptyList())
                                    )
                            );
                }).addOnFailureListener(e -> {
                    accountFunnelModel.postValue(
                            new HBarChartModel(Collections.emptyList(), Collections.emptyList())
                    );
                });
    }
    private String formatDayLabel(String yyyymmdd) {
        if (yyyymmdd == null || yyyymmdd.length() != 8) return yyyymmdd;
        String mm = yyyymmdd.substring(4, 6);
        String dd = yyyymmdd.substring(6, 8);
        return mm + "/" + dd;
    }
    private String shortId(String s) {
        if (s == null) return "";
        if (s.length() <= 6) return s;
        return s.substring(0, 6) + "…";
    }
    private String normalizeAccountStatus(String raw) {
        if (raw == null) return "UNKNOWN";
        String s = raw.trim().toUpperCase(Locale.US);

        // nếu Firestore lưu đúng enum name thì ok luôn
        if (s.equals(AccountStatus.OPEN.name())) return AccountStatus.OPEN.name();
        if (s.equals(AccountStatus.FROZEN.name())) return AccountStatus.FROZEN.name();
        if (s.equals(AccountStatus.CLOSED.name())) return AccountStatus.CLOSED.name();

        // thêm vài alias phòng trường hợp schema cũ
        if (s.equals("ACTIVE")) return AccountStatus.OPEN.name();
        if (s.equals("LOCKED")) return AccountStatus.FROZEN.name();
        if (s.equals("INACTIVE")) return AccountStatus.CLOSED.name();

        return "UNKNOWN";
    }

    private void loadMetricsForRange(DateRange range, MetricsCallback callback){
        Log.d(TAG, "loadMetricsForRange: range=" + range.start + " - " + range.end);
        txnRepo.getTransactionsInRange(range.start, range.end, new TransactionRepository.TransactionListCallback() {
            @Override
            public void onSuccess(List<Transaction> transactions) {
                Log.d(TAG, "Metrics transactions count=" + (transactions != null ? transactions.size() : 0));
                MetricsPack pack = new MetricsPack();
                // 1) Tính tx stats + gom accountIds (CHỈ 1 LẦN)
                Set<String> accountIds = new HashSet<>();
                for (Transaction txn : transactions) {
                    pack.totalTx++;
                    if(txn.getStatus() == TnxStatus.FAILED){
                        pack.failedTx++;
                    }
                    Long amount = txn.getAmount();
                    if (amount != null && txn.getStatus() == TnxStatus.COMPLETED) {
                        pack.gmv += amount;
                    }
                    if(txn.getAccountId() != null) accountIds.add(txn.getAccountId());
                }
                pack.failRate = pack.totalTx > 0 ? (pack.failedTx * 100.0 / pack.totalTx) : 0.0;

                // IMPORTANT: Firestore whereIn / IN queries do NOT accept empty lists.
                // If there are no accountIds in the range, compute DAU from active users only.
                if (accountIds.isEmpty()) {
                    userRepo.getActiveUserIdsInRange(range.start, range.end, new UserRepository.ActiveUsersCallback() {
                        @Override
                        public void onSuccess(Set<String> activeIds) {
                            Log.d(TAG, "Active user IDs count=" + (activeIds != null ? activeIds.size() : 0));
                            pack.dau = (activeIds == null) ? 0 : activeIds.size();
                            loadNewUsersCount(range, new NewUsersCallback() {
                                @Override
                                public void onSuccess(int count) {
                                    pack.newUsers = count;
                                    callback.onSuccess(pack);
                                }

                                @Override
                                public void onFailure(Exception e) {
                                    callback.onSuccess(pack);
                                }
                            });
                        }

                        @Override
                        public void onFailure(Exception e) {
                            Log.e(TAG, "Failed to get active users for DAU", e);
                            callback.onSuccess(pack);
                        }
                    });
                    return;
                }
                // 2) Map accountId -> userId (GỌI 1 LẦN, không nằm trong for)
                accountRepo.getUserIdsByAccountIds(accountIds, new AccountRepository.AccountUserMapCallback() {
                    @Override
                    public void onSuccess(Map<String, String> accountUserMap) {
                        Log.d(TAG, "AccountUserMap loaded, size=" + (accountUserMap != null ? accountUserMap.size() : 0));
                        Set<String> userIdsFromTx = new HashSet<>();
                        for(Transaction tx: transactions){
                            String accId = tx.getAccountId();
                            if (accId == null) continue;
                            String userId = accountUserMap.get(accId);
                            if (userId != null) userIdsFromTx.add(userId);
                        }
                        // 4) userIds active từ repo
                        userRepo.getActiveUserIdsInRange(range.start, range.end, new UserRepository.ActiveUsersCallback() {
                            @Override
                            public void onSuccess(Set<String> activeIds) {
                                Log.d(TAG, "Active users loaded for DAU: count=" + (activeIds != null ? activeIds.size() : 0));
                                Set<String> dauSet = new HashSet<>(userIdsFromTx);
                                if (activeIds != null) dauSet.addAll(activeIds);
                                pack.dau = dauSet.size();
                                loadNewUsersCount(range, new NewUsersCallback() {
                                    @Override
                                    public void onSuccess(int count) {
                                        pack.newUsers = count;
                                        callback.onSuccess(pack);
                                    }

                                    @Override
                                    public void onFailure(Exception e) {
                                        // vẫn trả pack, newUsers=0
                                        callback.onSuccess(pack);
                                    }
                                });
                            }

                            @Override
                            public void onFailure(Exception e) {
                                Log.e(TAG, "Failed to get active users in range for DAU", e);
                                callback.onSuccess(pack);
                            }
                        });
                    }

                    @Override
                    public void onFailure(Exception e) {
                        Log.e(TAG, "Failed to map account IDs to user IDs", e);
                        callback.onFailure(e);
                    }
                });
            }
            @Override
            public void onFailure(Exception e) {
                Log.e(TAG, "Failed to load transactions for metrics", e);
                callback.onFailure(e);
            }
        });
    }
    // Callback for counting new users within a range
    private interface NewUsersCallback {
        void onSuccess(int count);
        void onFailure(Exception e);
    }
    private void loadNewUsersCount(DateRange range, NewUsersCallback cb) {
        Log.d(TAG, "loadNewUsersCount: range=" + range.start + " - " + range.end);
        Timestamp startTs = new Timestamp(range.start);
        Timestamp endTs = new Timestamp(range.end);
        db.collection(USERS_COLLECTION)
                .whereGreaterThanOrEqualTo(USER_CREATED_AT_FIELD, startTs)
                .whereLessThanOrEqualTo(USER_CREATED_AT_FIELD, endTs)
                .get()
                .addOnSuccessListener(snap -> cb.onSuccess(snap.size()))
                .addOnFailureListener(cb::onFailure);
    }
    private void postAllKpis(MetricsPack cur, MetricsPack prev) {
        Log.d(TAG, "postAllKpis: cur=" + cur.dau + ", gmv=" + cur.gmv + ", totalTx=" + cur.totalTx + ", newUsers=" + cur.newUsers + ", failRate=" + cur.failRate + (prev == null ? " (no prev)" : " prev dau=" + prev.dau));
        dauValueText.postValue(formatInt(cur.dau));
        gvmValueText.postValue(formatCurrencyVnd(cur.gmv));
        txnCountText.postValue(formatInt(cur.totalTx));
        newUsersText.postValue(formatInt(cur.newUsers));
        failRateText.postValue(String.format(Locale.getDefault(), "%.2f%%", cur.failRate));
        if(prev == null){
            dauDeltaText.postValue("N/A");
            gvmDeltaText.postValue("N/A");
            txnDeltaText.postValue("N/A");
            newUsersDeltaText.postValue("N/A");
            failRateDeltaText.postValue("N/A");
            return;
        }
        dauDeltaText.postValue(pctDelta((long) cur.dau, (long) prev.dau));
        gvmDeltaText.postValue(pctDelta(cur.gmv, prev.gmv));
        txnDeltaText.postValue(pctDelta((long) cur.totalTx, (long) prev.totalTx));
        newUsersDeltaText.postValue(pctDelta((long) cur.newUsers, (long) prev.newUsers));
        failRateDeltaText.postValue(pctDelta(cur.failRate, prev.failRate));
    }
    private String formatInt(int number) {
        if (number < 0) return "0";
        return NumberFormat.getIntegerInstance(Locale.getDefault()).format(number);
    }

    private String formatCurrencyVnd(Long amount) {
        NumberFormat formatter = NumberFormat.getNumberInstance(new Locale("vi", "VN"));
        return formatter.format(amount) + " đ";
    }
    private String formatCurrencyVnd(Double amount) {
        NumberFormat formatter = NumberFormat.getNumberInstance(new Locale("vi", "VN"));
        return formatter.format(amount) + " đ";
    }
    private String pctDelta(Long cur, Long prev) {
        if (prev == 0) return "N/A";
        Double pct = ((cur - prev) / prev) * 100.0;
        String sign = pct >= 0 ? "+" : "";
        return String.format(Locale.getDefault(), "%s%.2f%%", sign, pct);
    }

    private String pctDelta(Double cur, Double prev) {
        if (prev == 0) return "N/A";
        Double pct = ((cur - prev) / prev) * 100.0;
        String sign = pct >= 0 ? "+" : "";
        return String.format(Locale.getDefault(), "%s%.2f%%", sign, pct);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        if(userStatusListener != null){
            userStatusListener.remove();
            userStatusListener = null;
        }
    }
}
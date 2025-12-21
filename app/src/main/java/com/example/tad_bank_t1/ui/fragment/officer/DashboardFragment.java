package com.example.tad_bank_t1.ui.fragment.officer;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.tad_bank_t1.R;
import com.example.tad_bank_t1.ui.viewmodel.officer.DashboardViewModel;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.List;
import java.util.Locale;

public class DashboardFragment extends Fragment {

    private DashboardViewModel viewModel;
    private LineChart chartLine;
    private BarChart chartBarTop;
    private PieChart chartFailure;
    private HorizontalBarChart chartFunnel;

    // Card Users
    private TextView tvValueOnl;
    private TextView tvValueOff;

    // KPI cards
    private TextView tvKpi1Value, tvKpi1Delta; // DAU
    private TextView tvKpi2Value, tvKpi2Delta; // GVM
    private TextView tvKpi3Value, tvKpi3Delta; // Transactions
    private TextView tvKpi4Value, tvKpi4Delta; // New Users
    private TextView tvKpi5Value, tvKpi5Delta; // Fail Rate

    // Chips
    private ChipGroup chipFilters;
    private Chip chipToday, chip7d, chip30d, chipAll;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_dashboard, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(DashboardViewModel.class);

        // ====== findViewById Users card ======
        tvValueOnl = view.findViewById(R.id.tvValueUserOnl);
        tvValueOff = view.findViewById(R.id.tvValueOff);

        // ====== findViewById KPI ======
        tvKpi1Value = view.findViewById(R.id.tvKpi1Value);
        tvKpi1Delta = view.findViewById(R.id.tvKpi1Delta);

        tvKpi2Value = view.findViewById(R.id.tvKpi2Value);
        tvKpi2Delta = view.findViewById(R.id.tvKpi2Delta);

        tvKpi3Value = view.findViewById(R.id.tvKpi3Value);
        tvKpi3Delta = view.findViewById(R.id.tvKpi3Delta);

        tvKpi4Value = view.findViewById(R.id.tvKpi4Value);
        tvKpi4Delta = view.findViewById(R.id.tvKpi4Delta);

        tvKpi5Value = view.findViewById(R.id.tvKpi5Value);
        tvKpi5Delta = view.findViewById(R.id.tvKpi5Delta); // nhớ sửa XML như mình nói ở trên

        // ====== findViewById Chips ======
        chipFilters = view.findViewById(R.id.chipFilters);
        chipToday = view.findViewById(R.id.chipToday);
        chip7d = view.findViewById(R.id.chip7d);
        chip30d = view.findViewById(R.id.chip30d);
        chipAll = view.findViewById(R.id.chipAllChannels);
        // ====== findViewById Cho Chart ======
        chartLine = view.findViewById(R.id.chartLinePlaceholder);
        chartBarTop = view.findViewById(R.id.chartBarPlaceholder);
        chartFailure = view.findViewById(R.id.failurePlaceholder);
        chartFunnel = view.findViewById(R.id.accountFunnel);

        // ====== OBSERVE: Users online/offline ======
        viewModel.getOnlineUsers().observe(getViewLifecycleOwner(),
                count -> tvValueOnl.setText(String.valueOf(count)));

        viewModel.getOfflineUsers().observe(getViewLifecycleOwner(),
                count -> tvValueOff.setText(String.valueOf(count)));

        // ====== OBSERVE: KPI values + delta/sub ======
        viewModel.getDauValueText().observe(getViewLifecycleOwner(),
                s -> tvKpi1Value.setText(s));
        viewModel.getDauDeltaText().observe(getViewLifecycleOwner(),
                s -> tvKpi1Delta.setText(s));

        viewModel.getGvmValueText().observe(getViewLifecycleOwner(),
                s -> tvKpi2Value.setText(s));
        viewModel.getGvmDeltaText().observe(getViewLifecycleOwner(),
                s -> tvKpi2Delta.setText(s));

        viewModel.getTxnCountText().observe(getViewLifecycleOwner(),
                s -> tvKpi3Value.setText(s));
        viewModel.getTxnDeltaText().observe(getViewLifecycleOwner(),
                s -> tvKpi3Delta.setText(s));

        viewModel.getNewUsersText().observe(getViewLifecycleOwner(),
                s -> tvKpi4Value.setText(s));
        viewModel.getNewUsersDeltaText().observe(getViewLifecycleOwner(),
                s -> tvKpi4Delta.setText(s));

        viewModel.getFailRateText().observe(getViewLifecycleOwner(),
                s -> tvKpi5Value.setText(s));
        viewModel.getFailRateDeltaText().observe(getViewLifecycleOwner(),
                s -> tvKpi5Delta.setText(s));
        chipFilters.check(R.id.chipToday);
        // ====== CHIP FILTER -> gọi ViewModel tải lại KPI từ Firebase ======
        chipFilters.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (checkedIds == null || checkedIds.isEmpty()) return;

            int id = checkedIds.get(0);
            if (id == R.id.chipToday) {
                viewModel.onFilterChanged(DashboardViewModel.TimeFilter.TODAY);
            } else if (id == R.id.chip7d) {
                viewModel.onFilterChanged(DashboardViewModel.TimeFilter.LAST_7_DAYS);
            } else if (id == R.id.chip30d) {
                viewModel.onFilterChanged(DashboardViewModel.TimeFilter.LAST_30_DAYS);
            } else if (id == R.id.chipAllChannels) {
                viewModel.onFilterChanged(DashboardViewModel.TimeFilter.ALL);
            }
        });
        viewModel.startListening();
        observeCharts();
    }
    private void observeCharts() {

        // ===== LINE CHART =====
        viewModel.getLineChartModel().observe(getViewLifecycleOwner(), model -> {
            if (model == null || model.entries == null || model.entries.isEmpty()) return;

            LineDataSet set = new LineDataSet(model.entries, "GMV");
            set.setDrawCircles(false);
            set.setDrawValues(false);
            set.setLineWidth(2f);
            set.setColor(Color.parseColor("#6200EE"));
            LineData data = new LineData(set);
            chartLine.setData(data);
            setupLineXAxis(chartLine, model.xLabels);
            setupCommonChart(chartLine);
            chartLine.setTouchEnabled(true);
            chartLine.setDragEnabled(true);
            chartLine.setScaleXEnabled(true);
            chartLine.setScaleYEnabled(true);
            chartLine.setPinchZoom(true);
            chartLine.getLegend().setEnabled(false);
            chartLine.animateX(500);
            chartLine.notifyDataSetChanged();
            chartLine.invalidate();
        });

        // ===== TOP DISTRIBUTION BAR =====
        viewModel.getTopDistributionBarModel().observe(getViewLifecycleOwner(), model -> {
            if (model == null || model.entries == null || model.entries.isEmpty()) {
                chartBarTop.clear();
                chartBarTop.invalidate();
                return;
            }

            BarDataSet set = new BarDataSet(model.entries, "Top accounts");
            set.setDrawValues(true);
            set.setValueTextSize(12f);
            set.setValueFormatter(new com.github.mikephil.charting.formatter.ValueFormatter() {
                @Override
                public String getBarLabel(BarEntry barEntry) {
                    return String.format(Locale.getDefault(), "%,.0f", barEntry.getY());
                }
            });

            BarData data = new BarData(set);
            data.setBarWidth(0.6f);

            chartBarTop.setData(data);

            setupHorizontalBar(chartBarTop, model.yLabels);   // <- đổi hàm setup
            setupCommonChart(chartBarTop);

            chartBarTop.setTouchEnabled(true);
            chartBarTop.setDragEnabled(true);
            chartBarTop.setScaleEnabled(true);
            chartBarTop.setScaleXEnabled(true);   // Cho phép zoom và kéo ngang (rất quan trọng!)
            chartBarTop.setScaleYEnabled(false);


            chartBarTop.getLegend().setEnabled(false);

            chartBarTop.animateY(500);
            chartBarTop.notifyDataSetChanged();
            chartBarTop.invalidate();
        });


        // ===== FAILURE PIE =====
        viewModel.getFailurePieModel().observe(getViewLifecycleOwner(), model -> {
            if (model == null || model.entries == null || model.entries.isEmpty()) {
                chartFailure.clear();
                chartFailure.setNoDataText("Không có dữ liệu");
                chartFailure.invalidate();
                return;
            }

            PieDataSet set = new PieDataSet(model.entries, "");
            set.setSliceSpace(2f);
            set.setColors(ColorTemplate.MATERIAL_COLORS);
            set.setValueTextSize(12f);
            chartFailure.setData(new PieData(set));
            chartFailure.getDescription().setEnabled(false);
            chartFailure.setDrawEntryLabels(true);
            chartFailure.setUsePercentValues(false);
            chartFailure.setRotationEnabled(true);
            chartFailure.animateY(500);
            chartFailure.getLegend().setEnabled(false);
            chartFailure.notifyDataSetChanged();
            chartFailure.invalidate();
        });

        // ===== ACCOUNT FUNNEL =====
        viewModel.getAccountFunnelModel().observe(getViewLifecycleOwner(), model -> {
            if (model == null || model.entries == null || model.entries.isEmpty()){
                chartFunnel.clear();
                chartFunnel.setNoDataText("Không có dữ liệu");
                chartFunnel.invalidate();
                return;
            }

            // DEBUG log
            Log.e("FUNNEL_DEBUG", "Labels: " + model.yLabels);
            for (int i = 0; i < model.entries.size(); i++) {
                Log.e("FUNNEL_DEBUG", model.yLabels.get(i) + ": " + model.entries.get(i).getY());
            }

            chartFunnel.clear();

            BarDataSet set = new BarDataSet(model.entries, "");

            // Đặt màu sắc cho từng bar
            int[] colors = new int[model.entries.size()];
            for (int i = 0; i < model.yLabels.size(); i++) {
                String label = model.yLabels.get(i);
                if ("OPEN".equals(label)) {
                    colors[i] = Color.parseColor("#4CAF50"); // Green
                } else if ("FROZEN".equals(label)) {
                    colors[i] = Color.parseColor("#F44336"); // Red
                } else if ("CLOSED".equals(label)) {
                    colors[i] = Color.parseColor("#9E9E9E"); // Gray
                } else {
                    colors[i] = Color.parseColor("#FFA726"); // Orange (UNKNOWN)
                }
            }
            set.setColors(colors);
            set.setDrawValues(true);
            set.setValueTextSize(12f);
            set.setValueFormatter(new com.github.mikephil.charting.formatter.ValueFormatter() {
                @Override
                public String getFormattedValue(float value) {
                    return String.valueOf((int) value);
                }

                @Override
                public String getBarLabel(BarEntry barEntry) {
                    return String.valueOf((int) barEntry.getY());
                }
            });

            BarData data = new BarData(set);
            data.setBarWidth(0.8f);

            chartFunnel.setData(data);
            setupFunnelLikeList(chartFunnel, model.yLabels); // GỌI HÀM CỦA BẠN

            setupCommonChart(chartFunnel);
            chartFunnel.setTouchEnabled(true);
            chartFunnel.setDragEnabled(true);
            chartFunnel.setScaleYEnabled(true);
            chartFunnel.setScaleXEnabled(true);
            chartFunnel.setPinchZoom(false);
            chartFunnel.getLegend().setEnabled(false);


            float max = data.getYMax(); // value nằm ở Y của BarEntry(i, count)
            YAxis valueAxis = chartFunnel.getAxisRight();
            valueAxis.setAxisMinimum(0f);
            valueAxis.setAxisMaximum(max <= 0f ? 1f : max * 1.15f);

            chartFunnel.animateY(600);
            chartFunnel.notifyDataSetChanged();
            chartFunnel.invalidate();

            Log.e("FUNNEL_DEBUG", "Chart setup completed");
        });
    }

    /**
     * Dùng cho HorizontalBarChart kiểu "Top phân bổ" (ít category).
     * Category nằm bên trái, số lượng nằm dưới.
     */
    private void setupHorizontalBar(BarChart chart, List<String> labels) {
        chart.getDescription().setEnabled(false);
        chart.setDrawGridBackground(false);
        chart.setFitBars(true); // bar đều khoảng cách
        chart.setTouchEnabled(true);              // bật chạm
        chart.setDragEnabled(true);               // kéo ngang
        chart.setScaleEnabled(true);              // pinch zoom
        chart.setScaleXEnabled(true);             // zoom ngang
        chart.setScaleYEnabled(true);             // zoom dọc (tùy chọn)
        chart.setPinchZoom(true);                 // pinch zoom mượt
        chart.setDoubleTapToZoomEnabled(true);    // double tap zoom

        // Highlight khi chạm cột
        chart.setHighlightFullBarEnabled(false);  // chỉ highlight cột được chạm
        chart.setHighlightPerTapEnabled(true);
        chart.setHighlightPerDragEnabled(false);
        XAxis x = chart.getXAxis();
        x.setPosition(XAxis.XAxisPosition.BOTTOM);
        x.setDrawGridLines(false);

        // QUAN TRỌNG: ép vẽ 1 label cho mỗi cột
        x.setGranularity(1f);
        x.setGranularityEnabled(true);

        x.setLabelCount(labels.size(), true);      // force count
        x.setAxisMinimum(0f);                   // cover first bar
        x.setAxisMaximum(labels.size() - 1f);    // cover last bar

        x.setValueFormatter(new IndexAxisValueFormatter(labels));
        x.setCenterAxisLabels(false);              // đừng bật true
        x.setAvoidFirstLastClipping(false);

        // Nếu label dài thì xoay để khỏi đè nhau
        x.setLabelRotationAngle(30f);

        chart.getAxisRight().setEnabled(false);
        chart.getAxisLeft().setAxisMinimum(0f);
        chart.setExtraBottomOffset(16f);
    }
    /**
     * Dùng cho Funnel: OPEN/FROZEN/CLOSED hiển thị như list.
     */
    private void setupFunnelLikeList(HorizontalBarChart chart, List<String> labels) {
        chart.getDescription().setEnabled(false);
        chart.getLegend().setEnabled(false);
        chart.setDrawGridBackground(false);
        chart.setDrawBarShadow(false);
        chart.setFitBars(true);
        chart.setDrawValueAboveBar(true);

        // ===== CATEGORY (LABEL) -> XAxis =====
        XAxis x = chart.getXAxis();
        x.setPosition(XAxis.XAxisPosition.BOTTOM); // với horizontal bar, label sẽ nằm bên trái
        x.setGranularity(1f);
        x.setGranularityEnabled(true);
        x.setAxisMinimum(0f);
        x.setAxisMaximum(labels.size()- 1f);
        x.setLabelCount(labels.size(), true);
        x.setValueFormatter(new IndexAxisValueFormatter(labels));
        x.setDrawGridLines(false);
        x.setDrawAxisLine(false);
        x.setTextSize(12f);

        // ===== VALUE (SỐ) -> AxisRight =====
        YAxis value = chart.getAxisRight();
        value.setEnabled(true);
        value.setAxisMinimum(0f);
        value.setGranularity(1f);
        value.setGranularityEnabled(true);
        value.setDrawGridLines(true);
        value.setDrawAxisLine(true);

        // ===== TẮT AxisLeft để khỏi hiện 0-1-2-3 =====
        chart.getAxisLeft().setEnabled(false);

        chart.setExtraLeftOffset(24f);
        chart.setExtraRightOffset(16f);
        chart.setExtraTopOffset(20f);
        chart.setExtraBottomOffset(30f);

    }


    private void setupCommonChart(com.github.mikephil.charting.charts.Chart<?> chart) {
        chart.getDescription().setEnabled(false);
        chart.setNoDataText("Không có dữ liệu");
        chart.setExtraOffsets(8f, 8f, 8f, 8f);
    }

    private void setupLineXAxis(LineChart chart, java.util.List<String> xLabels) {
        XAxis x = chart.getXAxis();
        x.setPosition(XAxis.XAxisPosition.BOTTOM);
        x.setGranularity(1f);
        x.setDrawGridLines(false);
        x.setValueFormatter(new IndexAxisValueFormatter(xLabels));

        chart.getAxisRight().setEnabled(false);

        YAxis left = chart.getAxisLeft();
        left.setAxisMinimum(0f);
    }
}

package com.example.tad_bank_t1.ui.viewmodel.officer.saving;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.tad_bank_t1.data.model.SavingsRatePolicy;
import com.example.tad_bank_t1.data.model.enums.saving.SavingPolicyStatus;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SavingContractListViewModel extends ViewModel {
    public enum FilterChip { ALL, ACTIVE, INACTIVE }

    public static class UiState {
        public final boolean loading;
        public final String toastMessage;
        public final int total;
        public final int shown;

        public UiState(boolean loading, String toastMessage, int total, int shown) {
            this.loading = loading;
            this.toastMessage = toastMessage;
            this.total = total;
            this.shown = shown;
        }
        public static UiState idle() { return new UiState(false, null, 0, 0); }

        public UiState withLoading(boolean v) { return new UiState(v, toastMessage, total, shown); }
    }
    private final MutableLiveData<UiState> uiState = new MutableLiveData<>(UiState.idle());
    private final MutableLiveData<List<SavingsRatePolicy>> filteredList = new MutableLiveData<>(new ArrayList<>());
    private final List<SavingsRatePolicy> all = new ArrayList<>();

    private String query = "";
    private FilterChip chip = FilterChip.ALL;

    private ListenerRegistration registration;

    public LiveData<UiState> getUiState() { return uiState; }

    public LiveData<List<SavingsRatePolicy>> getFilteredList() { return filteredList; }

    public void clearToast() {
        UiState cur = uiState.getValue();
        if (cur == null) cur = UiState.idle();
        uiState.setValue(new UiState(cur.loading, null, cur.total, cur.shown));
    }

    public void start() {
        if (registration != null) return;

        uiState.setValue(UiState.idle().withLoading(true));
        registration = FirebaseFirestore.getInstance()
                .collection("savingRatePolicies")
                .addSnapshotListener((snap, e) -> {
                    if (e != null) {
                        uiState.setValue(new UiState(false, e.getMessage(), all.size(), safeSize(filteredList.getValue())));
                        return;
                    }
                    all.clear();
                    if (snap != null) {
                        for (DocumentSnapshot doc : snap.getDocuments()) {
                            SavingsRatePolicy p = doc.toObject(SavingsRatePolicy.class);
                            if (p != null) {
                                if (p.getSavingPolicyId() == null) {
                                    p.setSavingPolicyId(doc.getId());
                                }
                                all.add(p);
                            }
                        }
                    }
                    applyFilter();
                    uiState.setValue(new UiState(false, null, all.size(), safeSize(filteredList.getValue())));
                });
    }
    public void stop() {
        if (registration != null) {
            registration.remove();
            registration = null;
        }
    }
    public void setQuery(@Nullable String q) {
        this.query = (q == null) ? "" : q.trim();
        applyFilter();
    }
    public void setChip(FilterChip chip) {
        this.chip = (chip == null) ? FilterChip.ALL : chip;
        applyFilter(); //
    }
    private void applyFilter() {
        String q = query == null ? "" : query.trim().toLowerCase(Locale.US);

        List<SavingsRatePolicy> out = new ArrayList<>();
        for (SavingsRatePolicy p : all) {
            if (p == null) continue;

            // chip filter
            boolean okChip;
            if (chip == FilterChip.ALL) okChip = true;
            else {
                SavingPolicyStatus st = p.getStatus();
                if (st == null) st = SavingPolicyStatus.ACTIVE;

                if (chip == FilterChip.ACTIVE) okChip = st == SavingPolicyStatus.ACTIVE;
                else okChip = st == SavingPolicyStatus.INACTIVE;
            }
            if (!okChip) continue;

            // realtime search
            if (q.isEmpty()) {
                out.add(p);
            } else {
                String code = safeLower(p.getContractCode());
                String name = safeLower(p.getPolicyName());
                if (code.contains(q) || name.contains(q)) out.add(p);
            }
        }
        filteredList.setValue(out);

        UiState cur = uiState.getValue();
        if (cur == null) cur = UiState.idle();
        uiState.setValue(new UiState(cur.loading, cur.toastMessage, all.size(), out.size()));
    }
    private String safeLower(String s) {
        return s == null ? "" : s.toLowerCase(Locale.US);
    }
    private int safeSize(List<?> list) {
        return list == null ? 0 : list.size();
    }

    @Override
    protected void onCleared() {
        stop();
        super.onCleared();
    }
}

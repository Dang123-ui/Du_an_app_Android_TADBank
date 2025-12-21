package com.example.tad_bank_t1.ui.viewmodel.account;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.tad_bank_t1.data.model.MortgagePaymentSchedule;
import com.example.tad_bank_t1.data.repository.callbacks.ResultCallback;
import com.example.tad_bank_t1.data.repository.mortgagePaymentSchedule.FirebaseMortgagePaymentScheduleRepository;
import com.example.tad_bank_t1.data.repository.mortgagePaymentSchedule.MortgagePaymentScheduleRepository;
import com.example.tad_bank_t1.data.response.ResultWrapper;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class MortgageScheduleViewModel extends ViewModel {
    private MortgagePaymentScheduleRepository scheduleRepository = new FirebaseMortgagePaymentScheduleRepository();

    // states 
    private MutableLiveData<ResultWrapper<MortgagePaymentSchedule>> _detailState = new MutableLiveData<>();

    private MutableLiveData<ResultWrapper<MortgagePaymentSchedule>> _createState = new MutableLiveData<>();
    private MutableLiveData<ResultWrapper<List<MortgagePaymentSchedule>>> _listState = new MutableLiveData<>();

    private MutableLiveData<ResultWrapper<MortgagePaymentSchedule>> _updateState = new MutableLiveData<>();

    public MortgageScheduleViewModel() {
    }

    // get states
    public MutableLiveData<ResultWrapper<MortgagePaymentSchedule>> getDetailState() {
        return _detailState;
    }

    public MutableLiveData<ResultWrapper<MortgagePaymentSchedule>> getCreateState() {
        return _createState;
    }

    public MutableLiveData<ResultWrapper<List<MortgagePaymentSchedule>>> getListState() {
        return _listState;
    }

    public MutableLiveData<ResultWrapper<MortgagePaymentSchedule>> getUpdateState() {
        return _updateState;
    }

    // reset states
    public void resetStates() {
        _detailState.postValue(null);
        _createState.postValue(null);
        _listState.postValue(null);
        _updateState.postValue(null);
    }

    // create
    public void create(MortgagePaymentSchedule mortgageSchedule) {
        _createState.postValue(ResultWrapper.loading());
        scheduleRepository.create(mortgageSchedule, new ResultCallback<MortgagePaymentSchedule>() {
            @Override
            public void onSuccess(MortgagePaymentSchedule data) {
                if (data == null) {
                    _createState.postValue(ResultWrapper.error("Create failed"));
                } else {
                    _createState.postValue(ResultWrapper.success(data));
                }
            }

            @Override
            public void onError(String error) {
                _createState.postValue(ResultWrapper.error(error));
            }
        });
    }

    // get by id
    public void getById(String mortgageScheduleId) {
        _detailState.postValue(ResultWrapper.loading());

        scheduleRepository.getById(mortgageScheduleId, new ResultCallback<MortgagePaymentSchedule>() {
            @Override
            public void onSuccess(MortgagePaymentSchedule data) {
                if (data == null) {
                    _detailState.postValue(ResultWrapper.error("Not found"));
                    return;
                }
                _detailState.postValue(ResultWrapper.success(data));
            }
            @Override
            public void onError(String error) {
                _detailState.postValue(ResultWrapper.error(error));
            }
        });
    }

    // get all
    public void getAll() {
        _listState.postValue(ResultWrapper.loading());

        scheduleRepository.getAll(new ResultCallback<List<MortgagePaymentSchedule>>() {
            @Override
            public void onSuccess(List<MortgagePaymentSchedule> data) {
                if (data == null) {
                    _listState.postValue(ResultWrapper.error("Not found"));
                    return;
                }
                _listState.postValue(ResultWrapper.success(data));
            }

            @Override
            public void onError(String error) {
                _listState.postValue(ResultWrapper.error(error));
            }
        });
    }

    // update
    public void update(MortgagePaymentSchedule mortgageSchedule) {
        _updateState.postValue(ResultWrapper.loading());

        scheduleRepository.update(mortgageSchedule, new ResultCallback<MortgagePaymentSchedule>() {
            @Override
            public void onSuccess(MortgagePaymentSchedule data) {
                if (data == null){
                    _updateState.postValue(ResultWrapper.error("Update failed"));
                    return;
                }
                _updateState.postValue(ResultWrapper.success(data));
            }

            @Override
            public void onError(String error) {
                _updateState.postValue(ResultWrapper.error(error));
            }
        });
    }


    // ===========
    // Mock
    // ===========

    private final AtomicBoolean hasSeededMock = new AtomicBoolean(false);
    public void seedMockSchedulesOnce(List<MortgagePaymentSchedule> schedules) {
        if (schedules == null || schedules.isEmpty()) return;
        if (hasSeededMock.getAndSet(true)) return;

        createMockSequentially(schedules, 0);
    }

    private void createMockSequentially(List<MortgagePaymentSchedule> schedules, int index) {
        if (index >= schedules.size()) return;

        MortgagePaymentSchedule item = schedules.get(index);

        // Create "silent" (không spam state loading)
        scheduleRepository.create(item, new ResultCallback<MortgagePaymentSchedule>() {
            @Override
            public void onSuccess(MortgagePaymentSchedule data) {
                createMockSequentially(schedules, index + 1);
            }

            @Override
            public void onError(String error) {
                // vẫn tiếp tục tạo item tiếp theo
                createMockSequentially(schedules, index + 1);
            }
        });
    }


}

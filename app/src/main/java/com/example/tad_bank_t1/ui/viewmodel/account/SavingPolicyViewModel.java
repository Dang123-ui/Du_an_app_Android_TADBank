package com.example.tad_bank_t1.ui.viewmodel.account;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.tad_bank_t1.data.model.SavingsRatePolicy;
import com.example.tad_bank_t1.data.repository.callbacks.ResultCallback;
import com.example.tad_bank_t1.data.repository.savingPolicy.FirebaseSavingPolicyRepository;
import com.example.tad_bank_t1.data.repository.savingPolicy.SavingPolicyRepository;
import com.example.tad_bank_t1.data.response.ResultWrapper;

import java.util.List;

public class SavingPolicyViewModel extends ViewModel {
    private final SavingPolicyRepository savingPolicyRepository = new FirebaseSavingPolicyRepository();

    // states 
    private MutableLiveData<ResultWrapper<SavingsRatePolicy>> _detailState = new MutableLiveData<>();

    private MutableLiveData<ResultWrapper<SavingsRatePolicy>> _createState = new MutableLiveData<>();
    private MutableLiveData<ResultWrapper<List<SavingsRatePolicy>>> _listState = new MutableLiveData<>();

    private MutableLiveData<ResultWrapper<SavingsRatePolicy>> _updateState = new MutableLiveData<>();

    public SavingPolicyViewModel() {
    }

    // get states
    public MutableLiveData<ResultWrapper<SavingsRatePolicy>> getDetailState() {
        return _detailState;
    }

    public MutableLiveData<ResultWrapper<SavingsRatePolicy>> getCreateState() {
        return _createState;
    }

    public MutableLiveData<ResultWrapper<List<SavingsRatePolicy>>> getListState() {
        return _listState;
    }

    public MutableLiveData<ResultWrapper<SavingsRatePolicy>> getUpdateState() {
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
    public void create(SavingsRatePolicy savingPolicy) {
        _createState.postValue(ResultWrapper.loading());
        savingPolicyRepository.create(savingPolicy, new ResultCallback<SavingsRatePolicy>() {
            @Override
            public void onSuccess(SavingsRatePolicy data) {
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
    public void getById(String savingPolicyId) {
        _detailState.postValue(ResultWrapper.loading());

        savingPolicyRepository.getById(savingPolicyId, new ResultCallback<SavingsRatePolicy>() {
            @Override
            public void onSuccess(SavingsRatePolicy data) {
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

        savingPolicyRepository.getAll(new ResultCallback<List<SavingsRatePolicy>>() {
            @Override
            public void onSuccess(List<SavingsRatePolicy> data) {
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
    public void update(SavingsRatePolicy savingPolicy) {
        _updateState.postValue(ResultWrapper.loading());

        savingPolicyRepository.update(savingPolicy, new ResultCallback<SavingsRatePolicy>() {
            @Override
            public void onSuccess(SavingsRatePolicy data) {
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

}

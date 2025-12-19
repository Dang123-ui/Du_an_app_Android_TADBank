package com.example.tad_bank_t1.ui.fragment.customer.bills;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.transition.Slide;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.tad_bank_t1.R;
import com.example.tad_bank_t1.data.model.Account;
import com.example.tad_bank_t1.data.model.Transaction;
import com.example.tad_bank_t1.data.model.enums.TnxStatus;
import com.example.tad_bank_t1.data.model.enums.TxnChannel;
import com.example.tad_bank_t1.data.model.enums.TxnType;
import com.example.tad_bank_t1.data.model.remote.Bill;
import com.example.tad_bank_t1.data.model.remote.Provider;
import com.example.tad_bank_t1.databinding.FragmentElectricBillPaymentBinding;
import com.example.tad_bank_t1.ui.activity.MainActivity;
import com.example.tad_bank_t1.ui.base.BaseCustomFragment;
import com.example.tad_bank_t1.ui.base.UiConfig;
import com.example.tad_bank_t1.ui.form.payload.transactions.PhoneTopupPayload;
import com.example.tad_bank_t1.ui.fragment.customer.transaction.TransactionConfirmFragment;
import com.example.tad_bank_t1.ui.fragment.customer.transfer.SearchTransferInfomationFragment;
import com.example.tad_bank_t1.ui.fragment.customer.transfer.CardPaymentFragment;
import com.example.tad_bank_t1.ui.viewmodel.BillViewModel;
import com.example.tad_bank_t1.ui.viewmodel.ProviderViewModel;
import com.example.tad_bank_t1.ui.viewmodel.SessionViewModel;
import com.example.tad_bank_t1.ui.viewmodel.TransactionPayloadViewModel;
import com.example.tad_bank_t1.ui.viewmodel.TransactionViewModel;
import com.example.tad_bank_t1.util.CurrencyUtil;
import com.example.tad_bank_t1.util.TadConstants;
import com.example.tad_bank_t1.util.FragmentUtil;
import com.example.tad_bank_t1.util.TransactionUtil;

public class ElectricBillPaymentFragment extends Fragment implements UiConfig, BaseCustomFragment {
    // view binding
    private FragmentElectricBillPaymentBinding binding;

    // declare view
    private BillViewModel billViewModel;
    private ProviderViewModel providerViewModel;
    private SessionViewModel sessionViewModel;
    private TransactionPayloadViewModel transactionPayloadViewModel;
    private TransactionViewModel transactionViewModel;

    private final String BILL_TYPE = TadConstants.BILL_ELECTRICITY;

    private Provider providerSelected;

    // view model
    //
    // declare data
    private Account accountSource;

    @Override
    public String getAppBarTitle() {
        return getString(R.string.thanh_toan_tien_dien);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        if (getArguments() != null) {
//            mParam1 = getArguments().getString(ARG_PARAM1);
//            mParam2 = getArguments().getString(ARG_PARAM2);
//        }

        // Transition khi Fragment mới xuất hiện (Enter)
        setEnterTransition(new Slide(Gravity.RIGHT));

        // Transition khi Fragment hiện tại biến mất (Exit)
        setExitTransition(new Slide(Gravity.RIGHT));

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentElectricBillPaymentBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initFragment();
    }

    @Override
    public void initView() {
        FragmentUtil.replaceFragment(new CardPaymentFragment(),
                getParentFragmentManager(),
                R.id.fragment_card_electricity_bill,
                false);

    }

    @Override
    public void initViewModel() {
        billViewModel = new ViewModelProvider(requireActivity()).get(BillViewModel.class);
        providerViewModel = new ViewModelProvider(requireActivity()).get(ProviderViewModel.class);
        sessionViewModel = new ViewModelProvider(requireActivity()).get(SessionViewModel.class);
        transactionPayloadViewModel = new ViewModelProvider(requireActivity()).get(TransactionPayloadViewModel.class);
        transactionViewModel = new ViewModelProvider(requireActivity()).get(TransactionViewModel.class);

        // observe
        sessionViewModel.payAccount.observe(getViewLifecycleOwner(), account -> {
            if (account != null) {
                accountSource = account;
            }
        });

        providerViewModel.getSelectedProvider().observe(getViewLifecycleOwner(), provider -> {
            if (provider == null || provider.getData() == null) return;

            providerSelected = provider.getData();

            binding.txtElectricityProviderName.setText(providerSelected.getName());
        });
    }

    @Override
    public void setUpEvents() {
        binding.imbtShowListElectricityProvider.setOnClickListener(v -> {
            SearchTransferInfomationFragment searchTransferInfomationFragment = SearchTransferInfomationFragment.newInstance(
                    getString(R.string.chon_nha_cung_cap),
                    getString(R.string.hint_chon_nha_cung_cap),
                    getString(R.string.danh_sach_ncc),
                    TadConstants.SEARCH_ELECTRICITY_PROVIDER
            );
            FragmentUtil.replaceFragment(searchTransferInfomationFragment,
                    getParentFragmentManager(),
                    R.id.fragment_container_search_electricity);
        });

        // continue
        binding.btnContinueElectricityBill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (providerSelected == null) {
                    showError(getContext(), "Lỗi", "Vui lòng chọn nhà cung cấp");
                    return;
                }

                String customerCode = binding.txtElectricityCustomerCode.getText().toString().trim();
                if (customerCode.isEmpty()) {
                    showError(getContext(), "Lỗi", "Vui lòng nhập mã khách hàng");
                    return;
                }

                // tim kiem
                billViewModel.getBillByProviderAndCustomer(
                        providerSelected.getProviderId(),
                        customerCode
                ).observe(getViewLifecycleOwner(), rs -> {
                    if (rs == null) return;

                    if (rs.isLoading()) {
                        toggleLoading(true);
                        return;
                    }

                    toggleLoading(false);


                    if (rs.getError() != null) {
                        showError(getContext(), "Lỗi", rs.getError());
                        return;
                    }

                    if (rs.getData() == null) {
                        showError(getContext(), "Lỗi", "Không tìm thấy hóa đơn");
                        return;
                    }

                    // chuyen man hinh
                    Bill billRemote = rs.getData();
                    binding.txtElectricityBillAmount.setText(CurrencyUtil.formatVND(billRemote.amount));


                    // Valid phone
                    Toast.makeText(requireContext(), "Hóa đơn hợp lệ", Toast.LENGTH_SHORT).show();

                    // call api thanh toan hoac tao payload
                    if (accountSource == null) {
                        showError(getContext(), "Lỗi", "Thiếu thông tin account");
                        return;
                    }

                    if (billRemote.amount > accountSource.getBalance()) {
                        showError(getContext(), "Lỗi", "Không đủ số dư để thanh toán");
                        return;
                    }

                    // check view model
                    if (transactionPayloadViewModel == null) {
                        showError(getContext(), "Lỗi view model", "transactionPayloadViewModel is null");
                        return;
                    }


                    // set loading
                    binding.btnContinueElectricityBill.setEnabled(false);
                    binding.btnContinueElectricityBill.setText("Đang chuyển hướng...");

                    // check type
                    TxnType type = TxnType.BILL_PAYMENT;

                    // Input form
                    String sourceAccNumber = accountSource.getAccountNumber();
                    String targetAccNumber = customerCode;
                    Long amountTransfer = billRemote.amount;

                    String content = accountSource.getAccountName() + " thanh toán hóa đơn";

                    // id phải độc nhất vô nhị
                    String newId = TransactionUtil.generateTransactionId();
                    // tạo idempotency key
                    // Kiem tra view model co idem chua neu chua co thi phai tao moi
                    boolean hasIdempotencyKey = transactionViewModel.getIdempotencyKey() != null;
                    String idempotencyKey = "";
                    if (!hasIdempotencyKey) {
                        idempotencyKey = TransactionUtil.generateIdempotencyKey(amountTransfer, sourceAccNumber, targetAccNumber, type);
                        transactionViewModel.setIdempotencyKey(idempotencyKey);

                        Log.d("TAG CREATE TRANSACTION", "idempotencyKey mới tạo: " + idempotencyKey);
                    } else {
                        idempotencyKey = transactionViewModel.getIdempotencyKey();

                        // log idem
                        Log.d("TAG CREATE TRANSACTION", "idempotencyKey đã có: " + idempotencyKey);
                    }

                    // Tạo ref
                    String transactionRef = TransactionUtil.generateRef();


                    // ---------------------
                    // Tạo đối tượng giao dịch pending
                    // ---------------------
                    // log acc
                    Log.d("TAG CREATE TRANSACTION", "SourceAccNumber: " + accountSource);
                    Transaction transaction = Transaction.builder()
                            .transactionId(newId)
                            .accountId(accountSource.getAccountId())
                            .accountNumber(sourceAccNumber)
                            .accountName(accountSource.getAccountName())
                            .counterpartyAccount(targetAccNumber)
                            .description(content)
                            .amount(amountTransfer)
                            .feeAmount(0L)
                            .status(TnxStatus.PENDING)
                            .type(type)
                            .channel(TxnChannel.VN_PAY)
                            .currency("VND")
                            .idempotencyKey(idempotencyKey)
                            .transactionReference(transactionRef)
                            .billId(billRemote.billId)
                            .build();

                    // ------------------
                    // Tạo transaction lên server
                    // ------------------
                    Log.d("TAG TRANSACTION", transaction.toString());

                    // chỉ cần preview chưa cần tạo giao dịch thật trên server ở bước này
                    //transactionViewModel.createTransaction(transaction);

                    // ---------------------
                    // Lưu transactionId, transactionRef, idempotencyKey,... vào payload
                    // ---------------------


                    // =============================
                    // save payload và chuyển màn hình
                    // =============================
                    PhoneTopupPayload payloadTopup = new PhoneTopupPayload(
                            accountSource,
                            transaction,
                            targetAccNumber
                    );


                    transactionPayloadViewModel.setTxnPayload(payloadTopup);

                    // =============================
                    //Chuyển màn hình
                    // =============================
                    binding.btnContinueElectricityBill.setEnabled(true);
                    binding.btnContinueElectricityBill.setText(getString(R.string.tiep_tuc));



                    ((MainActivity) requireActivity()).openFeatureFragment(
                            new TransactionConfirmFragment(),
                            getString(R.string.xac_nhan_giao_dich)
                    );
                });
            }
        });
    }

    @Override
    public void toggleLoading(boolean isLoading) {
        ((MainActivity) requireActivity()).showLoadingFeature(isLoading);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
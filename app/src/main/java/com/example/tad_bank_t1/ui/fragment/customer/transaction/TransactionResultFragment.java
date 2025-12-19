package com.example.tad_bank_t1.ui.fragment.customer.transaction;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Bundle;

import androidx.core.content.FileProvider;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.tad_bank_t1.R;
import com.example.tad_bank_t1.data.model.Bank;
import com.example.tad_bank_t1.data.model.Transaction;
import com.example.tad_bank_t1.data.model.enums.TxnType;
import com.example.tad_bank_t1.databinding.FragmentTransactionResultBinding;
import com.example.tad_bank_t1.ui.activity.MainActivity;
import com.example.tad_bank_t1.ui.base.UiConfig;
import com.example.tad_bank_t1.ui.fragment.customer.HomeCustomerFragment;
import com.example.tad_bank_t1.ui.fragment.customer.transfer.BankTransferFragment;
import com.example.tad_bank_t1.ui.viewmodel.TransactionPayloadViewModel;
import com.example.tad_bank_t1.ui.viewmodel.TransactionViewModel;
import com.example.tad_bank_t1.util.CurrencyUtil;
import com.example.tad_bank_t1.util.DateTimeUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

public class TransactionResultFragment extends Fragment implements UiConfig {
    // View binding
    private FragmentTransactionResultBinding binding;

    // View model
    private TransactionPayloadViewModel transactionPayloadViewModel;

    // declare private variable
    private Transaction transaction;
    private Bank bank;

    @Override
    public boolean showAppBar() {
        return false;
    }

    @Override
    public boolean showBottomNav() {
        return false;
    }

    @Override
    public String getAppBarTitle() {
        return getString(R.string.ket_qua_giao_dich);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        if (getArguments() != null) {
//            mParam1 = getArguments().getString(ARG_PARAM1);
//            mParam2 = getArguments().getString(ARG_PARAM2);
//        }
//        // Transition khi Fragment mới xuất hiện (Enter)
//        setEnterTransition(new Slide(Gravity.BOTTOM));
//
//        // Transition khi Fragment hiện tại biến mất (Exit)
//        setExitTransition(new Slide(Gravity.TOP));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentTransactionResultBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ViewCompat.setOnApplyWindowInsetsListener(binding.mainScrollTransactionResult, (v, insets) -> {
            int topInset = insets.getInsets(WindowInsetsCompat.Type.statusBars()).top;

            ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
            lp.topMargin = topInset;    // ⭐ auto margin theo status bar
            v.setLayoutParams(lp);

            return WindowInsetsCompat.CONSUMED;
//            return insets;
        });

        initAndObserverVM();

        setUpEvent();
    }

    private void initAndObserverVM(){
//        transactionViewModel = new ViewModelProvider(requireActivity()).get(TransactionViewModel.class);
        transactionPayloadViewModel = new ViewModelProvider(requireActivity()).get(TransactionPayloadViewModel.class);

        Transaction txn = transactionPayloadViewModel.getTxnPayload().getTransaction();
        updateUI(txn);
    }
    private void updateUI(Transaction txn){
        binding.txtTransactionResultAmount.setText(CurrencyUtil.formatVND(txn.getAmount()));
        binding.txtTransactionResultTime.setText(DateTimeUtil.formatDateToVNTime(txn.getCreatedAt()));

        binding.txtTransactionResultFeeAmount.setText(CurrencyUtil.formatVND(txn.getFeeAmount()));

        binding.txtTransactionResultSourceAccount.setText(txn.getAccountNumber());

        // transfer
        if (txn.getType() == TxnType.TRANSFER_INTERNAL || txn.getType() == TxnType.TRANSFER_EXTERNAL){
            binding.lnloTransactionResultReceiverName.setVisibility(View.VISIBLE);
            binding.lnloTransactionResultBankReceiver.setVisibility(View.VISIBLE);

            binding.txtTransactionResultTargetAccount.setText(txn.getCounterpartyAccount());
            binding.txtTransactionResultTargetAccountName.setText(txn.getCounterpartyName());
            binding.txtTransactionResultTargetBank.setText(txn.getCounterpartyBankCode() + "\n" + txn.getCounterpartyBankName());
        } else if (txn.getType() == TxnType.MOBILE_TOPUP){
            // an view
            binding.lnloTransactionResultReceiverName.setVisibility(View.GONE);
            binding.lnloTransactionResultBankReceiver.setVisibility(View.GONE);

            binding.txtTransactionResultTargetAccount.setText(txn.getCounterpartyAccount());

        } else if (txn.getType() == TxnType.BILL_PAYMENT){
            // an view
            binding.lnloTransactionResultReceiverName.setVisibility(View.GONE);
            binding.lnloTransactionResultBankReceiver.setVisibility(View.GONE);

            binding.txtTransactionResultTargetAccount.setText(txn.getCounterpartyAccount());

        } else {
            // unknown
        }

        binding.txtTransactionResultContent.setText(txn.getDescription());
        binding.txtTransactionResultTransactionCode.setText(txn.getTransactionReference());
        binding.txtTransactionResultType.setText(txn.getType().toString());
    }


    private void setUpEvent(){
        binding.btnSaveImage.setOnClickListener(v -> {
            Bitmap bitmap = captureView(binding.lnloTransactionResultContainer);
            saveBitmapToGallery(bitmap);
        });

        binding.btnShareImage.setOnClickListener(v -> {
            Bitmap bitmap = captureView(binding.lnloTransactionResultContainer);
            shareBitmap(bitmap);
        });


        binding.btnBackHome.setOnClickListener(v -> {
            ((MainActivity) requireActivity()).clearBackStack();

            ((MainActivity) requireActivity()).openFeatureFragment(
                    new HomeCustomerFragment(),
                    getString(R.string.trang_chu)
            );
        });

        binding.btnCreateNewTransaction.setOnClickListener(v -> {
            ((MainActivity) requireActivity()).clearBackStack();

            ((MainActivity) requireActivity()).openFeatureFragment(
                    new BankTransferFragment(),
                    getString(R.string.chuyen_tien)
            );
        });
    }

    // save anh
    private Bitmap captureView(View view) {
        Bitmap bitmap = Bitmap.createBitmap(
                view.getWidth(),
                view.getHeight(),
                Bitmap.Config.ARGB_8888
        );
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        return bitmap;
    }

    private void saveBitmapToGallery(Bitmap bitmap) {
        ContentResolver resolver = requireContext().getContentResolver();
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.DISPLAY_NAME, "tad_bank_transaction_" + System.currentTimeMillis() + ".png");
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/png");
        values.put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/TADBank");

        Uri uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        try {
            OutputStream out = resolver.openOutputStream(uri);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.close();

            Toast.makeText(getContext(), "Đã lưu ảnh vào thư viện!", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(getContext(), "Lỗi khi lưu ảnh!", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }


    // share
    private void shareBitmap(Bitmap bitmap) {
        try {
            File cachePath = new File(requireContext().getCacheDir(), "images");
            cachePath.mkdirs();
            File file = new File(cachePath, "txn_share.png");

            FileOutputStream stream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            stream.close();

            Uri uri = FileProvider.getUriForFile(
                    requireContext(),
                    requireContext().getPackageName() + ".fileprovider",
                    file
            );

            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("image/png");
            intent.putExtra(Intent.EXTRA_STREAM, uri);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            startActivity(Intent.createChooser(intent, "Share via"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;

        new ViewModelProvider(requireActivity())
                .get(TransactionPayloadViewModel.class)
                .clearPayload();
    }
}


package com.example.tad_bank_t1.ui.fragment.customer.payment;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.tad_bank_t1.BuildConfig;
import com.example.tad_bank_t1.R;
import com.example.tad_bank_t1.databinding.FragmentVnpayWebViewBinding;
import com.example.tad_bank_t1.ui.activity.MainActivity;
import com.example.tad_bank_t1.ui.base.BaseCustomFragment;
import com.example.tad_bank_t1.ui.base.UiConfig;
import com.example.tad_bank_t1.ui.viewmodel.TransactionViewModel;

public class VnpayWebViewFragment extends Fragment implements UiConfig, BaseCustomFragment {

    private static final String ARG_URL = "ARG_URL";
    private static final String ARG_TX_ID = "ARG_TX_ID";

    // ✅ host/path ReturnUrl của bạn
    private static final String RETURN_HOST = BuildConfig.BACKEND_HOST;
    private static final String RETURN_PATH = BuildConfig.BACKEND_VNPAY_RETURN;

    // view binding
    private FragmentVnpayWebViewBinding binding;

    private TransactionViewModel transactionViewModel;

    private boolean handledReturn = false;

    public static VnpayWebViewFragment newInstance(String url, String txId) {
        Bundle b = new Bundle();
        b.putString(ARG_URL, url);
        b.putString(ARG_TX_ID, txId);

        VnpayWebViewFragment f = new VnpayWebViewFragment();
        f.setArguments(b);
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentVnpayWebViewBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        initFragment();


    }

    private boolean isReturnUrl(Uri u) {
        return u != null
                && RETURN_HOST.equals(u.getHost())
                && RETURN_PATH.equals(u.getPath());
    }

    private void handleReturn(Uri u) {
        if (handledReturn) return;
        handledReturn = true;

        String code = u.getQueryParameter("vnp_ResponseCode"); // VNPay param
        String txId = getArguments() != null ? getArguments().getString(ARG_TX_ID) : null;

        if ("00".equals(code) && txId != null && !txId.isEmpty()) {
            // ✅ backend update xong -> app listen
            transactionViewModel.listenTransactionStatus(txId);

            // ✅ đóng webview fragment về màn confirm (nó sẽ auto navigate result khi listener báo COMPLETED)
            requireActivity().getSupportFragmentManager().popBackStack();
        } else {
            showError(requireContext(), "Lỗi thanh toán", "Thanh toán thất bại. Code=" + code);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (binding != null && binding.webView != null) {
            binding.webView.stopLoading();
            binding.webView.setWebViewClient(null);
            binding.webView.setWebChromeClient(null);
            binding.webView.destroy();
        }
        binding = null;
    }

    @Override
    public void initView() {
        WebSettings s = binding.webView.getSettings();
        s.setJavaScriptEnabled(true);
        s.setDomStorageEnabled(true);
        s.setLoadsImagesAutomatically(true);

        binding.webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                toggleLoading(newProgress < 100);
            }
        });

        binding.webView.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                Uri u = request.getUrl();
                if (isReturnUrl(u)) {
                    handleReturn(u);
                    return true; // chặn load tiếp
                }
                return false;
            }

            @Override
            public void onPageStarted(WebView view, String url, android.graphics.Bitmap favicon) {
                super.onPageStarted(view, url, favicon);

                // ✅ fallback cho Android cũ / một số redirect không gọi shouldOverride
                Uri u = Uri.parse(url);
                if (isReturnUrl(u)) {
                    handleReturn(u);
                }
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                // chỉ show web khi chưa return
                if (!handledReturn) {
                    toggleLoading(false);
                }
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
                showError(requireContext(), "Lỗi tải trang", "Vui lòng kiểm tra Internet.");
            }

            @Override
            public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
                super.onReceivedHttpError(view, request, errorResponse);
                showError(requireContext(), "Lỗi tải trang", "Vui lòng kiểm tra Internet.");
            }
        });

        String url = getArguments() != null ? getArguments().getString(ARG_URL) : null;
        if (url == null || url.isEmpty()) {
//            showNoData("Không có URL thanh toán");
            showError(requireContext(), "Lỗi", "Không có URL thanh toán");
            return;
        }

        toggleLoading(true);
        binding.webView.loadUrl(url);
    }

    @Override
    public void initViewModel() {
        transactionViewModel = new ViewModelProvider(requireActivity()).get(TransactionViewModel.class);

    }

    @Override
    public void setUpEvents() {

    }

    @Override
    public void toggleLoading(boolean isLoading) {
        ((MainActivity) requireActivity()).showLoadingFeature(isLoading);
    }

    @Override
    public String getAppBarTitle() {
        return "Thanh toán VNPay";
    }
}

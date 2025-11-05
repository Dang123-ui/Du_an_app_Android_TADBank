package com.example.tad_bank_t1.ui.fragment;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.example.tad_bank_t1.R;
import com.example.tad_bank_t1.data.model.Ekyc;
import com.example.tad_bank_t1.data.repository.ekyc.FirebaseEkycRepository;
import com.example.tad_bank_t1.ui.activity.SignUpActivity;
import com.google.mlkit.vision.barcode.BarcodeScanner;
import com.google.mlkit.vision.barcode.BarcodeScannerOptions;
import com.google.mlkit.vision.barcode.BarcodeScanning;
import com.google.mlkit.vision.barcode.common.Barcode;
import com.google.mlkit.vision.common.InputImage;

import com.google.mlkit.vision.face.FaceDetection;
import com.google.mlkit.vision.face.FaceDetector;
import com.google.mlkit.vision.face.FaceDetectorOptions;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TakePhotoFragment extends Fragment {

    // ----------- Args -----------
    private static final String ARG_UID = "uid";
    private static final String ARG_PHONE = "phone";
    private static final String ARG_USERNAME = "username";
    private static final String ARG_EMAIL = "email";
    private static final String INTERNAL_SUBDIR = "ekyc";

    private String uid;
    private String phone;
    private String username;
    private String email;

    // ----------- UI -----------
    private ImageView imgFront;
    private ImageView imgBack;
    private ImageButton btnFrontCamera;
    private ImageButton btnBackCamera;
    private ImageButton btnFrontGallery;
    private ImageButton btnBackGallery;
    private ImageView imgLoading;
    private Button btnXacThuc;

    // ----------- Launchers -----------
    private ActivityResultLauncher<Uri> takePictureLauncher;
    private ActivityResultLauncher<String> requestPermissionLauncher;
    private ActivityResultLauncher<String> pickImageLauncher;

    // ----------- State -----------
    private boolean capturingFront = true;
    private boolean pickingFront = true;
    private Uri currentPhotoUri;
    private Bitmap frontBitmap;
    private Bitmap backBitmap;
    private Ekyc ekyc;
    private FirebaseEkycRepository ekycRepository;
    // Cờ hoàn tất async
    private volatile boolean qrFrontDone = false;
    private volatile boolean backDone = false;
    private volatile boolean faceSaved = false;

    public TakePhotoFragment() {
    }

    public static TakePhotoFragment newInstance(String uid, String phone, String username, String email) {
        TakePhotoFragment fragment = new TakePhotoFragment();
        Bundle args = new Bundle();
        args.putString(ARG_UID, uid);
        args.putString(ARG_PHONE, phone);
        args.putString(ARG_USERNAME, username);
        args.putString(ARG_EMAIL, email);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            uid = getArguments().getString(ARG_UID);
            phone = getArguments().getString(ARG_PHONE);
            username = getArguments().getString(ARG_USERNAME);
            email = getArguments().getString(ARG_EMAIL);
        }
        if (getActivity() instanceof SignUpActivity) {
            ((SignUpActivity) requireActivity()).setHeaderBackEnabled(true);
        }
        // Register launchers sớm để tránh null do lifecycle
        takePictureLauncher = registerForActivityResult(
                new ActivityResultContracts.TakePicture(),
                success -> {
                    if (Boolean.TRUE.equals(success) && currentPhotoUri != null) {
                        processCapturedImage(currentPhotoUri, capturingFront);
                    } else {
                        currentPhotoUri = null; // người dùng hủy
                    }
                });

        requestPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (isGranted) {
                        openCamera();
                    } else {
                        Toast.makeText(getContext(), "Bạn cần cấp quyền camera để sử dụng tính năng này",
                                Toast.LENGTH_SHORT).show();
                    }
                });
        pickImageLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        // Forward to common processing logic. Determine front/back based on
                        // pickingFront flag.
                        processCapturedImage(uri, pickingFront);
                    }
                });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_take_photo, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ekyc = new Ekyc();
        ekycRepository = new FirebaseEkycRepository();
        imgFront = view.findViewById(R.id.imgFront);
        imgBack = view.findViewById(R.id.imgBack);
        btnFrontCamera = view.findViewById(R.id.btnFrontCamera);
        btnBackCamera = view.findViewById(R.id.btnBackCamera);
        btnFrontGallery = view.findViewById(R.id.btnFrontGallery);
        btnBackGallery = view.findViewById(R.id.btnBackGrallery);
        btnXacThuc = view.findViewById(R.id.btn_XacThuc);
        btnXacThuc.setEnabled(false);
        btnFrontCamera.setOnClickListener(v -> {
            capturingFront = true;
            startCaptureFlow();
        });
        btnBackCamera.setOnClickListener(v -> {
            capturingFront = false;
            startCaptureFlow();
        });
        btnFrontGallery.setOnClickListener(v -> {
            pickingFront = true;
            pickImageLauncher.launch("image/*");
        });
        btnBackGallery.setOnClickListener(v -> {
            pickingFront = false;
            pickImageLauncher.launch("image/*");
        });
        imgLoading = view.findViewById(R.id.loading2);
        btnXacThuc.setOnClickListener(v -> {
            btnXacThuc.setEnabled(false);
            imgLoading.setVisibility(View.VISIBLE);
            btnBackCamera.setEnabled(false);
            btnBackGallery.setEnabled(false);
            btnFrontCamera.setEnabled(false);
            btnFrontGallery.setEnabled(false);
            String missing = validateEkyc(ekyc, qrFrontDone, backDone, faceSaved);
            if (missing != null) {
                Toast.makeText(getContext(), "Không trích xuất được thông tin của: " + missing, Toast.LENGTH_LONG)
                        .show();
                return;
            }
            ekyc.setUserId(uid);
            // ekycRepository.createWithUploads(ekyc)
            // .addOnSuccessListener(id -> {
            // // Sau khi tạo thành công trên server và nhận lại ID, gán nó vào object
            // ekyc.setId(id);
            // Toast.makeText(getContext(), "Đã lưu eKYC (id: " + id + ")",
            // Toast.LENGTH_LONG).show();
            // btnXacThuc.setEnabled(true);
            // // Ở đây bạn có thể điều hướng người dùng sang màn hình khác
            // })
            // .addOnFailureListener(e -> {
            // Toast.makeText(getContext(), "Lưu eKYC thất bại: ",
            // Toast.LENGTH_LONG).show();
            // btnXacThuc.setEnabled(true);
            // });
            ekyc.setVerifiedAt(new Date());
            ekycRepository.create(ekyc)
                    .addOnSuccessListener(id -> {
                        // Sau khi tạo thành công trên server và nhận lại ID, gán nó vào object
                        ekyc.setId(id);
                        btnXacThuc.setEnabled(true);
                        CCCDInfoVerifyFragment verifyFragment = CCCDInfoVerifyFragment.newInstance(
                                ekyc, uid, phone, username, email);
                        if (getActivity() instanceof SignUpActivity) {
                            ((SignUpActivity) getActivity()).navigateTo(verifyFragment, true);
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(getContext(), "Qúa trình xác thực có lỗi: " + e.getMessage(), Toast.LENGTH_LONG)
                                .show();
                        btnXacThuc.setEnabled(true);
                        imgLoading.setVisibility(View.GONE);
                        btnBackCamera.setEnabled(true);
                        btnBackGallery.setEnabled(true);
                        btnFrontCamera.setEnabled(true);
                        btnFrontGallery.setEnabled(true);
                    });
        });
    }

    private void startCaptureFlow() {
        if (!hasAnyCamera()) {
            Toast.makeText(getContext(), "Thiết bị không có camera khả dụng", Toast.LENGTH_SHORT).show();
            return;
        }
        if (ContextCompat.checkSelfPermission(requireContext(),
                Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            openCamera();
        } else {
            requestPermissionLauncher.launch(Manifest.permission.CAMERA);
        }
    }

    private void openCamera() {
        try {
            currentPhotoUri = createImageUri();
            if (currentPhotoUri != null) {
                takePictureLauncher.launch(currentPhotoUri);
            } else {
                Toast.makeText(getContext(), "Không thể tạo file ảnh", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e("TakePhotoFragment", "openCamera failed", e);
            Toast.makeText(getContext(), "Không mở được camera", Toast.LENGTH_SHORT).show();
        }
    }

    private Uri createImageUri() {
        try {
            Context context = requireContext();
            String fileName = "ekyc_capture_" + System.currentTimeMillis() + ".jpg";
            File storageDir = new File(context.getFilesDir(), INTERNAL_SUBDIR);
            if (!storageDir.exists()) {
                boolean ok = storageDir.mkdirs();
                if (!ok)
                    Log.w("TakePhotoFragment", "Không tạo được thư mục internal ekyc");
            }
            File imageFile = new File(storageDir, fileName);
            if (!imageFile.exists())
                imageFile.createNewFile();
            String authority = context.getPackageName() + ".fileprovider";
            return FileProvider.getUriForFile(context, authority, imageFile);
        } catch (IOException e) {
            Log.e("TakePhotoFragment", "createImageUri failed", e);
            return null;
        }
    }

    private Bitmap loadBitmapFromUri(Uri uri) throws IOException {
        Context ctx = requireContext();
        if (Build.VERSION.SDK_INT >= 28) {
            ImageDecoder.Source src = ImageDecoder.createSource(ctx.getContentResolver(), uri);
            return ImageDecoder.decodeBitmap(src);
        } else {
            @SuppressWarnings("deprecation")
            Bitmap bmp = MediaStore.Images.Media.getBitmap(ctx.getContentResolver(), uri);
            return bmp;
        }
    }

    private void processCapturedImage(Uri uri, boolean frontSide) {
        try {
            Bitmap bitmap = loadBitmapFromUri(uri);
            if (frontSide) {
                frontBitmap = bitmap;
                imgFront.setImageBitmap(bitmap);
                runFaceDetection(bitmap);
                runQrScanFront(bitmap);
            } else {
                backBitmap = bitmap;
                imgBack.setImageBitmap(bitmap);
                backDone = true;
            }
            updateConfirmButtonState();
        } catch (IOException e) {
            Toast.makeText(getContext(), "Lỗi đọc ảnh", Toast.LENGTH_SHORT).show();
        }

    }

    private void runQrScanFront(Bitmap bitmap) {
        qrFrontDone = false;
        int w = bitmap.getWidth(), h = bitmap.getHeight();
        int left = (int) (w * 0.60f), top = (int) (h * 0.05f);
        int roiW = (int) (w * 0.35f), roiH = (int) (h * 0.35f);
        roiW = Math.min(roiW, w - left);
        roiH = Math.min(roiH, h - top);
        Bitmap roi = Bitmap.createBitmap(bitmap, left, top, roiW, roiH);
        BarcodeScannerOptions options = new BarcodeScannerOptions.Builder()
                .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
                .build();
        BarcodeScanner scanner = BarcodeScanning.getClient(options);
        InputImage imgRoi = InputImage.fromBitmap(roi, 0);
        InputImage imgFull = InputImage.fromBitmap(bitmap, 0);

        scanner.process(imgRoi)
                .addOnSuccessListener(bcs -> {
                    if (bcs != null && !bcs.isEmpty() && bcs.get(0).getRawValue() != null) {
                        parseQrCodeData(bcs.get(0).getRawValue());
                        qrFrontDone = true;
                        updateConfirmButtonState();
                    } else {
                        // fallback
                        scanner.process(imgFull).addOnSuccessListener(bcs2 -> {
                            if (bcs2 != null && !bcs2.isEmpty() && bcs2.get(0).getRawValue() != null) {
                                parseQrCodeData(bcs2.get(0).getRawValue());
                            }
                            qrFrontDone = true;
                            updateConfirmButtonState();
                        }).addOnFailureListener(e -> {
                            qrFrontDone = true;
                            updateConfirmButtonState();
                        });
                    }
                })
                .addOnFailureListener(e -> {
                    qrFrontDone = true;
                    updateConfirmButtonState();
                });

        // Timeout mềm 10s để cập nhật UI nếu MLKit treo (hiếm)
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            if (!qrFrontDone) {
                qrFrontDone = true;
                updateConfirmButtonState();
                Toast.makeText(this.getContext(), "Hết thời gian quét vui lòng thử lại", Toast.LENGTH_SHORT).show();
            }
        }, 10_000);
    }

    private boolean parseQrCodeData(String raw) {
        if (raw == null || raw.trim().isEmpty())
            return false;
        raw = raw.trim();
        try {
            JSONObject obj = new JSONObject(raw);
            if (obj.length() > 0) {
                if (obj.has("id"))
                    ekyc.setNationalIdNumber(obj.optString("id", ekyc.getNationalIdNumber()));
                if (obj.has("cccd"))
                    ekyc.setNationalIdNumber(obj.optString("cccd", ekyc.getNationalIdNumber()));
                if (obj.has("cmnd"))
                    ekyc.setNationalIdNumber(obj.optString("cmnd", ekyc.getNationalIdNumber()));

                if (obj.has("fullName"))
                    ekyc.setFullName(obj.optString("fullName", ekyc.getFullName()));
                if (obj.has("name"))
                    ekyc.setFullName(obj.optString("name", ekyc.getFullName()));
                if (obj.has("dob"))
                    ekyc.setDateOfBirth(normalizeDate(obj.optString("dob", ekyc.getDateOfBirth())));
                if (obj.has("birthDate"))
                    ekyc.setDateOfBirth(normalizeDate(obj.optString("birthDate", ekyc.getDateOfBirth())));

                if (obj.has("gender"))
                    ekyc.setGender(obj.optString("gender", ekyc.getGender()));
                if (obj.has("address"))
                    ekyc.setAddress(obj.optString("address", ekyc.getAddress()));
                if (obj.has("dateOfIssue"))
                    ekyc.setDateOfIssue(normalizeDate(obj.optString("dateOfIssue", ekyc.getDateOfIssue())));
                ekyc.setPlaceOfIssue("Cục cảnh sát");
                return hasRequiredFrontFields();
            }
        } catch (Exception ignore) {
        }

        if (raw.contains("|")) {
            String[] parts = raw.split("\\|", -1);
            if (parts.length == 7) {
                String cccd = parts[0].trim();
                String cmndOld = parts[1].trim();
                String fullName = parts[2].trim();
                String dobRaw = parts[3].trim();
                String genderRaw = parts[4].trim();
                String address = parts[5].trim();
                String dateOfIssueRaw = parts[6].trim();
                if (!cccd.isEmpty())
                    ekyc.setNationalIdNumber(cccd);
                if (!fullName.isEmpty()) {
                    ekyc.setFullName(fullName);
                }
                if (!dobRaw.isEmpty()) {
                    String parsedDob = parseDateFromCompact(dobRaw);
                    ekyc.setDateOfBirth(parsedDob != null ? parsedDob : normalizeDate(dobRaw));
                }
                if (!genderRaw.isEmpty()) {
                    String g = genderRaw.trim().toLowerCase();
                    if (g.equals("nam") || g.equals("n") || g.equals("male"))
                        ekyc.setGender("Nam");
                    else if (g.equals("nu") || g.equals("nữ") || g.equals("female"))
                        ekyc.setGender("Nữ");
                    else
                        ekyc.setGender(capitalizeFirst(genderRaw));
                }
                if (!address.isEmpty())
                    ekyc.setAddress(address);
                if (!dateOfIssueRaw.isEmpty()) {
                    String parsedDoI = parseDateFromCompact(dateOfIssueRaw);
                    ekyc.setDateOfIssue(parsedDoI != null ? parsedDoI : normalizeDate(dateOfIssueRaw));
                }
                ekyc.setPlaceOfIssue("Cục cảnh sát");
                return hasRequiredFrontFields();
            } else {
                String[] parts2 = raw.split("\\|");
                if (parts2.length >= 3) {
                    if (!parts2[0].trim().isEmpty())
                        ekyc.setNationalIdNumber(parts2[0].trim());
                    if (!parts2[2].trim().isEmpty())
                        ekyc.setFullName(parts2[2].trim());
                    // cố gắng lấy DOB từ vị trí 3 nếu tồn tại
                    if (parts2.length > 3 && !parts2[3].trim().isEmpty()) {
                        String pd = parseDateFromCompact(parts2[3].trim());
                        ekyc.setDateOfBirth(pd != null ? pd : normalizeDate(parts2[3].trim()));
                    }
                    return hasRequiredFrontFields();
                }
            }
        }
        Pattern idPattern = Pattern.compile("\\b\\d{9,13}\\b");
        Pattern datePattern = Pattern.compile("(\\d{1,2}[-/]\\d{1,2}[-/]\\d{4})");

        Matcher idm = idPattern.matcher(raw.replaceAll("\\s", ""));
        if (idm.find()) {
            ekyc.setNationalIdNumber(idm.group());
        }
        Matcher dm = datePattern.matcher(raw);
        if (dm.find()) {
            String d = dm.group(1).replaceAll("[-/]", "");
            String parsed = parseDateFromCompact(d);
            if (parsed != null && isBlank(ekyc.getDateOfBirth())) {
                ekyc.setDateOfBirth(parsed);
            } else if (parsed != null && isBlank(ekyc.getDateOfIssue())) {
                ekyc.setDateOfIssue(parsed);
            }
        }
        return hasRequiredFrontFields();
    }

    private String capitalizeFirst(String s) {
        if (s == null || s.isEmpty())
            return s;
        s = s.trim();
        return s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase();
    }

    private String parseDateFromCompact(String s) {
        if (s == null)
            return null;
        String onlyDigits = s.replaceAll("\\D", "");
        if (onlyDigits.length() == 8) {
            String dd = onlyDigits.substring(0, 2);
            String mm = onlyDigits.substring(2, 4);
            String yyyy = onlyDigits.substring(4, 8);
            // kiểm tra valid basic (1..31 , 1..12)
            try {
                int di = Integer.parseInt(dd);
                int mi = Integer.parseInt(mm);
                if (mi >= 1 && mi <= 12 && di >= 1 && di <= 31) {
                    if (dd.length() == 1)
                        dd = "0" + dd;
                    if (mm.length() == 1)
                        mm = "0" + mm;
                    return yyyy + "-" + String.format("%02d", Integer.parseInt(mm)) + "-"
                            + String.format("%02d", Integer.parseInt(dd));
                }
            } catch (Exception ignored) {
            }
        }
        return null;
    }

    private String normalizeDate(String input) {
        try {
            String sanitized = input.replace('-', '/');
            String[] parts = sanitized.split("/");
            if (parts.length == 3) {
                String day = parts[0];
                String month = parts[1];
                String year = parts[2];
                if (day.length() == 1)
                    day = "0" + day;
                if (month.length() == 1)
                    month = "0" + month;
                return year + "-" + month + "-" + day;
            }
        } catch (Exception ignored) {
        }
        return input;
    }

    // ---------------- Face detection ----------------

    private void runFaceDetection(Bitmap bitmap) {
        FaceDetectorOptions options = new FaceDetectorOptions.Builder()
                .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
                .build();
        FaceDetector detector = FaceDetection.getClient(options);
        InputImage image = InputImage.fromBitmap(bitmap, 0);

        detector.process(image)
                .addOnSuccessListener(faces -> {
                    if (faces == null || faces.isEmpty()) {
                        faceSaved = false;
                        Toast.makeText(getContext(), "Không tìm thấy khuôn mặt trong ảnh mặt trước", Toast.LENGTH_SHORT)
                                .show();
                        updateConfirmButtonState();
                        return;
                    }
                    saveFaceBitmap(bitmap, faces.get(0).getBoundingBox());
                }).addOnFailureListener(e -> {
                    faceSaved = false;
                    Toast.makeText(getContext(), "Lỗi nhận diện khuôn mặt", Toast.LENGTH_SHORT).show();
                    updateConfirmButtonState();
                });
    }

    private void saveFaceBitmap(Bitmap src, Rect box) {
        int left = Math.max(0, box.left);
        int top = Math.max(0, box.top);
        int right = Math.min(src.getWidth(), box.right);
        int bottom = Math.min(src.getHeight(), box.bottom);
        int w = Math.max(1, right - left);
        int h = Math.max(1, bottom - top);

        try {
            // 1) Cắt khuôn mặt
            Bitmap faceBmp = Bitmap.createBitmap(src, left, top, w, h);

            // 2) Thu nhỏ để an toàn dung lượng (giữ doc Firestore < 1MB)
            // 224–256 là ổn; ở đây dùng 256x256.
            Bitmap faceResized = Bitmap.createScaledBitmap(faceBmp, 256, 256, true);

            // 3) Nén JPEG mức ~75–80 để cân bằng chất lượng/kích thước
            java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
            faceResized.compress(Bitmap.CompressFormat.JPEG, 78, baos);
            byte[] bytes = baos.toByteArray();

            // 4) Mã hóa Base64 thành Data URL
            String b64 = android.util.Base64.encodeToString(bytes, android.util.Base64.NO_WRAP);
            String dataUrl = "data:image/jpeg;base64," + b64;

            // 5) Lưu **chuỗi** vào model (Firestore chỉ lưu string này)
            ekyc.setFaceImagePath(dataUrl);
            faceSaved = true;

            // (không còn ghi file nội bộ / không cần Uri)
        } catch (Exception e) {
            Log.e("TakePhotoFragment", "Tạo data URL khuôn mặt thất bại", e);
            faceSaved = false;
        } finally {
            updateConfirmButtonState();
        }
    }

    // ---------------- Utilities ----------------

    private boolean hasAnyCamera() {
        PackageManager pm = requireContext().getPackageManager();
        return pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY);
    }

    private boolean hasRequiredFrontFields() {
        return isNotBlank(ekyc.getNationalIdNumber()) && isNotBlank(ekyc.getFullName());
    }

    private void updateConfirmButtonState() {
        boolean hasFront = frontBitmap != null;
        boolean hasBack = backBitmap != null;
        boolean basicOk = hasRequiredFrontFields();
        boolean ready = hasFront && hasBack && basicOk && qrFrontDone && backDone && faceSaved;
        if (btnXacThuc != null)
            btnXacThuc.setEnabled(ready);
    }

    @Nullable
    private String validateEkyc(Ekyc e, boolean frontDone, boolean backReady, boolean faceOk) {
        StringBuilder sb = new StringBuilder();
        if (frontBitmap == null)
            sb.append("Ảnh mặt trước, ");
        if (backBitmap == null)
            sb.append("Ảnh mặt sau, ");
        if (!frontDone)
            sb.append("Quét QR mặt trước chưa xong, ");
        if (!backReady)
            sb.append("Mặt sau chưa có ảnh, ");
        if (!faceOk || e.getFaceImagePath() == null)
            sb.append("Khuôn mặt chưa trích xuất, ");

        // Tối thiểu: số CCCD + họ tên
        if (isBlank(e.getNationalIdNumber()))
            sb.append("Số CCCD, ");
        if (isBlank(e.getFullName()))
            sb.append("Họ tên, ");

        // Nếu cần khắt khe hơn, bật thêm:
        if (isBlank(e.getDateOfBirth()))
            sb.append("Ngày sinh, ");
        if (isBlank(e.getGender()))
            sb.append("Giới tính, ");
        if (isBlank(e.getAddress()))
            sb.append("Địa chỉ, ");
        if (isBlank(e.getDateOfIssue()))
            sb.append("Ngày cấp, ");
        if (sb.length() == 0)
            return null;
        String s = sb.toString().trim();
        if (s.endsWith(","))
            s = s.substring(0, s.length() - 1);
        return s;
    }

    private String v(String s) {
        return (s == null || s.trim().isEmpty()) ? "—" : s.trim();
    }

    private boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }

    private boolean isNotBlank(String s) {
        return !isBlank(s);
    }
}

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
import android.os.Environment;
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
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.face.Face;
import com.google.mlkit.vision.face.FaceDetection;
import com.google.mlkit.vision.face.FaceDetector;
import com.google.mlkit.vision.face.FaceDetectorOptions;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Fragment chụp ảnh CCCD (mặt trước / mặt sau) + OCR + Detect Face.
 * - Hỗ trợ emulator chụp ảnh "thật" qua webcam (AVD Camera = Webcam0).
 * - Nút Xác thực chỉ bật khi đã chụp đủ 2 mặt, OCR xong, và đã cắt/lưu khuôn mặt.
 */
public class TakePhotoFragment extends Fragment {

    // ----------- Args -----------
    private static final String ARG_UID = "uid";
    private static final String ARG_PHONE = "phone";
    private static final String ARG_USERNAME = "username";
    private static final String ARG_EMAIL = "email";

    private String uid;
    private String phone;
    private String username;
    private String email;

    // ----------- UI -----------
    private ImageView imgFront;
    private ImageView imgBack;
    private ImageButton btnFrontCamera;
    private ImageButton btnBackCamera;
    private Button btnXacThuc;

    // ----------- Launchers -----------
    private ActivityResultLauncher<Uri> takePictureLauncher;
    private ActivityResultLauncher<String> requestPermissionLauncher;

    // ----------- State -----------
    private boolean capturingFront = true;   // TRUE = mặt trước, FALSE = mặt sau
    private Uri currentPhotoUri;

    private Bitmap frontBitmap;
    private Bitmap backBitmap;
    private Ekyc ekyc;
    private FirebaseEkycRepository ekycRepository;

    // Cờ hoàn tất async
    private volatile boolean ocrFrontDone = false;
    private volatile boolean ocrBackDone  = false;
    private volatile boolean faceSaved    = false;

    public TakePhotoFragment() { }

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

    // ---------------- Lifecycle ----------------

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
                }
        );

        requestPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (isGranted) {
                        openCamera();
                    } else {
                        Toast.makeText(getContext(), "Bạn cần cấp quyền camera để sử dụng tính năng này", Toast.LENGTH_SHORT).show();
                    }
                }
        );
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
        // Gợi ý: bạn có thể set sẵn các thông tin từ args nếu cần
        // ekyc.setUserId(uid);
        // ...

        ekycRepository = new FirebaseEkycRepository();

        imgFront = view.findViewById(R.id.imgFront);
        imgBack = view.findViewById(R.id.imgBack);
        btnFrontCamera = view.findViewById(R.id.btnFrontCamera);
        btnBackCamera = view.findViewById(R.id.btnBackCamera);
        btnXacThuc = view.findViewById(R.id.btn_XacThuc);

        btnXacThuc.setEnabled(false); // ban đầu tắt

        btnFrontCamera.setOnClickListener(v -> {
            capturingFront = true;
            startCaptureFlow();
        });

        btnBackCamera.setOnClickListener(v -> {
            capturingFront = false;
            startCaptureFlow();
        });

        btnXacThuc.setOnClickListener(v -> {
            String missing = validateEkyc(ekyc, ocrFrontDone, ocrBackDone, faceSaved);
            if (missing != null) {
                Toast.makeText(getContext(), "Thiếu/Chưa xong: " + missing, Toast.LENGTH_LONG).show();
                return;
            }
            ekyc.setUserId(uid);
            ekycRepository.createWithUploads(ekyc)
                    .addOnSuccessListener(id -> {
                        // Sau khi tạo thành công trên server và nhận lại ID, gán nó vào object
                        ekyc.setId(id);
                        Toast.makeText(getContext(), "Đã lưu eKYC (id: " + id + ")", Toast.LENGTH_LONG).show();
                        btnXacThuc.setEnabled(true);
                        // Ở đây bạn có thể điều hướng người dùng sang màn hình khác
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(getContext(), "Lưu eKYC thất bại: ", Toast.LENGTH_LONG).show();
                        btnXacThuc.setEnabled(true);
                    });
        });
    }

    // ---------------- Capture helpers ----------------

    private void startCaptureFlow() {
        if (!hasAnyCamera()) {
            Toast.makeText(getContext(), "Thiết bị không có camera khả dụng", Toast.LENGTH_SHORT).show();
            return;
        }
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
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
            File storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            if (storageDir == null) storageDir = context.getFilesDir();
            if (!storageDir.exists()) storageDir.mkdirs();
            File imageFile = new File(storageDir, fileName);
            if (!imageFile.exists()) imageFile.createNewFile();
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
                ekyc.setIdFrontPath(uri.toString());
                runFaceDetection(bitmap);
                runTextRecognition(bitmap, true);
            } else {
                backBitmap = bitmap;
                imgBack.setImageBitmap(bitmap);
                ekyc.setIdBackPath(uri.toString());
                runTextRecognition(bitmap, false);
            }
            updateConfirmButtonState();
        } catch (IOException e) {
            Toast.makeText(getContext(), "Lỗi đọc ảnh", Toast.LENGTH_SHORT).show();
        }

    }

    // ---------------- OCR ----------------

    private void runTextRecognition(Bitmap bitmap, boolean front) {
        TextRecognizer recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);
        InputImage image = InputImage.fromBitmap(bitmap, 0);
        recognizer.process(image)
                .addOnSuccessListener(result -> {
                    parseRecognizedText(result, front);
                    if (front) ocrFrontDone = true; else ocrBackDone = true;
                    updateConfirmButtonState();
                })
                .addOnFailureListener(e -> {
                    // đánh dấu done để không kẹt UI; nhưng confirm sẽ vẫn fail do thiếu field
                    if (front) ocrFrontDone = true; else ocrBackDone = true;
                    updateConfirmButtonState();
                    Toast.makeText(getContext(), "Nhận diện chữ thất bại", Toast.LENGTH_SHORT).show();
                    Log.e("TakePhotoFragment", "Text recognition failed", e);
                });
    }

    private void parseRecognizedText(com.google.mlkit.vision.text.Text result, boolean front) {
        String fullText = result.getText();
        if (fullText == null || fullText.isEmpty()) return;

        String[] lines = fullText.split("\n");
        Pattern idPattern = Pattern.compile("\\b\\d{9,13}\\b");
        Pattern datePattern = Pattern.compile("(\\d{2}[-/]\\d{2}[-/]\\d{4})");

        for (int i = 0; i < lines.length; i++) {
            String line = lines[i].trim();
            if (line.isEmpty()) continue;
            String lower = line.toLowerCase();

            if (front) {
                // Số CCCD/CMND
                Matcher idMatcher = idPattern.matcher(line.replaceAll("\\s", ""));
                if (ekyc.getNationalIdNumber() == null && idMatcher.find()) {
                    ekyc.setNationalIdNumber(idMatcher.group());
                    continue;
                }
                // Họ và tên
                if ((lower.contains("họ") || lower.contains("ho")) && (lower.contains("tên") || lower.contains("ten"))) {
                    int colon = line.indexOf(":");
                    String name = null;
                    if (colon != -1) {
                        name = line.substring(colon + 1).trim();
                        if (i + 1 < lines.length && !lines[i + 1].contains(":")) {
                            name = (name + " " + lines[i + 1].trim()).trim();
                        }
                    } else {
                        int j = i + 1;
                        while (j < lines.length && lines[j].trim().isEmpty()) j++;
                        if (j < lines.length) name = lines[j].trim();
                    }
                    if (name != null && !name.isEmpty()) {
                        ekyc.setFullName(name);
                    }
                    continue;
                }
                // Ngày sinh
                if (lower.contains("ngày sinh") || lower.contains("ngay sinh")) {
                    Matcher dateMatcher = datePattern.matcher(line);
                    if (dateMatcher.find()) {
                        ekyc.setDateOfBirth(normalizeDate(dateMatcher.group(1)));
                    }
                    continue;
                }
                // Giới tính
                if (lower.contains("giới tính") || lower.contains("gioi tinh")) {
                    int colon = line.indexOf(":");
                    if (colon != -1) {
                        String[] tokens = line.substring(colon + 1).trim().split("\\s+");
                        if (tokens.length > 0) ekyc.setGender(tokens[0]);
                    } else {
                        if (lower.contains("nam")) ekyc.setGender("Nam");
                        else if (lower.contains("nữ") || lower.contains("nu")) ekyc.setGender("Nữ");
                    }
                    continue;
                }
                // Nơi thường trú
                if (lower.contains("nơi thường trú") || lower.contains("noi thuong tru")) {
                    int colon = line.indexOf(":");
                    StringBuilder addr = new StringBuilder();
                    if (colon != -1) {
                        addr.append(line.substring(colon + 1).trim());
                    } else {
                        String cleaned = line.replaceAll("(?i)nơi thường trú|noi thuong tru|place of residence", "").trim();
                        addr.append(cleaned);
                    }
                    int j = i + 1;
                    while (j < lines.length) {
                        String next = lines[j].trim();
                        if (next.contains(":")) break;
                        addr.append(" ").append(next);
                        j++;
                    }
                    ekyc.setAddress(addr.toString().trim());
                    continue;
                }
            } else {
                // Ngày cấp
                if (lower.contains("ngày, tháng, năm") || lower.contains("ngay, thang, nam") || lower.contains("ngày cấp") || lower.contains("ngay cap")) {
                    Matcher dateMatcher = datePattern.matcher(line);
                    if (dateMatcher.find()) {
                        ekyc.setDateOfIssue(normalizeDate(dateMatcher.group(1)));
                    }
                    continue;
                }
                // Nơi cấp (heuristic đơn giản)
                if (lower.contains("cục") || lower.contains("cong an") || lower.contains("công an")) {
                    int colon = line.indexOf(":");
                    String place;
                    if (colon != -1) {
                        place = line.substring(colon + 1).trim();
                    } else {
                        place = line.replaceAll("(?i)cục trưởng|cuc truong", "").trim();
                    }
                    if (!place.isEmpty()) {
                        ekyc.setPlaceOfIssue(place);
                    }
                    continue;
                }
            }
        }
    }

    private String normalizeDate(String input) {
        try {
            String sanitized = input.replace('-', '/');
            String[] parts = sanitized.split("/");
            if (parts.length == 3) {
                String day = parts[0];
                String month = parts[1];
                String year = parts[2];
                if (day.length() == 1) day = "0" + day;
                if (month.length() == 1) month = "0" + month;
                return year + "-" + month + "-" + day;
            }
        } catch (Exception ignored) { }
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
                        Toast.makeText(getContext(), "Không tìm thấy khuôn mặt trong ảnh mặt trước", Toast.LENGTH_SHORT).show();
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
    private void saveFaceBitmap(Bitmap src, Rect box){
        int left   = Math.max(0, box.left);
        int top    = Math.max(0, box.top);
        int right  = Math.min(src.getWidth(), box.right);
        int bottom = Math.min(src.getHeight(), box.bottom);
        int w = Math.max(1, right - left);
        int h = Math.max(1, bottom - top);

        try {
            Bitmap faceBmp = Bitmap.createBitmap(src, left, top, w, h);

            Context context = requireContext();
            String fileName = "face_" + (uid != null ? uid : "anon") + "_" + System.currentTimeMillis() + ".jpg";
            File storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            if (storageDir == null) storageDir = context.getFilesDir();
            if (!storageDir.exists()) storageDir.mkdirs();

            File imageFile = new File(storageDir, fileName);
            try (FileOutputStream out = new FileOutputStream(imageFile)) {
                faceBmp.compress(Bitmap.CompressFormat.JPEG, 90, out);
            }

            // Để repo có thể upload dễ hơn, dùng absolute path (file:// không cần),
            // repo sẽ tự chuyển sang Uri.fromFile(...)
            ekyc.setFaceImagePath(imageFile.getAbsolutePath());
            faceSaved = true;
        } catch (Exception e) {
            Log.e("TakePhotoFragment", "Lưu file khuôn mặt thất bại", e);
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

    private void updateConfirmButtonState() {
        boolean hasFront = frontBitmap != null && ekyc.getIdFrontPath() != null;
        boolean hasBack  = backBitmap  != null && ekyc.getIdBackPath()  != null;
        boolean basicOcr = (ekyc.getNationalIdNumber() != null && ekyc.getFullName() != null);
        boolean ready = hasFront && hasBack && basicOcr && ocrFrontDone && ocrBackDone && faceSaved;
        if (btnXacThuc != null) btnXacThuc.setEnabled(ready);
    }

    @Nullable
    private String validateEkyc(Ekyc e, boolean frontDone, boolean backDone, boolean faceOk) {
        StringBuilder sb = new StringBuilder();
        if (frontBitmap == null || e.getIdFrontPath() == null) sb.append("Ảnh mặt trước, ");
        if (backBitmap  == null || e.getIdBackPath()  == null) sb.append("Ảnh mặt sau, ");
        if (!frontDone) sb.append("OCR mặt trước chưa xong, ");
        if (!backDone)  sb.append("OCR mặt sau chưa xong, ");
        if (!faceOk || e.getFaceImagePath() == null) sb.append("Khuôn mặt chưa trích xuất, ");

        // Tối thiểu yêu cầu để lưu
        if (e.getNationalIdNumber() == null) sb.append("Số CCCD, ");
        if (e.getFullName() == null)         sb.append("Họ tên, ");

//         Nếu muốn khắt khe hơn thì bật thêm các check bên dưới:
         if (e.getDateOfBirth() == null)      sb.append("Ngày sinh, ");
         if (e.getGender() == null)           sb.append("Giới tính, ");
         if (e.getAddress() == null)          sb.append("Địa chỉ, ");
         if (e.getDateOfIssue() == null)      sb.append("Ngày cấp, ");
         if (e.getPlaceOfIssue() == null)     sb.append("Nơi cấp, ");

        if (sb.length() == 0) return null;
        String s = sb.toString().trim();
        if (s.endsWith(",")) s = s.substring(0, s.length() - 1);
        return s;
    }
}

package com.example.tad_bank_t1.ui.fragment;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.graphics.Rect;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.airbnb.lottie.Lottie;
import com.airbnb.lottie.LottieAnimationView;
import com.example.tad_bank_t1.R;
import com.example.tad_bank_t1.data.model.Ekyc;
import com.example.tad_bank_t1.data.repository.ekyc.FirebaseEkycRepository;
import com.example.tad_bank_t1.data.repository.users.FirebaseUserRepository;
import com.example.tad_bank_t1.data.repository.users.UserRepository;
import com.example.tad_bank_t1.ui.activity.MainActivity;
import com.example.tad_bank_t1.ui.activity.SignUpActivity;
import com.example.tad_bank_t1.util.FaceEmbeddingModel;
import com.google.android.gms.tasks.Task;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.face.Face;
import com.google.mlkit.vision.face.FaceDetection;
import com.google.mlkit.vision.face.FaceDetector;
import com.google.mlkit.vision.face.FaceDetectorOptions;

import java.io.IOException;
import java.util.Date;
import java.util.List;

public class FaceVerify1Fragment extends Fragment {
    private static final String ARG_MODE = "key_mode";
    private static final int MODE_SIGNUP = 0;
    private static final int MODE_FACE_LOGIN = 1;

    private static final String ARG_EKYC = "key_ekyc";
    private static final String ARG_UID = "key_uid";
    private static final String ARG_PHONE = "key_phone";
    private static final String ARG_USERNAME = "key_username";
    private static final String ARG_EMAIL = "key_email";

    private int mode = MODE_SIGNUP;
    private ImageView imgPicture;
    private Ekyc ekyc;
    private String uid, phone, username, email;
    private ImageButton btnTakePhoto, btnFile;
    private LottieAnimationView success;
    private String avatarDataUrl;

    private ActivityResultLauncher<Void> takePreviewLauncher;
    private ActivityResultLauncher<String> pickImageLauncher;

    public FaceVerify1Fragment() {
    }

    public static FaceVerify1Fragment newInstance(Ekyc ekyc, String uid, String phone, String username, String email) {
        FaceVerify1Fragment fragment = new FaceVerify1Fragment();
        Bundle args = new Bundle();
        args.putInt(ARG_MODE, MODE_SIGNUP);
        args.putSerializable(ARG_EKYC, ekyc);
        args.putString(ARG_UID, uid);
        args.putString(ARG_PHONE, phone);
        args.putString(ARG_USERNAME, username);
        args.putString(ARG_EMAIL, email);
        fragment.setArguments(args);
        return fragment;
    }
    public static FaceVerify1Fragment newForFaceLogin(String uid) {
        FaceVerify1Fragment fragment = new FaceVerify1Fragment();
        Bundle args = new Bundle();
        args.putInt(ARG_MODE, MODE_FACE_LOGIN);
        args.putString(ARG_UID, uid);
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                ekyc = getArguments().getSerializable(ARG_EKYC, Ekyc.class);
            } else {
                ekyc = (Ekyc) getArguments().getSerializable(ARG_EKYC);
            }
            uid = getArguments().getString(ARG_UID);
            phone = getArguments().getString(ARG_PHONE);
            username = getArguments().getString(ARG_USERNAME);
            email = getArguments().getString(ARG_EMAIL);
            mode = getArguments().getInt(ARG_MODE, MODE_SIGNUP);
        }
        takePreviewLauncher = registerForActivityResult(new ActivityResultContracts.TakePicturePreview(), bmp -> {
            if (bmp == null) {
                toast("Không chụp được ảnh.");
                return;
            }
            verifyAgainstReference(bmp);
        });
        pickImageLauncher = registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
            if (uri == null) return;
            try {
                Bitmap bmp = loadBitmapFromUri(uri);
                verifyAgainstReference(bmp);
            } catch (IOException e) {
                toast("Không đọc được ảnh đã chọn.");
            }
        });
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_face_verify1, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        btnTakePhoto = view.findViewById(R.id.btnTakePhoto);
        btnFile = view.findViewById(R.id.btnFile);
        imgPicture = view.findViewById(R.id.imgPicture);
//        imgPicture.setImageBitmap(decodeDataUrlToBitmap(ekyc != null ? ekyc.getFaceImagePath() : null));
        btnTakePhoto.setOnClickListener(v -> takePreviewLauncher.launch(null));
        btnFile.setOnClickListener(v -> pickImageLauncher.launch("image/*"));
        success = view.findViewById(R.id.success);
    }

    private void verifyAgainstReference(Bitmap captureBmp) {
//        if (ekyc == null || ekyc.getFaceImagePath() == null || !ekyc.getFaceImagePath().startsWith("data:image/")) {
//            toast("Thiếu ảnh tham chiếu.");
//            return;
//        }
//        Bitmap refBmp = decodeDataUrlToBitmap(ekyc.getFaceImagePath());
//        Bitmap softwareRef = ensureSoftwareBitmap(refBmp);
//        if (softwareRef == null) {
//            toast("Không thể xử lý ảnh tham chiếu.");
//            return;
//        }
//        final Context appContext = requireContext().getApplicationContext();
//        final FaceEmbeddingModel embeddingModel = FaceEmbeddingModel.getInstance(appContext);
//        detectAndCropSingleFace(captureBmp)
//                .addOnSuccessListener(capFace -> {
//                    if (capFace == null) {
//                        toast("Vui lòng chụp 1 khuôn mặt rõ ràng vào khung.");
//                        return;
//                    }
//                    Bitmap softwareCap = ensureSoftwareBitmap(capFace);
//                    if (softwareCap == null) {
//                        toast("Không thể xử lý khuôn mặt chụp được.");
//                        return;
//                    }
//                    Bitmap refScaled = null;
//                    Bitmap capScaled = null;
//                    try {
//                        refScaled = Bitmap.createScaledBitmap(softwareRef, 160, 160, true);
//                        capScaled = Bitmap.createScaledBitmap(softwareCap, 160, 160, true);
//                        imgPicture.setImageBitmap(capScaled);
//                        final Bitmap finalRef = refScaled;
//                        final Bitmap finalCap = capScaled;
//                        new Thread(() -> {
//                            try {
//                                float[] embRef = embeddingModel.embed(finalRef);
//                                float[] embCap = embeddingModel.embed(finalCap);
//                                if (embRef == null || embCap == null) {
//                                    runOnUiThreadSafe(() -> toast("Không trích xuất được đặc trưng khuôn mặt."));
//                                    return;
//                                }
//                                double similarity = cosineSimilarity(embRef, embCap);
//                                boolean isMatch = similarity >= 0.60;
//                                if(isMatch){
//                                    ekyc.setVerified(true);
//                                    ekyc.setVerifiedAt(new Date());
//                                    FirebaseEkycRepository ekycRepository = new FirebaseEkycRepository();
//                                    ekycRepository.update(ekyc.getId(), ekyc)
//                                            .addOnSuccessListener(aVoid -> {
//                                                runOnUiThreadSafe(() -> toast("Xác thực khuôn mặt thành công!"));
//                                                success.setVisibility(View.VISIBLE);
//                                                success.playAnimation();
//                                                success.addAnimatorListener(new android.animation.AnimatorListenerAdapter() {
//                                                    @Override
//                                                    public void onAnimationEnd(Animator animation) {
//                                                        super.onAnimationEnd(animation);
//                                                        EmailVerifyFragment emailVerifyFragment =
//                                                                EmailVerifyFragment.newInstance(ekyc, uid, phone, username, email);
//
//                                                        if (getActivity() instanceof SignUpActivity) {
//                                                            ((SignUpActivity) getActivity()).navigateTo(emailVerifyFragment, true);
//                                                        }
//
//                                                    }
//                                                });
//                                            })
//                                            .addOnFailureListener(e -> {
//                                                runOnUiThreadSafe(() ->
//                                                        toast("Cập nhật EKYC thất bại: " + e.getMessage()));
//                                            });
//                                }else{
//                                    runOnUiThreadSafe(() -> toast("Khuôn mặt không trùng khớp"));
//                                }
//
////                                String message = isMatch
////                                        ? String.format(Locale.US, "Xác thực thành công (%.3f)", similarity)
////                                        : String.format(Locale.US, "Không khớp (%.3f)", similarity);
////                                runOnUiThreadSafe(() -> toast(message));
//
//                            } catch (Exception e) {
//                                String msg = e.getMessage() != null ? e.getMessage() : "Lỗi xác thực.";
//                                runOnUiThreadSafe(() -> toast(msg));
//                            }
//                        }).start();
//                    } catch (OutOfMemoryError oom) {
//                        runOnUiThreadSafe(() -> toast("Bộ nhớ không đủ."));
//                    } catch (Exception e) {
//                        runOnUiThreadSafe(() -> toast("Lỗi xử lý ảnh."));
//                    }
//                })
//                .addOnFailureListener(e -> {
//                    toast("Lỗi nhận diện khuôn mặt: " + e.getMessage());
//                });
        ensureReferenceLoadedThen(() -> {
            final String refDataUrl = (mode == MODE_FACE_LOGIN)
                    ? avatarDataUrl
                    : (ekyc != null ? ekyc.getFaceImagePath() : null);
            if (refDataUrl == null || !refDataUrl.startsWith("data:image/")) {
                toast("Thiếu ảnh tham chiếu.");
                return;
            }
            Bitmap refBmp = decodeDataUrlToBitmap(refDataUrl);
            Bitmap softwareRef = ensureSoftwareBitmap(refBmp);
            if (softwareRef == null) {
                toast("Không thể xử lý ảnh tham chiếu.");
                return;
            }

            final Context appContext = requireContext().getApplicationContext();
            final FaceEmbeddingModel embeddingModel = FaceEmbeddingModel.getInstance(appContext);

            detectAndCropSingleFace(captureBmp)
                    .addOnSuccessListener(capFace -> {
                        if (capFace == null) {
                            toast("Vui lòng chụp 1 khuôn mặt rõ ràng vào khung.");
                            return;
                        }
                        Bitmap softwareCap = ensureSoftwareBitmap(capFace);
                        if (softwareCap == null) {
                            toast("Không thể xử lý khuôn mặt chụp được.");
                            return;
                        }

                        try {
                            Bitmap refScaled = Bitmap.createScaledBitmap(softwareRef, 160, 160, true);
                            Bitmap capScaled = Bitmap.createScaledBitmap(softwareCap, 160, 160, true);
                            imgPicture.setImageBitmap(capScaled);

                            new Thread(() -> {
                                try {
                                    float[] embRef = embeddingModel.embed(refScaled);
                                    float[] embCap = embeddingModel.embed(capScaled);
                                    if (embRef == null || embCap == null) {
                                        runOnUiThreadSafe(() -> toast("Không trích xuất được đặc trưng khuôn mặt."));
                                        return;
                                    }
                                    double similarity = cosineSimilarity(embRef, embCap);
                                    boolean isMatch = similarity >= 0.60;

                                    if (isMatch) {
                                        if (mode == MODE_SIGNUP) {
                                            // Hành vi cũ: cập nhật EKYC -> EmailVerify
                                            ekyc.setVerified(true);
                                            ekyc.setVerifiedAt(new Date());
                                            FirebaseEkycRepository ekycRepository = new FirebaseEkycRepository();
                                            ekycRepository.update(ekyc.getId(), ekyc)
                                                    .addOnSuccessListener(aVoid -> {
                                                        runOnUiThreadSafe(() -> toast("Xác thực khuôn mặt thành công!"));
                                                        success.setVisibility(View.VISIBLE);
                                                        success.playAnimation();
                                                        success.addAnimatorListener(new AnimatorListenerAdapter() {
                                                            @Override
                                                            public void onAnimationEnd(Animator animation) {
                                                                EmailVerifyFragment emailVerifyFragment =
                                                                        EmailVerifyFragment.newInstance(ekyc, uid, phone, username, email);
                                                                if (getActivity() instanceof SignUpActivity) {
                                                                    ((SignUpActivity) getActivity()).navigateTo(emailVerifyFragment, true);
                                                                }
                                                            }
                                                        });
                                                    })
                                                    .addOnFailureListener(e ->
                                                            runOnUiThreadSafe(() -> toast("Cập nhật EKYC thất bại: " + e.getMessage())));
                                        } else {
                                            // FACE_LOGIN: mở MainActivity với uid
                                            runOnUiThreadSafe(() -> {
                                                toast("Đăng nhập bằng Face ID thành công!");
                                                success.setVisibility(View.VISIBLE);
                                                success.playAnimation();
                                                success.addAnimatorListener(new android.animation.AnimatorListenerAdapter(){
                                                    @Override
                                                    public void onAnimationEnd(Animator animation) {
                                                        super.onAnimationEnd(animation);
                                                        if (getActivity() != null) {
                                                            Intent i = new Intent(getActivity(), MainActivity.class);
                                                            i.putExtra(MainActivity.EXTRA_USERID, uid);
                                                            startActivity(i);
                                                            getActivity().finish();
                                                        }
                                                    }
                                                });
                                            });
                                        }
                                    } else {
                                        runOnUiThreadSafe(() -> toast("Khuôn mặt không trùng khớp"));
                                    }
                                } catch (Exception e) {
                                    runOnUiThreadSafe(() -> toast(e.getMessage() != null ? e.getMessage() : "Lỗi xác thực."));
                                }
                            }).start();

                        } catch (OutOfMemoryError oom) {
                            runOnUiThreadSafe(() -> toast("Bộ nhớ không đủ."));
                        } catch (Exception e) {
                            runOnUiThreadSafe(() -> toast("Lỗi xử lý ảnh."));
                        }
                    })
                    .addOnFailureListener(e -> toast("Lỗi nhận diện khuôn mặt: " + e.getMessage()));
        });
    }
    private void ensureReferenceLoadedThen(Runnable cont) {
        if (mode == MODE_SIGNUP) {
            cont.run();
            return;
        }
        // FACE_LOGIN
        if (!TextUtils.isEmpty(avatarDataUrl) && avatarDataUrl.startsWith("data:image/")) {
            cont.run();
            return;
        }
        if (TextUtils.isEmpty(uid)) {
            toast("Thiếu UID để tải ảnh tham chiếu.");
            return;
        }

        UserRepository userRepo = new FirebaseUserRepository();
        userRepo.getById(uid)
                .addOnSuccessListener(user -> {
                    if (user == null) {
                        toast("Không tìm thấy tài khoản.");
                        return;
                    }
                    avatarDataUrl = user.getAvatar();
                    if (TextUtils.isEmpty(avatarDataUrl) || !avatarDataUrl.startsWith("data:image/")) {
                        toast("Tài khoản chưa lưu ảnh chân dung hợp lệ.");
                        return;
                    }
                    cont.run();
                })
                .addOnFailureListener(e -> toast("Lỗi tải tài khoản: " + e.getMessage()));
    }


    private Bitmap loadBitmapFromUri(Uri uri) throws IOException {
        if (Build.VERSION.SDK_INT >= 28) {
            ImageDecoder.Source src = ImageDecoder.createSource(requireContext().getContentResolver(), uri);
            return ImageDecoder.decodeBitmap(src);
        } else {
            return MediaStore.Images.Media.getBitmap(requireContext().getContentResolver(), uri);
        }
    }
    private Bitmap decodeDataUrlToBitmap(String dataUrl) {
        if (dataUrl == null) return null;
        try {
            int comma = dataUrl.indexOf(',');
            if (comma < 0) return null;
            String b64 = dataUrl.substring(comma + 1);
            byte[] bytes = Base64.decode(b64, Base64.DEFAULT);
            return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        } catch (Exception e) {
            return null;
        }
    }
    private Task<Bitmap> detectAndCropSingleFace(Bitmap bitmap) {
        FaceDetectorOptions options = new FaceDetectorOptions.Builder()
                .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
                .build();
        FaceDetector detector = FaceDetection.getClient(options);
        InputImage image = InputImage.fromBitmap(bitmap, 0);
        return detector.process(image).continueWith(task -> {
            List<Face> faces = task.getResult();
            if (faces == null || faces.isEmpty()) {
                return null;
            }
            // Choose the largest face by area
            Face best = faces.get(0);
            int bestArea = area(best.getBoundingBox());
            for (int i = 1; i < faces.size(); i++) {
                Face f = faces.get(i);
                int a = area(f.getBoundingBox());
                if (a > bestArea) {
                    best = f;
                    bestArea = a;
                }
            }
            Rect b = best.getBoundingBox();
            // Clamp bounding box to the image dimensions
            Rect safe = new Rect(
                    Math.max(0, b.left),
                    Math.max(0, b.top),
                    Math.min(bitmap.getWidth(), b.right),
                    Math.min(bitmap.getHeight(), b.bottom)
            );
            if (safe.width() <= 0 || safe.height() <= 0) return null;
            return Bitmap.createBitmap(bitmap, safe.left, safe.top, safe.width(), safe.height());
        });
    }

    /**
     * Calculates the area of a {@link Rect}.
     */
    private int area(Rect r) {
        return Math.max(0, r.width()) * Math.max(0, r.height());
    }

    /**
     * Computes the cosine similarity between two embedding vectors.
     */
    private double cosineSimilarity(float[] a, float[] b) {
        double dot = 0, na = 0, nb = 0;
        for (int i = 0; i < Math.min(a.length, b.length); i++) {
            dot += a[i] * b[i];
            na += a[i] * a[i];
            nb += b[i] * b[i];
        }
        return dot / (Math.sqrt(na) * Math.sqrt(nb) + 1e-9);
    }

    private boolean isHardwareBitmap(Bitmap bitmap) {
        if (bitmap == null) return false;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return bitmap.getConfig() == Bitmap.Config.HARDWARE;
        }
        return false;
    }
    private Bitmap ensureSoftwareBitmap(Bitmap bitmap) {
        if (bitmap == null) return null;
        if (!isHardwareBitmap(bitmap)) {
            return bitmap;
        }
        Bitmap software = bitmap.copy(Bitmap.Config.ARGB_8888, false);
        if (software != null) return software;
        // Fallback: draw the bitmap into a new ARGB_8888 bitmap using Canvas
        Bitmap fallback = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(fallback);
        canvas.drawBitmap(bitmap, 0, 0, null);
        return fallback;
    }
    private void runOnUiThreadSafe(Runnable runnable) {
        if (isAdded() && getActivity() != null && !getActivity().isFinishing() && !getActivity().isDestroyed()) {
            requireActivity().runOnUiThread(runnable);
        }
    }
    private void toast(String msg) {
        Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
    }
}
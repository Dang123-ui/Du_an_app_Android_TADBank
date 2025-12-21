package com.example.tad_bank_t1.ui.fragment.officer;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.example.tad_bank_t1.R;
import com.example.tad_bank_t1.ui.viewmodel.officer.PersonalCardUiState;
import com.example.tad_bank_t1.ui.viewmodel.officer.PersonalCardViewModel;
import com.example.tad_bank_t1.util.DataUriUtil;
import com.example.tad_bank_t1.util.PhoneUtil;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import com.google.firebase.Timestamp;
/**
 * A simple {@link Fragment} subclass.
 * Use the {@link EditPersonalCardDialogFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EditPersonalCardDialogFragment extends BottomSheetDialogFragment {

    private static final String ARG_USER_ID = "user_id";

    private String userId;

    public EditPersonalCardDialogFragment() {
        // Required empty public constructor
    }
    // TODO: Rename and change types and number of parameters
    public static EditPersonalCardDialogFragment newInstance(String userId) {
        EditPersonalCardDialogFragment fragment = new EditPersonalCardDialogFragment();
        Bundle args = new Bundle();
        args.putString(ARG_USER_ID, userId);
        fragment.setArguments(args);
        return fragment;
    }
    private PersonalCardViewModel vm;
    private ImageView imgAvatarPreview;
    private MaterialButton btnCancel, btnSave;
    private ImageButton btnTakePhoto,  btnPickPhoto;
    private TextInputEditText edtFullName, edtPhone, edtEmail;
    private MaterialAutoCompleteTextView edtGender, edtStatus, edtSegment, edtRisk;
    private Uri pendingCameraUri = null;
    private Uri selectedAvatarUri = null;

    private PersonalCardUiState currentState;

    private final ActivityResultLauncher<String> requestCameraPermission =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), granted -> {
                if (granted) launchCamera();
            });

    private final ActivityResultLauncher<String> requestReadImagesPermission =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), granted -> {
                if (granted) pickFromGallery();
            });
    private final ActivityResultLauncher<Uri> takePicture =
            registerForActivityResult(new ActivityResultContracts.TakePicture(), success -> {
                if (success && pendingCameraUri != null) {
                    selectedAvatarUri = pendingCameraUri;
                    imgAvatarPreview.setImageURI(selectedAvatarUri);
                }
            });
    private final ActivityResultLauncher<String> pickImage =
            registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
                if (uri != null) {
                    selectedAvatarUri = uri;
                    imgAvatarPreview.setImageURI(selectedAvatarUri);
                }
            });
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            userId = args.getString(ARG_USER_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_edit_personal_card_dialog, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        vm = new ViewModelProvider(requireActivity()).get(PersonalCardViewModel.class);
        bindView(view);
        setupDropdowns();
        vm.getState().observe(getViewLifecycleOwner(), s -> {
            if(s == null) return;
            currentState = s;
            prefill(s);
        });
        if(!TextUtils.isEmpty(userId)) vm.start(userId);

        btnTakePhoto.setOnClickListener(v -> ensureCameraPermissionThenLaunch());
        btnPickPhoto.setOnClickListener(v -> ensureReadImagesPermissionThenPick());

        btnCancel.setOnClickListener(v -> dismiss());
        btnSave.setOnClickListener(v -> onSaveClicked());
    }
    private void bindView(View view){
        imgAvatarPreview = view.findViewById(R.id.imgAvatarPreview);
        btnTakePhoto = view.findViewById(R.id.btnTakePhoto);
        btnPickPhoto = view.findViewById(R.id.btnPickPhoto);
        btnCancel = view.findViewById(R.id.btnCancel);
        btnSave = view.findViewById(R.id.btnSave);

        edtFullName = view.findViewById(R.id.edtFullName);
        edtPhone = view.findViewById(R.id.edtPhone);
        edtEmail = view.findViewById(R.id.edtEmail);
        edtGender = view.findViewById(R.id.edtGender);
        edtStatus = view.findViewById(R.id.edtStatus);
        edtSegment = view.findViewById(R.id.edtSegment);
        edtRisk = view.findViewById(R.id.edtRisk);
    }
    private void setupDropdowns(){
        if (edtGender != null) {
            String[] genders = new String[]{"MALE", "FEMALE", "OTHER"};
            edtGender.setAdapter(new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, genders));
        }
        if (edtStatus != null) {
            String[] statuses = new String[]{"ACTIVE", "LOCKED"};
            edtStatus.setAdapter(new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, statuses));
        }
        if (edtSegment != null) {
            String[] segments = new String[]{"MASS", "PREMIER", "PRIORITY"};
            edtSegment.setAdapter(new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, segments));
        }
        if (edtRisk != null) {
            String[] risks = new String[]{"LOW", "MEDIUM", "HIGH"};
            edtRisk.setAdapter(new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, risks));
        }
    }
    private void prefill(@NonNull PersonalCardUiState s) {
        edtFullName.setText(nz(s.fullName));
        edtPhone.setText(nz(s.phone));
        edtEmail.setText(nz(s.email));

        edtGender.setText(nz(s.gender), false);
        edtStatus.setText(nz(s.status), false);
        edtSegment.setText(nz(s.segment), false);
        edtRisk.setText(nz(s.risk), false);
        if (s.avatarUrl != null && !s.avatarUrl.trim().isEmpty()) {
            Bitmap bmp = DataUriUtil.decodeToBitmap(s.avatarUrl);
            if (bmp != null) {
                imgAvatarPreview.setImageBitmap(bmp);
            } else {
                imgAvatarPreview.setImageResource(R.drawable.ic_user);
            }
        } else {
            imgAvatarPreview.setImageResource(R.drawable.ic_user);
        }

        // Avatar preview: nếu avatar là dataUri/base64 bạn load theo util + Glide như adapter.
        // Ở đây không hardcode vì tuỳ DataUriUtil của bạn.
    }
    private void onSaveClicked() {
        if (TextUtils.isEmpty(userId)) return;
        String fullName = safeText(edtFullName);
        if (TextUtils.isEmpty(fullName)) {
            edtFullName.setError("Required");
            return;
        }
        String phone = safeText(edtPhone);
        phone = PhoneUtil.toE164Vn(phone);
        String email = safeText(edtEmail);

        String gender = safeText(edtGender);
        String status = safeText(edtStatus);
        String segment = safeText(edtSegment);
        String risk = safeText(edtRisk);
        com.google.firebase.Timestamp dobTs = (currentState != null)
                ? toTimestampFromDobStringOrNow(currentState.dobDisplay)
                : com.google.firebase.Timestamp.now();

        // idNumber: giữ nguyên
        String idNumber = currentState != null ? nz(currentState.idNumber) : "";
        String avatarDataUri;

        if (selectedAvatarUri != null) {
            avatarDataUri = toDataUriFromUri(selectedAvatarUri);
            if (avatarDataUri == null) {
                avatarDataUri = currentState != null ? nz(currentState.avatarUrl) : "";
            }
        } else {
            avatarDataUri = currentState != null ? nz(currentState.avatarUrl) : "";
        }
        if (TextUtils.isEmpty(gender) && currentState != null) gender = nz(currentState.gender);
        if (TextUtils.isEmpty(status) && currentState != null) status = nz(currentState.status);
        if (TextUtils.isEmpty(segment) && currentState != null) segment = nz(currentState.segment);
        if (TextUtils.isEmpty(risk) && currentState != null) risk = nz(currentState.risk);

        if (TextUtils.isEmpty(gender)) gender = "MALE";
        if (TextUtils.isEmpty(status)) status = "ACTIVE";
        if (TextUtils.isEmpty(segment)) segment = "MASS";
        if (TextUtils.isEmpty(risk)) risk = "LOW";

        vm.updatePersonal(
                        userId,
                        fullName,
                        phone,
                        email,
                        dobTs,
                        gender,
                        idNumber,
                        status,
                        segment,
                        risk,
                        avatarDataUri
                ).addOnSuccessListener(unused -> {
                    Bundle b = new Bundle();
                    b.putBoolean(CustomUserProfileFragment.KEY_UPDATED, true);

                    getParentFragmentManager().setFragmentResult(
                            CustomUserProfileFragment.REQ_PERSONAL_UPDATED,
                            b
                    );

                    dismiss();
                })
                .addOnFailureListener(e -> {
                    // tuỳ bạn: Toast/Snackbar; hiện tại không dismiss để user sửa lại
                });
    }
    private void ensureCameraPermissionThenLaunch() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            launchCamera();
        } else {
            requestCameraPermission.launch(Manifest.permission.CAMERA);
        }
    }

    private void ensureReadImagesPermissionThenPick() {
        String perm = (Build.VERSION.SDK_INT >= 33)
                ? Manifest.permission.READ_MEDIA_IMAGES
                : Manifest.permission.READ_EXTERNAL_STORAGE;

        if (ContextCompat.checkSelfPermission(requireContext(), perm)
                == PackageManager.PERMISSION_GRANTED) {
            pickFromGallery();
        } else {
            requestReadImagesPermission.launch(perm);
        }
    }

    private void launchCamera() {
        pendingCameraUri = createImageUri();
        if (pendingCameraUri != null) takePicture.launch(pendingCameraUri);
    }

    private void pickFromGallery() {
        pickImage.launch("image/*");
    }

    private Uri createImageUri() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.DISPLAY_NAME, "avatar_" + System.currentTimeMillis() + ".jpg");
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        return requireContext().getContentResolver()
                .insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
    }

    // ===== Helpers =====

    private static String safeText(@Nullable android.widget.EditText edt) {
        if (edt == null || edt.getText() == null) return "";
        return edt.getText().toString().trim();
    }

    private static String nz(@Nullable String s) {
        return s == null ? "" : s.trim();
    }

    private Timestamp toTimestampFromDobStringOrNow(@Nullable String dobText) {
        // dobText đang là "dd/MM/yyyy" từ PersonalCardViewModel.start()
        if (!TextUtils.isEmpty(dobText)) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                sdf.setLenient(false);
                Date d = sdf.parse(dobText);
                if (d != null) return new Timestamp(d);
            } catch (Exception ignored) {}
        }
        return new Timestamp(new Date());
    }

    /**
     * TODO: Thay bằng DataUriUtil của project bạn:
     * - đọc bytes từ InputStream
     * - encode base64
     * - tạo "data:image/jpeg;base64,...." (đúng format bạn đang dùng)
     */
    @Nullable
    private String toDataUriFromUri(@NonNull Uri uri) {
        try {
            ContentResolver cr = requireContext().getContentResolver();

            Bitmap bitmap;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                bitmap = android.graphics.ImageDecoder.decodeBitmap(
                        android.graphics.ImageDecoder.createSource(cr, uri)
                );
            } else {
                bitmap = MediaStore.Images.Media.getBitmap(cr, uri);
            }

            if (bitmap == null) return null;

            // ===== Resize để tránh Firestore > 1MB =====
            int maxSize = 512; // 256 / 512 đều ổn
            int w = bitmap.getWidth();
            int h = bitmap.getHeight();

            if (w > maxSize || h > maxSize) {
                float ratio = Math.min(
                        maxSize / (float) w,
                        maxSize / (float) h
                );
                bitmap = Bitmap.createScaledBitmap(
                        bitmap,
                        Math.round(w * ratio),
                        Math.round(h * ratio),
                        true
                );
            }

            // ===== Nén JPEG =====
            java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 75, out); // quality 70–80 là đẹp
            byte[] jpegBytes = out.toByteArray();

            // ===== Convert sang dataUri =====
            return DataUriUtil.toJpegDataUri(jpegBytes);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
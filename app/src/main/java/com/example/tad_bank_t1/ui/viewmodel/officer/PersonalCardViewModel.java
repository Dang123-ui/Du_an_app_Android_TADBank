package com.example.tad_bank_t1.ui.viewmodel.officer;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.tad_bank_t1.data.model.User;
import com.example.tad_bank_t1.data.model.enums.Gender;
import com.example.tad_bank_t1.data.model.enums.UserRisk;
import com.example.tad_bank_t1.data.model.enums.UserSegment;
import com.example.tad_bank_t1.data.model.enums.UserStatus;
import com.example.tad_bank_t1.data.repository.users.FirebaseUserRepository;
import com.example.tad_bank_t1.data.repository.users.UserRepository;
import com.example.tad_bank_t1.util.PhoneUtil;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.Timestamp;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class PersonalCardViewModel extends ViewModel {

    private final UserRepository userRepo = new FirebaseUserRepository();
    private final MutableLiveData<PersonalCardUiState> state = new MutableLiveData<>(null);
    private final MutableLiveData<String> error = new MutableLiveData<>(null);
    private String currentUserId;

    public LiveData<PersonalCardUiState> getState() { return state; }
    public LiveData<String> getError() { return error; }

    public void start(@NonNull String userId) {
        currentUserId = userId;
        error.setValue(null);

        userRepo.getById(userId)
                .addOnSuccessListener(u -> {
                    if (!userId.equals(currentUserId)) return;

                    if (u == null) {
                        error.setValue("User not found");
                        return;
                    }

                    String fullName = nz(u.getFullName(), "—");
                    String idNumber = nz(u.getIdNumber(), null);
                    String uidText = (idNumber != null) ? idNumber : userId;

                    String statusText  = (u.getStatus()  != null) ? u.getStatus().name()  : "—";
                    String segmentText = (u.getSegment() != null) ? u.getSegment().name() : "—";
                    String riskText    = (u.getRisk()    != null) ? u.getRisk().name()    : "—";
                    String genderText  = (u.getGender()  != null) ? u.getGender().name()  : "MALE";

                    String phone = nz(u.getPhone(), "");
                    phone = PhoneUtil.toVnLocalDisplay(phone);

                    String email = nz(u.getEmail(), "");
                    String address = u.getAddress();

                    Date dob = u.getDateOfBirth();

                    // 2) dob display (phục vụ UI)
                    String dobDisplay = "—";
                    if (dob != null) {
                        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                        dobDisplay = sdf.format(dob);
                    }

                    String avatar = u.getAvatarUser();

                    // IMPORTANT: Giữ đúng thứ tự tham số theo constructor PersonalCardUiState của bạn
                    state.setValue(new PersonalCardUiState(
                            userId,
                            fullName,
                            "UID: " + uidText,
                            statusText,
                            segmentText,
                            riskText,
                            phone,
                            email,
                            dobDisplay,
                            genderText,
                            nz(idNumber, "—"),
                            avatar,
                            address
                    ));
                })
                .addOnFailureListener(e -> {
                    if (!userId.equals(currentUserId)) return;
                    error.setValue(e.getMessage());
                });
    }

    public void stop() {
        // nếu sau này bạn dùng snapshot listener thì remove ở đây
    }

    public Task<Void> updatePersonal(
            @NonNull String userId,
            @NonNull String fullName,
            @NonNull String phone,
            @NonNull String email,
            @NonNull Timestamp dateOfBirth,
            @NonNull String gender,
            @NonNull String idNumber,
            @NonNull String status,
            @NonNull String segment,
            @NonNull String risk,
            @NonNull String avatarDataUri
    ) {
        return userRepo.getById(userId).continueWithTask(t -> {
            if (!t.isSuccessful()) {
                Exception e = t.getException();
                return Tasks.forException(e != null ? e : new RuntimeException("Load user failed"));
            }

            User u = t.getResult();
            if (u == null) return Tasks.forException(new RuntimeException("User not found"));

            u.setFullName(fullName);
            u.setPhone(phone);
            u.setEmail(email);
            u.setIdNumber(idNumber);

            u.setDateOfBirth(dateOfBirth.toDate());

            UserStatus st = parseUserStatus(status);
            if (st != null) u.setStatus(st);

            Gender g = parseGender(gender);
            if (g != null) u.setGender(g);

            UserSegment seg = parseSegment(segment);
            if (seg != null) u.setSegment(seg);

            UserRisk rk = parseRisk(risk);
            if (rk != null) u.setRisk(rk);

            u.setLastActiveAt(new Date());
            u.setAvatarUser(avatarDataUri);

            return userRepo.update(userId, u);
        }).addOnSuccessListener(v -> {
            // Sau khi update xong, reload lại state để UI refresh ngay
            if (userId.equals(currentUserId)) start(userId);
        });
    }

    @Override
    protected void onCleared() {
        stop();
    }

    private static String nz(String s, String def) {
        if (s == null) return def;
        String t = s.trim();
        return t.isEmpty() ? def : t;
    }

    private static UserStatus parseUserStatus(String s) {
        if (s == null) return null;
        String t = s.trim();
        if (t.isEmpty()) return null;

        try { return UserStatus.valueOf(t.toUpperCase()); } catch (Exception ignored) {}

        String up = t.toUpperCase();
        if (up.equals("LOCK")) return UserStatus.LOCKED;

        return null;
    }

    private static Gender parseGender(String s) {
        if (s == null) return null;
        String t = s.trim();
        if (t.isEmpty()) return null;

        String up = t.toUpperCase();
        if (up.equals("NAM") || up.equals("MALE")) return Gender.MALE;
        if (up.equals("NỮ") || up.equals("NU") || up.equals("FEMALE")) return Gender.FEMALE;
        if (up.equals("KHÁC") || up.equals("KHAC") || up.equals("OTHER")) return Gender.OTHER;

        try { return Gender.valueOf(up); } catch (Exception ignored) {}
        return null;
    }

    private static UserSegment parseSegment(String s) {
        if (s == null) return null;
        String up = s.trim().toUpperCase();
        if (up.isEmpty()) return null;

        if (up.equals("MASS")) return UserSegment.MASS;
        if (up.equals("PREMIER")) return UserSegment.PREMIER;
        if (up.equals("PRIORITY")) return UserSegment.PRIORITY;

        try { return UserSegment.valueOf(up); } catch (Exception ignored) {}
        return null;
    }

    private static UserRisk parseRisk(String s) {
        if (s == null) return null;
        String up = s.trim().toUpperCase();
        if (up.isEmpty()) return null;

        if (up.equals("LOW") || up.equals("THAP") || up.equals("THẤP")) return UserRisk.LOW;
        if (up.equals("MEDIUM") || up.equals("VUA") || up.equals("VỪA")) return UserRisk.MEDIUM;
        if (up.equals("HIGH") || up.equals("CAO")) return UserRisk.HIGH;

        try { return UserRisk.valueOf(up); } catch (Exception ignored) {}
        return null;
    }
}

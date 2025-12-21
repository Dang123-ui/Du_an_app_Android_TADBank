package com.example.tad_bank_t1.data.repository.users;

public interface UserOnlineOfflineListener {
    void onChanged(int onlineCount, int offlineCount);
}

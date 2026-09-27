package ru.edgar.nlremake.network;

import androidx.annotation.NonNull;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.HashMap;

public class FirebaseRepository {

    // Одно место инициализации для всего проекта
    private static DatabaseReference getBaseRef() {
        return FirebaseDatabase.getInstance().getReference();
    }

    // Получение ссылки на текущего пользователя
    private static String getUid() {
        return FirebaseAuth.getInstance().getUid();
    }

    /**
     * Однократное чтение данных о сервере пользователя (addListenerForSingleValueEvent)
     */
    public static void loadUserServerInfo(ValueEventListener listener) {
        if (getUid() == null) return;
        getBaseRef().child("Users").child("User-server").child(getUid())
                .addListenerForSingleValueEvent(listener);
    }

    /**
     * Безопасное сохранение информации о сервере
     */
    public static void saveServerInfo(int id, String name, String color, String personName) {
        if (getUid() == null) return;

        HashMap<String, Object> info = new HashMap<>();
        info.put("serverId", id);
        info.put("serverName", name);
        info.put("serverColor", color);
        info.put("personName", personName);

        getBaseRef().child("Users").child("User-server").child(getUid())
                .setValue(info);
    }
}

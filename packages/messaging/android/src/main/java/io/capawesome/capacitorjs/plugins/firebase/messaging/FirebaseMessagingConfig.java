package io.capawesome.capacitorjs.plugins.firebase.messaging;

import androidx.annotation.Nullable;
import org.json.JSONException;
import org.json.JSONObject;

public class FirebaseMessagingConfig {

    private JSONObject defaultNotificationChannel;

    public FirebaseMessagingConfig() throws JSONException {
        this.defaultNotificationChannel = new JSONObject();
        this.defaultNotificationChannel.put("id", "PushDefaultForeground");
        this.defaultNotificationChannel.put("name", "Push Notifications Foreground");
        this.defaultNotificationChannel.put("description", "Push notifications in foreground");
        this.defaultNotificationChannel.put("importance", 4);
    }

    @Nullable
    public JSONObject getDefaultNotificationChannel() {
        return defaultNotificationChannel;
    }

    public void setDefaultNotificationChannel(@Nullable JSONObject defaultNotificationChannel) {
        this.defaultNotificationChannel = defaultNotificationChannel;
    }
}

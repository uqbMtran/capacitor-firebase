package io.capawesome.capacitorjs.plugins.firebase.messaging;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.service.notification.StatusBarNotification;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import com.getcapacitor.JSArray;
import com.getcapacitor.JSObject;
import com.getcapacitor.Logger;
import java.util.List;
import org.json.JSONObject;

public class FirebaseMessaging {

    private FirebaseMessagingPlugin plugin;
    private FirebaseMessagingConfig config;
    private NotificationManager notificationManager;
    private com.google.firebase.messaging.FirebaseMessaging firebaseMessagingInstance;

    public FirebaseMessaging(FirebaseMessagingPlugin plugin, FirebaseMessagingConfig config) {
        this.plugin = plugin;
        this.config = config;
        this.notificationManager = (NotificationManager) plugin.getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
        this.firebaseMessagingInstance = com.google.firebase.messaging.FirebaseMessaging.getInstance();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            this.createDefaultNotificationChannel();
        }
    }

    public void getToken(final GetTokenResultCallback resultCallback) {
        this.firebaseMessagingInstance.setAutoInitEnabled(true);
        this.firebaseMessagingInstance.getToken()
            .addOnCompleteListener(
                task -> {
                    if (!task.isSuccessful()) {
                        Exception exception = task.getException();
                        Log.w(FirebaseMessagingPlugin.TAG, "Fetching FCM registration token failed", exception);
                        resultCallback.error(exception.getLocalizedMessage());
                        return;
                    }

                    String token = task.getResult();
                    resultCallback.success(token);
                }
            );
    }

    public void deleteToken() {
        this.firebaseMessagingInstance.deleteToken();
    }

    public StatusBarNotification[] getDeliveredNotifications() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return notificationManager.getActiveNotifications();
        } else {
            return new StatusBarNotification[] {};
        }
    }

    public void removeDeliveredNotifications(List<String> tags, List<String> ids) {
        for (int i = 0; i < tags.size(); i++) {
            try {
                String tag = tags.get(i);
                int id = Integer.parseInt(ids.get(i));
                notificationManager.cancel(tag, id);
            } catch (NumberFormatException exception) {
                Log.w(FirebaseMessagingPlugin.TAG, "removeDeliveredNotifications failed", exception);
            }
        }
    }

    public void removeAllDeliveredNotifications() {
        notificationManager.cancelAll();
    }

    public void subscribeToTopic(String topic) {
        firebaseMessagingInstance.subscribeToTopic(topic);
    }

    public void unsubscribeFromTopic(String topic) {
        firebaseMessagingInstance.unsubscribeFromTopic(topic);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void createChannel(NotificationChannel notificationChannel) {
        notificationManager.createNotificationChannel(notificationChannel);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void deleteChannelById(String id) {
        notificationManager.deleteNotificationChannel(id);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public List<NotificationChannel> getNotificationChannels() {
        return notificationManager.getNotificationChannels();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createDefaultNotificationChannel() {
        JSONObject defaultNotificationsChannelConfig = this.config.getDefaultNotificationChannel();
        if (defaultNotificationsChannelConfig == null) {
            return;
        }
        NotificationChannel notificationChannel = null;
        try {
            notificationChannel =
                FirebaseMessagingHelper.createNotificationChannel(defaultNotificationsChannelConfig, plugin.getContext().getPackageName());
        } catch (Exception ex) {
            Logger.error("createDefaultNotificationChannel failed.", ex);
        }
        createChannel(notificationChannel);
    }
}

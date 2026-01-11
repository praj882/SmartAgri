package com.example.myapplication.data.local.entity;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;

public class NetworkMonitor {

    public interface Listener {
        void onNetworkChanged(boolean isOnline);
    }

    private final ConnectivityManager cm;
    private final Listener listener;

    public NetworkMonitor(Context context, Listener listener) {
        this.listener = listener;
        cm = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
    }

    public void register() {
        NetworkRequest request = new NetworkRequest.Builder().build();
        cm.registerNetworkCallback(request, networkCallback);

        // 🔴 Force initial check (IMPORTANT)
        listener.onNetworkChanged(isOnline());
    }

    public void unregister() {
        try {
            cm.unregisterNetworkCallback(networkCallback);
        } catch (Exception ignored) {}
    }

    private final ConnectivityManager.NetworkCallback networkCallback =
            new ConnectivityManager.NetworkCallback() {
                @Override
                public void onAvailable(Network network) {
                    listener.onNetworkChanged(isOnline());
                }

                @Override
                public void onLost(Network network) {
                    listener.onNetworkChanged(isOnline());
                }
            };

    // 🔴 Real validation (not just connection)
    private boolean isOnline() {
        Network network = cm.getActiveNetwork();
        if (network == null) return false;

        NetworkCapabilities cap =
                cm.getNetworkCapabilities(network);

        return cap != null &&
                cap.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
                cap.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED);
    }
}



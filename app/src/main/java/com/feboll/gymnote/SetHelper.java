package com.feboll.gymnote;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class SetHelper {
	public boolean checkConnection (ConnectivityManager cm) {
		if (cm != null){
			NetworkInfo[] netInfo = cm.getAllNetworkInfo();
			if(netInfo != null)
				for (NetworkInfo ni : netInfo) {
					if (ni.getTypeName().equalsIgnoreCase("WIFI"))
						if (ni.isConnected()) return true;
					if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
						if (ni.isConnected()) return true;
				}
		}
		return false;
	}




}

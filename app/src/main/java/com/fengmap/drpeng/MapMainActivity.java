package com.fengmap.drpeng;


import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;

import com.android.mgwaiter.R;
import com.fengmap.android.FMMapSDK;
import com.fengmap.android.data.FMDataManager;
import com.fengmap.android.wrapmv.Tools;
import com.fengmap.drpeng.common.ResourcesUtils;

public class MapMainActivity extends Activity {

	private Handler mHandler = new Handler();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_map);

		if (Build.VERSION.SDK_INT < 23) {
			copyMap();
		} else {
			int p1 = MapMainActivity.this.checkSelfPermission(FMMapSDK.SDK_PERMISSIONS[0]);
			int p2 = MapMainActivity.this.checkSelfPermission(FMMapSDK.SDK_PERMISSIONS[1]);

			if (p1 != PackageManager.PERMISSION_GRANTED || p2 != PackageManager.PERMISSION_GRANTED ) {
				// apply
				MapMainActivity.this.requestPermissions(FMMapSDK.SDK_PERMISSIONS,
													 FMMapSDK.SDK_PERMISSION_RESULT_CODE);
			} else {
				copyMap();
			}
		}
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
		if (grantResults[0] != PackageManager.PERMISSION_GRANTED ||
			grantResults[1] != PackageManager.PERMISSION_GRANTED) {

			this.finish();
			return;

		}

		if (requestCode == FMMapSDK.SDK_PERMISSION_RESULT_CODE) {
			copyMap();
		}
	}


	void copyMap() {

		FMMapSDK.initResource();

		writeMapFile("79980");
		writeMapFile("79981");
		writeMapFile("79982");
		writeMapFile("70144");
		writeMapFile("70145");
		writeMapFile("70146");
		writeMapFile("70147");
		writeMapFile("70148");

		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				Bundle b = new Bundle();
				b.putString(FMAPI.ACTIVITY_WHERE, MapMainActivity.class.getName());
				b.putString(FMAPI.ACTIVITY_MAP_ID, Tools.OUTSIDE_MAP_ID);
				FMAPI.instance().gotoActivity(MapMainActivity.this, OutdoorMapActivity.class, b);
				MapMainActivity.this.finish();
			}
		}, 500);

	}


	private void writeMapFile(String mapId) {
		String dstFileName = mapId + ".fmap";
		String srcFileName = "data/" + dstFileName;
		ResourcesUtils.writeRc(this,
							   FMDataManager.getFMMapResourceDirectory() + mapId + "/", dstFileName, srcFileName);
	}

}

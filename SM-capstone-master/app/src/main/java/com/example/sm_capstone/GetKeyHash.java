package com.example.sm_capstone;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import static com.kakao.util.helper.Utility.getPackageInfo;
public class GetKeyHash {

    public static String getKeyHash(final Context context) { // 카카오링크 API를 쓰려면 고유한 key값을 가져와야하는데 이를 가져오게 해주는 메소드입니다.
        PackageInfo packageInfo = getPackageInfo(context, PackageManager.GET_SIGNATURES);
        if (packageInfo == null)
            return null;

        for (Signature signature : packageInfo.signatures) {
            try {
                Toast.makeText(context, "해시값찾고있습니다. :", Toast.LENGTH_LONG);
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                return Base64.encodeToString(md.digest(), Base64.NO_WRAP);
            } catch (NoSuchAlgorithmException e) {
                Toast.makeText(context, "해시값입니다 :"+ e.toString(), Toast.LENGTH_LONG);
                e.printStackTrace();
                Log.d("확인", e.toString());

            }
        }
        return null;
    }


}

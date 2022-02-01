package com.example.sm_capstone;

import android.app.Activity;
import android.app.Application;
import android.graphics.Color;

import org.aviran.cookiebar2.CookieBar;

//Application을 상속받아 전역함수나 변수들을 모아놓는 클래스(프젝당 하나만 존재가능하고 모든 전역객체는 여기에 작성하시면 됩니다)
public class GlobalMethod extends Application {
    public void LoginBlank(Activity a){
        CookieBar.build(a)
                .setCookiePosition(CookieBar.TOP)
                .setBackgroundColor(R.color.default_bg_color)
                .setMessage("아이디 비밀번호를 입력하세요")
                .show();
    }
    public void LoginFail(Activity a){
        CookieBar.build(a)
                .setCookiePosition(CookieBar.TOP)
                .setBackgroundColor(R.color.warningColor)
                .setMessage("아이디, 비밀번호를 확인하세요")
                .show();
    }
    public void idDuplicate(Activity a){
        CookieBar.build(a)
                .setCookiePosition(CookieBar.TOP)
                .setBackgroundColor(R.color.emplobackground)
                .setMessage("이미 사용중인 아이디 입니다")
                .show();
    }
    public void storeDuplicate(Activity a){
        CookieBar.build(a)
                .setCookiePosition(CookieBar.TOP)
                .setBackgroundColor(R.color.emplobackground)
                .setMessage("이미 사용중인 매장번호입니다")
                .show();
    }
    public void storeChkPlz(Activity a){
        CookieBar.build(a)
                .setCookiePosition(CookieBar.TOP)
                .setBackgroundColor(R.color.design_default_color_primary_variant)
                .setMessage("매장 중복검사를 진행하세요")
                .show();
    }

    public void idPossible(Activity a){
        CookieBar.build(a)
                .setCookiePosition(CookieBar.TOP)
                .setBackgroundColor(R.color.welcomeColor)
                .setMessage("사용 가능한 아이디 입니다")
                .show();
    }
    public void storePossible(Activity a){
        CookieBar.build(a)
                .setCookiePosition(CookieBar.TOP)
                .setBackgroundColor(R.color.welcomeColor)
                .setMessage("사용 가능한 매장아이디 입니다")
                .show();
    }
    public void idChkPlz(Activity a){
        CookieBar.build(a)
                .setCookiePosition(CookieBar.TOP)
                .setBackgroundColor(R.color.design_default_color_primary_variant)
                .setMessage("ID중복검사를 확인하세요")
                .show();
    }
    public void modifyOK(Activity a){
        CookieBar.build(a)
                .setCookiePosition(CookieBar.BOTTOM)
                .setBackgroundColor(R.color.emplobackground)
                .setMessage("수정 되었습니다.")
                .show();
    }
}

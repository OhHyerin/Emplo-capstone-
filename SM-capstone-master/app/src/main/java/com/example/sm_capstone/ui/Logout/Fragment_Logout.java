package com.example.sm_capstone.ui.Logout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.sm_capstone.MainActivity;
import com.example.sm_capstone.R;
import com.google.firebase.auth.FirebaseAuth;

public class Fragment_Logout extends DialogFragment {

    private Context context;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_fragment__logout, container, false);


        context = container.getContext();
        Button.OnClickListener onClickListener = new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.logout_yes : //로그아웃하면 다시 로그인 화면으로 이동
                        Toast.makeText(context,"로그아웃 할게요",Toast.LENGTH_SHORT).show();
                        FirebaseAuth.getInstance().signOut();
                        //getActivity().onBackPressed();
                        FragmentManager fm = getActivity().getSupportFragmentManager();
                        fm.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                        Intent i = getActivity().getBaseContext().getPackageManager().
                                getLaunchIntentForPackage(getActivity().getBaseContext().getPackageName());
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(i);


                        break ;
                    case R.id.logout_no : //로그아웃안하면 직전의 화면으로 이동해야하지만 아직 그것을 구현하지 못함
                        Toast.makeText(context,"로그아웃 안할래요",Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getActivity(), MainActivity.class));
                        break ;

                }
            }
        } ;
        Button logout_yes_button = (Button) v.findViewById(R.id.logout_yes) ;
        logout_yes_button.setOnClickListener(onClickListener) ;
        Button logout_no_button = (Button) v.findViewById(R.id.logout_no) ;
        logout_no_button.setOnClickListener(onClickListener) ;


        return v;
    }

}

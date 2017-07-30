package com.coded2.wearbatteryalert;

import android.app.Fragment;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;



public class AboutFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_about,container,false);

        ImageButton emailBtn = (ImageButton) view.findViewById(R.id.email_btn);
        ImageButton twitterBtn = (ImageButton) view.findViewById(R.id.twitter_btn);

        emailBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent email = new Intent(android.content.Intent.ACTION_SEND);
                email.setType("message/rfc822");
                email.putExtra(Intent.EXTRA_EMAIL, new String[]{"rogerioanderson+nabuwatercoach@gmail.com"});
                email.putExtra(Intent.EXTRA_SUBJECT,"Nabu Water Coach - Contact");
                try{
                    startActivity(email);
                }catch(ActivityNotFoundException e){
                    Toast.makeText(getActivity(), "There are no email clients installed.", Toast.LENGTH_SHORT).show();
                }
            }
        });


        twitterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction("android.intent.action.VIEW");
                intent.addCategory("android.intent.category.BROWSABLE");
                Uri uri = Uri.parse("http://twitter.com/rogerioanderson");
                intent.setData(uri);
                try
                {
                    startActivity(intent);
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }

            }
        });


        return view;

    }

}

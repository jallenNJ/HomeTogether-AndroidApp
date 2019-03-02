package edu.ramapo.jallen6.hometogether;

import android.support.annotation.NonNull;
import android.widget.Button;
import android.widget.LinearLayout;

import java.util.Observable;
import java.util.Observer;

public class MemberBar implements Observer {
    LinearLayout memberLayout;

    MemberBar(@NonNull LinearLayout layout){
        memberLayout = layout;
        ActiveHousehold.getInstance().addObserver(this);
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        ActiveHousehold.getInstance().deleteObserver(this);
    }

    @Override
    public void update(Observable observable, Object o) {
        if(memberLayout == null){
            throw new IllegalStateException("memberLayout has become null");
        }

        memberLayout.removeAllViewsInLayout();
        ActiveHousehold cache = ActiveHousehold.getInstance();
        for(int i =0; i < cache.getMembersSize(); i++){
            String id =  cache.getMemberId(i);
            Button button = new Button(memberLayout.getContext());
            button.setText(id);
            memberLayout.addView(button);
        }

        Button button = new Button(memberLayout.getContext());
        button.setText("DEBUG: Add member");
        memberLayout.addView(button);

    }


}

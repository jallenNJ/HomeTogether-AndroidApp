package edu.ramapo.jallen6.hometogether;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

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

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(memberLayout.getContext());
                builder.setTitle("Username of member to add");

                // Set up the input
                final EditText input = new EditText(memberLayout.getContext());
                // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                input.setInputType(InputType.TYPE_CLASS_TEXT );
                builder.setView(input);

                // Set up the buttons
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                       // memberToAddText = input.getText().toString();
                        Toast.makeText(memberLayout.getContext(), input.getText().toString(), Toast.LENGTH_SHORT).show();
                        //TODO: Send request to find member to add;
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });


                builder.show();
            }
        });

    }


}

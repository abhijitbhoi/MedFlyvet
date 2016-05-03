package discovermypet.medflyvet.Notification;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import discovermypet.medflyvet.R;
import discovermypet.medflyvet.appurl.APPURL;
import discovermypet.medflyvet.authentication.SharedPrefereneceUtil;
import discovermypet.medflyvet.listviewfeed.Timeline;
import discovermypet.medflyvet.server.ServerClass;
import discovermypet.medflyvet.social.FriendRequestActivity;

/**
 * Created by dipali on 5/2/2016.
 */


    public class RequestNotification extends ListActivity {
        /**
         * Holds reference for Progress Dialog.
         */
        ProgressDialog dialog;
        /**
         * Holds reference for Logcat.
         */
        private String TAG = RequestNotification.class.getName();
        /**
         * Holds reference for ArrayList.
         */
        ArrayList<NotifyModel> notifyModels = new ArrayList<NotifyModel>();
        private int activity;
        String type;
        String user_image;
        String timeline_image;
        /**
         * Holds reference for Resources.
         */
        public Resources res;
        /**
         * Holds reference for TextView.
         */
        TextView chatCountText;
        /**
         * Holds reference for LayoutInflater.
         */
        private static LayoutInflater layoutInflater = null;
        /**
         * Holds reference for ImageView.
         */
        ImageView imageView;
        ListView list;
        /**
         * Holds reference for LinerLayout.
         */
        private LinearLayout linearLayout;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.notificationlist);
            Log.v(TAG, "onCreate()");
            inITUI();
        }


        private void inITUI() {

            imageView = (ImageView) findViewById(R.id.thumbImage);
            chatCountText = (TextView) findViewById(R.id.chatCountText);
//        linearLayout = (LinearLayout)findViewById(R.id.lnrl);
            notifyregister();

        }

        private void notifyregister() {
            RegisterUser ru = new RegisterUser();
            ru.execute(SharedPrefereneceUtil.getUserId(RequestNotification.this));
        }

        /**
         * Perform the asyncTask sends data to server and gets response.
         */

        class RegisterUser extends AsyncTask<String, Void, String> {

            ServerClass ruc = new ServerClass();

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                showProgressDialog();
            }

            @Override
            protected void onPostExecute(String response) {
                super.onPostExecute(response);
                Log.v(TAG, String.format("onPostExecute ::  notification response = %s", response));
                dismissProgressDialog();
                Toast.makeText(getApplicationContext(), response, Toast.LENGTH_LONG).show();
                parseJsonResponse(response);
            }

            @Override
            protected String doInBackground(String... params) {
                Log.v(TAG, String.format("doInBackground ::  params= %s", params));
                HashMap<String, String> signupData = new HashMap<String, String>();
                signupData.put("user_id","5");

                signupData.put(ServerClass.ACTION, "notification_history");
                String loginResult = ruc.sendPostRequest(APPURL.APP_URL, signupData);
                Log.v(TAG, String.format("doInBackground :: loginResult= %s", loginResult));
                return loginResult;
            }
        }

        /**
         * for display progress dialog.
         */
        private void showProgressDialog() {
            dialog = new ProgressDialog(RequestNotification.this, AlertDialog.THEME_HOLO_DARK);
            dialog.setMessage("Please Wait");
            dialog.show();
        }

        /**
         * to call dismissProgressDialog.
         */
        private void dismissProgressDialog() {
            dialog.dismiss();

        }


        private void parseJsonResponse(String jsonValue) {
            Log.v(TAG, String.format("parseJsonResponse :: jsonValue= %s", jsonValue));

            try {
                JSONObject object = new JSONObject(jsonValue);
                JSONArray Jarray = object.getJSONArray("result");
                String count = object.getString("count");

                System.out.println("Json Size ::: " + Jarray.length());
                System.out.println("count ::: " + count);

                for (int i = 0; i < Jarray.length(); i++) {
                    JSONObject Jasonobject = Jarray.getJSONObject(i);

                    NotifyModel notifyModel = new NotifyModel();
                    String name = Jasonobject.getString("Name");
                    String time = Jasonobject.getString("time");
                    String text = Jasonobject.getString("text");

                    String type = Jasonobject.getString("type");

                    if(type.equals("timeline"))
                    {
                        timeline_image = Jasonobject.getString("timeline_image");
                        Log.v("timeline_image",timeline_image);
                    }
                    else if(type.equals("profile"))
                    {
                        user_image = Jasonobject.getString("user_image");
                        Log.v("user_image",user_image);
                    }

                    // String user_Image=Jasonobject.getString("user_image");
                    Log.v("type", type);
                    // Log.v("userImage",user_Image);


//                Log.v(TAG, String.format("parseJsonResponse NotifyModel :: name = %s, time = %s, text = %s, image = %s,type=%s", name, time, text,  type));

                    notifyModel.setName(name);
                    notifyModel.setTime(time);
                    notifyModel.setText(text);
                    notifyModel.setImage(user_image);
                    notifyModel.setType(type);
                    notifyModel.setUser_Image(timeline_image);
                    notifyModel.setCount(count);
                    notifyModels.add(notifyModel);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            displayData();
        }


        private void displayData() {
            ListView list = (ListView) findViewById(R.id.generalNotifList);


            notifyadapter vaccinationAdapter = new notifyadapter();
            list.setAdapter(vaccinationAdapter);

            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                final NotifyModel notifyModel = new NotifyModel();

                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {
                    if (notifyModels.get(position).getType().equalsIgnoreCase("timeline")) {
                        Intent intent = new Intent(RequestNotification.this, Timeline.class);
                        intent.putExtra("key", position);
                        startActivity(intent);
                    } else if (notifyModels.get(position).getType().equalsIgnoreCase("profile")) {
                        Intent intent = new Intent(RequestNotification.this,FriendRequestActivity.class);
                        intent.putExtra("key", position);
                        startActivity(intent);
                    }
                }

                @SuppressWarnings("unused")
                public void onClick(View v) {
                }


            });
        }

        public class notifyadapter extends BaseAdapter {

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                final NotifyModel itemss = notifyModels.get(position);
                LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = layoutInflater.inflate(R.layout.activitylist, null);
                ImageView bullet = (ImageView) convertView.findViewById(R.id.thumbImage);
                TextView username = (TextView) convertView.findViewById(R.id.username);
                TextView date = (TextView) convertView.findViewById(R.id.time);
                TextView text = (TextView) convertView.findViewById(R.id.text);


                text.setText(notifyModels.get(position).getText());
                date.setText(notifyModels.get(position).getTime());
                username.setText(notifyModels.get(position).getName());
                chatCountText.setVisibility(View.VISIBLE);
                chatCountText.setText(notifyModels.get(position).getCount());

//            if (itemss.getImage() != null) {
//                Picasso.with(NotificationActivity.this)
//                        .load(itemss.getImage())
//                        .placeholder(R.drawable.splash0)
//                        .into(bullet);
//                return convertView;
//            }

                if (itemss.getImage() != null) {
                    Picasso.with(RequestNotification.this)
                            .load(itemss.getImage())
                            .placeholder(R.drawable.placeholder)
                            .into(bullet);
                }
                else if(itemss.getUser_Image()!=null)
                {
                    Picasso.with(RequestNotification.this)
                            .load(itemss.getUser_Image())
                            .placeholder(R.drawable.placeholder)
                            .into(bullet);
                }
                return convertView;
            }


            @Override
            public int getCount () {
                return notifyModels.size();
            }

            @Override
            public Object getItem ( int position){
                return null;
            }

            @Override
            public long getItemId ( int position){
                return 0;
            }
        }




        @Override
        public void onBackPressed() {
            Intent intent = new Intent(RequestNotification.this,Timeline.class);
            startActivity(intent);
            finish();
        }









    }//endactivity







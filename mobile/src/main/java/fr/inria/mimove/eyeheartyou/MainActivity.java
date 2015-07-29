package fr.inria.mimove.eyeheartyou;

import android.os.StrictMode;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.support.v4.content.LocalBroadcastManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.BroadcastReceiver;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.Scanner;
import java.util.TooManyListenersException;


public class MainActivity extends ActionBarActivity {
    /**
     *
     *  Socket Gloabal Varibales
     */

    private Socket socket;

    private static int SERVERPORT; ///=8080; //Port 80--> Open to the web ---> 8080 for testing purposes
    private static String SERVER_IP; //="10.102.100.167";//"10.107.182.196";// --> this is your IP Address//"10.0.2.2";

    public static String adddress;
    public static int asdf;
    static BufferedReader in;
    static PrintStream out;

    /** To Server--> PK Vals  */
    static String pk_to_server;


    //Paillie PK Values
    static String k="";
    static String n="";
    static  String m="";

    //ciphertext and plaintext array values so that they can be accessed thoughout the program
    static BigInteger[] ciphertext;
    static BigInteger[] plaintext;
    //FInal result
    BigInteger final_result;
    String final_resit_tv;
    private  static long time_taken;


    //Vraribales for HeartRateApp
    private TextView mTextView,dec_result,txtV_time;//holds the heart rate values and time taken for encrypted sums
    private TextView list_data,list_data_no_dups,txtV_result; //iterates over a list and displays data[i] postion values
    public ListView hear_rate, heart_rate_no_dups; // listView to store a visible sets of all heart rate data sets
    public ArrayAdapter HRadapter, HRadapterDuplicates;// adapter to hold the values that will be stored in the ListView
    public ArrayList<String> data; // ArrayList that will store all the heart rate data sets samples
    public ArrayList<String> exclude_duplicates;
    public String garbage=""; // any data values that dont need to be in the set can be collected and stored here
    public HashMap HashMap;
    public EditText address,port;
   // static Paillier pail = new Paillier();
    //static PublicKey pk = new PublicKey();
    //static PrivateKey sk = new PrivateKey(1024);




    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        IntentFilter messageFilter = new IntentFilter(Intent.ACTION_SEND);
        MessageReceiver messageReceiver = new MessageReceiver();
        LocalBroadcastManager.getInstance(this).registerReceiver(messageReceiver, messageFilter);

        //TextViewsId
        txtV_result = (TextView)findViewById(R.id.txtView_decr_res);
        txtV_time=(TextView)findViewById(R.id.textView_time_taken);
        //EditText
        address=(EditText)findViewById(R.id.editText_Address);
        port=(EditText)findViewById(R.id.editText_Port);
        //Buttons
        Button display_vals = (Button)findViewById(R.id.button_show_vals);//Removes all duplicate values;//displays values by iterating over a list and removing duplicates
        Button send = (Button)findViewById(R.id.button_calc);//button that when pressed --> 1) Connects to Server and gets the public key 2) takes this public key and encrypts a vector of data 3) sends the encrypted vector data and a public key to the server where the values are summed homomorphically. 4) The encrypted sum is the sent back to the phone and decrypted to reveal the sum of the the values
        //Button show_vals = (Button)findViewById(R.id.btn_show_vector);
        Button clear_list = (Button)findViewById(R.id.btn_clr_lst);
        Button connect = (Button)findViewById(R.id.btn_connect);

        hear_rate =(ListView)findViewById(R.id.listView);//holds the heart rate values
       // heart_rate_no_dups = (ListView)findViewById(R.id.listView_no_dups);
        data = new ArrayList<String>();// listView to store a visible sets of all heart rate data sets
        exclude_duplicates = new ArrayList<String>();




        //Inintial List of all Values
        HRadapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1,data);// adapter to hold the values that will be stored in the ListView
        hear_rate.setAdapter(HRadapter);// ArrayList that will store all the heart rate data sets samples

        //list of all values that are not duplicates
       // HRadapterDuplicates = new ArrayAdapter(this,android.R.layout.simple_list_item_1,exclude_duplicates);// adapter to hold the values that will be stored in the ListView
        //heart_rate_no_dups.setAdapter(HRadapterDuplicates);// ArrayList that will store all the heart rate data sets samples


        list_data=(TextView)findViewById(R.id.textView_list_data);



        //buttun that when pressed clears all data values in the list
        clear_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                data.clear();
                txtV_result.setText("Decrypted Result:");
                txtV_time.setText("Time Taken:");

            }
        });

        /*
            Button on click connects to the Server


         */
        /**
         *
         * This is whats does it....
         */

        connect.setOnClickListener(new View.OnClickListener() {

                                       @Override
                                       public void onClick(View view) {
                                        try{
                                            SERVER_IP=address.getText().toString();
                                            SERVERPORT=Integer.parseInt(port.getText().toString());
                                            //Toast.makeText(getApplicationContext(),test1,Toast.LENGTH_SHORT).show();
                                            //Toast.makeText(getApplicationContext(),Integer.toString(test2),Toast.LENGTH_SHORT).show();
                                            ClientThread clientThread = new ClientThread();
                                            clientThread.run();
                                            String result = clientThread.getResult();
                                            String result_time=clientThread.getTimeTaken();

                                            txtV_result.setText("Decrypted Result: " + result);//displays the decrypted result
                                            txtV_time.setText("Time Taken:" + result_time);
                                           //new Thread(new ClientThread()).start(); //creates new thread

                                           } catch (Exception e) {
                                             e.printStackTrace();
                                        }

        }
                                   });


        /**   This allows for a socket to be opened on the main thread without having to create
         *     seperate thread which requires AyncTask                                           */
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        StrictMode.setThreadPolicy(policy);
    }//end onCreate




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        mTextView = (TextView) findViewById(R.id.text);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public class MessageReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String message = intent.getStringExtra("heart_value");
           // Log.v("myTag", "Main activity received message: " + message);
            // Display message in UI
            mTextView.setText(message); //sets the TextView on the phone to the Hear Rate Values from the smart watch sensor
            if (message.equals("/start")) {
                garbage += message;
            }
            /**
             * HashMap to handle duplicates
             */

            int key = hashCode();//key for table
            HashMap = new HashMap(data.size());//initilizes HashMap to handle duplicates in the data set


            //String  num_test = data.get(i);
            char first_num = message.charAt(0);

            int char_to_int = Character.getNumericValue(first_num);
            //Log.d("FirtNum", Integer.toString(char_to_int));
            if (char_to_int > 0) {

                data.add(message);

            }

        }
    }

    class ClientThread implements Runnable {

        @Override

        public void run() {

            try {

                InetAddress serverAddr = InetAddress.getByName(SERVER_IP);

                socket = new Socket(serverAddr, SERVERPORT);
                Log.d("Debug","New Client Thread");


                //create send and reacievin streams for sever communications
                in = new BufferedReader(new InputStreamReader((socket.getInputStream()))); //--< receives input from the server
                out = new PrintStream(socket.getOutputStream(),true); //--> sends output to the server








                    String keyFile = "keyfile";
                    Paillier pail = new Paillier();

                    PublicKey pk = new PublicKey();
                    PrivateKey sk = new PrivateKey(1024);


                    long start = System.currentTimeMillis();
                    pail.keyGen(sk, pk);
                    pk_to_server=pk.toString(); ///grabs the pk values before reseting them with the values taken from the server
                    out.println(pk_to_server); //sends this info to the server


                    //Store the ciphertext as an arraylist
                    ciphertext = new BigInteger[data.size()];
                    plaintext = new BigInteger[data.size()];
                    int encrypted_vals[] = new int[data.size()];
                    for (int i = 0; i < data.size(); i++) {
                        encrypted_vals[i] = (int) Double.parseDouble(data.get(i));
                        plaintext[i] = BigInteger.valueOf((long) encrypted_vals[i]);
                        ciphertext[i] = pail.encrypt(plaintext[i], pk);
                    }

                /**
                 *  Send data to the server
                 * */
            int num_elems = ciphertext.length;//grab number of elements
            out.println(num_elems); //send to server so it know how many elements to expect
                //interate over the ciphertext array read into server one by one
                for(int i=0; i<ciphertext.length;i++) {
                    out.println(ciphertext[i]);
                }


                //get summ from server
                String result_from_server = in.readLine();
                BigInteger decrpyed_result = new BigInteger(result_from_server);
                final_result=pail.decrypt(decrpyed_result,sk);
                long end = System.currentTimeMillis();
                time_taken=end-start;
                Log.d("TIme Taken-->",Long.toString(time_taken));
                final_resit_tv = final_result.toString();
                Log.d("Decryptd Result-->", decrpyed_result.toString());
            } catch (UnknownHostException e1) {
                e1.printStackTrace();
            } catch (IOException e1) {
                e1.printStackTrace();
            }

        }
        public String getResult(){
            return final_resit_tv;

        }
        public  String getTimeTaken(){
            return Long.toString(time_taken);
        }

    }





}


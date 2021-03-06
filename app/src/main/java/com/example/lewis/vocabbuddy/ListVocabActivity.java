package com.example.lewis.vocabbuddy;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class ListVocabActivity extends AppCompatActivity {

    // Android tutorial for starting another Activity:
    // https://developer.android.com/training/basics/firstapp/starting-activity
    TextView vocabTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_vocab);

        Intent startListVocabIntent = getIntent();
        String spokenWords = startListVocabIntent.getStringExtra(MainActivity.EXTRA_SPEECH);


        if (spokenWords == null) {
            try
            {
                FileInputStream fileInputStream = new FileInputStream(this.getFilesDir()+"/myfile");
                ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
                Map myNewlyReadHashMap = (Map)objectInputStream.readObject();

                class CustomAdapter extends BaseAdapter {

                    ArrayList<String> outputVocabArray = Vocab.getVocabArray(myNewlyReadHashMap);
                    ArrayList<String> outputFreqArray = Vocab.getFreqArray(myNewlyReadHashMap);

                    @Override
                    public int getCount() {
                        return outputVocabArray.size();
                    }

                    @Override
                    public Object getItem(int position) {
                        return null;
                    }

                    @Override
                    public long getItemId(int position) {
                        return 0;
                    }

                    @Override
                    public View getView(int position, View convertView, ViewGroup parent) {
                        convertView = getLayoutInflater().inflate(R.layout.list_vocab_adapter_layout, null);

                        TextView tv_vocab = (TextView) convertView.findViewById(R.id.word_textview);
                        TextView tv_freq = (TextView) convertView.findViewById(R.id.freq_textview);

                        tv_vocab.setText(outputVocabArray.get(position));
                        tv_freq.setText(outputFreqArray.get(position));

                        return convertView;
                    }
                }

                ListView listView = (ListView) findViewById(R.id.vocab_listview);
                CustomAdapter customAdapter = new CustomAdapter();
                listView.setAdapter(customAdapter);
            } catch(ClassNotFoundException | IOException | ClassCastException e) {
                e.printStackTrace();
            }
        }


        if (spokenWords != null) {

            String[] spokenArray = Vocab.convertTextToArray(spokenWords);
            HashMap<String, Integer> zeroValueSpokenHashMap = Vocab.populateHashMapWithWords(spokenArray);

            try {
                FileInputStream fileInputStream = new FileInputStream(this.getFilesDir() + "/myfile");
                ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
                Map myNewlyReadHashMap = (Map) objectInputStream.readObject();

                for (String keyWord: zeroValueSpokenHashMap.keySet()) {
                    if (myNewlyReadHashMap.containsKey(keyWord)) {
                        continue;
                    } else {
                        myNewlyReadHashMap.put(keyWord, 0);
                    }
                }
                zeroValueSpokenHashMap = (HashMap<String, Integer>) myNewlyReadHashMap;

            } catch(ClassNotFoundException | IOException | ClassCastException e) {
                e.printStackTrace();
            }

            HashMap<String, Integer> nonZeroSpokenHashMap = Vocab.incrementHashMapValues(zeroValueSpokenHashMap, spokenArray);
            Map<String, Integer> sortedVocab = Vocab.orderHashMap(nonZeroSpokenHashMap);

            try
            {
                FileOutputStream fos = this.openFileOutput("myfile", Context.MODE_PRIVATE);
                ObjectOutputStream oos = new ObjectOutputStream(fos);
                oos.writeObject(sortedVocab);
                oos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            class CustomAdapter extends BaseAdapter {

                ArrayList<String> outputVocabArray = Vocab.getVocabArray(sortedVocab);
                ArrayList<String> outputFreqArray = Vocab.getFreqArray(sortedVocab);

                @Override
                public int getCount() {
                    return outputVocabArray.size();
                }

                @Override
                public Object getItem(int position) {
                    return null;
                }

                @Override
                public long getItemId(int position) {
                    return 0;
                }

                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    convertView = getLayoutInflater().inflate(R.layout.list_vocab_adapter_layout, null);

                    TextView tv_vocab = (TextView) convertView.findViewById(R.id.word_textview);
                    TextView tv_freq = (TextView) convertView.findViewById(R.id.freq_textview);

                    tv_vocab.setText(outputVocabArray.get(position));
                    tv_freq.setText(outputFreqArray.get(position));

                    return convertView;
                }
            }

            ListView listView = (ListView) findViewById(R.id.vocab_listview);
            CustomAdapter customAdapter = new CustomAdapter();
            listView.setAdapter(customAdapter);
        }
    }
}

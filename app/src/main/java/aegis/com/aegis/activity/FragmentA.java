package aegis.com.aegis.activity;


import android.app.ProgressDialog;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import aegis.com.aegis.R;
import aegis.com.aegis.adapter.Contact_Adapter;
import aegis.com.aegis.model.Contact_Model;

public class FragmentA extends Fragment {

    RecyclerView recyclerView;
    ListView list;
    private static ListView contact_listview;
    private static ArrayList<Contact_Model> arrayList;
    private static ArrayList<String> contactNames;
    private static Contact_Adapter adapter;
    LinearLayout indexLayout;
    Map<String, Integer> mapIndex;
    EditText edtSearch;
    private static ProgressDialog pd;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_contact_list, container, false);
        contact_listview = rootView.findViewById(R.id.contact_listview);
        indexLayout = (LinearLayout) rootView.findViewById(R.id.side_index);
        edtSearch = (EditText) rootView.findViewById(R.id.search);
        contactNames = new ArrayList<String>();
        new LoadContacts().execute();// Execute the async task
        edtSearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable arg0) {
                // TODO Auto-generated method stub
                String text = edtSearch.getText().toString().toLowerCase(Locale.getDefault());
                if (adapter != null)
                    adapter.filter(text);
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1,
                                          int arg2, int arg3) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2,
                                      int arg3) {
                // TODO Auto-generated method stub
            }
        });
        return rootView;
    }

    // Async task to load contacts
    private class LoadContacts extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            arrayList = readContacts();// Get contacts array list from this method
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            if (arrayList != null && arrayList.size() > 0) {
                adapter = null;
                if (adapter == null) {
                    adapter = new Contact_Adapter(getContext(), arrayList);
                    contact_listview.setAdapter(adapter);// set adapter
                    getIndexList(contactNames);
                    displayIndex();
                }
                adapter.notifyDataSetChanged();
            } else {
                Toast.makeText(getContext(), "There are no contacts.", Toast.LENGTH_LONG).show();
            }

            if (pd.isShowing())
                pd.dismiss();

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = ProgressDialog.show(getContext(), "Loading Contacts", "Please Wait...");
        }

    }

    // Method that return all contact details in array format
    private ArrayList<Contact_Model> readContacts() {
        ArrayList<Contact_Model> contactList = new ArrayList<Contact_Model>();
        Cursor phones = getActivity().getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, "UPPER(" + ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + ") ASC");
        while (phones.moveToNext()) {
            String name = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            long contactId = phones.getLong(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone._ID));
            contactList.add(new Contact_Model(Long.toString(contactId), name, phoneNumber));
            contactNames.add(name);
        }
        phones.close();

        return contactList;
    }

    private void getIndexList(ArrayList<String> ContactNames) {
        mapIndex = new LinkedHashMap<String, Integer>();
        for (int i = 0; i < ContactNames.size(); i++) {
            String strName = ContactNames.get(i);
            String index = strName.substring(0, 1);

            if (mapIndex.get(index) == null)
                mapIndex.put(index, i);
        }
    }

    private void displayIndex() {
        TextView textView;
        List<String> indexList = new ArrayList<String>(mapIndex.keySet());
        for (String index : indexList) {
            textView = (TextView) getLayoutInflater().inflate(R.layout.side_index_item, null);
            textView.setText(index);
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TextView selectedIndex = (TextView) v;
                    contact_listview.setSelection(mapIndex.get(selectedIndex.getText()));
                }
            });
            indexLayout.addView(textView);
        }
    }


}
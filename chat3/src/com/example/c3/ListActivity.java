package com.example.c3;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore.Video;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

public class ListActivity extends Activity {
	ListView listView;
	Button refresh;
	//list
	ArrayAdapter<String> adapter;
	ArrayList<String>list;
	
	String id;
	Socket socket;
	int port=1234;
	PrintWriter cpw;
	BufferedReader cbr;
	SocketHandler sh;
	protected void onCreate(Bundle savedInstaceState)
	{
		super.onCreate(savedInstaceState);
		setContentView(R.layout.activity_list);
		
		//ID�� �޾ƿ´�.
		Intent i=getIntent();
		id=i.getStringExtra("id");
		
		//ListView �κ�
		list =new ArrayList<String>();
		adapter=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,list);
		listView=(ListView)findViewById(R.id.listView1);
		listView.setAdapter(adapter);
		//ListView Item Ŭ�� �κ�
		listView.setOnItemClickListener(new OnItemClickListener(){

			//listview click listener
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// TODO Auto-generated method stub
				String clientinfo=(String)parent.getItemAtPosition(position);
				
				Intent j=new Intent(getApplicationContext(), FragMain.class);
				j.putExtra("cli", clientinfo);
				startActivity(j);
			}
			
		});
		
		//Refresh ��ư �κ�
		refresh=(Button)findViewById(R.id.refreshBtn);
		refresh.setOnClickListener(new clickHandler());
		
		sh=new SocketHandler();
		sh.start();
		
	}
	//�ڷΰ��� ��ư�� �������� _delete �޽����� ���� ó��
	public void onBackPressed(){
		cpw.println("_delete"+"="+id+":"+socket.getLocalAddress().getHostAddress()+":"+port);
		finish();
	}
	class SocketHandler extends Thread{
		public void run()
		{
			String msg;
			try{
				socket=new Socket("10.30.112.94",port);
				cpw=new PrintWriter(socket.getOutputStream(),true);
				cbr=new BufferedReader(new InputStreamReader(socket.getInputStream()));
				
				cpw.println("_add"+"="+id+":"+socket.getLocalAddress().getHostAddress()+":"+port);
				
				while((msg=cbr.readLine())!=null){
					list.add(msg);
					runOnUiThread(new Runnable(){
						@Override
						public void run() {
							// TODO Auto-generated method stub
							adapter.notifyDataSetChanged();
						}
					});
				}
				cpw.println("_refresh");

			}catch(Exception e){}
		}
	}
	
	//refresh ���ش�.
	class clickHandler implements OnClickListener{
		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			try {
				cpw.println("_refresh");
			}catch (Exception e) {	e.printStackTrace();}
		}
	}
}

package org.mconf.bbb.android;

import java.util.ArrayList;
import java.util.List;

import org.mconf.bbb.listeners.IListener;
import org.mconf.bbb.users.IParticipant;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ListenerAdapter extends BaseAdapter {
	private Context context;
	View view;
	
	private List<IListener> listListener = new ArrayList<IListener>();
	
	public ListenerAdapter(Context context) {
        this.context = context;
    }
    
    public void addSection(IListener listener) {
    	ListenerContact contact = new ListenerContact(listener);
    	listListener.add(contact);
    }
    
    public void removeSection(IListener listener){
    	ListenerContact contact = getUserById(listener.getUserId());
    	if (contact != null)
    		listListener.remove(contact);
    }
    
    public int getCount() {
        return listListener.size();
    }

    public Object getItem(int position) {
        return listListener.get(position);
    }

    public long getItemId(int position) {
        return listListener.get(position).getUserId();
    }
    
    public ListenerContact getUserById(int id) {
    	for (IListener listener : listListener) {
    		if (listener.getUserId() == id)
    			return (ListenerContact) listener;
    	}
    	return null;
    }
    
    public View getView(int position, View convertView, ViewGroup viewGroup) {
    	ListenerContact entry = (ListenerContact) listListener.get(position);
        
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.listener, null);
        }
        view = convertView;
        
        String name = entry.getCidName();
        TextView contactName = (TextView) convertView.findViewById(R.id.listener_name);
        contactName.setTextAppearance(context, R.style.ParticipantNameStyle);
        contactName.setText(name);
        contactName.setTag(name);

        if(entry.isTalking())
        	convertView.setBackgroundResource(android.R.color.darker_gray);
       
        return convertView;
    }

}

package org.vt.indiatab;

import org.vt.indiatab.data.MeetingsDbAdapter;
import org.vt.indiatab.data.MembersDbAdapter;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.Menu;
import android.support.v4.view.MenuItem;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

public class OverviewFragment extends Fragment {
	
	private SimpleCursorAdapter adapter;
	private long group;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		
		group = getArguments().getLong(MembersFragment.GROUP_EXTRA);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View root = inflater.inflate(R.layout.overview_fragment, container, false);
		
		ListView lv = (ListView) root.findViewById(R.id.overview_listview);
		changeAdapterCursor();
		lv.setAdapter(adapter);
		
		return root;
	}
	
	public void changeAdapterCursor() {
		String[] from = new String[] {
				MeetingsDbAdapter.COL_MEETING_NUM,
				MeetingsDbAdapter.COL_INIT_POT,
				MeetingsDbAdapter.COL_LOANS_IN,
				MeetingsDbAdapter.COL_LOANS_OUT,
				MeetingsDbAdapter.COL_POST_POT
				};
		int[] to = new int[] {
				R.id.overview_row_meeting,
				R.id.overview_row_prepot,
				R.id.overview_row_in,
				R.id.overview_row_out,
				R.id.overview_row_postpot
				};
		
		MeetingsDbAdapter meetingsDb = new MeetingsDbAdapter(getActivity());
		meetingsDb.open();
		Cursor c = meetingsDb.fetchMeetings(group);
		getActivity().startManagingCursor(c);
		
		if (adapter == null) {
			adapter = new SimpleCursorAdapter(getActivity(),
					R.layout.overview_row, c, from, to, 0);
		}
		else {
			adapter.changeCursor(c);
		}
		
		meetingsDb.close();
	}
	
	/*
	 * Options Menu and ActionBar.
	 */

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		menu.add(0, TabsActivity.MENU_DELETE_MEETINGS, 0, "Delete All Meetings")
				.setIcon(R.drawable.delete_white)
				.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		
		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case TabsActivity.MENU_DELETE_MEETINGS:
			MeetingsDbAdapter meetingsDb = new MeetingsDbAdapter(getActivity());
			meetingsDb.open();
			meetingsDb.deleteMeetings(group);
			meetingsDb.close();
			
			MembersDbAdapter membersDb = new MembersDbAdapter(getActivity());
			membersDb.open();
			membersDb.absolveMemberLoans(group);
			membersDb.close();
			
			((TabsActivity) getActivity()).notifyTabs();
			
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
}

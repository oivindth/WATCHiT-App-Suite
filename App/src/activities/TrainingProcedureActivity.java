package activities;

import no.ntnu.emergencyreflect.R;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.Tab;

import fragments.StatusFragment;
import fragments.TPMainFragment;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;

public class TrainingProcedureActivity extends BaseActivity {
	
	private TPMainFragment tpMainFragment;
	private StatusFragment statusFragment;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		
		tpMainFragment = new TPMainFragment();
		statusFragment = new StatusFragment();
		
		ActionBar bar = getSupportActionBar();
		bar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		
		ActionBar.Tab tab1 = bar.newTab();
		ActionBar.Tab tab2 = bar.newTab();
		tab1.setText(getString(R.string.gateway_tab_status));
		tab2.setText(getString(R.string.gateway_tab_config));
		tab1.setTabListener(new MyTabListener());
		tab2.setTabListener(new MyTabListener());
		bar.addTab(tab1);
		bar.addTab(tab2);
	}
	
	private class MyTabListener implements ActionBar.TabListener
	{
		@Override
		public void onTabSelected(Tab tab, FragmentTransaction ft) {
			int tabPosition = tab.getPosition();
			switch (tabPosition) {
			case 0:
				ft.replace(android.R.id.content, tpMainFragment);
				break;
			case 1:
				ft.replace(android.R.id.content, statusFragment);
				break;
			default:
				break;
			}
		}
		@Override
		public void onTabUnselected(Tab tab, FragmentTransaction ft) {
			// TODO Auto-generated method stub
		}
		@Override
		public void onTabReselected(Tab tab, FragmentTransaction ft) {
			// TODO Auto-generated method stub
		}
	}
}

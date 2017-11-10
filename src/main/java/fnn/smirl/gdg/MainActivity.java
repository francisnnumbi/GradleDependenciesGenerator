package fnn.smirl.gdg;

import android.app.*;
import android.os.*;
import android.support.v7.app.*;
import android.widget.*;
import android.view.View.*;
import android.view.*;
import fnn.smirl.gdg.utils.*;
import com.android.volley.toolbox.*;
import com.android.volley.*;
import org.json.*;
import fnn.smirl.gdg.pojo.*;
import android.support.v4.widget.*;
import android.content.*;
import android.support.design.widget.*;
import fnn.smirl.gdg.adapters.*;
import android.content.res.*;
import java.util.*;
import android.view.inputmethod.*;
import android.support.v7.widget.*;
import android.graphics.*;
import de.hdodenhof.circleimageview.*;
import android.graphics.drawable.*;

public class MainActivity extends AppCompatActivity implements Constants {
  android.support.v7.widget.SearchView searchView;
  Resources res;
  RecyclerView rv;
  TextView info_tv, max_tv;
  Button btn;
  SwipeRefreshLayout swipeRefresh;
  Dependencies dependencies = new Dependencies();
  Dependency currDep = null;
  String searchKey;
  int max = 20;
  ClipboardManager myClipboard;
  public  BottomSheetBehavior bottomBehavior;
  NestedScrollView bottomView;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.main);
	res = getResources();
	myClipboard = (ClipboardManager)getSystemService(CLIPBOARD_SERVICE);
	setupToolbar();

	info_tv = (TextView)findViewById(R.id.info_tv);
	max_tv = (TextView)findViewById(R.id.max_tv);
	max_tv.setText("Max " + max);

	swipeRefresh = (SwipeRefreshLayout) findViewById(R.id.mainSwipeRefresh);
	rv = (RecyclerView) findViewById(R.id.mainListView);
	rv.setHasFixedSize(true);

	bottomView = (NestedScrollView) findViewById(R.id.bottom_sheet);
	bottomBehavior = BottomSheetBehavior.from(bottomView);

	setupBottomSheet();
	bottomBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
	bottomBehavior.setPeekHeight(0);

	swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener(){
		@Override
		public void onRefresh() {	  
		  runSearch();
		}
	  });

	setRecyclerViewListeners();
  }

  @Override
  public void onBackPressed() {
	// TODO: Implement this method
	if (bottomBehavior.getState() != bottomBehavior.STATE_COLLAPSED) {
	  bottomBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
	} else super.onBackPressed();
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
	getMenuInflater().inflate(R.menu.main_menu, menu);
	MenuItem ma = menu.findItem(R.id.action_search);
	searchView = (android.support.v7.widget.SearchView) ma.getActionView();
	setupSearchView();


	return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
	switch (item.getItemId()) {
	  case R.id.mm_max:
loadAletDialogForMax();
		break;
	  case R.id.mm_about:

		break;
	}
	return true;
  }



  private void setupBottomSheet() {
	bottomBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback(){

		@Override
		public void onStateChanged(View p1, int p2) {
		  //  if (p2 == BottomSheetBehavior.STATE_EXPANDED) {
		  reloadBottomData();
		  //  }
		}

		@Override
		public void onSlide(View p1, float p2) {
		  //  if (p2 == BottomSheetBehavior.STATE_EXPANDED) {
		  reloadBottomData();
		  //  }
		}
	  });
  }

  private void setupToolbar() {
	android.support.v7.widget.Toolbar tb = (android.support.v7.widget.Toolbar)findViewById(R.id.toolbar);
	setSupportActionBar(tb);
	getSupportActionBar().setTitle(R.string.app_name);
	getSupportActionBar().setSubtitle(R.string.app_sub_name);
  }

  private void setRecyclerViewListeners() {

  }

  private void setupSearchView() {
searchView.setQueryHint(res.getString(R.string.search));
	searchView.setOnQueryTextListener(new android.support.v7.widget.SearchView.OnQueryTextListener(){
		@Override
		public boolean onQueryTextSubmit(String p1) {
		  // TODO: Implement this method
		  searchKey = p1;
		  hideKeyboard(searchView);
		  runSearch();
		  return true;
		}

		@Override
		public boolean onQueryTextChange(String p1) {
		  // TODO: Implement this method
		  return false;
		}
	  });
  }

  private void runSearch() {
	if (searchKey != null && searchKey.length() > 0) {  
	  swipeRefresh.setRefreshing(true);
	  info_tv.setText(R.string.loading);
	  String url = BASE_URL + ACTION + "q=" + searchKey + "&rows=" + max + "&" + FORMAT;
	  retrieve(url);
	}
  }

  private void retrieve(String url) {
	StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>(){

		@Override
		public void onResponse(String p1) {
		  try {

			JSONObject root = new JSONObject(p1);
			JSONObject response = root.getJSONObject("response");
			JSONArray docs = response.getJSONArray("docs");
			info_tv.setText(String.format((res.getString(R.string.done)), docs.length()));

			dependencies.clear();
			for (int i = 0; i < docs.length(); i++) {
			  JSONObject obj = docs.getJSONObject(i);
			  String attr1 = obj.getString("id");
			  String attr2 = obj.getString("g");
			  String attr3 = obj.getString("a");
			  String attr4 = obj.getString("p");
			  String attr5 = obj.getString("latestVersion");
			  dependencies.add(new Dependency(attr1, attr2, attr3, attr4, attr5));
			}
		  } catch (JSONException e) {
			swipeRefresh.setRefreshing(false);
			info_tv.setText(R.string.error_parsing);

		  }
		  MyListAdapter adapter = new MyListAdapter(MainActivity.this, dependencies);
		  rv.setAdapter(adapter);
		  rv.setLayoutManager(new LinearLayoutManager(MainActivity.this));
		  swipeRefresh.setRefreshing(false);
		}
	  }, new Response.ErrorListener(){

		@Override
		public void onErrorResponse(VolleyError p1) {
		  swipeRefresh.setRefreshing(false);
		  info_tv.setText(R.string.connexion_failed);
		}
	  });

	RequestQueue queue = Volley.newRequestQueue(this);
	queue.add(request);
  }

  public void copy(String text) {
	ClipData myClip = ClipData.newPlainText("text", text);
	myClipboard.setPrimaryClip(myClip);
	snackIt(rv, res.getString(R.string.dependency_copied));
  }

  public void showBottomSheet(Dependency dep) {
	currDep = dep;
	reloadBottomData();
	bottomBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);

  }

  private void loadAletDialogForMax() {
	final EditText maxEt = new EditText(this);
	maxEt.setText("" + max);
	maxEt.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);

	new android.support.v7.app.AlertDialog.Builder(this)
	  .setTitle(R.string.max_search_size)
	  .setView(maxEt)
	  .setPositiveButton(R.string.modify, new DialogInterface.OnClickListener(){

		@Override
		public void onClick(DialogInterface p1, int p2) {
		  try {
			int ii = Integer.parseInt(maxEt.getText().toString());
			if (ii > 0) max = ii;
		  } catch (Exception ee) {}
		  max_tv.setText("Max " + max);
		}
	  })
	  .create()
	  .show();
  }

  

  private void reloadBottomData() {
// bottoming
	if (currDep != null) {
	  TextView bs_tv1 = (TextView) findViewById(R.id.bottom_sheetTextView);
	  String gd = "...<br/>dependencies {<br/>" +
		"    <font color=\"#0000ff\">" +  currDep.toGradle() + "</font><br/>" +
		"    compile fileTree(dir: 'libs', include: ['*.jar'])<br/>" +
		"}";
	  bs_tv1.setText(android.text.Html.fromHtml(gd));

	  TextView bs_tv2 = (TextView) findViewById(R.id.bottom_sheetTextView1);
	  bs_tv2.setText(currDep.id);
	}
  }

  private void snackIt(View v, String info) {
	Snackbar.make(v, info, Snackbar.LENGTH_SHORT)
	  .show();
  }

  private void hideKeyboard(View v) {
	InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
	imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
  }
}

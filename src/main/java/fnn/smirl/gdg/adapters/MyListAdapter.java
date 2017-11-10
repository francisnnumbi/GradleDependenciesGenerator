package fnn.smirl.gdg.adapters;
import android.widget.*;
import fnn.smirl.gdg.pojo.*;
import android.content.*;
import android.view.*;
import fnn.smirl.gdg.*;
import android.support.v7.widget.*;

public class MyListAdapter extends RecyclerView.Adapter<MyListAdapter.Holder> {

  private LayoutInflater li;
  private final MainActivity activity;
  private Dependencies deps;
  public MyListAdapter(MainActivity activity, Dependencies list) {
	this.activity = activity;
	li = LayoutInflater.from(activity.getBaseContext());
	deps = list;
  }

  @Override
  public MyListAdapter.Holder onCreateViewHolder(ViewGroup p1, int p2) {
	View v = li.inflate(R.layout.list_item, p1, false);
	return new Holder(v);
  }

  @Override
  public void onBindViewHolder(MyListAdapter.Holder h, int p2) {
	Dependency dep = deps.get(p2);

	h.group.setText(dep.g);
	h.artifact.setText(dep.a);
	h.version.setText(dep.latestVersion);
  }

  @Override
  public int getItemCount() {
	return deps.size();
  }

  class Holder extends RecyclerView.ViewHolder {
	TextView group, artifact, version;
	CardView cv;
	public Holder(View view) {
	  super(view);
	  cv = (CardView)view.findViewById(R.id.cardview);
	  group = (TextView) view.findViewById(R.id.list_item_group); 
	  artifact = (TextView) view.findViewById(R.id.list_item_artifact);
	  version = (TextView) view.findViewById(R.id.list_item_version);

	  cv.setOnClickListener(new View.OnClickListener(){

		  @Override
		  public void onClick(View p1) {
			activity.showBottomSheet(deps.get(getAdapterPosition()));
		  }
		});
	  
	  cv.setOnLongClickListener(new View.OnLongClickListener(){

		  @Override
		  public boolean onLongClick(View p1) {
			Dependency dep = deps.get(getAdapterPosition());
			activity.copy(dep.toGradle());
			return true;
		  }
		});

	}
  }
}

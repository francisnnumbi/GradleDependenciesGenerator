package fnn.smirl.gdg.pojo;

public class Dependency
{
  public String id, g, a, p,  latestVersion;

  public Dependency(String id, String g, String a, String p, String latestVersion) {
	this.id = id;
	this.g = g;
	this.a = a;
	this.p = p;
	this.latestVersion = latestVersion;
  }
  
  public String toGradle(){
	return "compile '" + id + ":" + latestVersion + "'";
  }

  @Override
  public String toString() {
	return "id : " + id +
	"\ng : " +g + 
	"\na : " + a +
	"\nlatestVersion : " + latestVersion +
	"\np : " + p;
  }
  
  
}

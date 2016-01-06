package cn.edu.njnu.infoextract.impl.incubators;

public class ValueFeature {
	private String path;
	private String tag;
	private String classname;
	private String right_tag;
	private String left_tag;
	public ValueFeature(String path, String tag, String classname, String right_tag, String left_tag) {
		super();
		this.path = path;
		this.tag = tag;
		this.classname = classname;
		this.right_tag = right_tag;
		this.left_tag = left_tag;
	}
	public String getRight_tag() {
		return right_tag;
	}
	public void setRight_tag(String right_tag) {
		this.right_tag = right_tag;
	}
	public String getLeft_tag() {
		return left_tag;
	}
	public void setLeft_tag(String left_tag) {
		this.left_tag = left_tag;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public String getTag() {
		return tag;
	}
	public void setTag(String tag) {
		this.tag = tag;
	}
	public String getClassname() {
		return classname;
	}
	public void setClassname(String classname) {
		this.classname = classname;
	}
}

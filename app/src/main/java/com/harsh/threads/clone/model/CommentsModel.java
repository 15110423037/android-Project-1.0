package com.harsh.threads.clone.model;

import java.util.List;

public class CommentsModel{
	private String uid;
	private List<String> images;
	private List<String> replies;
	private int visibility;
	private String id;
	private String text;
	private String time;
	private String username;
	private List<String> likes;

	public CommentsModel() {
	}

	public CommentsModel(String uid, List<String> images, List<String> replies, int visibility, String id, String text, String time, String username, List<String> likes) {
		this.uid = uid;
		this.images = images;
		this.replies = replies;
		this.visibility = visibility;
		this.id = id;
		this.text = text;
		this.time = time;
		this.username = username;
		this.likes = likes;
	}

	public void setUid(String uid){
		this.uid = uid;
	}

	public String getUid(){
		return uid;
	}

	public void setImages(List<String> images){
		this.images = images;
	}

	public List<String> getImages(){
		return images;
	}

	public void setReplies(List<String> replies){
		this.replies = replies;
	}

	public List<String> getReplies(){
		return replies;
	}

	public void setVisibility(int visibility){
		this.visibility = visibility;
	}

	public int getVisibility(){
		return visibility;
	}

	public void setId(String id){
		this.id = id;
	}

	public String getId(){
		return id;
	}

	public void setText(String text){
		this.text = text;
	}

	public String getText(){
		return text;
	}

	public void setTime(String time){
		this.time = time;
	}

	public String getTime(){
		return time;
	}

	public void setUsername(String username){
		this.username = username;
	}

	public String getUsername(){
		return username;
	}

	public void setLikes(List<String> likes){
		this.likes = likes;
	}

	public List<String> getLikes(){
		return likes;
	}

	@Override
 	public String toString(){
		return 
			"CommentsModel{" + 
			"uid = '" + uid + '\'' + 
			",images = '" + images + '\'' + 
			",replies = '" + replies + '\'' + 
			",visibility = '" + visibility + '\'' + 
			",id = '" + id + '\'' + 
			",text = '" + text + '\'' + 
			",time = '" + time + '\'' + 
			",username = '" + username + '\'' + 
			",likes = '" + likes + '\'' + 
			"}";
		}
}
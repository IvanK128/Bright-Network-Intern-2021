package com.google;

import java.util.Collections;
import java.util.List;

/** A class used to represent a video. */
class Video {

  private final String title;
  private final String videoId;
  private final List<String> tags;

  Video(String title, String videoId, List<String> tags) {
    this.title = title;
    this.videoId = videoId;
    this.tags = Collections.unmodifiableList(tags);
  }

  /** Returns the title of the video. */
  String getTitle() {
    return title;
  }

  /** Returns the video id of the video. */
  String getVideoId() {
    return videoId;
  }

  /** Returns a readonly collection of the tags of the video. */
  List<String> getTags() {
    return tags;
  }

  public String toString(){
    StringBuilder videoDetail = new StringBuilder();
    videoDetail.append(title
            +" ("+videoId+") ");
    if(tags.size()>1){
      videoDetail.append("["+tags.get(0)+" "+tags.get(1)+"]");
    }else{
      videoDetail.append("[]");
    }
    return videoDetail.toString();
  }
}
